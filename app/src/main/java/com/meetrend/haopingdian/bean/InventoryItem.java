package com.meetrend.haopingdian.bean;

import java.io.Serializable;

public class InventoryItem implements Serializable {
	/**
	 * 图片地址
	 */
	public String avatarId;
	/**
	 * 库存id
	 */
	public String inventoryId;
	/**
	 * 报价
	 */
	public String offerPiecePrice;
	/**
	 * 平均报价
	 */
	public String offerUnitPrice;
	/**
	 * 库存量 件
	 */
	public String picecInventory;
	/**
	 * 展示量 件
	 */
	public String pieceDispInventory;
	/**
	 * 库存量 饼
	 */
	public String unitInventory;
	/**
	 * 展示量 饼
	 */
	public String unitDispInventory;
	/**
	 * 产品id
	 */
	public String productId;
	/**
	 * 产品名称
	 */
	public String productName;
	/**
	 * 产品批次
	 */
	public String productPici;
	/**
	 * 最小单位
	 */
	public String unitCount;
	/**
	 * 单位Id
	 */
	public String unitId;
	/**
	 * 单位名称
	 */
	public String unitName;
	
	@Override
	public String toString() {
		return inventoryId + productName;
	}
	
	public InventoryItem(String productName, String productAvatarId, String productId) {
		this.productId = productId;
		this.productName = productName;
		this.avatarId = productAvatarId;
	}
	
}
