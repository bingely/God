package com.meetrend.haopingdian.adatper;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.MessageRecord;
import com.meetrend.haopingdian.enumbean.MsgType;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.StringUtil;
import com.meetrend.haopingdian.widget.RoundImageView;

/**
 * MainActivity主界面MessageListFragment的适配器
 * @author tigereye
 *
 */
public class MessageListAdapter extends BaseAdapter {
	private static final String TAG = MessageListAdapter.class.getSimpleName();
	private FinalBitmap mLoader1;
	private List<MessageRecord> mList;
	private LayoutInflater mLayoutInflater;

	public MessageListAdapter(Context context, List<MessageRecord> data) {
		mLayoutInflater = LayoutInflater.from(context);
		mList = data;
		
		mLoader1 = FinalBitmap.create(context);
		mLoader1.configBitmapMaxHeight(48);
		mLoader1.configBitmapMaxWidth(48);
		mLoader1.configLoadingImage(R.drawable.loading_default);
		mLoader1.configLoadfailImage(R.drawable.loading_failed);
	}
	
	public void setList(List<MessageRecord> mList){
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
			convertView = mLayoutInflater.inflate(R.layout.item_message, null);
			holder = new ViewHolder();
			holder.photo = (RoundImageView) convertView.findViewById(R.id.iv_message_photo);
			holder.llamas = (TextView) convertView.findViewById(R.id.tv_message_name);
			holder.content = (TextView) convertView.findViewById(R.id.tv_message_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_message_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MessageRecord msgrecord = mList.get(position);
		holder.llamas.setText(msgrecord.name);//发送人
		holder.time.setText(StringUtil.formatDateStr(msgrecord.createTime));//发送时间

		//会话的最后一条信息显示
		if (msgrecord.isGroupMsg) {
			mLoader1.display(holder.photo, Server.BASE_URL+msgrecord.imgId);
			holder.content.setText(Html.fromHtml(msgrecord.content));
		}else {
			MsgType type = MsgType.create(msgrecord.messageType);
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
			mLoader1.display(holder.photo, Server.BASE_URL+msgrecord.imgId);
		}
		return convertView;
	}

	final static class ViewHolder {
		public RoundImageView photo;
		public TextView llamas;
		public TextView content;
		public TextView time;
	};
}
