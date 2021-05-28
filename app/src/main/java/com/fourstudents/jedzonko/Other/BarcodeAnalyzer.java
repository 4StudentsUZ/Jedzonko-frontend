package com.fourstudents.jedzonko.Other;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;

import com.fourstudents.jedzonko.MainActivity;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    private MainActivity activity;
    private BarcodeListener barcodeListener;
//    private BarcodeScannerOptions options = new BarcodeScannerOptions.Builder().build();
    private final BarcodeScanner scanner = BarcodeScanning.getClient();

    public BarcodeAnalyzer(MainActivity activity) {
        this.activity = activity;
    }
    public BarcodeAnalyzer(BarcodeListener barcodeListener) {
        this.barcodeListener = barcodeListener;
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            if (barcode.getRawValue() != null) {
                                barcodeListener.listen(barcode.getRawValue());
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.v("HarryBarcodeAnalyzer", e.toString());
                        e.getCause().printStackTrace();

                    })
                    .addOnCompleteListener(task -> {
                        imageProxy.close();
                    });
        }

    }
}
