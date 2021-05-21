package com.fourstudents.jedzonko.Other;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

public class BluetoothSendThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final Handler handler;

    public BluetoothSendThread(BluetoothDevice device, Handler handler) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.handler = handler;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(BluetoothServiceClass.bluetoothUUID);
        } catch (IOException e) {
            Log.e("HarrySend", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            Log.e("HarrySend", "Could not connect", connectException);
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("HarrySend", "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        BluetoothConnectedThread thread = new BluetoothConnectedThread(mmSocket, handler);
        thread.start();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("HarrySend", "Could not close the client socket", e);
        }
    }

}
