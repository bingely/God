package com.meetrend.haopingdian.bean;

import java.io.Serializable;

public class SearchMember implements Serializable {

	/**
	 * 搜索的会员(普通用户)
	 */
	private static final long serialVersionUID = 1L;
	
	public String customerName;
	public String status;
	public String userId;
	public String memberId;
	public String pictureId;
	public String mobile;
	public boolean canTalk;//是否可以聊天
	public String managerId;//用户未分配标识
}
