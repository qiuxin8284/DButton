package com.sfr.dbuttonapplication.entity;

public class RenewData {
    private String lowestVer;
    private String newVer;
    private String link;
	private boolean isOK;
	public String getLowestVer() {
		return lowestVer;
	}
	public void setLowestVer(String lowestVer) {
		this.lowestVer = lowestVer;
	}
	public String getNewVer() {
		return newVer;
	}
	public void setNewVer(String newVer) {
		this.newVer = newVer;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public boolean isOK() {
		return isOK;
	}
	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}
	@Override
	public String toString() {
		return "RenewData [lowestVer=" + lowestVer + ", newVer=" + newVer
				+ ", link=" + link + ", isOK=" + isOK + "]";
	}
    
    
}
