package com.meetrend.haopingdian.adatper;

import java.util.List;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Voucher;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DiscountAdapter extends BasicAdapter<Voucher>{
	
	private String vourcherId;

	public DiscountAdapter( String vourcherId,List<Voucher> list, Context context) {
		super(list, context);
		this.vourcherId = vourcherId;
	}
	
	public void setVourId(String vourcherId){
		this.vourcherId = vourcherId;
	}
	
	@Override
	public int getCount() {
		
		return mList == null ? 0 : super.getCount();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Voucher voucher = mList.get(position);
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.dialog_list_item_layout, parent, false);
			holder.title = (TextView) convertView.findViewById(R.id.list_popup_title_name);
			holder.select = (ImageView) convertView.findViewById(R.id.list_popup_title_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(voucher.value+ voucher.name);
		
		if (voucher.volumeid.equals(vourcherId)) {
			holder.title.setTextColor(Color.parseColor("#ff0000"));
			holder.select.setVisibility(View.VISIBLE);
		}else {
			holder.title.setTextColor(Color.parseColor("#252525"));
			holder.select.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	
	 class ViewHolder {
		public TextView title;
		public ImageView select;
	}

}
