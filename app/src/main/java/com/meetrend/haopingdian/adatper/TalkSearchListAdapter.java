/*package com.meetrend.haopingdian.adatper;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.TalkSearchEntity;
import com.meetrend.haopingdian.env.Server;

public class TalkSearchListAdapter extends SearchListAdapter{
	private LayoutInflater mLayoutInflater;
	private FinalBitmap loader;
	private final float scale;
	
	public TalkSearchListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		scale = context.getResources().getDisplayMetrics().density;
		float dimension = context.getResources().getDimension(R.dimen.message_list_image_size);
		int pixels = (int) (dimension * scale + 0.5f);
		loader = FinalBitmap.create(context);
		loader.configBitmapMaxHeight(pixels);
		loader.configBitmapMaxWidth(pixels);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
	}
	@Override
	public void clear() {
		App.talkSearchList.clear();
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return App.talkSearchList.size();
	}

	@Override
	public Object getItem(int position) {
		return App.talkSearchList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_talk_search, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView)convertView.findViewById(R.id.iv_talk_search);
			holder.name = (TextView)convertView.findViewById(R.id.tv_talk_search_name);
			holder.content = (TextView)convertView.findViewById(R.id.tv_talk_search_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TalkSearchEntity entity = App.talkSearchList.get(position);
		loader.display(holder.image, Server.BASE_URL + entity.avatarId);
		holder.name.setText(entity.name);
		holder.content.setText(entity.content);
		return convertView;
	}
	
	final static class ViewHolder {
		public ImageView image;
		public TextView name;
		public TextView content;
	};
}
*/