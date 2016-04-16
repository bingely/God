package com.meetrend.haopingdian.bean;

public class SearchConnactMember {
	
	//普通成员属性
	public String customerName;
	public String status;
	public String userId;
	public String memberId;
	public String pictureId;
	public String mobile;
	public SearchConnactMember(String customerName, String status,
			String userId, String memberId, String pictureId, String mobile,boolean tag,String tagName) {
		super();
		this.customerName = customerName;
		this.status = status;
		this.userId = userId;
		this.memberId = memberId;
		this.pictureId = pictureId;
		this.mobile = mobile;
		this.tag = tag;
		this.tagName = tagName;
	}
	
	public boolean tag;//标签
	public String tagName;//标签名
	
	
	
}
