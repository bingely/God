package com.meetrend.haopingdian.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class PlaceOrderEntity implements Parcelable{
	

	/**
	 *  产品 
	 * 
	 */
	public Products products;
	
	/**
	 *  购买数量
	 */
	public double count;
	
	
	/**
	 * 产品批次
	 */
	public Pici pici;
	
	


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}
	
	/**
	 * 判断是否是赠品
	 */
	public boolean isGift;
	
	/**
	 * 是否计价条码，如果条码是29开头，为true否则为false
	 * 
	 */
	public boolean isAmountBarCode;


}