package com.meetrend.haopingdian.bean;


public class OrderEntity {
	
	/**
	 * 执行人idexcutorUserIdexcutorUserIdexcutorUserId
	 */
	public String excutorUserId;
	/**
	 * 产品图片路径
	 */
	public String avatarId;
	/**
	 * 下单时间
	 */
	public String crateTime;
	/**
	 * 执行人/送货人
	 */
	public String executeUserId;
	/**
	 * 订单id
	 */
	public String id;
	/**
	 * 门店名称
	 */
	public String name;
	/**
	 * 订单编号
	 */
	public String orderName;
	/**
	 * 数量(饼)
	 */
	public String offerPieceQty;
	/**
	 * 付款状态
	 */
	public String payStatus;
	/**
	 * 产品批次
	 */
	public String productPici;
	/**
	 * 数量(件)
	 */
	public String quantity;
	/**
	 * 改价后的金额（应收/总价）
	 */
	public String receivableAmount;
	/**
	 * 单位名称
	 */
	public String unitName;
	/**
	 * 订单状态
	 */
	public String status;
	/**
	 * 下单人
	 */
	public String userId;
	
	/**
	 * “店内，线上 标识” 1店内2线上
	 */
	public String orderSource;
	
	@Override
	public String toString() {
			return orderName + " : " + id + this.status + this.payStatus;
	}

}
