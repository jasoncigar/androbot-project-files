package com.org;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;

public class ZeeTest extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            for (int i = 0; i < 3; i++) {
                test();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean connected = false;
    private BluetoothSocket sock;
    private InputStream in;
    public void test() throws Exception {
        if (connected) {
            return;
        }
        BluetoothDevice zee = BluetoothAdapter.getDefaultAdapter().
            getRemoteDevice("00:15:83:15:A3:10");
        Method m = zee.getClass().getMethod("createRfcommSocket",
            new Class[] { int.class });
        sock = (BluetoothSocket)m.invoke(zee, Integer.valueOf(1));
        Log.d("ZeeTest", "++++ Connecting");
        sock.connect();
        Log.d("ZeeTest", "++++ Connected");
        in = sock.getInputStream();
        byte[] buffer = new byte[50];
        int read = 0;
        Log.d("ZeeTest", "++++ Listening...");
        try {
            while (true) {
                read = in.read(buffer);
                connected = true;
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < read; i++) {
                    int b = buffer[i] & 0xff;
                    if (b < 0x10) {
                        buf.append("0");
                    }
                    buf.append(Integer.toHexString(b)).append(" ");
                }
                Log.d("ZeeTest", "++++ Read "+ read +" bytes: "+ buf.toString());
            }
        } catch (IOException e) {}
        Log.d("ZeeTest", "++++ Done: test()");
    }
    @Override
    public void onDestroy() {
        try {
            if (in != null) {
                in.close();
            }
            if (sock != null) {
                sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}