package com.meetrend.haopingdian.event;

public class ReplayEvent {
	
	
	//要回复的人名
	private String commentUser;

	public ReplayEvent(){
		
	}

	public String getCommentUser() {
		return commentUser;
	}

	public void setCommentUser(String commentUser) {
		this.commentUser = commentUser;
	}
	
	
}
