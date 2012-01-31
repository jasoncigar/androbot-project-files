package com.org;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

public class JoyStick extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display mDisplay= mWindowManager.getDefaultDisplay();
    }
}