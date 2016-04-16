package com.meetrend.haopingdian.adatper;

import java.util.List;
import net.tsz.afinal.FinalBitmap;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.EventPictrue;
import com.meetrend.haopingdian.env.Server;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EventGridAdapter extends BaseAdapter{

	private Context context;
	private LayoutInflater layoutInflater;
	private List<EventPictrue> list;
	private FinalBitmap loader;
	
	public EventGridAdapter(Context context,List<EventPictrue> list){
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.list = list;
		loader = FinalBitmap.create(context);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
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
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.event_grid_adapter_layout, null);
			viewHolder.gridImg = (ImageView) convertView.findViewById(R.id.grid_img);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		EventPictrue eventPictrue = list.get(position);
		loader.display(viewHolder.gridImg, Server.BASE_URL + eventPictrue.pictureId);
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView gridImg;
	}

}
