package com.sfr.dbuttonapplication.entity;

import java.util.ArrayList;

public class LayerListData {
	private ArrayList<LayerData> list;
	private boolean isOK;

	public ArrayList<LayerData> getList() {
		return list;
	}

	public void setList(ArrayList<LayerData> list) {
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
		return "LayerListData{" +
				"list=" + list +
				", isOK=" + isOK +
				'}';
	}
}
