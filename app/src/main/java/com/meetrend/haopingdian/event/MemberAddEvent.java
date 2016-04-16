package com.meetrend.haopingdian.event;

import java.util.ArrayList;

public class MemberAddEvent {
	
	//创建群组的 的用户组，和对应的头像
	public ArrayList<String> imgList;
	public String groupUsers;

	public MemberAddEvent(ArrayList<String> imgList,String groupUsers) {
		this.groupUsers = groupUsers;
		this.imgList = imgList;
	}

}
