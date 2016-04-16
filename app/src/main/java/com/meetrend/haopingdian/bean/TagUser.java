package com.meetrend.haopingdian.bean;

import java.util.ArrayList;
import java.util.List;

public class TagUser {

	public String tagName;
	public String tagRelationId;
	public String number;
	
	public List<User> userArray = new ArrayList<TagUser.User>();

	public class User {
		public String id;
		public String avatarId;
		public String name;
		public String memberId;
	}
}
