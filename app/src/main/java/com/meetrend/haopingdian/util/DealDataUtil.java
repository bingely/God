package com.meetrend.haopingdian.util;

import java.util.ArrayList;
import java.util.List;

public class DealDataUtil {
	
	public static List<String> getNumList(List<String> timeList){
		
		List<String> list = new ArrayList<String>();
		
		int size = timeList.size();
		for (int i = 0; i < size; i++) {
			
			if (i == 0) {
				list.add(timeList.get(0));
				continue;
			}
			
			if (size == 30) {
				list.add(timeList.get(i*5-1));
				
			}else if (size == 90) {
				list.add(timeList.get(i*15-1));
			}
			
		}
		
		return list;
	}

}
