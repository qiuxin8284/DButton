package com.sfr.dbuttonapplication.entity;

public class LayerData {
	private String id;
	private String mac;
	private String name;
	private String isDefalut;
	private boolean isOK;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsDefalut() {
		return isDefalut;
	}

	public void setIsDefalut(String isDefalut) {
		this.isDefalut = isDefalut;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "LayerData{" +
				"id='" + id + '\'' +
				", mac='" + mac + '\'' +
				", name='" + name + '\'' +
				", isDefalut='" + isDefalut + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
