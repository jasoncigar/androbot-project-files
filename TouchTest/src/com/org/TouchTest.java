package com.org;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class TouchTest extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ImageView im = (ImageView) findViewById(R.id.imageView1);
        im.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
				Log.e("Touch", event.getX() + "=x   " + event.getY()+"=y");
				return false;
			}
		});
    }
}