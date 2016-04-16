package com.meetrend.haopingdian.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a on 2016/2/22.
 */
public class CostBean  implements  Parcelable{

    public String name;
    public String money;
    public String limitAmount;

    public CostBean(){
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
       parcel.writeString(name);
        parcel.writeString(money);
        parcel.writeString(limitAmount);
    }

    public static final Parcelable.Creator<CostBean> CREATOR = new Parcelable.Creator<CostBean>()
    {
        public CostBean createFromParcel(Parcel in)
        {
            return new CostBean(in);
        }

        public CostBean[] newArray(int size)
        {
            return new CostBean[size];
        }
    };

    public CostBean(Parcel in)
    {
        name = in.readString();
        money = in.readString();
        limitAmount = in.readString();
    }

}
