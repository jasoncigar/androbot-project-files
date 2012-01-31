package com.org.movementhandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import com.cellbots.CellbotProtos;
import com.cellbots.CellbotProtos.ControllerState;
import com.cellbots.CellbotProtos.PhoneState;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MovementHandler extends Thread {

	private Handler comHandler;
	public boolean listening = false;

	public static String TAG = "MovementHandler";
	private static String ROBOT_ID = "jason's";
	private static String URL = "192.168.1.203";

	private CellbotProtos.PhoneState.Builder stateb;
	private CellbotProtos.PhoneState statephone;
	HttpClient httpclient;
	InetSocketAddress clientAddress = null;
	ControllerState controllerState;
	long lastControllerTimeStamp = 0;
	public Handler handler;

	public MovementHandler(Handler cHandler, String botid, String url) {
		comHandler = cHandler;
		ROBOT_ID = botid;
		URL = url;
		stateb = CellbotProtos.PhoneState.newBuilder();
		stateb.setBotID(ROBOT_ID);
		stateb.setTimestamp(System.currentTimeMillis());
		statephone = stateb.build();
	}

	@Override
	public void run() {
		try {
			Looper.prepare();

			handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {

					try {
						if (msg.obj != null && msg.obj instanceof PhoneState) {
							statephone = (PhoneState) msg.obj;

							httpclient = new DefaultHttpClient();

							HttpPost post = new HttpPost("http://" + URL
									+ "/robotstate");
							post.setEntity(new ByteArrayEntity(statephone
									.toByteArray()));
							HttpResponse resp = httpclient.execute(post);
							HttpEntity ent = resp.getEntity();

							if (ent == null)
								return;
							InputStream resStream = ent.getContent();
							ControllerState cs = ControllerState
									.parseFrom(resStream);

							String txt = processControllerStateEvent(cs);

							if (cs != null
									&& cs.getTimestamp() != lastControllerTimeStamp) {
								if (cs.hasTxtCommand()) {
									lastControllerTimeStamp = cs.getTimestamp();
								} else if (txt != null) {
									lastControllerTimeStamp = cs.getTimestamp();
								}

							}
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (com.google.protobuf.InvalidProtocolBufferException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (NullPointerException e) {
						Log.e(TAG, "npe", e);
						e.printStackTrace();
					}

				}

				private String processControllerStateEvent(ControllerState cs) {
					for (ControllerState.KeyEvent ev : cs.getKeyEventList()) {
						if (ev.getKeyDown()) {
							Log.e("Movement", ev.getKeyCode());
							String code = ev.getKeyCode();

							if (code.substring(0, code.indexOf(':')).equals(
									"com")) {
								Message msg = comHandler.obtainMessage(1,
										Integer.parseInt(code.substring(code
												.indexOf(':') + 1, code
												.length())), 0);
								msg.sendToTarget();
							} else if (code.substring(0, code.indexOf(':'))
									.equals("mov")) {
								Message msg = comHandler.obtainMessage(2, 0, 0);
								Bundle bundle = new Bundle();
								bundle.putString("mov", code.substring(code
										.indexOf(':') + 1, code.length()));
								msg.setData(bundle);
								comHandler.sendMessage(msg);

							} else if (code.substring(0, code.indexOf(':'))
									.equals("tts")) {
								Message msg = comHandler.obtainMessage(3, 0, 0);
								Bundle bundle = new Bundle();
								bundle.putString("tts", code.substring(code
										.indexOf(':') + 1, code.length()));
								msg.setData(bundle);
								comHandler.sendMessage(msg);

							} else if (code.substring(0, code.indexOf(':'))
									.equals("sms")) {
								Message msg = comHandler.obtainMessage(4, 0, 0);
								Bundle bundle = new Bundle();
								bundle.putString("phoneNo", code.substring(code
										.indexOf(':') + 1, code.indexOf('-')));
								bundle.putString("msg", code.substring(code
										.indexOf('-') + 1, code.length()));
								
								Log.e("Msg", "Phone No"+code.substring(code
										.indexOf(':') + 1, code.indexOf('-')));
								
								Log.e("Msg", "Message"+code.substring(code
										.indexOf('-') + 1, code.length()));	
								msg.setData(bundle);
								comHandler.sendMessage(msg);
							}
						}

						if (ev.getKeyUp()) {
							Log.e("Movement", ev.getKeyCode());
						}
					}
					return "";
				}

				/*
				 * private void processKeyUpEvent(int parseInt) {
				 * 
				 * }
				 * 
				 * private String processKeyDownEvent(int parseInt) { return
				 * null; }
				 */
			};

			Looper.loop();

			Log.i(TAG, "Thread exiting gracefully");
		} catch (Throwable t) {
			Log.e(TAG, "Thread halted due to an error", t);
		}
	}
}
