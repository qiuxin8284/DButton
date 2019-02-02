package com.sfr.dbuttonapplication.entity;

import java.util.ArrayList;

public class AlarmListData {
	private String count;
	private String isNext ;
	private ArrayList<AlarmResultData> alarmResultDataArrayList;
	private boolean isOK;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getIsNext() {
		return isNext;
	}

	public void setIsNext(String isNext) {
		this.isNext = isNext;
	}

	public ArrayList<AlarmResultData> getAlarmDataArrayList() {
		return alarmResultDataArrayList;
	}

	public void setAlarmDataArrayList(ArrayList<AlarmResultData> alarmResultDataArrayList) {
		this.alarmResultDataArrayList = alarmResultDataArrayList;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "AlarmListData{" +
				"count='" + count + '\'' +
				", isNext='" + isNext + '\'' +
				", alarmDataArrayList=" + alarmResultDataArrayList +
				", isOK=" + isOK +
				'}';
	}
}
