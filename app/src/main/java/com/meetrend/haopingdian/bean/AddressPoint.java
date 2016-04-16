package com.meetrend.haopingdian.bean;

public class AddressPoint
{
    
    public String name;//地名
    
    public String address; //地址
    
    public Location location;//经纬度
    
    public boolean isSelect;//是否选中
    
    public class Location
    {
        public String lng;
        
        public String lat;
    }
    
}
