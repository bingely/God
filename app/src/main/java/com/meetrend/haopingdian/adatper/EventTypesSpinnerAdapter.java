package com.meetrend.haopingdian.adatper;

import java.util.List;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.EventTypeBean;
import com.meetrend.haopingdian.bean.Schema;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class EventTypesSpinnerAdapter extends BaseAdapter {

	public Context context;
	public List<EventTypeBean> list;
	public TextView spinner;

	public EventTypesSpinnerAdapter(Context context, List<EventTypeBean> list, TextView spinner) {
		this.context = context;
		this.list = list;
		this.spinner = spinner;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public EventTypeBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_popup_simple, parent, false);
			holder.title = (TextView) convertView.findViewById(R.id.list_popup_title_name);
			holder.select = (ImageView) convertView.findViewById(R.id.list_popup_title_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		EventTypeBean schema = list.get(position);
		holder.title.setText(schema.FText);
		if (schema.FText.equals(spinner.getText())) {
			holder.title.setTextColor(Color.parseColor("#e33b3d"));
			holder.select.setVisibility(View.VISIBLE);
		} 
		
		else if (schema.FText.toString().equals("全部活动") && spinner.getText().toString().equals("活动总览")) {
			holder.title.setTextColor(Color.parseColor("#e33b3d"));
			holder.select.setVisibility(View.VISIBLE);
		}
		
		else {
			holder.title.setTextColor(Color.parseColor("#252525"));
			holder.select.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	 class ViewHolder {
		public TextView title;
		public ImageView select;
	}
}
