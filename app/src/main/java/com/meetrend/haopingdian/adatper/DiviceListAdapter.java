package com.meetrend.haopingdian.adatper;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Device;

public class DiviceListAdapter extends BaseAdapter {

	private Context context;
	private String  id;
	private List<Device> types;
	private LayoutInflater mLayoutiInflater;

	public DiviceListAdapter(Context context,List<Device> types, String id) {
		this.types = types;
		this.id = id;
		this.context = context;
		mLayoutiInflater = LayoutInflater.from(context);
	}
	
	public void setAddress(String dvAddress){
		this.id = dvAddress;
		notifyDataSetChanged();
	}
	
	public void setEventTypeId(String id){
		this.id = id;
	}

	@Override
	public int getCount() {
		return types.size();
	}

	@Override
	public Device getItem(int position) {
		return types.get(position);
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
			convertView = mLayoutiInflater.inflate(R.layout.list_popup_simple, parent, false);
			holder.title = (TextView) convertView.findViewById(R.id.list_popup_title_name);
			holder.select = (ImageView) convertView.findViewById(R.id.list_popup_title_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(types.get(position).deviceName + "  "+ types.get(position).deviceAddress);
		if (types.get(position).deviceAddress.equals(id)) {
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
