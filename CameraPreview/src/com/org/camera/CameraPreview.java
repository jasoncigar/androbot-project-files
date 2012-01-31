package com.org.camera;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class CameraPreview extends Activity {
	private Preview mPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mPreview = new Preview(this);
		setContentView(mPreview);
		ImageView imv = new ImageView(this);
		imv.setImageResource(R.drawable.confusedsmiley);
		addContentView(imv, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		imv.setImageResource(R.drawable.laughing);
		imv.setVisibility(ImageView.GONE);
	}
}

class Preview extends SurfaceView implements SurfaceHolder.Callback {
	SurfaceHolder mHolder;
	Camera mCamera;
	byte[] cdata;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		Log.e("Camera", "Surface Changed");
		Camera.Parameters parameters = mCamera.getParameters();

		parameters.setPreviewSize(320, 240);
		parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		mCamera.setParameters(parameters);
		cdata = new byte[115200];
		mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
			public void onPreviewFrame(byte[] data, Camera camera) {
				cdata = data;
				Log.e("Camera", "inPreview");
			}
		});
		mCamera.addCallbackBuffer(cdata);
		mCamera.startPreview();
	}

}