package com.meetrend.haopingdian.bean;

/**
 * 门店订单类
 * @author tigereye
 *
 */
public class StoreOrderDetail {
	public String avatarId;
	public String detailAmount; 
	public String discount;  
	
	public String orderDate;
	public String orderId;
	public String orderName;   
	
	public String payStatus;  
	public String perHourPrice; 

	public String receivableAmount; 
	public String roomHour;  
	public String roomId;       
	public String roomName;     
	public String roomPrice;    
 
	public String status;    
	public String userName;     
   
    public class Detail {
    	public String productNumber;
    	public String productStorePicture;
    	public String unitName;
    	public String serialNumber;
    	public String unitPrice;
    	public String productName;
    	
    	@Override
    	public String toString() {
    		 return productNumber + productStorePicture + unitName + serialNumber + unitPrice + productName;
    	}
    }
}
