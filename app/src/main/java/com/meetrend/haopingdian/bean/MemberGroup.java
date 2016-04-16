package com.meetrend.haopingdian.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MemberGroup implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public boolean isSystemTag;//是否是系统分组
	
	public String tagName;//群组名
	public String tagRelationId;
	public String number;
	public boolean checkstatus;
	public String image;//图片
	
	public ArrayList<MemberGroupInfo> userArray = new ArrayList<MemberGroupInfo>();
	
	public MemberGroup(){
	}
	
	
	public  ArrayList<MemberGroupInfo> getUserArray(){
		return userArray;
	}
	
	public MemberGroup(String tagName, String tagRelationId, String number) {
		super();
		this.tagName = tagName;
		this.tagRelationId = tagRelationId;
		this.number = number;
	}


	public void setUserArray(ArrayList<MemberGroupInfo> userArray) {
		this.userArray = userArray;
	}
}

