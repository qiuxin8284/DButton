package com.sfr.dbuttonapplication.entity;

public class AlarmCallData {
    private String id;
    private String alarmId;
	private String vipId;
	private String contactId;
	private AlarmContactData alarmContactData;
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

	public String getVipId() {
		return vipId;
	}

	public void setVipId(String vipId) {
		this.vipId = vipId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public AlarmContactData getAlarmContactData() {
		return alarmContactData;
	}

	public void setAlarmContactData(AlarmContactData alarmContactData) {
		this.alarmContactData = alarmContactData;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "AlarmCallData{" +
				"id='" + id + '\'' +
				", alarmId='" + alarmId + '\'' +
				", vipId='" + vipId + '\'' +
				", contactId='" + contactId + '\'' +
				", alarmContactData=" + alarmContactData +
				", isOK=" + isOK +
				'}';
	}
}
