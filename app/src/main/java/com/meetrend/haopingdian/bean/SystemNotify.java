package com.meetrend.haopingdian.bean;

import java.io.Serializable;

public class SystemNotify  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SystemNotify(){
		
	}
	//内容
	public String content;
	//时间戳
	public String sendTime;
	//标题
	public String subject;

}
