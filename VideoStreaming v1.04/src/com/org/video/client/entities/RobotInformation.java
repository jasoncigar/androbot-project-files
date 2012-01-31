package com.org.video.client.entities;

import java.io.Serializable;
import javax.persistence.Id;

@SuppressWarnings("serial")
public class RobotInformation implements Serializable {
	
	@Id
	Long id;
	private String BOT_ID;
	private long timestamp;
	double latitude;
	double longitude;

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


	String streetAddress;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getPhoneBatteryLevel() {
		return phoneBatteryLevel;
	}

	public void setPhoneBatteryLevel(double phoneBatteryLevel) {
		this.phoneBatteryLevel = phoneBatteryLevel;
	}

	public double getPhoneBatteryTemp() {
		return phoneBatteryTemp;
	}

	public void setPhoneBatteryTemp(double phoneBatteryTemp) {
		this.phoneBatteryTemp = phoneBatteryTemp;
	}

	public double getPhoneBatteryVoltage() {
		return phoneBatteryVoltage;
	}

	public void setPhoneBatteryVoltage(double phoneBatteryVoltage) {
		this.phoneBatteryVoltage = phoneBatteryVoltage;
	}

	public double getAccX() {
		return AccX;
	}

	public void setAccX(double accX) {
		AccX = accX;
	}

	public double getAccY() {
		return AccY;
	}

	public void setAccY(double accY) {
		AccY = accY;
	}

	public double getAccZ() {
		return AccZ;
	}

	public void setAccZ(double accZ) {
		AccZ = accZ;
	}

	public double getOrientX() {
		return OrientX;
	}

	public void setOrientX(double orientX) {
		OrientX = orientX;
	}

	public double getOrientY() {
		return OrientY;
	}

	public void setOrientY(double orientY) {
		OrientY = orientY;
	}

	public double getOrientZ() {
		return OrientZ;
	}

	public void setOrientZ(double orientZ) {
		OrientZ = orientZ;
	}

	public String getNetworkProvider() {
		return NetworkProvider;
	}

	public void setNetworkProvider(String networkProvider) {
		NetworkProvider = networkProvider;
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public int getMnc() {
		return mnc;
	}

	public void setMnc(int mnc) {
		this.mnc = mnc;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public int getCellid() {
		return cellid;
	}

	public void setCellid(int cellid) {
		this.cellid = cellid;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public void setBOT_ID(String bOT_ID) {
		BOT_ID = bOT_ID;
	}

	public String getBOT_ID() {
		return BOT_ID;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
}
