package com.meetrend.haopingdian.bean;

import java.io.Serializable;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import com.meetrend.haopingdian.enumbean.OrderStatus;

public class OnlineOrderDetail implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 转账帐号
	 */
	public String account;
	/**
	 * 转账开户名
	 */
	public String accountName;
	
    /**
     * 转账开户行
     */
	public String bank;
	/**
	 * 转账备注
	 */
	public String bankRemark;
	/**
	 * 下单时间
	 */
	public String createTime;
	/**
	 * 执行人Id 
	 */
	public String executeUserId;
	/**
	 * 执行人 订单状态为未确认时该值为空
	 */
	public String executeUserName;
	/**
	 * 订单Id
	 */
	public String id;
	
	/**
	 * 订单送货地址
	 */
	public String obtainAddress;
	
	/**
	 * 原金额
	 */
	public String detailAmount;
	/**
	 * 订单编号
	 */
	public String orderName;
	/**
	 * 付款状态
	 */
	//public String payStatus;//注：该字段已经取消
	
	/**
	 * 应收金额(改价后的金额)
	 */
	public String receivableAmount;
	/**
	 * 订单状态
	 */
	public String status;
	/**
	 *  产品单位
	 */
	public String unitName;
	
	/**
	 * 客户电话
	 */
	public String shipPhone;
	/**
	 * 下单时间
	 */
	public String crateTime;
	
	/**
	 * 快递单号
	 * 
	 * */
	public String expressCode;
	
	
	/**
	 * 订单列表数据
	 */
	public ArrayList<JsonArray> jsonArray;
	
	/**
	 * 配送方式
	 */
	public String shipMethod;
	
	/**
	 * 下单人id
	 */
	public String createUserId;
	
	/**
	 * 下单人名字
	 */
	public String createUserName;
	
	/**
	 * 客户Id
	 */
	public String userId;
	
	/**
	 * 客户名称
	 */
	public String userName;
	
	//备注
	public String description;
	
	//二维码图片url
	public String footerUrl;
	
	
	//小票打印相关属性
	public String mendianSQH;//门店授权号
	public String paidAmount;//实收金额
	public String discountAmount;//优惠金额
	public String integralAmount;//积分金额
	public String incomeAmount;//合计金额
	public String changeAmount;//找零
	public String mobile;//门店电话
	public String checkoutname;//收银员
	public String storeName;//门店名称
	public String companyname;//公司名字
	public String payType;//支付方式
	
	//详情新增
	public String inputPoint;
	public String voucherName;
	public String voucherValue;
	public String voucherRecordId;
	
	public OrderStatus getOrderStatus() {
		return OrderStatus.get(this.status);
	}
	
	public class JsonArray implements Parcelable{
		
		/**
		 * 优惠的金额
		 */
		public String discountAmount;
		
		/**批次id 
		 * 
		 */
		public String unitId;
		
		/**
		 * 产品id 
		 */
		public String productId;
		
		/**
		 * 图片路径
		 */
	    public String avatarId;
	    
	    /**
		 *  产品单位
		 */
		public String unitName;
		
		/**
		 * 门店名称
		 */
		public String name;
		
		/**
		 * 单价
		 */
		public String incomeAmount;
		
		/**
		 * 产品批次
		 */
		public String productPici;
		
		/**
		 * 数量
		 */
		public String quantity;
		
		/**
		 * 数量
		 */
		public String offerPieceQty;
		
		/**
		 * 单价
		 */
		public String piecePrice;
		
		public String model1Value;//规格1
		public String model2Value;//规格2
		public String fullName;//产品全名

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
		}
	}
	
}
