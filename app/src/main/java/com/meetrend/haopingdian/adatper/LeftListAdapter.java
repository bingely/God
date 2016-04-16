package com.meetrend.haopingdian.adatper;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ProductReportoryActivity;

public class LeftListAdapter extends BaseAdapter{
	
	LayoutInflater layoutInflater;
	private List<LeftItemBean> productNameList;
	
	public LeftListAdapter(List<LeftItemBean> productNameList,Context context){
		this.productNameList = productNameList;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return productNameList.size();
	}

	@Override
	public LeftItemBean getItem(int position) {
		return productNameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LeftHodler leftHodler = null;
		if (convertView == null) {
			leftHodler = new LeftHodler();
			convertView = layoutInflater.inflate(R.layout.left_listview_item_layout, null);
			leftHodler.layout = (LinearLayout) convertView.findViewById(R.id.layout);
			leftHodler.productNameView = (TextView) convertView.findViewById(R.id.left_name_view);
			convertView.setTag(leftHodler);
		}else {
			
			leftHodler = (LeftHodler) convertView.getTag();
		}
		
		leftHodler.productNameView.setText(productNameList.get(position).name);
		if (productNameList.get(position).isSelect) {
			leftHodler.layout.setBackgroundResource(R.drawable.icon_selected);
		}else {
			leftHodler.layout.setBackgroundResource(R.drawable.left_un_select_item_bg);
		}
		
		return convertView;
	}
	
	final class LeftHodler{
		TextView productNameView;
		LinearLayout layout;
	}
	
}


