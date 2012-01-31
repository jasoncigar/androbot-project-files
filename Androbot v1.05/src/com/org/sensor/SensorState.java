package com.org.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.cellbots.CellbotProtos;
import com.cellbots.CellbotProtos.PhoneState;

public class SensorState implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mOrientation;

	private LocationManager locationManager;
	private LocationListener listenerCoarse;
	private LocationListener listenerFine;
	protected boolean locationAvailable;

	private TelephonyManager TeleMan;

	private CpuUsage cu;
	private CellbotProtos.PhoneState.Builder state;
	private Location currentLocation;

	private Handler mHandler;
	private String ROBOT_ID;
	
	public SensorState(SensorManager mSensorManage,
			LocationManager locationManage, TelephonyManager telephonyManager,
			Handler handle,String BOTID) {

		mSensorManager = mSensorManage;
		locationManager = locationManage;
		TeleMan = telephonyManager;
		mHandler = handle;
		ROBOT_ID = BOTID;
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		registerListeners();

		state = CellbotProtos.PhoneState.newBuilder();

		cu = new CpuUsage();
		Thread cpuThread = new Thread() {

			@Override
			public void run() {
				state.setScreenBrightness((int) cu.getUsage());
				sendPhoneState();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		cpuThread.start();

	}

	public void registerListeners() {

		mSensorManager.registerListener((SensorEventListener) this,
				mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener((SensorEventListener) this,
				mOrientation, SensorManager.SENSOR_DELAY_FASTEST);

		Criteria fine = new Criteria();
		fine.setAccuracy(Criteria.ACCURACY_FINE);
		Criteria coarse = new Criteria();
		coarse.setAccuracy(Criteria.ACCURACY_COARSE);

		currentLocation = locationManager.getLastKnownLocation(locationManager
				.getBestProvider(fine, true));

		if (listenerFine == null || listenerCoarse == null)
			createLocationListeners();
		locationManager.requestLocationUpdates(locationManager.getBestProvider(
				coarse, true), 500, 1000, listenerCoarse);
		locationManager.requestLocationUpdates(locationManager.getBestProvider(
				fine, true), 500, 50, listenerFine);
	}

	private void createLocationListeners() {
		listenerCoarse = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				switch (status) {
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					locationAvailable = false;
					break;
				case LocationProvider.AVAILABLE:
					locationAvailable = true;
				}
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
				currentLocation.getSpeed();
			}

			public void onLocationChanged(Location location) {
				currentLocation = location;
				Log.e("COARSE", location.getLatitude() + " "
						+ location.getLongitude());
				/*
				 * if (location.getAccuracy() > 1000 && location.hasAccuracy())
				 * locationManager.removeUpdates(this);
				 */
				CellbotProtos.PhoneState.Location.Builder b = CellbotProtos.PhoneState.Location
						.newBuilder();
				CellbotProtos.PhoneState.WIFI.Builder w = CellbotProtos.PhoneState.WIFI
						.newBuilder();
				b.setLatitude(location.getLatitude());
				b.setLongitude(location.getLongitude());
				b.setProvider(TeleMan.getNetworkOperatorName());
				String network = TeleMan.getNetworkOperator();
				w.setKbps(Integer.parseInt(network.substring(0, 3)));
				w.setSsid(Integer.parseInt(network.substring(3)));
				GsmCellLocation locateCell = (GsmCellLocation) TeleMan
						.getCellLocation();
				w.setIp(locateCell.getCid());
				w.setChanel(locateCell.getLac());
				state.setWifi(w);
				state.setLocation(b);
				sendPhoneState();
			}
		};

		listenerFine = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				switch (status) {
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					locationAvailable = false;
					break;
				case LocationProvider.AVAILABLE:
					locationAvailable = true;
				}
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				Log.e("FINE", location.getLatitude() + " "
						+ location.getLongitude());
				currentLocation = location;
				/*
				 * if (location.getAccuracy() > 1000 && location.hasAccuracy())
				 * locationManager.removeUpdates(this);
				 */
				CellbotProtos.PhoneState.Location.Builder b = CellbotProtos.PhoneState.Location
						.newBuilder();
				b.setLatitude(location.getLatitude());
				b.setLongitude(location.getLongitude());
				state.setLocation(b);
				sendPhoneState();
			}
		};
	}

	public void unregisterListers() {
		mSensorManager.unregisterListener(this);
		locationManager.removeUpdates(listenerCoarse);
		locationManager.removeUpdates(listenerFine);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometer) {
			CellbotProtos.PhoneState.Accelerometer.Builder b = CellbotProtos.PhoneState.Accelerometer
					.newBuilder();

			b.setX(event.values[0]);
			b.setY(event.values[1]);
			b.setZ(event.values[2]);
			state.setAccelerometer(b);
			sendPhoneState();

		}
		if (event.sensor == mOrientation) {
			CellbotProtos.PhoneState.Compass.Builder b = CellbotProtos.PhoneState.Compass
					.newBuilder();

			b.setX(event.values[0]);
			b.setY(event.values[1]);
			b.setZ(event.values[2]);
			state.setCompass(b);
			sendPhoneState();
		}
		
	}

	public BroadcastReceiver getBatteryReciever() {
		return myBatteryReceiver;
	}

	private BroadcastReceiver myBatteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (arg1.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				state.setPhoneBatteryLevel(arg1.getIntExtra("level", 0));
				state.setLightLevel(arg1.getIntExtra("voltage", 0));
				state.setPhoneBatteryTemp(arg1.getIntExtra("temperature", 0));
			}
			sendPhoneState();
		}

	};
	private synchronized void sendPhoneState()
	  {

	    long now = System.currentTimeMillis();
	   
	    state.setTimestamp(now);
	    state.setBotID(ROBOT_ID);
	    PhoneState ps = state.build();

	    Message msg = mHandler.obtainMessage(0, ps);
	    mHandler.sendMessageAtFrontOfQueue(msg);
	    state = CellbotProtos.PhoneState.newBuilder(ps);

	  }

}
