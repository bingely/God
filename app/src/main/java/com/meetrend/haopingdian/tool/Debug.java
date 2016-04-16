package com.meetrend.haopingdian.tool;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import android.util.Log;

public class Debug {
	private static final String TAG = "Debug";
	
	private Debug() {
		
	}
	private static Properties mp;
	private static Debug only;
	public static Debug newInstance() {
		if (only == null) {
			only = new Debug();
			mp = new Properties();
			FileInputStream input = null; 
			try {
				input = new FileInputStream("/mnt/sdcard/debug.config");
				mp.load(input);
			} catch (IOException e) {
				LogUtil.e(TAG, e.getMessage());
			} 
		}
		return only;
	}
	
	public static String getValue(String key) {
		return mp.getProperty(key);
	}
}
