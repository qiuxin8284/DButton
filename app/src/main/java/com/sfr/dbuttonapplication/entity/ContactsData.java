package com.sfr.dbuttonapplication.entity;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.Date;

public class ContactsData {
	private String Name;
	private String NickName;
	private String autograph;
	private String callName;
	private Drawable head;
	private String groupName;
	private String username;
	private Date addTime;
	private String smallUrl;
	private String bigUrl;
	private int addState;
	private int gender;
	private Uri bitHead;
	private String address;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	public String getAutograph() {
		return autograph;
	}
	public void setAutograph(String autograph) {
		this.autograph = autograph;
	}
	public void setHead(Drawable head) {
		this.head = head;
	}
	public Drawable getHead() {
		return head;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public int getAddState() {
		return addState;
	}
	public void setAddState(int addState) {
		this.addState = addState;
	}


	public String getSmallUrl() {
		return smallUrl;
	}
	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}
	public String getBigUrl() {
		return bigUrl;
	}
	public void setBigUrl(String bigUrl) {
		this.bigUrl = bigUrl;
	}
	public String getCallName() {
		return callName;
	}
	public void setCallName(String callName) {
		this.callName = callName;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public Uri getBitHead() {
		return bitHead;
	}
	public void setBitHead(Uri bitHead) {
		this.bitHead = bitHead;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "ContactsData [Name=" + Name + ", NickName=" + NickName
				+ ", autograph=" + autograph + ", callName=" + callName
				+ ", head=" + head + ", groupName=" + groupName + ", username="
				+ username + ", addTime=" + addTime + ", smallUrl=" + smallUrl
				+ ", bigUrl=" + bigUrl + ", addState=" + addState + ", gender="
				+ gender + ", bitHead=" + bitHead + ", address=" + address
				+ "]";
	}




}
