package com.org.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Camera.PreviewCallback;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.cellbots.CellbotProtos.AudioVideoFrame;
import com.google.protobuf.ByteString;
import com.org.androbot.BluetoothThread;
import com.org.androbot.R;
import com.org.movementhandler.MovementHandler;
import com.org.sensor.SensorState;

public class StreamVideo extends Activity {

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

	private static String BOTID;
	private static String URL;
	private Preview mPreview;
	private Camera mCamera;
	private ByteArrayOutputStream out;
	byte[] mCallBackBuffer;
	private int previewHeight = 0;
	private int previewWidth = 0;
	private int previewFormat = 0;
	private ConversionWorker convWorker;
	private MovementHandler moveHandler;
	private TextToSpeech mTts;
	private SensorState sState;

	private SensorManager mSensorManager;
	private TelephonyManager TeleMan;
	private LocationManager locationManager;
	private SmsManager smsMng;

	private ImageView expression;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mTts = new TextToSpeech(this, new OnInitListener() {

			public void onInit(int status) {

			}
		});
		mTts.setLanguage(Locale.US);
		mPreview = new Preview(this);

		setContentView(mPreview);
		expression = new ImageView(this);
		addContentView(expression, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		expression.setVisibility(ImageView.GONE);
		mChatService = new BluetoothThread(this, mHandler);
		Bundle extras = getIntent().getExtras();
		address = extras.getString("DeviceAddress");
		if (extras.containsKey("BOTID") && extras.containsKey("URL")) {
			BOTID = extras.getString("BOTID");
			URL = extras.getString("URL");
		} else {
			BOTID = "jason's";
			URL = "192.168.1.203:8888";
		}
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mChatService.connect(device);

		out = new ByteArrayOutputStream();
		convWorker = new ConversionWorker();
		while (convWorker.cHandler == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		startListening(convWorker.cHandler);
		Log.e("Listen", "Listen over");

		smsMng = SmsManager.getDefault();

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		TeleMan = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		sState = new SensorState(mSensorManager, locationManager, TeleMan,
				moveHandler.handler, BOTID);
		sState.registerListeners();
		registerReceiver(sState.getBatteryReciever(), new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	private synchronized void startListening(Handler cHandler) {
		Log.e("Listen", "startListening called");

		if (moveHandler == null) {
			moveHandler = new MovementHandler(cHandler, BOTID, URL);
			moveHandler.start();
			while (moveHandler.handler == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		sState.unregisterListers();

	}

	@Override
	protected void onResume() {
		super.onResume();
		sState.registerListeners();
	}

	protected void onDestroy() {
		super.onDestroy();
		convWorker.kill();
		mChatService.stop();

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
					break;
				case BluetoothThread.STATE_CONNECTING:
					break;
				case BluetoothThread.STATE_LISTEN:
				case BluetoothThread.STATE_NONE:
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

	class Preview extends SurfaceView implements SurfaceHolder.Callback {
		SurfaceHolder mHolder;

		Preview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mCamera = Camera.open();
			Log.e("Camera", "SurfaceCreated");
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {

			Log.e("Camera", "Surface Changed");
			Camera.Parameters parameters = mCamera.getParameters();

			parameters.setPreviewSize(320, 240);
			parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			previewHeight = parameters.getPreviewSize().height;
			previewWidth = parameters.getPreviewSize().width;
			previewFormat = parameters.getPreviewFormat();
			mCamera.setParameters(parameters);
			mCallBackBuffer = new byte[115200];
			mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
				public void onPreviewFrame(byte[] data, Camera camera) {
					convWorker.nextFrame(data);

				}
			});
			mCamera.addCallbackBuffer(mCallBackBuffer);
			mCamera.startPreview();

		}
	}

	class ConversionWorker extends Thread {

		HttpClient httpclient;
		boolean alive;
		volatile HttpPost post;
		volatile boolean sending = false;
		volatile int compressionlevel = 70;

		public ConversionWorker() {
			httpclient = new DefaultHttpClient();
			alive = true;
			start();
		}

		public void kill() {
			alive = false;
		}

		Handler cHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				String movement;
				switch (msg.what) {
				case 1:
					compressionlevel = msg.arg1;
					break;
				case 2:
					bundle = msg.getData();
					movement = (String) bundle.get("mov");

					if (movement.equals("forward")) {
						StreamVideo.this.sendMessage("a");

					} else if (movement.equals("backward")) {
						StreamVideo.this.sendMessage("b");

					} else if (movement.equals("left")) {
						StreamVideo.this.sendMessage("c");

					} else if (movement.equals("right")) {
						StreamVideo.this.sendMessage("d");

					} else if (movement.equals("stop")) {
						StreamVideo.this.sendMessage("e");

					}
					break;
				case 3:
					bundle = msg.getData();
					movement = (String) bundle.get("tts");
					Log.e("tts", movement);
					mTts.speak(movement, TextToSpeech.QUEUE_FLUSH, null);
				case 4:
					bundle = msg.getData();
					String phoneno = (String) bundle.get("phoneNo");
					String message = (String) bundle.get("msg");
					smsMng.sendTextMessage(phoneno, null, message, null, null);
				case 5:
					bundle = msg.getData();
					String express = (String) bundle.get("express");
					if (express.equals("happy")) {
						expression.setImageResource(R.drawable.happysmiley);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("sad")) {
						expression.setImageResource(R.drawable.sadsmiley);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("angry")) {
						expression.setImageResource(R.drawable.angrysmiley);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("veryangry")) {
						expression.setImageResource(R.drawable.veryangrysmiley);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("confused")) {
						expression.setImageResource(R.drawable.confusedsmiley);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("laughing")) {
						expression.setImageResource(R.drawable.laughing);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("shocked")) {
						expression.setImageResource(R.drawable.shocked);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("error")) {
						expression.setImageResource(R.drawable.errorsmiley);
						expression.setVisibility(ImageView.VISIBLE);
					} else if (express.equals("wink")) {
						expression.setImageResource(R.drawable.wink);
						expression.setVisibility(ImageView.VISIBLE);
					}else if (express.equals("camera")) {
						expression.setVisibility(ImageView.GONE);
					}
				default:
					break;
				}

			}
		};

		@Override
		public synchronized void run() {
			try {
				wait();
			} catch (InterruptedException e) {
			}
			while (alive) {

				try {

					httpclient = new DefaultHttpClient();
					sending = true;
					Rect r = new Rect(0, 0, previewWidth, previewHeight);
					YuvImage yuvImage = new YuvImage(mCallBackBuffer,
							previewFormat, previewWidth, previewHeight, null);
					yuvImage.compressToJpeg(r, compressionlevel, out);
					AudioVideoFrame.Builder avFrame = AudioVideoFrame
							.newBuilder();

					avFrame.setData(ByteString.copyFrom(out.toByteArray()));
					avFrame.setBotID(BOTID);
					avFrame.setCompressionLevel(compressionlevel);
					avFrame.setTimestamp(System.currentTimeMillis());
					post = new HttpPost("http://" + URL + "/video");

					post.setEntity(new ByteArrayEntity(avFrame.build()
							.toByteArray()));
					httpclient.execute(post);

					sending = false;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {

				} catch (IOException e) {
					e.printStackTrace();

				} finally {
					out.reset();
					if (mCamera != null && moveHandler.handler != null) {
						mCamera.addCallbackBuffer(mCallBackBuffer);
						Message msg = moveHandler.handler.obtainMessage();
						msg.obj = "garbage";
						msg.sendToTarget();
					}
					sending = false;
				}
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			if (!alive)
				return;
		}

		synchronized boolean nextFrame(byte[] frame) {
			if (this.getState() == Thread.State.WAITING && !sending) {
				this.notify();
				return true;
			} else {
				return false;

			}

		}
	}

}