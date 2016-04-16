package com.meetrend.haopingdian.speechrecord;

class Recorder {
	
	private String filepath;
	private float time;
	
	public Recorder(float time, String filepath) {
		super();
		this.filepath = filepath;
		this.time = time;
	}

	public String getFilepath() {
		return filepath;
	}
	
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public float getTime() {
		return time;
	}
	
	public void setTime(float time) {
		this.time = time;
	}
	
}