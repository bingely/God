package com.meetrend.haopingdian.bean;

public class InventorySearchEntity {
	public String productDescription;
	public String productName;
	public String productAvatarId;
	public String productId;

	public InventoryItem convert() {
		return new InventoryItem(productName, productAvatarId, productId);
	}
}
