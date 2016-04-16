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
import com.meetrend.haopingdian.bean.SharePeople;
import com.meetrend.haopingdian.env.Server;
/**
 * 
 * 已到场
 * */
public class ShareGridAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private Context context;
	private List<SharePeople> list;
	private FinalBitmap loader;
	
	public ShareGridAdapter(Context context,List<SharePeople> list){
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
		SharePeople sharePeople = list.get(position);
		if (convertView == null) {
			viewHelper = new ViewHelper();
			convertView = layoutInflater.inflate(R.layout.sharelist_item_layout, null);
			viewHelper.Img = (ImageView) convertView.findViewById(R.id.arrive_adapter_child_img);
			viewHelper.NameView = (TextView) convertView.findViewById(R.id.arrive_adapter_child_name);
			convertView.setTag(viewHelper);
		}else {
			viewHelper = (ViewHelper) convertView.getTag();
		}
		loader.display(viewHelper.Img, Server.BASE_URL + sharePeople.head);
		viewHelper.NameView.setText(sharePeople.userName);
		return convertView;
	}
	class ViewHelper {
		ImageView Img;
		TextView NameView;
	}
}
