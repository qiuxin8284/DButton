package com.sfr.dbuttonapplication.entity;

public class Music {
	
	String name;
	long size;
	String url;
	long duration;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Music [name=" + name + ", size=" + size + ", url=" + url
				+ ", duration=" + duration + "]";
	}
	
	
	
	

}
