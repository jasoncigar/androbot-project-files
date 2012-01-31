package com.org.geocoder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;



public class ReverseGeocoding implements EntryPoint {

	public void onModuleLoad() {
		final RootPanel rootPanel = RootPanel.get();
		
		final TextArea textArea = new TextArea();
		rootPanel.add(textArea, 12, 7);
		textArea.setSize("282px", "148px");
		LatLng ltlg = LatLng.newInstance(19.130843, 72.85908 );
		Geocoder geocoder = new Geocoder();
		geocoder.getLocations(ltlg, new LocationCallback() {

			@Override
			public void onSuccess(JsArray<Placemark> locations) {
				for (int i = 0; i < locations.length(); ++i) {
		              Placemark location = locations.get(i);
		              StringBuilder value = new StringBuilder();
		              if (location.getAddress() != null) {
		                value.append(location.getAddress());
		              } else {
		                if (location.getCity() != null) {
		                  value.append(location.getCity());
		                }
		                if (location.getAdministrativeArea() != null) {
		                  value.append(location.getAdministrativeArea() + ", ");
		                }
		                if (location.getCountry() != null) {
		                  value.append(location.getCountry());
		                }
		              }
		              int ordinal = (i + 1);
		              textArea.setText(value.toString());
		              rootPanel.add(new Label("  " + ordinal + ") " + value.toString()));
		            }
		           
			}

			@Override
			public void onFailure(int statusCode) {
				
			}
		});
	}
}
