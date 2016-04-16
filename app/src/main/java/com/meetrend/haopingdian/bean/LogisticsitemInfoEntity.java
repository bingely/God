package com.meetrend.haopingdian.bean;

public class LogisticsitemInfoEntity {

	/**
	 * 接收时间
	 */
	private String time;
	/**
	 * 详情
	 */
	private String context;
	/**
	 * 发货时间
	 */
	private String ftime;
	
	
	public LogisticsitemInfoEntity(String time, String context, String ftime) {
		super();
		this.time = time;
		this.context = context;
		this.ftime = ftime;
	}
	
}
