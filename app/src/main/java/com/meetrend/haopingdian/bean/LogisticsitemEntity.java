package com.meetrend.haopingdian.bean;

public class LogisticsitemEntity {
	/**
	 * 物流消息
	 */
	private String message;
	
	/**
	 * 快递单号
	 */
	private String nu;
	
	/**
	 * 物流公司编码
	 */
	private String companytype;
	
	/**
	 * 更新时间
	 */
	private String updatetime;
	
	/**
	 * 状态
	 */
	private int state;
	
	/**
	 * 物流详情
	 */
	private LogisticsitemInfoEntity data;

	public LogisticsitemEntity(String message, String nu, String companytype,
			String updatetime, int state, LogisticsitemInfoEntity data) {
		super();
		this.message = message;
		this.nu = nu;
		this.companytype = companytype;
		this.updatetime = updatetime;
		this.state = state;
		this.data = data;
	}
	
}
