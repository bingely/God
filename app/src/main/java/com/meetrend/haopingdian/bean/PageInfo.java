package com.meetrend.haopingdian.bean;

public class PageInfo {
	public int index;
	public int total;

	public PageInfo() {
		this.index = 0;
		this.total = 1;
	}
	
	public PageInfo(int index, int total) {
		this.index = index;
		this.total = total;
	}

	@Override
	public String toString() {
		return "PageInfo [index=" + index + ", total=" + total + "]";
	}

}
