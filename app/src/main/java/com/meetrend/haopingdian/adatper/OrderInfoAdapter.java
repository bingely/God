package com.meetrend.haopingdian.adatper;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.StringUtil;
import com.meetrend.haopingdian.util.NumerUtil;

public class OrderInfoAdapter extends BaseAdapter {

	private ArrayList<JsonArray> jsonArray;
	private LayoutInflater mInflater;
//	private int resId;
	FinalBitmap loader;
	
	public OrderInfoAdapter(Context context,ArrayList<JsonArray> jsonArray) {
		super();
		this.jsonArray = jsonArray;
		mInflater = LayoutInflater.from(context);
		loader = FinalBitmap.create(context);
//		this.resId = resId;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return jsonArray.size();
	}


	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return jsonArray.get(arg0);
	}


	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_order_info_head, null);
			holder.order_info_avatar = (ImageView) convertView.findViewById(R.id.order_info_avatar);
			holder.order_info_teaname = (TextView) convertView.findViewById(R.id.order_info_teaname);
			holder.order_info_price = (TextView) convertView.findViewById(R.id.order_info_price);
			holder.order_info_num = (TextView) convertView.findViewById(R.id.order_info_num);
			holder.order_info_total = (TextView) convertView.findViewById(R.id.order_info_total);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		loader.display(holder.order_info_avatar, Server.BASE_URL + jsonArray.get(position).avatarId);
		holder.order_info_teaname.setText(jsonArray.get(position).name + "  " + jsonArray.get(position).productPici);
		holder.order_info_price.setText("单价: "+jsonArray.get(position).incomeAmount);
		holder.order_info_num.setText("数量: "+NumerUtil.saveThreeDecimal(jsonArray.get(position).quantity));
		holder.order_info_total.setText("总价: "+NumerUtil.saveOneDecimal(Float.parseFloat(jsonArray.get(position).incomeAmount)*Float.parseFloat(jsonArray.get(position).quantity)+""));
//		holder.order_info_status.setBackgroundResource(resId);
		return convertView;
	}
	
	final static class ViewHolder { 
		public ImageView order_info_avatar;//产品图片
		public TextView order_info_teaname;//茶品名称
		public TextView order_info_price;//单价
		public TextView order_info_num;//数量
		public TextView order_info_total;//总价
	}
}