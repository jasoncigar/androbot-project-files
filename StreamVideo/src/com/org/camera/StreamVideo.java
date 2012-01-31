package com.org.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.cellbots.CellbotProtos.AudioVideoFrame;
import com.google.protobuf.ByteString;
import com.org.movementhandler.MovementHandler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class StreamVideo extends Activity {

	private static String BOTID = "jason's";
	private Preview mPreview;
	private Camera mCamera;
	private ByteArrayOutputStream out;
	byte[] mCallBackBuffer;
	private int previewHeight = 0;
	private int previewWidth = 0;
	private int previewFormat = 0;
	private ConversionWorker convWorker;
	private MovementHandler moveHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mPreview = new Preview(this);
		setContentView(mPreview);
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
	}

	private synchronized void startListening(Handler cHandler) {
		Log.e("Listen", "startListening called");

		if (moveHandler == null) {
			moveHandler = new MovementHandler(cHandler);
			moveHandler.start();
			while (moveHandler.handler == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}

		}
	}

	protected void onDestroy() {
		super.onDestroy();
		convWorker.kill();

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
		volatile int compressionlevel;

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
				compressionlevel = msg.arg1;
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
					post = new HttpPost("http://192.168.1.203:8888/video");

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