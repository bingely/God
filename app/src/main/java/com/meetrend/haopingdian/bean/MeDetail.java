package com.meetrend.haopingdian.bean;

public class MeDetail {
//	public String avatarId;
//	public String userName;
//	public String loginName;
//	public String mobile;
//	public String gender;
//	public String region ;
	
	public String region;
	public String storeQrCodr;
	public String storeName;
	public String avatarId;
	public String qrCode;
	public String gender; //性别1男 2女
	public String userName;
	public String userQrCodr;
	public String mobile;
	public String loginName;
	
	@Override
	public String toString() {
		return avatarId + " : " + userName + " : " + loginName + " : " + gender + " : " + mobile + " : " + region;
	}
}
