package com.meetrend.haopingdian.adatper;

import java.util.List;
import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.GroupChatEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.StringUtil;
import com.meetrend.haopingdian.widget.ProgressWheel;

public class GroupChatListAdapter extends BaseAdapter {
	private static final String TAG = ChatListAdapter.class.getSimpleName();
	private static final long ONE_MINIUTE = 60 * 1000;
	// private FinalBitmap avatarLoader, contentImageLoader;
	private List<GroupChatEntity> mList;
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private String loginId = "";

	public GroupChatListAdapter(Context context, List<GroupChatEntity> list) {
		mLayoutInflater = LayoutInflater.from(context);
		mContext = context;
		mList = list;
		SPUtil mUtil = SPUtil.getDefault(mContext);
		loginId = mUtil.getId();

		// avatarLoader = FinalBitmap.create(context);
		// avatarLoader.configBitmapMaxHeight(50);
		// avatarLoader.configBitmapMaxWidth(50);
		// avatarLoader.configLoadingImage(R.drawable.loading_default);
		// avatarLoader.configLoadfailImage(R.drawable.loading_failed);
		//
		// contentImageLoader = FinalBitmap.create(context);
		// contentImageLoader.configBitmapMaxHeight(50);
		// contentImageLoader.configBitmapMaxWidth(50);
		// contentImageLoader.configLoadingImage(R.drawable.loading_default);
		// contentImageLoader.configLoadfailImage(R.drawable.loading_failed);
	}

	public void setList(List<GroupChatEntity> list) {
		this.mList = list;
		this.notifyDataSetChanged();
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

		GroupChatEntity item = mList.get(position); // 倒序显示
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_chat_right,
					null);
			holder.time = (TextView) convertView
					.findViewById(R.id.item_chatright_time);
			holder.photo = (SimpleDraweeView) convertView
					.findViewById(R.id.item_chatright_photo);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_chatright_content);
			holder.pb = (ProgressWheel) convertView
					.findViewById(R.id.item_chatright_pb);
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_chatright_image);
			holder.err = (ImageView) convertView
					.findViewById(R.id.item_chatright_failed);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.time.setText(StringUtil.formatDateStr(item.sendTime));
		// 时间相差过短不显示
		if (position != 0) {
			GroupChatEntity oldItem = mList.get(position);
			long result = Long.parseLong(item.sendTime)
					- Long.parseLong(oldItem.sendTime);
			if (result < ONE_MINIUTE) {
				holder.time.setVisibility(View.GONE);
			} else {
				holder.time.setVisibility(View.VISIBLE);
			}
		}

		if (item.status == null) {
			holder.pb.setVisibility(View.GONE);
		} else {
			holder.pb.setVisibility(View.VISIBLE);
		}

		if (item.status != null) {
			switch (item.status) {
			case SUCCESS:
				holder.pb.setVisibility(View.GONE);
				holder.err.setVisibility(View.GONE);
				break;
			case FAILED:
				holder.pb.setVisibility(View.GONE);
				holder.err.setVisibility(View.VISIBLE);
				break;
			case SENDING:
				holder.pb.setVisibility(View.VISIBLE);
				holder.err.setVisibility(View.GONE);
				break;
			}
		}
		holder.content.setText(Html.fromHtml(item.sendContent));
		Linkify.addLinks(holder.content, Linkify.WEB_URLS
				| Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
		holder.image.setVisibility(View.GONE);
		holder.content.setOnClickListener(null);
		//avatarLoader.display(holder.photo, Server.BASE_URL +item.avatarId);
		holder.photo.setImageURI(Uri.parse(Server.BASE_URL +item.avatarId));
		return convertView;
	}

	final static class ViewHolder {
		public TextView time;
		public SimpleDraweeView photo;
		public ImageView image;
		public TextView content;
		public ProgressWheel pb;
		public ImageView err;

	};
}
