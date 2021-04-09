package com.fourstudents.jedzonko;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {

    private final String TAG = "CameraXBasic";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    ImageCapture imageCapture = null;
    Preview preview = null;

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
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        view.findViewById(R.id.camera_capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        outputDirectory = getOutPutDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext);

        cameraProviderFuture.addListener(() -> {

            preview = new Preview.Builder().build();
            preview.setSurfaceProvider(((PreviewView)requireView().findViewById(R.id.viewFinder)).getSurfaceProvider());

            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


        }, ContextCompat.getMainExecutor(safeContext));
    };
    private void takePhoto() {};

    private File getOutPutDirectory() {
        File mediaDir = requireActivity().getExternalMediaDirs()[0];
        File appDir = new File(mediaDir, getResources().getString(R.string.app_name));
        appDir.mkdirs();
        return (mediaDir != null && appDir.exists()) ? appDir : requireActivity().getFilesDir();
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