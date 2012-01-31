package com.org.video.server;

import com.org.video.client.EventListenerService;
import com.cellbots.CellbotProtos.ControllerState.KeyEvent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EventListenerServiceImpl extends RemoteServiceServlet implements
		EventListenerService {
	long currentav;
	long previousav;
	long current;
	long previous;

	@Override
	public String handleMouseDown(String name, String botid)
			throws IllegalArgumentException {
		System.out.println("Got Button Down : " + name);
		KeyEvent.Builder key = KeyEvent.newBuilder();
		key.setKeyDown(true);
		key.setKeyCode(name);
		return String.valueOf(StateHolder.getInstance(botid).addKeyEvent(key));
	}

	@Override
	public String handleMouseUp(String name, String botid)
			throws IllegalArgumentException {
		System.out.println("Got Button Up : " + name);
		KeyEvent.Builder key = KeyEvent.newBuilder();
		key.setKeyDown(true);
		key.setKeyCode(name);
		return String.valueOf(StateHolder.getInstance(botid).addKeyEvent(key));

	}

	@Override
	public long getLag(long time, String botid) throws IllegalArgumentException {
		currentav = StateHolder.getInstance(botid).getavTimeStamp();
		if (currentav == previousav) {
			return (previous - previousav) % 1000;
		} else {
			previousav = currentav;
			previous = time;
			time = System.currentTimeMillis();
			return (time - currentav) % 1000;

		}

	}

	@Override
	public String sendEmail(String emailid, String botid)
			throws IllegalArgumentException {
		return "";
	}
}
