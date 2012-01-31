package com.org.video.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class videoLayout implements EntryPoint {

	Label lblBatteryLevel;
	Label lblPhoneBatteryInfo;
	Label lblBatteryTemp;
	Label lblBatteryVoltage;
	Label lblAccelerometer;
	Label lblAccX;
	Label lblAccY;
	Label lblAccZ;
	Label lblOrientation;
	Label lblOrientationX;
	Label lblOrientationY;
	Label lblOrientationZ;
	Label lblGpsCoordinates;
	Label lblLatitude;
	Label lblLongitu;
	Label lblGsmCellInformation;
	Label lblNetworkOperator;
	Label lblMobileContryCode;
	Label lblMobileNetworkCode;
	Label lblLocationAreaCode;
	Label lblCellIdentifier;
	Label lblAddress;

	double latitude;
	double longitude;
	double prevlatitude;
	double prevlongitude;

	double phoneBatteryLevel;
	double phoneBatteryTemp;
	double phoneBatteryVoltage;

	double AccX;
	double AccY;
	double AccZ;

	double OrientX;
	double OrientY;
	double OrientZ;

	String NetworkProvider;
	int mcc;
	int mnc;
	int lac;
	int cellid;
	int cpuUsage;

	String streetAddress;
	boolean isVideoNull = true;

	@SuppressWarnings("unused")
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	public static String test = "Test";
	static String VIDEO_URL = "/video";
	public static String BOT_ID = "jason's";
	private final EventListenerServiceAsync eventService = GWT
			.create(EventListenerService.class);
	final GWTCanvas canvas = new GWTCanvas(128, 64);

	public void onModuleLoad() {
		Timer elapsedTimer;
		Timer sensorTimer;
		Timer mapTimer;
		Timer databaseTimer;
		RootPanel rootPanel = RootPanel.get();
		Window.setTitle("Androbot's Console");
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		rootPanel.add(verticalPanel, 10, 10);
		verticalPanel.setSize("320px", "255px");

		final Image image = new Image("/video");
		verticalPanel.add(image);
		image.setSize("320px", "240px");

		final Label lblLag = new Label("Lag :");
		verticalPanel.add(lblLag);
		lblLag.setWidth("151px");
		verticalPanel.setCellVerticalAlignment(lblLag,
				HasVerticalAlignment.ALIGN_BOTTOM);

		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_1);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_1,
				HasVerticalAlignment.ALIGN_BOTTOM);
		horizontalPanel_1.setSize("315px", "19px");

		Label lblCommpressionLevel = new Label(
				"Commpression Level : (0 to 100)");
		horizontalPanel_1.add(lblCommpressionLevel);
		horizontalPanel_1.setCellVerticalAlignment(lblCommpressionLevel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblCommpressionLevel.setSize("201px", "15px");

		final TextBox textBox_1 = new TextBox();
		horizontalPanel_1.add(textBox_1);
		horizontalPanel_1.setCellHorizontalAlignment(textBox_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(textBox_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		textBox_1.setWidth("45px");

		Button btnSet = new Button("set");
		btnSet.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String text = textBox_1.getText();
				if (text != null) {
					int value = Integer.parseInt(text);
					if (value > 0 && value < 100) {
						eventService.handleMouseDown("com:" + value, BOT_ID,
								new AsyncCallback<String>() {
									@Override
									public void onSuccess(String result) {
									}

									@Override
									public void onFailure(Throwable caught) {
									}
								});
					}
				}
			}
		});
		horizontalPanel_1.add(btnSet);
		horizontalPanel_1.setCellHorizontalAlignment(btnSet,
				HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel_1.setCellVerticalAlignment(btnSet,
				HasVerticalAlignment.ALIGN_MIDDLE);

		VerticalPanel verticalPanel_1 = new VerticalPanel();
		rootPanel.add(verticalPanel_1, 10, 324);
		verticalPanel_1.setSize("346px", "281px");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel_1.add(horizontalPanel);
		horizontalPanel.setSize("321px", "50px");

		Button btnNewButton = new Button("Forward");
		btnNewButton.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				eventService.handleMouseUp("mov:stop", BOT_ID,
						new AsyncCallback<String>() {

							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		btnNewButton.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				eventService.handleMouseDown("mov:forward", BOT_ID,
						new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		horizontalPanel.add(btnNewButton);
		horizontalPanel.setCellHorizontalAlignment(btnNewButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		btnNewButton.setSize("192px", "56px");

		FlexTable flexTable = new FlexTable();
		verticalPanel_1.add(flexTable);
		flexTable.setSize("330px", "71px");

		Button btnNewButton_1 = new Button("Left");
		btnNewButton_1.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				eventService.handleMouseDown("mov:left", BOT_ID,
						new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		btnNewButton_1.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				eventService.handleMouseUp("mov:stop", BOT_ID,
						new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		flexTable.setWidget(0, 0, btnNewButton_1);
		btnNewButton_1.setSize("58px", "50px");

		Button btnNewButton_2 = new Button("Backward");
		btnNewButton_2.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				eventService.handleMouseDown("mov:backward", BOT_ID,
						new AsyncCallback<String>() {

							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		btnNewButton_2.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				eventService.handleMouseUp("mov:stop", BOT_ID,
						new AsyncCallback<String>() {

							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		flexTable.setWidget(0, 1, btnNewButton_2);
		btnNewButton_2.setSize("91px", "50px");

		Button btnNewButton_3 = new Button("Right");
		btnNewButton_3.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				eventService.handleMouseDown("mov:right", BOT_ID,
						new AsyncCallback<String>() {

							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		btnNewButton_3.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				eventService.handleMouseUp("mov:stop", BOT_ID,
						new AsyncCallback<String>() {

							@Override
							public void onSuccess(String result) {
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		});
		flexTable.setWidget(0, 2, btnNewButton_3);
		btnNewButton_3.setSize("72px", "51px");
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 2,
				HasHorizontalAlignment.ALIGN_CENTER);

		final Label lblNewLabel = new Label("Status");

		FlexTable flexTable_1 = new FlexTable();
		verticalPanel_1.add(flexTable_1);

		Label lblCommand = new Label("Command :");
		flexTable_1.setWidget(0, 0, lblCommand);
		lblCommand.setWidth("69px");

		final TextBox textBox = new TextBox();
		textBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					String text = textBox.getText();
					textBox.setText("");
					String action = text.substring(text.indexOf(':') + 1, text
							.length());
					text = text.substring(0, text.indexOf(':'));
					if (text.equals("email")) {
						String strTo = action;
						String tempstreetAddress = null;
						if (streetAddress != null) {
							tempstreetAddress = streetAddress.replace(" ",
									"%20");
						}
						String strSubject = BOT_ID + "DataandLocation";
						String strBody = BOT_ID + "%0A" + "latitude:%20"
								+ latitude + "%0Alongitude:%20" + longitude
								+ "%0APhone%20Battery%20Level:%20"
								+ phoneBatteryLevel
								+ "%0APhone%20Battery%20Temp:%20"
								+ phoneBatteryTemp
								+ "%0APhone%20Battery%20Voltage:%20"
								+ phoneBatteryVoltage
								+ "%0AAccelerometer%20Information:%0AX:%20"
								+ AccX + "%0AY:%20" + AccY + "%0AZ:%20" + AccZ
								+ "%0AOrientation%20Information:%20"
								+ "%0AX:%20" + OrientX + "%0AY:%20" + OrientY
								+ "%0AZ:%20" + OrientZ
								+ "%0ANetworkProvider:%20" + NetworkProvider
								+ "%0AMobileCountryCode:%20" + mcc
								+ "%0AMobileNetworkCode:%20" + mnc
								+ "%0ALocationAreaIdentifier:%20" + lac
								+ "%0ACellIdentifier:%20" + cellid
								+ "%0AStreet%20Address:%20" + tempstreetAddress;
						strBody = URL.encode(strBody);
						RequestBuilder builder = new RequestBuilder(
								RequestBuilder.GET, "/jemail?email_to=" + strTo
										+ "&email_subject=" + strSubject
										+ "&body=" + strBody);
						builder.setHeader("Content-Type", "text/plain");

						try {
							builder.sendRequest(null, new RequestCallback() {
								@Override
								public void onResponseReceived(Request request,
										Response response) {
									lblNewLabel.setText(response.getText());
								}

								@Override
								public void onError(Request request,
										Throwable exception) {
									lblNewLabel.setText("Failed to send email");
								}
							});
						} catch (RequestException e) {
							e.printStackTrace();
						}
					} else if (text.equals("sms")) {
						String temp = ("sms:" + action + "-"
								+ "GPS Coordinates\nlatitude " + latitude
								+ "\nlongitude " + longitude
								+ "\nStreet Address\n" + streetAddress);
						eventService.handleMouseDown(temp, BOT_ID,
								new AsyncCallback<String>() {

									@Override
									public void onSuccess(String result) {
										lblNewLabel.setText("Sms Sent");
									}

									@Override
									public void onFailure(Throwable caught) {
										lblNewLabel.setText("Failed to send");
									}
								});
					} else if (text.equals("botid")) {
						BOT_ID = action;
					} else if (text.equals("tts")) {
						String temp = ("tts:" + action);
						eventService.handleMouseDown(temp, BOT_ID,
								new AsyncCallback<String>() {

									@Override
									public void onSuccess(String result) {

									}

									@Override
									public void onFailure(Throwable caught) {
										lblNewLabel.setText("Failed to send");
									}
								});
					} else if (text.equals("exp")) {
						String temp = ("exp:" + action);
						eventService.handleMouseDown(temp, BOT_ID,
								new AsyncCallback<String>() {

									@Override
									public void onSuccess(String result) {

									}

									@Override
									public void onFailure(Throwable caught) {
										lblNewLabel.setText("Failed to send");
									}
								});
					}
				}
			}
		});

		flexTable_1.setWidget(0, 1, textBox);
		textBox.setWidth("188px");

		Button btnSend = new Button("Send");
		btnSend.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

			}
		});
		flexTable_1.setWidget(0, 2, btnSend);
		flexTable_1.getCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_CENTER);
		flexTable_1.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		verticalPanel_1.add(lblNewLabel);
		verticalPanel_1.setCellVerticalAlignment(lblNewLabel,
				HasVerticalAlignment.ALIGN_BOTTOM);

		VerticalPanel verticalPanel_2 = new VerticalPanel();
		rootPanel.add(verticalPanel_2, 396, 10);
		verticalPanel_2.setSize("287px", "359px");

		Label lblPhoneBatteryInfo = new Label("Phone Battery Info:");
		verticalPanel_2.add(lblPhoneBatteryInfo);

		lblBatteryLevel = new Label("Battery Level:");
		verticalPanel_2.add(lblBatteryLevel);

		lblBatteryTemp = new Label("Battery Temp:");
		verticalPanel_2.add(lblBatteryTemp);

		lblBatteryVoltage = new Label("Battery Voltage:");
		verticalPanel_2.add(lblBatteryVoltage);

		lblAccelerometer = new Label("Accelerometer");
		verticalPanel_2.add(lblAccelerometer);

		lblAccX = new Label("Acc X:");
		verticalPanel_2.add(lblAccX);

		lblAccY = new Label("Acc Y:");
		verticalPanel_2.add(lblAccY);

		lblAccZ = new Label("Acc Z:");
		verticalPanel_2.add(lblAccZ);

		lblOrientation = new Label("Orientation ");
		verticalPanel_2.add(lblOrientation);

		lblOrientationX = new Label("Orientation X:");
		verticalPanel_2.add(lblOrientationX);

		lblOrientationY = new Label("Orientation Y:");
		verticalPanel_2.add(lblOrientationY);

		lblOrientationZ = new Label("Orientation Z:");
		verticalPanel_2.add(lblOrientationZ);

		lblGpsCoordinates = new Label("GPS Co-ordinates");
		verticalPanel_2.add(lblGpsCoordinates);

		lblLatitude = new Label("Latitude:");
		verticalPanel_2.add(lblLatitude);

		lblLongitu = new Label("Longitude");
		verticalPanel_2.add(lblLongitu);

		lblGsmCellInformation = new Label("GSM Cell Information ");
		verticalPanel_2.add(lblGsmCellInformation);

		lblNetworkOperator = new Label("Network Operator:");
		verticalPanel_2.add(lblNetworkOperator);

		lblMobileContryCode = new Label("Mobile Contry Code:");
		verticalPanel_2.add(lblMobileContryCode);

		lblMobileNetworkCode = new Label("Mobile Network Code:");
		verticalPanel_2.add(lblMobileNetworkCode);

		lblLocationAreaCode = new Label("Location Area Code:");
		verticalPanel_2.add(lblLocationAreaCode);

		lblCellIdentifier = new Label("Cell Identifier:");
		verticalPanel_2.add(lblCellIdentifier);

		Label label = new Label("");
		verticalPanel_2.add(label);

		VerticalPanel verticalPanel_3 = new VerticalPanel();
		rootPanel.add(verticalPanel_3, 396, 385);
		verticalPanel_3.setSize("494px", "422px");
		image.addErrorHandler(new ErrorHandler() {
			public void onError(ErrorEvent event) {
				System.out.println("video error");
			}
		});
		elapsedTimer = new Timer() {
			public void run() {

				image.setUrl("video?BOTID=" + BOT_ID + "&st="
						+ System.currentTimeMillis());
				eventService.getLag(System.currentTimeMillis(), BOT_ID,
						new AsyncCallback<Long>() {

							@Override
							public void onSuccess(Long result) {
								lblLag.setText("Video Lag :" + result + " ms");
							}

							@Override
							public void onFailure(Throwable caught) {
								lblLag.setText("Network Error");
							}
						});

			}
		};
		sensorTimer = new Timer() {
			public void run() {
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
						"/robotstate?BOTID=" + BOT_ID);
				try {
					builder.setHeader("Content-Type", "application/json");

					builder.sendRequest(null, new RequestCallback() {
						public void onError(Request request, Throwable exception) {
							System.out.println("Couldn't retrieve JSON");
						}

						public void onResponseReceived(Request request,
								Response response) {

							if (response.getStatusCode() == 200)
								showPhoneState(PhoneState.parse(response
										.getText()));

							else
								System.out.println("Couldn't retrieve JSON");
						}

					});
				} catch (RequestException e) {
					System.out.println("Couldn't retrieve JSON");
				}

			}
		};
		databaseTimer = new Timer() {

			@Override
			public void run() {

				eventService.persistData(BOT_ID, streetAddress,
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								lblNewLabel
										.setText("PersistentService RPC call succeded");
							}

							@Override
							public void onFailure(Throwable caught) {
								lblNewLabel
										.setText("PersistentService RPC call Failed");
							}
						});
			}
		};
		databaseTimer.scheduleRepeating(1000);
		sensorTimer.scheduleRepeating(100);
		elapsedTimer.scheduleRepeating(50);

		final MapWidget mapWiget = new MapWidget(LatLng.newInstance(19.171634,
				72.864168), 15);
		mapWiget.setScrollWheelZoomEnabled(true);
		mapWiget.setSize("500px", "420px");

		mapWiget.addControl(new SmallMapControl());
		mapWiget.addControl(new MapTypeControl());

		mapWiget.addMapClickHandler(new MapClickHandler() {
			public void onClick(MapClickEvent e) {
				MapWidget sender = e.getSender();
				Overlay overlay = e.getOverlay();
				LatLng point = e.getLatLng();

				if (overlay != null && overlay instanceof Marker) {
					sender.removeOverlay(overlay);
				} else {
					sender.addOverlay(new Marker(point));
				}
			}
		});
		mapWiget.setCenter(LatLng.newInstance(0, 0));
		verticalPanel_3.add(mapWiget);

		final VerticalPanel verticalPanel_4 = new VerticalPanel();
		rootPanel.add(verticalPanel_4, 689, 10);
		verticalPanel_4.setSize("245px", "91px");

		final Label lblStreetAddress = new Label("Street Address");
		verticalPanel_4.add(lblStreetAddress);

		lblAddress = new Label("Address");
		verticalPanel_4.add(lblAddress);

		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		rootPanel.add(horizontalPanel_2, 689, 113);
		horizontalPanel_2.setSize("245px", "256px");
		horizontalPanel_2.add(canvas);

		mapTimer = new Timer() {

			@Override
			public void run() {

				LatLng ltlg = LatLng.newInstance(latitude, longitude);
				Geocoder geocoder = new Geocoder();
				geocoder.getLocations(ltlg, new LocationCallback() {

					@Override
					public void onSuccess(JsArray<Placemark> locations) {

						Placemark location = locations.get(0);
						StringBuilder value = new StringBuilder();
						if (location.getAddress() != null) {
							value.append(location.getAddress());
						} else {
							if (location.getCity() != null) {
								value.append(location.getCity());
							}
							if (location.getAdministrativeArea() != null) {
								value.append(location.getAdministrativeArea()
										+ ", ");
							}
							if (location.getCountry() != null) {
								value.append(location.getCountry());
							}
						}
						lblAddress.setText(value.toString());
						streetAddress = value.toString();
					}

					@Override
					public void onFailure(int statusCode) {
						lblAddress.setText("Error: status Code" + statusCode);
					}
				});
				if (latitude != prevlatitude && longitude != prevlongitude) {
					LatLng latlng = LatLng.newInstance(latitude, longitude);
					mapWiget.setCenter(latlng);
					mapWiget.addOverlay(new Marker(latlng));
					prevlatitude = latitude;
					prevlongitude = longitude;

				}
			}
		};
		mapTimer.scheduleRepeating(5000);
		drawCompass(0);
	}

	void showPhoneState(PhoneState state) {

		lblBatteryLevel.setText("Battery Level: "
				+ state.getPhoneBatteryLevel() + "%");
		phoneBatteryLevel = state.getPhoneBatteryLevel();

		lblBatteryTemp.setText("Battery Temprature: "
				+ state.getPhoneBatteryTemp() + " deg C");
		phoneBatteryTemp = state.getPhoneBatteryTemp();

		lblBatteryVoltage.setText("Battery Voltage: " + state.getLightLevel()
				+ " V");
		phoneBatteryVoltage = state.getLightLevel();

		lblAccX.setText("Acc X: " + state.getAccelerometer().getX());
		lblAccY.setText("Acc Y: " + state.getAccelerometer().getY());
		lblAccZ.setText("Acc Z: " + state.getAccelerometer().getZ());

		AccX = state.getAccelerometer().getX();
		AccY = state.getAccelerometer().getY();
		AccZ = state.getAccelerometer().getZ();

		lblOrientationX.setText("Orientation X: " + state.getCompass().getX());
		lblOrientationY.setText("Orientation Y: " + state.getCompass().getY());
		lblOrientationZ.setText("Orientation Z: " + state.getCompass().getZ());

		OrientX = state.getCompass().getX();
		OrientY = state.getCompass().getY();
		OrientZ = state.getCompass().getZ();

		lblLatitude.setText("Latitude: " + state.getLocation().getLatitude());
		lblLongitu.setText("Longitude: " + state.getLocation().getLongitude());
		lblNetworkOperator.setText("Network Provider: "
				+ state.getLocation().getProvider());

		latitude = state.getLocation().getLatitude();
		longitude = state.getLocation().getLongitude();
		NetworkProvider = state.getLocation().getProvider();

		lblMobileContryCode.setText("Mobile Contry Code: "
				+ state.getWifi().getKbps());
		lblMobileNetworkCode.setText("Mobile Network Code: "
				+ state.getWifi().getSsid());
		lblLocationAreaCode.setText("Location Area Code: "
				+ state.getWifi().getChanel());
		lblCellIdentifier
				.setText("Cell Identifier: " + state.getWifi().getIp());

		mcc = state.getWifi().getKbps();
		mnc = state.getWifi().getSsid();
		lac = state.getWifi().getChanel();
		cellid = state.getWifi().getIp();

		if (state.hasBotID()) {
			BOT_ID = state.getBotID();
			Window.setTitle(BOT_ID + " Androbot");
		}

		drawCompass(OrientX);
	}

	void drawCompass(double angle) {
		double rad = (Math.PI * 2 * ((angle) / 360.0));

		canvas.saveContext();
		canvas.translate(64, 32);
		canvas.rotate(rad);
		canvas.setFillStyle(Color.WHITE);
		canvas.fillRect(-32, -32, 64, 64);
		canvas.scale(.75, .75);

		canvas.setLineWidth(3);
		canvas.setStrokeStyle(Color.BLACK);
		canvas.beginPath();
		canvas.moveTo(0, 28);
		canvas.lineTo(12, -28);
		canvas.lineTo(0, -18);
		canvas.lineTo(-12, -28);
		canvas.closePath();
		canvas.stroke();
		canvas.restoreContext();
	}
}
