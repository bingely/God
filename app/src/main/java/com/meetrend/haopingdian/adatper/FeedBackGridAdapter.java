package com.meetrend.haopingdian.adatper;

import java.util.List;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FeedBackGridAdapter extends BasicAdapter<String>{

	public FeedBackGridAdapter(List<String> list, Context context) {
		super(list, context);
		int size = list.size();
		list.add(size, "add");
	}
	
	
	public void setGridList(List<String> paths){
		mList = paths;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.edit_member_head_item, null);
			holder.head = (SimpleDraweeView) convertView.findViewById(R.id.headimg);
			holder.delIcon = (ImageView) convertView.findViewById(R.id.delete);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String imgId = mList.get(position);
		
		if (imgId.equals("add")) {
			holder.head.setImageResource(R.drawable.add_pic);
			holder.delIcon.setVisibility(View.GONE);
			
		} else {
			
			holder.delIcon.setVisibility(View.GONE);
			holder.head.setImageURI(Uri.parse(Server.BASE_URL + imgId));
		}
		return convertView;
	}
	
	class ViewHolder {
		SimpleDraweeView head;
		ImageView delIcon;
	}

}