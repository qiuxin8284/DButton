package com.sfr.dbuttonapplication.entity;

public class RegisterData {
    private String token;
	private boolean isOK;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "UserData{" +
				"token='" + token + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
