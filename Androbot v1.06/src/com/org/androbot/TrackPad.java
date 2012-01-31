package com.org.androbot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class TrackPad extends Activity {

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

	private ImageView stop, forward, backward, right, left;
	private TextView mTitle;
	private Button back;
	private OnClickListener listenClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == back) {
				Intent intent = new Intent();
				intent.putExtra("ADDRESS", address);
				setResult(Activity.RESULT_OK, intent);
				mChatService.stop();
				finish();
			} else if (v == forward) {
				sendMessage("a");
			} else if (v == backward) {
				sendMessage("b");
			} else if (v == right) {
				sendMessage("c");
			} else if (v == left) {
				sendMessage("d");
			} else if (v == stop) {
				sendMessage("e");
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.trackpad);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		mChatService = new BluetoothThread(this, mHandler);

		Bundle extras = getIntent().getExtras();
		address = extras.getString("DeviceAddress");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mChatService.connect(device);

		stop = (ImageView) findViewById(R.id.start);
		stop.setOnClickListener(listenClick);

		forward = (ImageView) findViewById(R.id.forward);
		forward.setOnClickListener(listenClick);

		backward = (ImageView) findViewById(R.id.backward);
		backward.setOnClickListener(listenClick);

		right = (ImageView) findViewById(R.id.right);
		right.setOnClickListener(listenClick);

		left = (ImageView) findViewById(R.id.left);
		left.setOnClickListener(listenClick);

		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(listenClick);

	}

	public void onDestroy() {
		super.onDestroy();
		mChatService.stop();
	}

	private void sendMessage(String message) {
		if (mChatService.getState() != BluetoothThread.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}
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

}