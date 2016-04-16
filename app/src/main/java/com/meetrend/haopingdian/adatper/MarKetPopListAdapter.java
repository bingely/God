package com.meetrend.haopingdian.adatper;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meetrend.haopingdian.R;

	
  public class MarKetPopListAdapter extends BaseAdapter {

		private String  id;
		private List<String> mList;
		private LayoutInflater mLayoutInflater;

		public MarKetPopListAdapter(Context context,List<String> list, String id) {
			this.id = id;
			this.mList = list;
			mLayoutInflater = LayoutInflater.from(context);
		}

	  @Override
	  public int getCount() {
		  return mList.size();
	  }

	  @Override
	  public long getItemId(int i) {
		  return i;
	  }

	  @Override
	  public String getItem(int i) {
		  return mList.get(i);
	  }

	  @Override
	  public View getView(int i, View convertView, ViewGroup viewGroup) {

		  ViewHolder holder = null;

		  if (convertView == null) {
			  holder = new ViewHolder();
			  convertView = mLayoutInflater.inflate(R.layout.market_price_pop_list_item,null);
			  holder.title = (TextView) convertView.findViewById(R.id.market_pop_title);
			  convertView.setTag(holder);
		  } else {
			  holder = (ViewHolder) convertView.getTag();
		  }

		  String value = mList.get(i);
		  holder.title.setText(value);

		  if (value.equals(id == null ? "":id)) {
			  holder.title.setTextColor(Color.parseColor("#02bc00"));
		  }else {
			  holder.title.setTextColor(Color.parseColor("#252525"));
		  }

		  return convertView;
	  }

	  	public void setHasSettingValue(String id){
			this.id = id;
		}
		
		public void setList(List<String> list){
			mList  = list;
		}

		 final class ViewHolder {
			public TextView title;
		}
	}

