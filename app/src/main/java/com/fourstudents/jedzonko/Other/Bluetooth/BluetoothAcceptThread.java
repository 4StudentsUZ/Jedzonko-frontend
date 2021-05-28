package com.fourstudents.jedzonko.Other.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import static com.fourstudents.jedzonko.Other.Bluetooth.BluetoothServiceClass.bluetoothUUID;

public class BluetoothAcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private final Handler handler;
    BluetoothConnectedThread bluetoothConnectedThread;

    public BluetoothAcceptThread(BluetoothAdapter bluetoothAdapter, Handler handler) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        this.handler = handler;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Jedzonko", bluetoothUUID);
        } catch (IOException e) {
            Log.e("HarrySocket", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e("HarrySocket", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                bluetoothConnectedThread = new BluetoothConnectedThread(socket, handler);
                bluetoothConnectedThread.start();
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        Log.i("HarryAcceptCancel", "Cancel");
        try {
            mmServerSocket.close();
            if (bluetoothConnectedThread != null)
                bluetoothConnectedThread.cancel();
        } catch (IOException e) {
            Log.e("HarrySocket", "Could not close the connect socket", e);
        }
    }

}
