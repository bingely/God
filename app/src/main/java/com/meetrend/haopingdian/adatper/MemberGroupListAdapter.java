package com.meetrend.haopingdian.adatper;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.MemberGroup;
import com.meetrend.haopingdian.env.Server;

public class MemberGroupListAdapter extends  BaseAdapter{
	private static final String TAG = MemberGroupListAdapter.class.getSimpleName();
	private LayoutInflater mLayoutInflater;
	private List<MemberGroup> list = new ArrayList<MemberGroup>();
	private FinalBitmap loader;
	String tag = this.getClass().getSimpleName();

	public MemberGroupListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		loader = FinalBitmap.create(context);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
	}

	public void refreshListValue(List<MemberGroup> list){
		this.list = list;
		this.notifyDataSetChanged();
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
		final MemberGroup memberGroup = list.get(position);
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag()==null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_member_group, null);
			holder.memberGroup_photo = (ImageView) convertView.findViewById(R.id.iv_member_group_photo);
			holder.memberGroup_name = (TextView) convertView.findViewById(R.id.tv_member_group_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.memberGroup_name.setText(memberGroup.tagName);

		loader.display(holder.memberGroup_photo, Server.BASE_URL + "");

		return convertView;
	}

	final static class ViewHolder {
		public ImageView memberGroup_photo;
		public TextView memberGroup_name;
	};
}
