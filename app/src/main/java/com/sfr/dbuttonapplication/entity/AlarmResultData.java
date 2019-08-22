package com.sfr.dbuttonapplication.entity;

import java.io.Serializable;

public class AlarmResultData implements Serializable {
	private String id;
	private String alarmId ;
	private AlarmData alarmData;
	private UserData userData;
	private boolean isOK;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public AlarmData getAlarmData() {
		return alarmData;
	}

	public void setAlarmData(AlarmData alarmData) {
		this.alarmData = alarmData;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "AlarmResultData{" +
				"id='" + id + '\'' +
				", alarmId='" + alarmId + '\'' +
				", alarmData=" + alarmData +
				", userData=" + userData +
				", isOK=" + isOK +
				'}';
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
}
