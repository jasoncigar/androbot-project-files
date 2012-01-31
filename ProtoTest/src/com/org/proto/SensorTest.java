package com.org.proto;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.cellbots.CellbotProtos;

public class SensorTest extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mProximity;
	private Sensor mOrientation;



	TextView xViewA = null;
	TextView yViewA = null;
	TextView zViewA = null;
	TextView xViewO = null;
	TextView yViewO = null;
	TextView zViewO = null;
	TextView proximity = null;
	TextView temprature = null;
	private TextView batteryLevel, batteryVoltage, batteryTemperature,
			cpuUsage;
	private CpuUsage cu;
	private CellbotProtos.PhoneState.Builder state;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		setContentView(R.layout.main);
		state = CellbotProtos.PhoneState.newBuilder();
		xViewA = (TextView) findViewById(R.id.xbox);
		yViewA = (TextView) findViewById(R.id.ybox);
		zViewA = (TextView) findViewById(R.id.zbox);
		xViewO = (TextView) findViewById(R.id.xboxo);
		yViewO = (TextView) findViewById(R.id.yboxo);
		zViewO = (TextView) findViewById(R.id.zboxo);
		proximity = (TextView) findViewById(R.id.proximity);
		temprature = (TextView) findViewById(R.id.temp);

		cu = new CpuUsage();

		batteryLevel = (TextView) findViewById(R.id.batterylevel);
		batteryVoltage = (TextView) findViewById(R.id.batteryvoltage);
		batteryTemperature = (TextView) findViewById(R.id.batterytemperature);

		cpuUsage = (TextView) findViewById(R.id.cpuUsage);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				state.setScreenBrightness((int) cu.getUsage());
				cpuUsage.setText("" + state.getScreenBrightness());
				cpuUsage.postDelayed(this, 500);
			}

		};
		cpuUsage.postDelayed(runnable, 500);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		this.registerReceiver(this.myBatteryReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
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
			CellbotProtos.PhoneState.Accelerometer.Builder b = CellbotProtos.PhoneState.Accelerometer
					.newBuilder();

			b.setX(event.values[0]);
			b.setY(event.values[1]);
			b.setZ(event.values[2]);
			state.setAccelerometer(b);
			xViewA.setText("Acc X " + state.getAccelerometer().getX());
			yViewA.setText("Acc Y " + state.getAccelerometer().getY());
			zViewA.setText("Acc Z " + state.getAccelerometer().getZ());

		}
		if (event.sensor == mOrientation) {
			CellbotProtos.PhoneState.Compass.Builder b = CellbotProtos.PhoneState.Compass
					.newBuilder();

			b.setX(event.values[0]);
			b.setY(event.values[1]);
			b.setZ(event.values[2]);
			state.setCompass(b);
			xViewO.setText("x :" + state.getCompass().getX());
			yViewO.setText("y :" + state.getCompass().getY());
			zViewO.setText("z :" + state.getCompass().getZ());
			// cpuUsage.setText(state.getScreenBrightness());

		}
		if (event.sensor == mProximity) {
			proximity.setText("proximity " + event.values[0]);
		}
	}

	private BroadcastReceiver myBatteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (arg1.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				state.setPhoneBatteryLevel(arg1.getIntExtra("level", 0));
				state.setLightLevel(arg1.getIntExtra("voltage", 0));
				state.setPhoneBatteryTemp(arg1.getIntExtra("temperature", 0));

				batteryLevel.setText("" + state.getPhoneBatteryLevel());
				batteryTemperature.setText("" + state.getPhoneBatteryTemp());
				batteryVoltage.setText("" + state.getLightLevel());
				// cpuUsage.setText(state.getScreenBrightness());
			}
		}

	};

}
