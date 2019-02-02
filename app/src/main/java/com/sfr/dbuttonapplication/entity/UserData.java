package com.sfr.dbuttonapplication.entity;

public class UserData {
	private String id;
    private String token;
    private String username;
    private String name;
    private String phone;
	private String gender;
	private String age;
	private long birth;
	private String blood;
	private String img;
	private boolean isOK;
	private String relation;
	private String isUrgent;
	private String createTime;
	private String delFlag;
	private String vipId;

	public long getBirth() {
		return birth;
	}

	public void setBirth(long birth) {
		this.birth = birth;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(String isUrgent) {
		this.isUrgent = isUrgent;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBlood() {
		return blood;
	}

	public void setBlood(String blood) {
		this.blood = blood;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getVipId() {
		return vipId;
	}

	public void setVipId(String vipId) {
		this.vipId = vipId;
	}

	@Override
	public String toString() {
		return "UserData{" +
				"id='" + id + '\'' +
				", token='" + token + '\'' +
				", username='" + username + '\'' +
				", name='" + name + '\'' +
				", phone='" + phone + '\'' +
				", gender='" + gender + '\'' +
				", age='" + age + '\'' +
				", birth=" + birth +
				", blood='" + blood + '\'' +
				", img='" + img + '\'' +
				", isOK=" + isOK +
				", relation='" + relation + '\'' +
				", isUrgent='" + isUrgent + '\'' +
				", createTime='" + createTime + '\'' +
				", delFlag='" + delFlag + '\'' +
				", vipId='" + vipId + '\'' +
				'}';
	}
}
