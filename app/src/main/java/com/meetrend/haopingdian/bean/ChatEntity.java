package com.meetrend.haopingdian.bean;

import com.meetrend.haopingdian.enumbean.MsgStatus;

public class ChatEntity {
	/**
	 * 消息id
	 */
	public String id;
	
	/**
	 * 语音时间
	 * 
	 * */
	public String voicelength;
	
	/**
	 * 消息类型
	 */
	public String msgType;
	/**
	 * 消息添加时间
	 */
	public String createTime;
	/**
	 * 聊天内容
	 */
	public String content;

	/**
	 * 头像id
	 */
	public String avatarId;
	/**
	 * 聊天对象用户id
	 */
	public String userId;
	/**
	 * 登陆用户id
	 */
	public String fromUserId;
	/**
	 * 登陆用户会员id
	 */
	public String fromMemberId;
	/**
	 * 消息状态
	 */
	public MsgStatus status;
	
	public String threadName;
	
	public  String replyStatus;//0发送中，1发送成功 2发送失败
	
	//public boolean filterMsg;
	
	public ChatEntity(String msgType, String createTime, String content, String avatarId,
			String userId, String fromMemberId, String fromUserId, MsgStatus status, String name,String recordTime) {
			
		this.msgType = msgType;
		this.createTime = createTime;
		this.content = content;

		this.avatarId = avatarId;
		this.userId = userId;
		this.fromMemberId = fromMemberId;
		this.fromUserId = fromUserId;

		this.status = status;
		this.threadName = name;
		this.voicelength = recordTime;
	}
	
	//未分配id
	public String managerId;
	
}
