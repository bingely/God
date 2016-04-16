package com.meetrend.haopingdian.env;

import java.util.concurrent.ConcurrentHashMap;

import net.tsz.afinal.http.AjaxParams;

public class CommonParams extends AjaxParams{
	
	public CommonParams(){
		super();
	}
	
	
	
	public ConcurrentHashMap<String, String> getUrlParams(){
		
		return urlParams;
	}
	
	@Override
	public void put(String key, String value) {
		super.put(key, value);
	}
	

	
}
