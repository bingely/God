package com.meetrend.haopingdian.util;

import android.text.TextUtils;

public class PhoneUtil {
	
	/**
	 * 是否是电话号码
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {  
	    String telRegex = "[1][358]\\d{9}";
	    if (TextUtils.isEmpty(mobiles)) 
	    	return false;  
	    else
	    	return mobiles.matches(telRegex);  
	} 
	
}
