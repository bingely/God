package com.meetrend.haopingdian.adatper;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OverViewEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.listviewanimations.ArrayAdapter;

public class OverViewAdapter extends ArrayAdapter<OverViewEntity> {

	private LayoutInflater mLayoutInflater;
	private List<OverViewEntity> mList = new ArrayList<OverViewEntity>();
	private Context context;

	public OverViewAdapter(Context context,List<OverViewEntity> mList) {
		super();
		mLayoutInflater = LayoutInflater.from(context);
		this.mList = mList;
		this.context = context;
	}
	
	public void setList(List<OverViewEntity> list){
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public OverViewEntity getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_activity_overview, null);
			holder = new ViewHolder();
			holder.item_overview_time = (TextView) convertView.findViewById(R.id.item_overview_time);
			holder.item_overview_status = (TextView) convertView.findViewById(R.id.item_overview_status);
			holder.item_overview_name = (TextView) convertView.findViewById(R.id.item_overview_name);
			holder.item_overview_img = (SimpleDraweeView) convertView.findViewById(R.id.item_overview_img);
			holder.item_overview_participate = (TextView) convertView.findViewById(R.id.item_overview_participate);
			holder.item_overview_share = (TextView) convertView.findViewById(R.id.item_overview_share);
			holder.item_overview_reply = (TextView) convertView.findViewById(R.id.item_overview_reply);
			holder.item_overview_praise = (TextView) convertView.findViewById(R.id.item_overview_praise);
			holder.item_read_num_tv = (TextView) convertView.findViewById(R.id.item_read_num_tv);
			holder.item_overview_canyu = (TextView) convertView.findViewById(R.id.item_overview_canyu);
			//是否免费
			holder.item_paystatus_tv = (TextView) convertView.findViewById(R.id.pay_status_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		OverViewEntity overView = mList.get(position);
		holder.item_overview_time.setText(overView.createTime);

		if (overView.isCharge){
			holder.item_paystatus_tv.setText("收费");
			holder.item_paystatus_tv.setBackgroundResource(R.drawable.eventpay);
		}else{
			holder.item_paystatus_tv.setText("免费");
			holder.item_paystatus_tv.setBackgroundResource(R.drawable.eventfree);
		}

		String activityStatus = overView.type;
		if(!TextUtils.isEmpty(activityStatus)){
				switch(Integer.parseInt(activityStatus)){
				case 1:{
					activityStatus ="未开始";
					holder.item_overview_status.setText(activityStatus);
					holder.item_overview_status.setBackgroundResource(R.drawable.event_end);
					break;
				}
				case 2:{
					activityStatus ="进行中";
					holder.item_overview_status.setText(activityStatus);
					holder.item_overview_status.setBackgroundResource(R.drawable.eventloading);
					break;
				}
				case 3:{
					activityStatus ="已结束";
					holder.item_overview_status.setText(activityStatus);
					holder.item_overview_status.setBackgroundResource(R.drawable.event_end);
					break;
				}
				default:{
					activityStatus ="";
					break;
				}

			}
		}

		holder.item_overview_status.setText(activityStatus);
		holder.item_overview_name.setText(overView.name);

		holder.item_overview_img.setImageURI(Uri.parse(Server.BASE_URL + overView.image));
		holder.item_overview_participate.setText(overView.joinPerson+"/");
		holder.item_overview_canyu.setText(overView.signNum+" ");
		holder.item_overview_share.setText("分享 "+overView.shareNumber);
		holder.item_overview_reply.setText("回复 "+overView.numCommet);
		holder.item_overview_praise.setText("点赞 "+overView.thumbsUp);
		holder.item_read_num_tv.setText("已阅"+overView.readNumber);
		return convertView;
	}
	
	final static class ViewHolder{
		public TextView item_overview_time;
		public TextView item_overview_status;
		public TextView item_overview_name;
		public SimpleDraweeView item_overview_img;
		public TextView item_overview_participate;
		public TextView item_overview_share;
		public TextView item_overview_reply;
		public TextView item_overview_praise;
		public TextView item_read_num_tv;
		public TextView item_overview_canyu;
		public TextView item_paystatus_tv;
	}
}