package com.meetrend.haopingdian.bean;


public class InventoryDetailEntity {
	public String productId;
	//以件为单位
	/**
	 * 库存量(件)
	 */
	public String picecInventory;
	/**
	 * 展示量(件)
	 */
	public String pieceDispInventory;
	/**
	 * 平均进价(件)
	 */
	public String piecePrice;
	/**
	 * 进价(件)
	 */
	public String offerPiecePrice;
	// 其他单位
	/**
	 * 库存量 其他单位
	 */
	public String unitInventory;
	/**
	 * 展示量 其他单位
	 */
	public String unitDispInventory;
	/**
	 * 平均进价 其他单位
	 */
	public String unitPrice;
	/**
	 * 平均进价 其他单位
	 */
	public String offerUnitPrice;

	public String operationState;
	public String unitCount;
	public String productPici;
	public String unitName;
	private static final int LENGTH = 10;

	public String[] getKeyArray() {
		String[] keyArray = new String[LENGTH];
		int index = 0;
		keyArray[index++] = "";
		keyArray[index++] = "库存量(件)";
		keyArray[index++] = "展示量(件)";
		keyArray[index++] = "平均进价(件)";
		keyArray[index++] = "进价(件)";
		keyArray[index++] = "";
		keyArray[index++] = "库存量(" + unitName + ")";
		keyArray[index++] = "展示量(" + unitName + ")";
		keyArray[index++] = "平均进价(" + unitName + ")";
		keyArray[index++] = "进价(" + unitName + ")";
		return keyArray;
	}

	public String[] getValueArray() {
		String[] valueArray = new String[LENGTH];
		int index = 0;
		valueArray[index++] = "";
		valueArray[index++] = this.picecInventory;
		valueArray[index++] = this.pieceDispInventory;
		valueArray[index++] = this.piecePrice;
		valueArray[index++] = this.offerPiecePrice;
		valueArray[index++] = "";
		valueArray[index++] = this.unitInventory;
		valueArray[index++] = this.unitDispInventory;
		valueArray[index++] = this.unitPrice;
		valueArray[index++] = this.offerUnitPrice;
		return valueArray;
	}

	@Override
	public String toString() {
		return productId + ", " + picecInventory + ", " + unitInventory;
	}
	
//	public Map<String, String> convert() {
//		Map<String, String> map = new HashMap<String, String>(10);
//		map.put("库存量(件)", this.picecInventory);
//		map.put("展示量(件)", this.pieceDispInventory);
//		map.put("平均进价(件)", this.piecePrice);
//		map.put("进价(件)", this.offerPiecePrice);
//
//		map.put("库存量(" + unitName + ")", this.unitInventory);
//		map.put("展示量(" + unitName + ")", this.unitDispInventory);
//		map.put("平均进价(" + unitName + ")", this.unitPrice);
//		map.put("进价(" + unitName + ")", this.offerUnitPrice);
//		return map;
//	}

}
