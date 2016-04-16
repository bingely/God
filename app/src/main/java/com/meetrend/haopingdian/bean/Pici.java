package com.meetrend.haopingdian.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.zxing.client.result.ISBNParsedResult;

/**
 * 批次
 * 
 */
public class Pici{

	public String productId;// 产品id
	public String productImage;// 产品分类图片
	public String unitId;// 产品id
	public String unitName;// 单位名称
	public float number;// 剩余数量（库存）
	public String price;// 价格
	public String model1Name;// 规格1
	public String model1Value;// 规格1值
	public String model2Name;// 规格2
	public String model2Value;// 规格2值
	public String inventoryId;// 库存id
	public String fullName;// 产品全名
	public String description;//描述
	public boolean isGift;//是赠送商品
	public String targetUrl;//连接地址


/*	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(productId);
		parcel.writeString(productImage);
		parcel.writeString(unitId);
		parcel.writeString(unitName);
		parcel.writeFloat(number);
		parcel.writeString(price);
		parcel.writeString(model1Name);
		parcel.writeString(model1Value);
		parcel.writeString(model2Name);
		parcel.writeString(model2Value);
		parcel.writeString(inventoryId);
		parcel.writeString(fullName);
		parcel.writeString(description);
	}

	public static final Parcelable.Creator<Pici> CREATOR = new Parcelable.Creator<Pici>()
	{
		public Pici createFromParcel(Parcel in)
		{
			return new Pici(in);
		}

		public Pici[] newArray(int size)
		{
			return new Pici[size];
		}
	};

	public Pici(Parcel in)
	{
		productId = in.readString();
		productImage = in.readString();
		unitName = in.readString();

		number = in.readFloat();
		price = in.readString();
		model1Name = in.readString();

		model1Value = in.readString();
		model2Name = in.readString();
		model2Value = in.readString();

		inventoryId = in.readString();
		fullName = in.readString();
		description = in.readString();
	}*/

}
