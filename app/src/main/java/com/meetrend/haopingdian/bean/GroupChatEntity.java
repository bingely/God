package com.meetrend.haopingdian.bean;

import com.meetrend.haopingdian.enumbean.MsgStatus;


public class GroupChatEntity {
	/**
	 * 消息id
	 */
	public String id;
	/**
	 * 消息类型
	 */
	public String msgType;
	/**
	 * 发送时间
	 */
	public String sendTime;
	/**
	 * 发送内容
	 */
	public String sendContent;

	/**
	 * 发送图片
	 */
	public String sendImage;
	/**
	 * 发送人群名称
	 */
	public String sendUserName;
	
	/**
	 * 
	 * 用户ID
	 * */
	public String fromUserId;
	/**
	 * 
	 * fromMemberId  
	 * */
	public String fromMemberId;
	/**
	 * 
	 * fromMemberId  
	 * */
	public String avatarId;
	/**
	 * 消息状态
	 */
	public MsgStatus status;
	
	public String threadName;
	
	public GroupChatEntity(String id,String msgType,String sendTime,String sendContent,String sendImage,String sendUserName,MsgStatus status,String threadName,String avatarId){
		this.id = id;
		this.msgType = msgType;
		this.sendTime = sendTime;
		this.sendContent = sendContent;
		this.sendImage = sendImage;
		this.sendUserName = sendUserName;
		this.status = status;
		this.threadName = threadName;
		this.avatarId = avatarId;
	}

}
