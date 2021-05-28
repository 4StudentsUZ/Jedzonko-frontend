package com.fourstudents.jedzonko.Other.Bluetooth;

import java.util.UUID;

public class BluetoothServiceClass {
    public static final UUID bluetoothUUID = UUID.fromString("2b513be7-fa85-4e8c-a12b-f32a661a4b47");

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 3;
    public static final int MESSAGE_READY = 5;
    public static final int MESSAGE_CLOSE_CONNECTION = 99;

}
