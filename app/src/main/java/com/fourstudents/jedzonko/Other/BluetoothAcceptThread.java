package com.fourstudents.jedzonko.Other;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static com.fourstudents.jedzonko.Other.BluetoothServiceClass.bluetoothUUID;

public class BluetoothAcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private final Handler handler;

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
                BluetoothConnectedThread thread = new BluetoothConnectedThread(socket, handler);
                thread.start();
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
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e("HarrySocket", "Could not close the connect socket", e);
        }
    }

}
