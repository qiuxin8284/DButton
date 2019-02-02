package com.sfr.dbuttonapplication.entity;

import java.util.Arrays;

public class DButtonData {
	private String device_name;
	private String device_class;
	private String device_mac;
	private int device_rssi;
	private byte[] broadcast_content;


	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getDevice_class() {
		return device_class;
	}

	public void setDevice_class(String device_class) {
		this.device_class = device_class;
	}

	public String getDevice_mac() {
		return device_mac;
	}

	public void setDevice_mac(String device_mac) {
		this.device_mac = device_mac;
	}

	public int getDevice_rssi() {
		return device_rssi;
	}

	public void setDevice_rssi(int device_rssi) {
		this.device_rssi = device_rssi;
	}

	public byte[] getBroadcast_content() {
		return broadcast_content;
	}

	public void setBroadcast_content(byte[] broadcast_content) {
		this.broadcast_content = broadcast_content;
	}

	@Override
	public String toString() {
		return "DButtonData{" +
				"device_name='" + device_name + '\'' +
				", device_class='" + device_class + '\'' +
				", device_mac='" + device_mac + '\'' +
				", device_rssi=" + device_rssi +
				", broadcast_content=" + Arrays.toString(broadcast_content) +
				'}';
	}
}
