package com.meetrend.haopingdian.adatper;

import java.util.ArrayList;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Sign;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SignListAdapter extends BaseAdapter{
	
	private Context context;
    private ArrayList<Sign> list;
    private LayoutInflater layoutInflater = null;
    
    public SignListAdapter(Context context,ArrayList<Sign> list){
    	this.list = list;
    	layoutInflater = LayoutInflater.from(context);
    }
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	
	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.sign_list_item_layout, null);
			viewHolder.leftIcon = (ImageView) convertView.findViewById(R.id.item_start_icon);
			viewHolder.addressView = (TextView) convertView.findViewById(R.id.addressView);
			viewHolder.desContentView = (TextView) convertView.findViewById(R.id.descontentview);
			viewHolder.dymView = (TextView) convertView.findViewById(R.id.dymview);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		
		if (position == 0) {
			viewHolder.leftIcon.setImageResource(R.drawable.sign_in_last);
		}else{
			viewHolder.dymView.setVisibility(View.GONE);
		}
			
		
		Sign sign = list.get(position);
		if (!TextUtils.isEmpty(sign.FLocation)) {
			if (!TextUtils.isEmpty(sign.FCheckinTime)) {
				String time = sign.FCheckinTime.substring(11, 16);
				String location = time +"  "+sign.FLocation;
				viewHolder.addressView.setText(location);
			}else {
				viewHolder.addressView.setText(sign.FLocation);
			}
			
		}else {
			viewHolder.addressView.setVisibility(View.GONE);
		}
		
		if (!TextUtils.isEmpty(sign.FDesc)) {
			viewHolder.desContentView.setText(sign.FDesc);
		}else {
			viewHolder.desContentView.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	class ViewHolder {
		ImageView leftIcon;
		TextView addressView;
		TextView desContentView;
		TextView dymView;
	}

}
