package com.meetrend.haopingdian.bean;

public class Sign {
	
	public String FId;
	public String FLocation;
	public String FDesc;
	public String FCheckinTime;
	
	public Sign(){
		
	}

	public Sign(String signId, String signAddress, String desContent, String signTime) {
		super();
		this.FId = signId;
		this.FLocation = signAddress;
		this.FDesc = desContent;
		this.FCheckinTime = signTime;
	}

	
	

}
