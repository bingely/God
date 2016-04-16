package com.meetrend.haopingdian.enumbean;

import android.util.Log;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.LogUtil;

public enum PayStatus {
	/**
	 * 未付款
	 */
	UNPAY("unpay", R.drawable.wfk),
	/**
	 * 已付款
	 */
	PAYED("payed", R.drawable.yfk),
	/**
	 * 付款中
	 */
	PAYING("paying", R.drawable.fkz),
//	/**
//	 * 已完成
//	 */
//	PAYED("payed", R.drawable.ywc),
	/**
	 * 已转账
	 */
	TRANSFERED("transfer", R.drawable.yzz);

	private static final String TAG = PayStatus.class.getSimpleName();
	private static final PayStatus[] array = PayStatus.values();
	private String english;
	private int drawableId;

	private PayStatus(String en, int resId) {
		english = en;
		drawableId = resId;
	}

	public static PayStatus get(String english) {
		for (PayStatus item : array) {
			if (english.toLowerCase().equals(item.english)) {
				return item;
			}
		}
		LogUtil.e(TAG, english + "新增字段 ??");
		return null;
	}

	public int getResourceId() {
		return this.drawableId;
	}
}
