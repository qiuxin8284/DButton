package com.sfr.dbuttonapplication.entity;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class SortModel {

	private String name;
	private String sortLetters;
	private String nameId;
	private int state; 
	private Drawable head;
	private Uri bitHead;
	private int rank;
	private String smallUrl;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getNameId() {
		return nameId;
	}
	public void setNameId(String nameId) {
		this.nameId = nameId;
	}
	
	public Drawable getHead() {
		return head;
	}
	public void setHead(Drawable head) {
		this.head = head;
	}
	
	@Override
	public String toString() {
		return "SortModel [name=" + name + ", sortLetters=" + sortLetters
				+ ", nameId=" + nameId + ", state=" + state + ", head=" + head
				+ ", rank=" + rank + "]";
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getSmallUrl() {
		return smallUrl;
	}
	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}
	public Uri getBitHead() {
		return bitHead;
	}
	public void setBitHead(Uri bitHead) {
		this.bitHead = bitHead;
	}
	
}
