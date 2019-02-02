package com.sfr.dbuttonapplication.entity;

import java.util.ArrayList;

public class ContactData {
	private ArrayList<UserData> list;
	private boolean isOK;

	public ArrayList<UserData> getList() {
		return list;
	}

	public void setList(ArrayList<UserData> list) {
		this.list = list;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "ContactData{" +
				"list=" + list +
				", isOK=" + isOK +
				'}';
	}
}
