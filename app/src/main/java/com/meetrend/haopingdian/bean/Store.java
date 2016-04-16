package com.meetrend.haopingdian.bean;

public class Store { 
	public String storeName;
	public String storeId;

	/**
	 * 
	 * @param name 门店名称
	 * @param id 门店id
	 */
	public Store(String name, String id) {
		this.storeName = name;
		this.storeId = id;
	}
	
	public Store() {
	
	}
	
	@Override
	public boolean equals(Object o) {
		Store store = (Store) o;
		return this.storeName.equals(store.storeName);
	}

}
