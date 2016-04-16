package com.meetrend.haopingdian.bean;

import java.util.ArrayList;
import java.util.List;

public class CommentUser {
	
	public String commentUser;
	
	public String commentContent;
	
	public String createTime;
	
	public String head;
	
	public List<Pictrue> picList = new ArrayList<Pictrue>();
	
	public String replyUser;
	
	public int type;
	
	public String id;
	
	public class Pictrue{
		String picture;
	}
	
	public int clickType;//0评论，1回复
	
}
