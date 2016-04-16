package com.meetrend.haopingdian.enumbean;

import java.util.HashMap;
import java.util.Map;

import com.meetrend.haopingdian.bean.Faceicon;
import com.meetrend.haopingdian.faceicon.FaceData;

public enum FacePageType {
	
	QQ1, QQ2, QQ3, QQ4, QQ5;
	private static final int LENGTH = FacePageType.values().length;
	
	public static Map<FacePageType, Faceicon[]> getFaceicon() {		
		Map<FacePageType, Faceicon[]> map = new HashMap<FacePageType, Faceicon[]>(LENGTH);
		//map.put(EMOJI, FaceData.EMOJI);
		map.put(QQ1, FaceData.QQ1);
		map.put(QQ2, FaceData.QQ2);
		map.put(QQ3, FaceData.QQ3);
		map.put(QQ4, FaceData.QQ4);
		map.put(QQ5, FaceData.QQ5);		   
		return map;
	}
}
/*原始
public enum FacePageType {
	
	EMOJI, QQ1, QQ2, QQ3, QQ4, QQ5;
	private static final int LENGTH = FacePageType.values().length;
	
	public static Map<FacePageType, Faceicon[]> getFaceicon() {		
		Map<FacePageType, Faceicon[]> map = new HashMap<FacePageType, Faceicon[]>(LENGTH);
		map.put(EMOJI, FaceData.EMOJI);
		map.put(QQ1, FaceData.QQ1);
		map.put(QQ2, FaceData.QQ2);
		map.put(QQ3, FaceData.QQ3);
		map.put(QQ4, FaceData.QQ4);
		map.put(QQ5, FaceData.QQ5);		   
		return map;
	}
}*/
