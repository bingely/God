package com.meetrend.haopingdian.bean;

import java.io.Serializable;

public class LoginEntity implements Serializable {
	public String name;
	public String token;
	public String storeId;
	public String storeName;

	public LoginEntity(String name, String token, Store store) {
		this.name = name;
		this.token = token;
		this.storeId = store.storeId;
		this.storeName = store.storeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Override
	public String toString() {
		return "LoginEntity [name=" + name + ", token=" + token + ", storeId=" + storeId + ", storeName=" + storeName + "]";
	}

}
