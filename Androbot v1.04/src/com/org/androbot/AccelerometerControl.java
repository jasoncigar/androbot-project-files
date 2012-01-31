package com.org.androbot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccelerometerControl extends Activity implements
		SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mOrientation;

	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	private String mConnectedDeviceName = null;
	private BluetoothThread mChatService = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private String address;
	private boolean stopFlag = false;
	private TextView x, y, z;

	private TextView mTitle;
	private Button bt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.trackpad);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
		setContentView(R.layout.accelerometer);
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		x = (TextView) findViewById(R.id.xbox);
		y = (TextView) findViewById(R.id.ybox);
		z = (TextView) findViewById(R.id.zbox);

		mChatService = new BluetoothThread(this, mHandler);
		bt = (Button) findViewById(R.id.button1);
		bt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					stopFlag = true;
					sendMessage("e");
					break;
				case MotionEvent.ACTION_UP:
					stopFlag = false;
					break;
				}
				return true;

			}
		});
		Bundle extras = getIntent().getExtras();
		address = extras.getString("DeviceAddress");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mChatService.connect(device);
	}

	public void onDestroy() {
		super.onDestroy();
		mChatService.stop();
	}

	private void sendMessage(String message) {
		if (message.length() > 0) {
			byte[] send = message.getBytes();
			mChatService.write(send);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothThread.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);

					break;
				case BluetoothThread.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothThread.STATE_LISTEN:
				case BluetoothThread.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener((SensorEventListener) this,
				mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener((SensorEventListener) this,
				mOrientation, SensorManager.SENSOR_DELAY_NORMAL);

	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		x.setText("x:" + event.values[0]);
		y.setText("y:" + event.values[1]);
		z.setText("z;" + event.values[2]);

		if (!stopFlag) {
			// forward
			if (event.values[1] < 2 && event.values[1] > -2
					&& event.values[0] < -2) {
				sendMessage("a");
				Log.e("ACC", "a");
			}
			// backward
			else if (event.values[1] < 2 && event.values[1] > -2
					&& event.values[0] > 2) {
				sendMessage("b");
				Log.e("ACC", "b");
			}
			// right
			else if (event.values[1] > 2
					&& event.values[0] < 2 && event.values[1] > -2) {
				sendMessage("d");
				Log.e("ACC", "d");
			}
			// left
			else if (event.values[1] < -2
					&& event.values[0] < 2 && event.values[0] > -2) {
				sendMessage("c");
				Log.e("ACC", "c");
			}
		}
	}
}
