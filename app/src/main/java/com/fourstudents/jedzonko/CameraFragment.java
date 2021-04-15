package com.fourstudents.jedzonko;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {

    private final String TAG = "CameraXBasic";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private boolean isOffline = false;

    Preview preview = null;
    ImageCapture imageCapture = null;
    Camera camera = null;

    File outputDirectory;
    ExecutorService cameraExecutor;
    Context safeContext;

    public CameraFragment() {
        super(R.layout.fragment_camera);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        safeContext = context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(safeContext, "Permission not granted", Toast.LENGTH_LONG).show();
                requireFragmentManager().popBackStack();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        view.findViewById(R.id.camera_capture_button).setOnClickListener(v -> takePhoto());

        outputDirectory = getOutPutDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext);

        cameraProviderFuture.addListener(() -> {

            preview = new Preview.Builder().build();
            imageCapture = new ImageCapture.Builder().build();

            preview.setSurfaceProvider(((PreviewView)requireView().findViewById(R.id.viewFinder)).getSurfaceProvider());

            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Harry startCamera()", "read stack trace");
                e.printStackTrace();
            }


        }, ContextCompat.getMainExecutor(safeContext));
    }

    private void takePhoto() {

        File photoFile = new File(outputDirectory, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(safeContext), new ImageCapture.OnImageSavedCallback() {

            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);
                String msg = "Photo capture success: " + savedUri;
                Toast.makeText(safeContext, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("Harry takePhoto().onError()", "read stack trace");
                exception.printStackTrace();
            }
        });

    }

    private File getOutPutDirectory() {
        File mediaDir = safeContext.getApplicationContext().getExternalMediaDirs()[0];
        File appDir = new File(mediaDir, "images");
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