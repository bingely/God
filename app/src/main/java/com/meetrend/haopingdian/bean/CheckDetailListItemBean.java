package com.meetrend.haopingdian.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 详情列表bean
 *
 */
public class CheckDetailListItemBean implements Parcelable{
	
	public String picture;
	public String productId;
	public  double amount;
	public String productName;
	public String unit;//商品单位
	public String detailId;//盘点详情id
	
	public CheckDetailListItemBean(){
		
	}
	
	public CheckDetailListItemBean(String productId,double amount){
		this.productId = productId;
		this.amount = amount;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		
		out.writeString(picture);
		out.writeString(productId);
		out.writeDouble(amount);
		out.writeString(productName);
	}
	
	 public static final Parcelable.Creator<CheckDetailListItemBean> CREATOR = new Creator<CheckDetailListItemBean>()
     {
        @Override
        public CheckDetailListItemBean[] newArray(int size)
        {
            return new CheckDetailListItemBean[size];
        }
        
        @Override
        public CheckDetailListItemBean createFromParcel(Parcel in)
        {
            return new CheckDetailListItemBean(in);
        }
     };
			    
     public CheckDetailListItemBean(Parcel in)
     {
        picture = in.readString();
        productId = in.readString();
        amount = in.readDouble();
        productName = in.readString();
     }
}
