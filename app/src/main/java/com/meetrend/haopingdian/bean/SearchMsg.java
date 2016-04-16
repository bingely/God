package com.meetrend.haopingdian.bean;

import java.io.Serializable;

public class SearchMsg implements Serializable{
	
	/**
	 * 全局搜索 消息bean
	 */
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String content;
	public String avatarId;
	public String toUserId;
	public String userId;
	public String name;
	public String memberId;
	
}
