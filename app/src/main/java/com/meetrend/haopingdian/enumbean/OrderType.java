package com.meetrend.haopingdian.enumbean;

public enum OrderType {
	STORE("1"),
	ONLINE("2");
	
	private String typeId;
	private OrderType(String typeId) {
		this.typeId = typeId;
	}
	public String getType() {
		return typeId;
	}
}
