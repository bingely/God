package com.meetrend.haopingdian.bean;

import java.util.ArrayList;

public class MemberDetail {
	public String id;//会员id
	public String userId;//用户id
	public String status;
	public String nickName;
	
	public String description;
	public String name;
	
	public String type;//member会员 ,employee员工
	/**
	 * 1男 2女
	 */
	public String gender;
	public String pictureId;//用户头像
	public String ageGroup;
	public String mobile;
	
	public String operationState;//操作状态
	public String email;//email
	public String remark;//remark
	public String tencentWeibo;//腾讯微博
	public String sinaWeibo;//新浪微博
	public ArrayList<Mhistory> history;//订单记录
	public ArrayList<Direct> direct;//已推荐列表
	
	//客户经理的id
	public String managerId;
	//客户经理的名字
	public String managerName;
	
	//是否可以聊天
	public boolean canTalk;
	
	//已推荐人
	public class Direct{
		public String name;
		public String userId;
		public String pictureId;
		
	}
	//订单历史记录
	public class Mhistory{
		/**
		 * 订单id
		 */
		public String orderId;
		/**
		 * 订单编号
		 */
		public String orderName;
		/**
		 * 下单人
		 */
		public String userId;
		/**
		 * 执行人
		 */
		public String executeUserId;
		/**
		 * 总价
		 */
		public String receivableAmount;
		/**
		 * 下单时间
		 */
		public String createTime;
		/**
		 * 订单列表
		 */
		public ArrayList<Orderlt> detail;
		/**
		 * 订单状态
		 */
		public String status;
		/**
		 * 付款状态
		 */
		public String payStatus;
		
	}
	
	public class Orderlt{
		/**
		 * 茶品牌名
		 */
		public String productName;
		/**
		 * 产品图片
		 */
		public String avatarId;
		/**
		 * 数量(饼)
		 */
		public String quantity;
		/**
		 * 数量(件)
		 */
		public String offerPieceQty;
		/**
		 * 产品批次
		 */
		public String productPici;
		/**
		 * 单位名称
		 */
		public String unitName;
		/**
		 * 产品单价
		 */
		public String price;
	}
	
	@Override
	public String toString() {
		return name + " : " + gender + " : " + mobile + " : " + ageGroup;
	}
}
