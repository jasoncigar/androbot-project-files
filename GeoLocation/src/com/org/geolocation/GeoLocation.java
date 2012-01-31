package com.org.geolocation;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class GeoLocation extends Activity {

	private LocationManager locationManager;
	private LocationListener listenerCoarse;
	private LocationListener listenerFine;
	private Location currentLocation;
	protected boolean locationAvailable;

	// Holds the most up to date location.
	
	// Set to false when location services are
	// unavailable.
	
	/** {@inheritDoc} */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);
		registerLocationListeners();
	}

	private void registerLocationListeners() {
		locationManager = (LocationManager)
			getSystemService(LOCATION_SERVICE);

		// Initialize criteria for location providers
		Criteria fine = new Criteria();
		fine.setAccuracy(Criteria.ACCURACY_FINE);
		Criteria coarse = new Criteria();
		coarse.setAccuracy(Criteria.ACCURACY_COARSE);

		// Get at least something from the device,
		// could be very inaccurate though
		currentLocation = locationManager.getLastKnownLocation(
			locationManager.getBestProvider(fine, true));

		if (listenerFine == null || listenerCoarse == null)
			createLocationListeners();

		// Will keep updating about every 500 ms until
		// accuracy is about 1000 meters to get quick fix.
		locationManager.requestLocationUpdates(
			locationManager.getBestProvider(coarse, true),
			500, 1000, listenerCoarse);
		// Will keep updating about every 500 ms until
		// accuracy is about 50 meters to get accurate fix.
		locationManager.requestLocationUpdates(
			locationManager.getBestProvider(fine, true),
			500, 50, listenerFine);
	}

	/**
	* 	Creates LocationListeners
	*/
	private void createLocationListeners() {
		listenerCoarse = new LocationListener() {
			public void onStatusChanged(String provider,
				int status, Bundle extras) {
				switch(status) {
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
				currentLocation = location;
			Log.e("COARSE", location.getLatitude() + " "+location.getLongitude());
			/*	if (location.getAccuracy() > 1000 &&
					location.hasAccuracy())
					locationManager.removeUpdates(this);
			*/
			}
		};
		listenerFine = new LocationListener() {
			public void onStatusChanged(String provider,
				int status, Bundle extras) {
				switch(status) {
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
				Log.e("FINE", location.getLatitude() + " "+location.getLongitude());
				currentLocation = location;
			/*	if (location.getAccuracy() > 1000
					&& location.hasAccuracy())
					locationManager.removeUpdates(this);
			*/
			}
		};
	}


	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		// Make sure that when the activity has been
		// suspended to background,
		// the device starts getting locations again
		registerLocationListeners();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// Make sure that when the activity goes to
		// background, the device stops getting locations
		// to save battery life.
		locationManager.removeUpdates(listenerCoarse);
		locationManager.removeUpdates(listenerFine);
		super.onPause();
	}
}
