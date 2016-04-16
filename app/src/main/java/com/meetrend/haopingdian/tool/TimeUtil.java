package com.meetrend.haopingdian.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	
	/**
	 * 
	 * @param time
	 */
	public static String judgeTime(String time){
		
		 long fiveMinu = 1000 * 60 * 5;//5分钟
		 long oneHour = 1000 * 60 * 60;//1个小时
		 long oneMinu = 1000 * 60;//一分钟
		 
		 String hint = "";
		
		try {
			
			long  currentTime = new Date().getTime();//当前时间戳
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date netDate = simpleDateFormat.parse(time);
			long longdate = netDate.getTime();
			long timeCha = currentTime - longdate;
			
			if (timeCha < fiveMinu) {
				hint = "刚刚";
			}else if (timeCha > fiveMinu && timeCha < oneHour) {
				long t = timeCha / oneMinu;
				hint = t +"钟前";
			}else {
				hint = time.substring(0, 16);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return hint;
	}
				

}
