package com.fourstudents.jedzonko.Other;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.fourstudents.jedzonko.Network.JedzonkoService;

import java.nio.ByteBuffer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HarryHelperClass {

    public static byte[] getBytesFromBuffer(ByteBuffer buffer) {
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return bytes.clone();
    }

    public static Bitmap rotateBitmapByAngle(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public enum CameraModes {
        Recipe,
        Product,
        Barcode
    }
}
