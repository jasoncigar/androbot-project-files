package com.org.video.server;

import com.cellbots.CellbotProtos.ControllerState.KeyEvent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.org.video.client.EventListenerService;
import com.org.video.client.entities.RobotInformation;

@SuppressWarnings("serial")
public class EventListenerServiceImpl extends RemoteServiceServlet implements
		EventListenerService {
	long currentav;
	long previousav;
	long current;
	long previous;

	long prevPersist = 0;

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

	@Override
	public void persistData(String BOT_ID, String streetAddress)
			throws IllegalArgumentException {

		if (StateHolder.getInstance(BOT_ID).getavTimeStamp() != 0
				&& StateHolder.getInstance(BOT_ID).getavTimeStamp() != prevPersist) {
			ObjectifyService.register(RobotInformation.class);
			Objectify ofy = ObjectifyService.begin();

			com.cellbots.CellbotProtos.PhoneState ps = StateHolder.getInstance(
					BOT_ID).getPhoneState();

			RobotInformation mRobotInformation = new RobotInformation();

			mRobotInformation.setBOT_ID(BOT_ID);
			mRobotInformation.setTimestamp(StateHolder.getInstance(BOT_ID)
					.getavTimeStamp());
			
			mRobotInformation.setAccX(ps.getAccelerometer().getX());
			mRobotInformation.setAccY(ps.getAccelerometer().getY());
			mRobotInformation.setAccZ(ps.getAccelerometer().getZ());

			mRobotInformation.setOrientX(ps.getCompass().getX());
			mRobotInformation.setOrientY(ps.getCompass().getY());
			mRobotInformation.setOrientZ(ps.getCompass().getZ());

			mRobotInformation.setPhoneBatteryLevel(ps.getPhoneBatteryLevel());
			mRobotInformation.setPhoneBatteryTemp(ps.getPhoneBatteryTemp());
			mRobotInformation.setPhoneBatteryVoltage(ps.getLightLevel());

			mRobotInformation.setLatitude(ps.getLocation().getLatitude());
			mRobotInformation.setLongitude(ps.getLocation().getLongitude());
			mRobotInformation
					.setNetworkProvider(ps.getLocation().getProvider());

			mRobotInformation.setMcc(ps.getWifi().getKbps());
			mRobotInformation.setMnc(ps.getWifi().getSsid());
			mRobotInformation.setLac(ps.getWifi().getChanel());
			mRobotInformation.setCellid(ps.getWifi().getIp());

			mRobotInformation.setStreetAddress(streetAddress);

			ofy.put(mRobotInformation);
			prevPersist = StateHolder.getInstance(BOT_ID).getavTimeStamp();
		} else {
			return;
		}
	}

}
