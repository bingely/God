package com.meetrend.haopingdian.adatper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.InventoryDetailEntity;

public class InventoryInfoAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private String[] keys, values;

	public InventoryInfoAdapter(Context context, InventoryDetailEntity entity) {
		mLayoutInflater = LayoutInflater.from(context);
		keys = entity.getKeyArray();
		values = entity.getValueArray();
	}

	@Override
	public int getCount() {
		return keys.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	enum TYPE {
		BIG, SMALL, DATA
	};

	private static final int TYPE_COUNT = TYPE.values().length;
	@Override
	public int getItemViewType(int position) {
		switch (position) {
		case 0:
			return TYPE.BIG.ordinal();
		case 5:
			return TYPE.SMALL.ordinal();
		default:
			return TYPE.DATA.ordinal();
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private View big, small;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		switch (position) {
		case 0:
			if (big == null) {
				big = mLayoutInflater.inflate(R.layout.item_inventory_detail_big, null);
			}
			return big;
		case 5:
			if (small == null) {
				small = mLayoutInflater.inflate(R.layout.item_inventory_detail_small, null);
			}
			return small;
		}

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_inventory_datail, null);
			holder = new ViewHolder();
			holder.key = (TextView) convertView.findViewById(R.id.tv_invetory_detail_key);
			holder.value = (TextView) convertView.findViewById(R.id.tv_invetory_detail_value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.key.setText(keys[position]);
		holder.value.setText(values[position]);
		return convertView;
	}

	static class ViewHolder {
		TextView key;
		TextView value;
	}

}
