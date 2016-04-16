package com.meetrend.haopingdian.bean;

public class NewMessage {
	
	public String name;
	public String userId;
	public String memberId;//个人id
	public String msgType;//消息类型
	public String format;
	public String image;
	public String groupId;//群成员id
	public String presonNumber;
	public String content;
	public String createTime;
	public String unReadNumber;
	
	//是否是群组消息
	public boolean isgroup;
	
	//未分配id，如果该字段对应的值为空字符串则为未分配
	public String managerId;
	
	//是否可以聊天
	public boolean canTalk;
	
}
