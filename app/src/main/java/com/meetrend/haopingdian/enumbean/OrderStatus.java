package com.meetrend.haopingdian.enumbean;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.LogUtil;

public enum OrderStatus {
	
	
	/**
	 * 待确认（图文：未确认）
	 */
	UN_CONFIRMED("unconfirmed", R.drawable.dqr),
	
	/**
	 * 待付款（图文：未付款）
	 */
	WAIT_APY("UnPay",R.drawable.dfk),
	
	/**
	 * 待发货（图文：已付款）
	 */
	HAVE_PAY("Payed",R.drawable.yfk),
	
	/**
	 * 已完成 
	 */
	FINISHED("Finished", R.drawable.ywc),
	
	/**
	 * 已取消
	 */
	CANCELED("Canceled", R.drawable.yqx);
	

	private static final String TAG = OrderStatus.class.getSimpleName();
	private static final OrderStatus[] array = OrderStatus.values();

	private String english;
	public int drawableId;

	private OrderStatus(String en, int resId) {
		english = en;
		drawableId = resId;
	}

	public static OrderStatus get(String english) {
		for (OrderStatus item : array) {
			if (english.equalsIgnoreCase(item.english)) {
				return item;
			}
		}
		return null;
	}

	public int getResourceId() {
		return this.drawableId;
	}
}
