package com.meetrend.haopingdian.adatper;

import java.util.List;
import net.tsz.afinal.FinalBitmap;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.NewMessage;
import com.meetrend.haopingdian.enumbean.MsgType;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.StringUtil;
import com.meetrend.haopingdian.widget.RoundImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.renderscript.Type;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NewMsgListAdapter extends BaseAdapter{
	
	private static final String TAG = MessageListAdapter.class.getName();
	
	private List<NewMessage> mList;
	private LayoutInflater mLayoutInflater;

	public NewMsgListAdapter(Context context, List<NewMessage> data) {
		mLayoutInflater = LayoutInflater.from(context);
		mList = data;
	}
	
	public void setList(List<NewMessage> mList){
		this.mList = mList;
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.new_item_message, null);
			holder = new ViewHolder();
			holder.photo = (SimpleDraweeView) convertView.findViewById(R.id.iv_message_photo);
			holder.llamas = (TextView) convertView.findViewById(R.id.tv_message_name);
			holder.content = (TextView) convertView.findViewById(R.id.tv_message_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_message_time);
			holder.unReadTv = (TextView) convertView.findViewById(R.id.unread_msg_num_tv);
			holder.unDistributeImg = (ImageView) convertView.findViewById(R.id.undistributeimg);
			holder.canTalkImg = (ImageView) convertView.findViewById(R.id.cantalk_msg_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NewMessage msgrecord = mList.get(position);
		holder.llamas.setText(msgrecord.name);//发送人
		holder.time.setText(StringUtil.formatDateStr(msgrecord.createTime));//发送时间
		
		if (msgrecord.isgroup) {
			holder.content.setText(Html.fromHtml(msgrecord.content));
			holder.unReadTv.setVisibility(View.GONE);
		}else {
			if (!msgrecord.unReadNumber.equals("")) {
				if (!msgrecord.unReadNumber.equals("0")) {
					
					if (Integer.parseInt(msgrecord.unReadNumber) >= 10)  {
						holder.unReadTv.setVisibility(View.VISIBLE);
						holder.unReadTv.setText("...");//超过10条显示方式
						
					}else {
						holder.unReadTv.setVisibility(View.VISIBLE);
						holder.unReadTv.setText(msgrecord.unReadNumber+"");//未读条数
					}
				}else {
					holder.unReadTv.setVisibility(View.GONE);
				}
			}else {
				holder.unReadTv.setVisibility(View.GONE);
			}
			
			MsgType type = MsgType.create(msgrecord.msgType);
			switch (type) {
				case TEXT:
					holder.content.setText(Html.fromHtml(msgrecord.content));
				break;
				case VOICE:
					holder.content.setText("[语音]");
				break;
				case IMAGE:
					holder.content.setText("[图片]");
				break;
			}
			
			String managerId = msgrecord.managerId;
			if ("".equals(managerId)) {
				holder.unDistributeImg.setVisibility(View.VISIBLE);
			}else {
				holder.unDistributeImg.setVisibility(View.GONE);
			}
		}
		//Fresco图片加载方式
		holder.photo.setImageURI(Uri.parse(Server.BASE_URL+msgrecord.image));
		
		if (msgrecord.canTalk) {
			holder.canTalkImg.setVisibility(View.GONE);
		}else {
			holder.canTalkImg.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	final static class ViewHolder {
		public SimpleDraweeView photo;
		public TextView llamas;
		public TextView content;
		public TextView time;
		public TextView unReadTv;
		public ImageView unDistributeImg;
		public ImageView canTalkImg;
	};
	
	
}
