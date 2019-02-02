package com.sfr.dbuttonapplication.entity;

public class AlarmContactData {
    private String id;
    private String phone;
	private String name;
	private String relation;
	private String img;
	private boolean isOK;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "AlarmContactData{" +
				"id='" + id + '\'' +
				", phone='" + phone + '\'' +
				", name='" + name + '\'' +
				", relation='" + relation + '\'' +
				", img='" + img + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
