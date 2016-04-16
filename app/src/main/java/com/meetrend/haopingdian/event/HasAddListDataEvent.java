package com.meetrend.haopingdian.event;

import java.util.List;

public class HasAddListDataEvent {
	
	private List<String> memberIdList;
	
	public HasAddListDataEvent(){
		
	}

	public List<String> getmemberIdList() {
		return memberIdList;
	}

	public void setmemberIdList(List<String> imgidList) {
		this.memberIdList = imgidList;
	}
}
