package com.sfr.dbuttonapplication.entity;

public class AlarmData {
	private String id;
	private String type;
	private String beginTime;
	private String endTime;
	private String point;
	private String address;
	private String record;
	private String duration;
	private AlarmCallData alarmCallData;
	private String vipName;
	private String vipImg;
	private String vipPhone;
	private String session;
	private boolean isOK;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public AlarmCallData getAlarmCallData() {
		return alarmCallData;
	}

	public void setAlarmCallData(AlarmCallData alarmCallData) {
		this.alarmCallData = alarmCallData;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getVipName() {
		return vipName;
	}

	public void setVipName(String vipName) {
		this.vipName = vipName;
	}

	public String getVipImg() {
		return vipImg;
	}

	public void setVipImg(String vipImg) {
		this.vipImg = vipImg;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getVipPhone() {
		return vipPhone;
	}

	public void setVipPhone(String vipPhone) {
		this.vipPhone = vipPhone;
	}

	@Override
	public String toString() {
		return "AlarmData{" +
				"id='" + id + '\'' +
				", type='" + type + '\'' +
				", beginTime='" + beginTime + '\'' +
				", endTime='" + endTime + '\'' +
				", point='" + point + '\'' +
				", address='" + address + '\'' +
				", record='" + record + '\'' +
				", duration='" + duration + '\'' +
				", alarmCallData=" + alarmCallData +
				", vipName='" + vipName + '\'' +
				", vipImg='" + vipImg + '\'' +
				", vipPhone='" + vipPhone + '\'' +
				", session='" + session + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
