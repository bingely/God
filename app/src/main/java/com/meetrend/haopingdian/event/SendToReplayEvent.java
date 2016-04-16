package com.meetrend.haopingdian.event;


/**
 * 通知回复
 * 
 * */
public class SendToReplayEvent {
	
	private String repalycontent;
	
	public SendToReplayEvent(){
		
	}

	public String getRepalycontent() {
		return repalycontent;
	}

	public void setRepalycontent(String repalycontent) {
		this.repalycontent = repalycontent;
	}
}
