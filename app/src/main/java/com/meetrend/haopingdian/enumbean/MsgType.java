package com.meetrend.haopingdian.enumbean;

import com.meetrend.haopingdian.tool.LogUtil;

public enum MsgType {
	TEXT("news"),
	VOICE("voice"),
	IMAGE("image");
	
	private static final String TAG = MsgType.class.getSimpleName();
	
	private String type;
	private MsgType(String type) {
		this.type = type;
	}
	public String type() {
		return type;
	}

	
	public static MsgType create(String type) {
		if (type == null) {
			LogUtil.w(TAG, "type == null");
		} 

		if (type.toLowerCase().equals(IMAGE.type)) {
			return IMAGE;
		} else if (type.toLowerCase().equals(VOICE.type)) {
			return VOICE;
		} else {
			return TEXT;
		} 
	}
}
