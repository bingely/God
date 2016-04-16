package com.meetrend.haopingdian.event;

import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.PlaceOrderEntity;
import com.meetrend.haopingdian.bean.Products;


public class TeaAddEvent {

	public PlaceOrderEntity placeOrderEntity;
	public Products products ;
	public Pici pici;
	public double count;
	
	public boolean isGift;//标识是礼品

	public boolean isScanBarCode;//扫描条码
	
	public TeaAddEvent(Products products,Pici pici,double count) {
		this.products = products;
		this.pici = pici;
		this.count = count;
	}

	public TeaAddEvent(boolean isGift){
			this.isGift = isGift;
	}

	public TeaAddEvent(){

	}

}