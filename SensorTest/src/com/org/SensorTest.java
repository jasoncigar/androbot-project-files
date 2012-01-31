package com.org;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorTest extends Activity implements SensorEventListener {
	SensorManager mSensorManager;
	Sensor mAccelerometer;
	Sensor mProximity;
	Sensor mOrientation;
	TextView xViewA = null;
	TextView yViewA = null;
	TextView zViewA = null;
	TextView xViewO = null;
	TextView yViewO = null;
	TextView zViewO = null;
	TextView proximity = null;
	TextView temprature = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		setContentView(R.layout.main);
		xViewA = (TextView) findViewById(R.id.xbox);
		yViewA = (TextView) findViewById(R.id.ybox);
		zViewA = (TextView) findViewById(R.id.zbox);
		xViewO = (TextView) findViewById(R.id.xboxo);
		yViewO = (TextView) findViewById(R.id.yboxo);
		zViewO = (TextView) findViewById(R.id.zboxo);
		proximity = (TextView) findViewById(R.id.proximity);
		temprature = (TextView) findViewById(R.id.temp);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

	}

	public SensorTest() {

	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener((SensorEventListener) this,
				mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener((SensorEventListener) this,
				mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener((SensorEventListener) this, mProximity,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometer) {
			xViewA.setText("Acc X " + event.values[0]);
			yViewA.setText("Acc Y " + event.values[1]);
			zViewA.setText("Acc Z " + event.values[2]);

		}
		if (event.sensor == mOrientation) {
			xViewO.setText("x :" + event.values[0]);
			yViewO.setText("y :" + event.values[1]);
			zViewO.setText("z :" + event.values[2]);

		}
		if (event.sensor == mProximity) {
			proximity.setText("proximity " + event.values[0]);
		}
	}
}
