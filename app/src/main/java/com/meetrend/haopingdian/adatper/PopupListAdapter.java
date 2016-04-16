package com.meetrend.haopingdian.adatper;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meetrend.haopingdian.R;

public class PopupListAdapter extends BaseAdapter {
	private List<String> mList;
	private LayoutInflater mLayoutInflater;

	public PopupListAdapter(Context context, List<String> list) {
		mLayoutInflater = LayoutInflater.from(context);
		mList = list;		
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null; 
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_popup, null);
			holder = new ViewHolder();
			holder.tv = (TextView)convertView.findViewById(R.id.item_popup);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv.setText(mList.get(position));
		return convertView;
	}

	private final static class ViewHolder {
		public TextView tv;
	}
}
