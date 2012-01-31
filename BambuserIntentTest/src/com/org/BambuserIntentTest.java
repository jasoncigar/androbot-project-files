package com.org;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BambuserIntentTest extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent newActivityIntent = getPackageManager().getLaunchIntentForPackage("com.bambuser.broadcaster");
        newActivityIntent.setAction("android.intent.action.MAIN");
        newActivityIntent.addCategory("android.intent.category.LAUNCHER");
        newActivityIntent.putExtra("autostart", true);
        startActivity(newActivityIntent);
    }
}