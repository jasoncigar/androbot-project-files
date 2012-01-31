package com.org;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class TrackPad extends Activity {

	private ImageView start;
	private OnClickListener listenClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == start) {
				Toast.makeText(getApplicationContext(), "start clicked",
						Toast.LENGTH_SHORT).show();
			}
	
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trackpad);
		start = (ImageView) findViewById(R.id.start);
		start.setOnClickListener(listenClick);
	
	}

}