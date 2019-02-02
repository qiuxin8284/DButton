package com.sfr.dbuttonapplication.entity;

public class AlarmIDData {
	private String id;
	private boolean isOK;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "AlarmData{" +
				"id='" + id + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
