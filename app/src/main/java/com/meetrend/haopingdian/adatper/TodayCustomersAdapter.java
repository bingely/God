package com.meetrend.haopingdian.adatper;

import java.util.List;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OrderBean;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TodayCustomersAdapter extends BasicAdapter{
	
	public TodayCustomersAdapter(List<OrderBean> list, Context context) {
		super(list, context);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		ViewHolder viewHolder = null;
		
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.today_orderlist_item_layout, null);
			viewHolder.dateView = (TextView) convertView.findViewById(R.id.order_time);
			viewHolder.orderNumView = (TextView) convertView.findViewById(R.id.order_num);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		OrderBean orderBean = (OrderBean) mList.get(position);
		viewHolder.dateView.setText(orderBean.Date);
		
		
		String str = "访客数："+ (int)Math.floor(orderBean.Num);
		int contentLength = str.length();
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3b2d")),4, contentLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色    
		
		viewHolder.orderNumView.setText(style);
		
		return convertView;
	}
	

	public class ViewHolder {
		
		TextView dateView ;
		TextView orderNumView;
		
	}

}