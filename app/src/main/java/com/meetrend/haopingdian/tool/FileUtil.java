package com.meetrend.haopingdian.tool;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();
	/**
	 * 每兆多少字节
	 */
	private static final long MB_UNIT = 1024 * 1024;
	/**
	 * 可用空间最低5兆
	 */
	private static final int MIN = 5;

	
	/**
	 * 创建缓存目录
	 * @param context
	 * @param dirName 目录名
	 * @return
	 */
	public static String getAppCache(Context context, String dirName) {
		String savePath = context.getCacheDir().getAbsolutePath() + File.separator + dirName + File.separator;
		Log.i("-----------语音地址----------------", savePath);
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}
		return savePath;
	}

	public static boolean isEnoughSpace() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) == false) {
			LogUtil.w(TAG, "MEDIA not mounted");
			return false;
		}

		File file = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(file.getAbsolutePath());
		long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
		long megAvailable = bytesAvailable / MB_UNIT;
		return megAvailable > MIN;
	}
	
	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return "";
		} else { 
			return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
		}
	}

	/**
	 * 创建临时文件
	 * @return
	 */
	public static String getImageCache() {
		String parent = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separatorChar + "dayitea" 
				+ File.separatorChar;
		File file = new File(parent);
		if (!file.exists()) {
			if (!file.mkdirs()){
				//mkdirs() 可创建多级目录，mkdir不能创建
				throw new NullPointerException(parent + " make dir failed");
			}
		}
		return parent;
	}
}

