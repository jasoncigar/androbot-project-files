package org.anddev.android.cellidtolatlong;

import java.io.DataInputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CellIDToLatLong extends Activity {

	// ===========================================================
	// Static Final Fields
	// ===========================================================

	protected static final boolean CONTINUOUS_UPDATING = false;
	protected static final int MY_NOTIFICATION_ID = 0x1337;
	protected static final int PROGRESSBAR_ID = 0x1234;

	// ===========================================================
	// Static Fields
	// ===========================================================

	protected static HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();

	// ===========================================================
	// Fields
	// ===========================================================

	protected PhoneStateListener myPhoneStateListener;
	/* GUI-Stuff */
	protected EditText myEditLac;
	protected EditText myEditCid;
	protected Button myCmdLocateMe;
	protected Button myCmdUpdate;

	protected int myCellID = -1;
	protected int myLAC = -1;

	protected ProgressDialog myProgressDialog;

	// ===========================================================
	// Fields with anonymous SubClasses
	// ===========================================================

	protected OnClickListener cmdLocateListener = new OnClickListener() {
		// @Override
		public void onClick(View arg0) {
			/* Show a progress-bar */
			myProgressDialog = ProgressDialog.show(CellIDToLatLong.this,
					"Please wait...", "Doing Extreme Calculations...", true);
			new Thread() {
				public void run() {

					try {
						/*
						 * Pretend this is really complex <img
						 * src="http://www.anddev.org/images/smilies/wink.png"
						 * alt=";)" title="Wink" />
						 */
						sleep(5000);

						/*
						 * Parse the values from the EditTexts and try to locate
						 * ourselves
						 */
						int cellid = Integer.parseInt(myEditCid.getText()
								.toString());
						int lac = Integer.parseInt(myEditLac.getText()
								.toString());

						tryToLoate(cellid, lac);

					} catch (NumberFormatException nfe) {
						// Crap was typed <img
						// src="http://www.anddev.org/images/smilies/wink.png"
						// alt=";)" title="Wink" />
					} catch (Exception e) {
						Log.e("LocateMe", e.toString(), e);
					}
					myProgressDialog.dismiss();
				}
			}.start();
		}
	};

	protected OnClickListener cmdUpdateListener = new OnClickListener() {
		// @Override
		public void onClick(View arg0) {
			updateTextFields();
		}
	};

	/** Notification Handler for changed PhoneState 'events' */
	Handler myServiceStateHandler = new Handler() {
		private boolean firstUpdate = true;

		// @Override
		public void handleMessage(Message msg) {
			/* Recognize our message based on the what-ID */
			switch (msg.what) {
			case MY_NOTIFICATION_ID:

				/* Save the CellID and the LAC */
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				String network = tm.getNetworkOperator();
				String mcc = network.substring(0, 3);
				String mnc = network.substring(3);
				Log.e("Cell", mcc + " " + mnc);
				GsmCellLocation locate = (GsmCellLocation) tm.getCellLocation();
				cid = locate.getCid();
				lac = locate.getLac();
				Log.e("Cell", cid + " " + lac);
				network = tm.getNetworkOperatorName();
				Log.e("cell", network);
				/* At least one refresh, then continuous refreshes, if set */
				if (firstUpdate || CONTINUOUS_UPDATING == true) {
					firstUpdate = false;
					updateTextFields();
				}
				return;
			}
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView(R.layout.main);

		/*
		 * Set up the PhoneStateIntentReceiver and register it to notify us on
		 * changes to the Phone's ServiceState.
		 */
		this.myPhoneStateReceiver = new PhoneStateIntentReceiver(this,
				this.myServiceStateHandler);
		this.myPhoneStateReceiver.notifyServiceState(MY_NOTIFICATION_ID);
		this.myPhoneStateReceiver.registerIntent();

		// Save references to the EditTexts
		this.myEditLac = (EditText) findViewById(R.id.edit_lac);
		this.myEditCid = (EditText) findViewById(R.id.edit_cellid);

		// Set the OnClickListeners
		this.myCmdUpdate = (Button) findViewById(R.id.cmd_update);
		this.myCmdUpdate.setOnClickListener(this.cmdUpdateListener);

		this.myCmdLocateMe = (Button) findViewById(R.id.cmd_locateme);
		this.myCmdLocateMe.setOnClickListener(this.cmdLocateListener);
	}

	/**
	 * Update the UI from the values stored in the notification handler
	 */
	private void updateTextFields() {
		this.myEditCid.setText("" + this.myCellID);
		this.myEditLac.setText("" + this.myLAC);
	}

	public void tryToLoate(int aCellID, int aLAC) throws Exception {
		// Create a connection to some 'hidden' Google-API
		String baseURL = "http://www.google.com/glm/mmap";
		// Setup the connection
		HttpURL httpURL = new HttpURL(baseURL);
		HostConfiguration host = new HostConfiguration();
		host.setHost(httpURL.getHost(), httpURL.getPort());
		HttpConnection connection = connectionManager.getConnection(host);

		try {
			// Open it
			connection.open();

			// Post (send) data to the connection
			PostMethod postMethod = new PostMethod(baseURL);
			/*
			 * MyCellIDRequestEntity will provides the request-content to send
			 */
			postMethod
					.setRequestEntity(new MyCellIDRequestEntity(aCellID, aLAC));
			postMethod.execute(new HttpState(), connection);

			InputStream response = postMethod.getResponseBodyAsStream();
			DataInputStream dis = new DataInputStream(response);

			// Read some prior data
			dis.readShort();
			dis.readByte();
			// Read the error-code
			int errorCode = dis.readInt();
			if (errorCode == 0) {
				double lat = (double) dis.readInt() / 1000000D;
				double lng = (double) dis.readInt() / 1000000D;
				// Read the rest of the data
				dis.readInt();
				dis.readInt();
				dis.readUTF();

				/*
				 * Create a geo-ContentURI containing the lat/long-values and
				 * start the Map-Application
				 */
			} else {
				/*
				 * If the return code was not valid or indicated an error, we
				 * display a Sorry-Notification
				 */
				NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			}
		} finally {
			connection.releaseConnection();
			connection.close();
		}
	}
}