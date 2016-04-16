package com.meetrend.haopingdian.db;

public class MessageEntity {
	private String avatarId;
	
	private String fromUserId;
	private String toUserId;
	private String fromMemberId;
	private String toMemberId;
	
	private String msgType;
	private String content;
	private String createTime;
	
	private boolean localFile;
	private boolean isSendFailed;
	
	public String getFromUserId() {
		return this.fromUserId;
	}
	public void setFromUserId(String userId) {
		this.fromUserId = userId;
	}
		
	public String getToUserId() {
		return this.toUserId;
	}
	public void setToUserId(String userId) {
		this.toUserId = userId;
	}
	
	public String getAvatarId() {
		return this.avatarId;
	}
	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}
	
	public String getMsgType() {
		return this.msgType;
	}	
	public void setMsgType(String type) {
		this.msgType = type;
	}	

	public String getContent() {
		return this.content;
	}	
	public void setContent(String content) {
		this.content = content;
	}	

	public String getCreateTime() {
		return this.createTime;
	}	
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}	

	public boolean getLocalFile () {
		return this.localFile;
	}	
	public void setLocalFile(boolean localFile) {
		this.localFile = localFile;
	}	

	public boolean getIsSendFailed () {
		return this.isSendFailed;
	}	
	public void setIsSendFailed (boolean isSendFailed) {
		this.isSendFailed = isSendFailed;
	}
	public String getFromMemberId() {
		return fromMemberId;
	}
	public void setFromMemberId(String fromMemberId) {
		this.fromMemberId = fromMemberId;
	}
	public String getToMemberId() {
		return toMemberId;
	}
	public void setToMemberId(String toMemberId) {
		this.toMemberId = toMemberId;
	}	
}
