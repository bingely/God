package com.meetrend.haopingdian.speechrecord;

import java.util.List;

import com.meetrend.haopingdian.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RecoderAdapter extends ArrayAdapter<Recorder>{
	
	private Context mContext;
	private List<Recorder> list;
	
	private int minWidth;
	private int maxWdith;
	
	private LayoutInflater layoutInflater = null;
	
	public RecoderAdapter(Context context, List<Recorder> objects) {
			
		super(context, -1, objects);
		mContext = context;
		list = objects;
		
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(displayMetrics);
		minWidth = (int) (displayMetrics.widthPixels* 0.15);
		maxWdith = (int) (displayMetrics.widthPixels * 0.8);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.record_item_layout, null);
			viewHolder.timeView = (TextView) convertView.findViewById(R.id.time_length_tv);
			viewHolder.voiceLayout = (FrameLayout) convertView.findViewById(R.id.content_voice_layout);
			convertView.setTag(viewHolder);
		}else { 
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final Recorder recorder = list.get(position);
		viewHolder.timeView.setText(Math.round(list.get(position).getTime())+"\"");
		ViewGroup.LayoutParams layoutParams = viewHolder.voiceLayout.getLayoutParams();
		layoutParams.width = (int) (minWidth  + (maxWdith / 60f * list.get(position).getTime()));
		return convertView;
	}

	class ViewHolder{
		TextView timeView;
		FrameLayout voiceLayout;
	}
	

}
