package com.meetrend.haopingdian.event;

import com.meetrend.haopingdian.bean.Member;

public class SendMemberEvent {
	
	public Member member;
	public SendMemberEvent(Member member) {
		this.member = member;
	}

}
