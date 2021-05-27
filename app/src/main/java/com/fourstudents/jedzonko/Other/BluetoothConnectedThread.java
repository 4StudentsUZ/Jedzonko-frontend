package com.fourstudents.jedzonko.Other;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler handler;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public BluetoothConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.handler = handler;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e("HarryConnected", "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("HarryConnected", "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        Log.i("HarryConnected", "connectedRun");

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                Message msg = new Message();
                msg.what = BluetoothServiceClass.MESSAGE_READY;
                msg.obj = this;
                handler.sendMessage(msg);
                mmBuffer = new byte[1024];
                int bytesLength; // bytes returned from read()
                // Read from the InputStream.
                bytesLength = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                handler.obtainMessage(BluetoothServiceClass.MESSAGE_READ, bytesLength, -1, mmBuffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.i("HarryConnected", "Input stream was disconnected", e);
                handler.sendEmptyMessage(99);
//                if (fragmentContext != null)
//                    Toast.makeText(fragmentContext, "Odbiorca nie jest już widzialny", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);

            // Share the sent message with the UI activity.
            Message writtenMsg = handler.obtainMessage(BluetoothServiceClass.MESSAGE_WRITE, bytes.length, -1, bytes);
            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e("HarryConnected", "Error occurred when sending data", e);

            // Send a failure message back to the activity.
            Message writeErrorMsg = handler.obtainMessage(BluetoothServiceClass.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Couldn't send data to the other device");
            writeErrorMsg.setData(bundle);
            handler.sendMessage(writeErrorMsg);
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("HarryConnected", "Could not close the connect socket", e);
        }
    }
}

