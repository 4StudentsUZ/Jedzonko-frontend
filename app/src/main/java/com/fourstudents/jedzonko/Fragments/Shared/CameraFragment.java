package com.fourstudents.jedzonko.Fragments.Shared;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Other.BarcodeAnalyzer;
import com.fourstudents.jedzonko.Other.HarryHelperClass;
import com.fourstudents.jedzonko.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraFragment extends Fragment {

    private final String TAG = "CameraXBasic";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    //CameraX
    Preview preview = null;
    ImageCapture imageCapture = null;
    ImageAnalysis imageAnalysis = null;
    Camera camera = null;

    //Capture
    File outputDirectoryForRecipe;
    File outputDirectoryForProduct;
    ExecutorService cameraExecutor;
    Context safeContext;

    //Fragment
    PreviewView previewView;

    //Helper
    MainActivity activity;
    Bundle args;
    int triesToClose = 0;
    AtomicBoolean processingBarcode = new AtomicBoolean(false);

    HarryHelperClass.CameraModes mode;

    public CameraFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        safeContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        processingBarcode.set(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int layoutId = R.layout.fragment_camera;
        activity = ((MainActivity) requireActivity());
        args = getArguments();
        mode = HarryHelperClass.CameraModes.Recipe;

        if (args != null) {
            mode = (HarryHelperClass.CameraModes) args.getSerializable("mode");
        }

        if (mode == HarryHelperClass.CameraModes.Barcode)
            layoutId = R.layout.fragment_camera_barcode;

        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(safeContext, R.string.permission_not_granted, Toast.LENGTH_LONG).show();
                getParentFragmentManager().popBackStack();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewView = view.findViewById(R.id.viewFinder);

        if (allPermissionsGranted()) {
            makeOutputDirectoryForImages();
            startCamera();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        if (mode != HarryHelperClass.CameraModes.Barcode)
            view.findViewById(R.id.camera_capture_button).setOnClickListener(v -> takePhoto());

        outputDirectoryForRecipe = getOutPutDirectoryForRecipe();
        outputDirectoryForProduct = getOutPutDirectoryForProduct();
        cameraExecutor = Executors.newSingleThreadExecutor();

    }

    private void makeOutputDirectoryForImages() {
        File mediaDir = safeContext.getApplicationContext().getExternalMediaDirs()[0];
        File appDir = new File(mediaDir, "images");
        appDir.mkdirs();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext);

        cameraProviderFuture.addListener(() -> {

            preview = new Preview.Builder().build();

            if (mode == HarryHelperClass.CameraModes.Barcode) {
                imageAnalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
                imageAnalysis.setAnalyzer(cameraExecutor, new BarcodeAnalyzer(barcode -> {
                    if (processingBarcode.compareAndSet(false, true)) {
                        activity.scannedBarcode = barcode;
                        Toast.makeText(safeContext, R.string.camera_barcode_scanned, Toast.LENGTH_LONG).show();
                        getParentFragmentManager().popBackStack();
                    }
                }));
            }

            imageCapture = new ImageCapture.Builder().build();
            imageCapture.setTargetRotation(Surface.ROTATION_0);

            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                if (mode == HarryHelperClass.CameraModes.Barcode) {
                    camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);
                    MeteringPoint autoFocusPoint =
                            new SurfaceOrientedMeteringPointFactory(1f, 1f)
                                    .createPoint(.5f, .5f);
                    FocusMeteringAction autoFocusAction =
                            new FocusMeteringAction
                                    .Builder(autoFocusPoint, FocusMeteringAction.FLAG_AF)
                                    .setAutoCancelDuration(1000, TimeUnit.MILLISECONDS)
                                    .build();

                    camera.getCameraControl().startFocusAndMetering(autoFocusAction);
                }
                else
                    camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);



//                previewView.setOnTouchListener((v, event) -> {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            return true;
//                        case MotionEvent.ACTION_UP:
//                            v.performClick();
//                            MeteringPointFactory factory =
//                                    new SurfaceOrientedMeteringPointFactory(
//                                            previewView.getMeasuredWidth(),
//                                            previewView.getMeasuredHeight()
//                                    );
//                            MeteringPoint touchFocusPoint = factory.createPoint(event.getX(), event.getY());
//                            FocusMeteringAction touchFocusAction =
//                                    new FocusMeteringAction
//                                            .Builder(touchFocusPoint, FocusMeteringAction.FLAG_AF)
//                                            .disableAutoCancel()
//                                            .build();
//                            camera.getCameraControl().startFocusAndMetering(touchFocusAction);
//                            return true;
//                        default:
//                            break;
//                    }
//                    return false;
//                });

            } catch (ExecutionException | InterruptedException e) {
                Log.e("Harry startCamera()", "read stack trace");
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(safeContext));


    }

    private void takePhoto() {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(safeContext),
            new ImageCapture.OnImageCapturedCallback() {

                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    super.onCaptureSuccess(image);
                    ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                    if (mode == HarryHelperClass.CameraModes.Recipe) {
                        activity.imageData = HarryHelperClass.getBytesFromBuffer(byteBuffer);
                        activity.imageRotation = image.getImageInfo().getRotationDegrees();
                    } else {
                        activity.productImageData = HarryHelperClass.getBytesFromBuffer(byteBuffer);
                        activity.productImageRotation = image.getImageInfo().getRotationDegrees();
                    }

                    Toast.makeText(safeContext, R.string.camera_photo_captured_info, Toast.LENGTH_LONG).show();
                    image.close();
                    tryToClose();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                    Log.e("Harry takePhoto().onError()", "read stack trace");
                    exception.printStackTrace();
                }
            }
        );


        File photoFile;

        if (mode == HarryHelperClass.CameraModes.Recipe)
            photoFile = new File(outputDirectoryForRecipe, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");
        else
            photoFile = new File(outputDirectoryForProduct, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(safeContext),
            new ImageCapture.OnImageSavedCallback() {

                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    Uri savedUri = Uri.fromFile(photoFile);
                    String msg = "Photo capture success: " + savedUri;
                    Toast.makeText(safeContext, msg, Toast.LENGTH_LONG).show();
                    tryToClose();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Log.e("Harry takePhoto().onError()", "read stack trace");
                    exception.printStackTrace();
                }
            }
        );
    }

    private void tryToClose() {
        if (++triesToClose == 2)
            getParentFragmentManager().popBackStack();
    }

    private File getOutPutDirectoryForRecipe() {
        File mediaDir = safeContext.getApplicationContext().getExternalMediaDirs()[0];
        File appDir = new File(mediaDir, "images/recipes");
        appDir.mkdirs();
        //return (mediaDir != null && appDir.exists()) ? appDir : requireActivity().getFilesDir();
        return appDir;
    }

    private File getOutPutDirectoryForProduct() {
        File mediaDir = safeContext.getApplicationContext().getExternalMediaDirs()[0];
        File appDir = new File(mediaDir, "images/products");
        appDir.mkdirs();
        //return (mediaDir != null && appDir.exists()) ? appDir : requireActivity().getFilesDir();
        return appDir;
    }

    private boolean allPermissionsGranted() {
        for (String permission: REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(safeContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}