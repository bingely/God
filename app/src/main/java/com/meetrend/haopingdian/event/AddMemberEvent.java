package com.meetrend.haopingdian.event;

import java.util.List;

import com.meetrend.haopingdian.bean.Member;

public class AddMemberEvent {
	
	private List<Member> list;
	
	public AddMemberEvent(List<Member> list){
		this.list = list;
	}

	public List<Member> getList() {
		return list;
	}

	public void setList(List<Member> list) {
		this.list = list;
	}
	
	

}
