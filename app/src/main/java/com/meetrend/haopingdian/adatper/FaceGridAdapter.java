package com.meetrend.haopingdian.adatper;

import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Faceicon;
import com.meetrend.haopingdian.enumbean.FacePageType;
import com.meetrend.haopingdian.widget.FaceiconTextView;

public class FaceGridAdapter extends BaseAdapter {

	private Faceicon[] mIconArray;
	private Context mContext;
	private static Map<FacePageType, Faceicon[]> map;
	private LayoutInflater mLayoutInflater = null;

	static {
		map = FacePageType.getFaceicon();
	}

	public FaceGridAdapter(Context context, FacePageType type) {
		mIconArray = map.get(type);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 20) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_faceicon, null);
			holder = new ViewHolder();
			holder.icon = (FaceiconTextView) convertView.findViewById(R.id.tv_facegrid_item);
			holder.delete = (ImageView) convertView.findViewById(R.id.iv_facegrid_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		int type = getItemViewType(position);
		Faceicon faceicon = mIconArray[position];

		if (type == 1) {
			holder.icon.setVisibility(View.GONE);
			holder.delete.setVisibility(View.VISIBLE);
		} else {
			holder.icon.setVisibility(View.VISIBLE);
			holder.delete.setVisibility(View.GONE);
			holder.icon.setText(faceicon.getValueOrEmoji());
		}
		return convertView;
	}


	@Override
	public int getCount() {
		return mIconArray.length;
	}

	@Override
	public Object getItem(int position) {
		return mIconArray[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		FaceiconTextView icon;
		ImageView delete;
	}
}