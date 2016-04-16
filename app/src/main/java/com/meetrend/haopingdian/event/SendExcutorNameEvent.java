package com.meetrend.haopingdian.event;

public class SendExcutorNameEvent {
	
	public String name;
	public String id;
	
	public SendExcutorNameEvent(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
