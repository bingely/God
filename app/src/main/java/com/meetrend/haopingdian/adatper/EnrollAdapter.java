package com.meetrend.haopingdian.adatper;

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
import com.meetrend.haopingdian.bean.EnrollUser;
import com.meetrend.haopingdian.env.Server;

public class EnrollAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	private Context context;
	private List<EnrollUser> list;
	private FinalBitmap loader;
	
	public EnrollAdapter(Context context,List<EnrollUser> list){
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
		ViewHelper viewHelper = null;
		EnrollUser enrollUser= list.get(position);
		if (convertView == null) {
			viewHelper = new ViewHelper();
			convertView = layoutInflater.inflate(R.layout.arrive_adapter_item, null);
			viewHelper.childImg = (ImageView) convertView.findViewById(R.id.arrive_adapter_child_img);
			viewHelper.childNameView = (TextView) convertView.findViewById(R.id.arrive_adapter_child_name);
			convertView.setTag(viewHelper);
		}else {
			viewHelper = (ViewHelper) convertView.getTag();
		}
		loader.display(viewHelper.childImg, Server.BASE_URL + enrollUser.head);
		viewHelper.childNameView.setText(enrollUser.userName);
		return convertView;
	}
	class ViewHelper {
		ImageView childImg;
		TextView childNameView;
	}
}
