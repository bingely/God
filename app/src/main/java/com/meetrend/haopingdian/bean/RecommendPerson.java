package com.meetrend.haopingdian.bean;

import java.util.ArrayList;
import java.util.List;

public class RecommendPerson {
	
	public String userName;
	public String userMobile;
	public String head;//头像
	public String inviteNum;//邀请人数
	public String isSign;//是否签到
	public List<Inviter> invite = new ArrayList<Inviter>();
}
