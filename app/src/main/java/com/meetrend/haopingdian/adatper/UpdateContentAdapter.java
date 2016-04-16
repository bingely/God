package com.meetrend.haopingdian.adatper;

import java.util.List;

import com.meetrend.haopingdian.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UpdateContentAdapter extends BasicAdapter<String>{
	

	public UpdateContentAdapter(List<String> list, Context context) {
		super(list, context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = mLayoutInflater.inflate(R.layout.dialog_update_listview_item_layout, null);
		TextView textView = (TextView) convertView.findViewById(R.id.update_list_item_view);
		textView.setText(mList.get(position));
		
		return convertView;
	}

}
