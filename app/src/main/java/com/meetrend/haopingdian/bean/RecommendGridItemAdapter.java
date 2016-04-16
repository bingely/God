package com.meetrend.haopingdian.bean;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.widget.RoundImageView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendGridItemAdapter extends BaseAdapter{
	
	private LayoutInflater layoutInflater;
	private Context context;
	private List<Inviter> list;
	private FinalBitmap loader;
	public RecommendGridItemAdapter(Context context,List<Inviter> list){
		layoutInflater = LayoutInflater.from(context);
		this.list = list;
		Log.i("------------list----------", list.size() +"");
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
		ViewHelper viewHelper = null;
		Inviter inviter = list.get(position);
		if (convertView == null) {
			viewHelper = new ViewHelper();
			convertView = layoutInflater.inflate(R.layout.recommend_child_gridview_item, null);
			viewHelper.childImg = (RoundImageView) convertView.findViewById(R.id.child_img);
			viewHelper.child_checkonImg = (ImageView) convertView.findViewById(R.id.child_check_off_img);
			viewHelper.childNameView = (TextView) convertView.findViewById(R.id.child_name);
			convertView.setTag(viewHelper);
		}else {
			viewHelper = (ViewHelper) convertView.getTag();
		}
		
		loader.display(viewHelper.childImg, Server.BASE_URL + inviter.head);
		if (inviter.isSign) {
			viewHelper.child_checkonImg.setVisibility(View.VISIBLE);
		}else {
			viewHelper.child_checkonImg.setVisibility(View.GONE);
		}
		viewHelper.childNameView.setText(inviter.userName);
		return convertView;
	}
	
	class ViewHelper {
		RoundImageView childImg;
		ImageView child_checkonImg;
		TextView childNameView;
	}

}
