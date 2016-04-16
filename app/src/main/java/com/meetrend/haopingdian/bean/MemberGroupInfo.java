package com.meetrend.haopingdian.bean;

public class MemberGroupInfo {
	
	public String id; //userid
	public String avatarId;
	public String memberId;//会员id
	public String name;
	public String type;//member(会员) ,employee(员工)
	
	public MemberGroupInfo(String id, String avatarId, String memberId,String name) {
		super();
		this.id = id;
		this.avatarId = avatarId;
		this.memberId = memberId;
		this.name = name;
	}
	
	public MemberGroupInfo(String id, String avatarId, String memberId,String name,String type) {
		super();
		this.id = id;
		this.avatarId = avatarId;
		this.memberId = memberId;
		this.name = name;
		this.type = type;
	}
}

