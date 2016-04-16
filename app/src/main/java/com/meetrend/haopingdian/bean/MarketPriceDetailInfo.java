package com.meetrend.haopingdian.bean;

public class MarketPriceDetailInfo {

	public String date;
	public String change;
	public String changeRate;
	public String price;
	public String dayChangeRate;
	public String dayChange;
	
	public boolean changeisOnZeor;//大于零
	public boolean changeRateisDownZeor;
	
	public boolean isShow = true;
}
