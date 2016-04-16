package com.meetrend.haopingdian.adatper;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.MarketPriceDetailInfo;

public class MarketPriceDetailAdapter extends BasicAdapter<MarketPriceDetailInfo>{

	public MarketPriceDetailAdapter(List<MarketPriceDetailInfo> list,Context context) {
		super(list, context);
	}
	
	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.adapter_marketdetail_list_item_layout, null);
			viewHolder.trangleImageView = (ImageView) convertView.findViewById(R.id.triangle_img);
			viewHolder.yearView = (TextView) convertView.findViewById(R.id.detail_date_tv);
			viewHolder.priceView = (TextView) convertView.findViewById(R.id.reference_price_view);
			viewHolder.zhangdieE = (TextView) convertView.findViewById(R.id.zhangdiee_tv);
			viewHolder.zhangdieF = (TextView) convertView.findViewById(R.id.zhangdiefu_tv);
			
			viewHolder.every_zhangdieE = (TextView) convertView.findViewById(R.id.everyday_zhangdie_num);
			viewHolder.every_zhangdieF = (TextView) convertView.findViewById(R.id.evety_day_zhangdiefu);
			
			viewHolder.titleLayout = (LinearLayout) convertView.findViewById(R.id.titlelayout);
			viewHolder.bottomLayout = (LinearLayout) convertView.findViewById(R.id.bottomlayout);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MarketPriceDetailInfo info = mList.get(position);
		String date = info.date.substring(0,10);
		viewHolder.yearView.setText(date);
		viewHolder.priceView.setText("参考价: "+info.price);
		
		viewHolder.zhangdieE.setText("¥"+info.change);
		viewHolder.every_zhangdieE.setText("¥"+info.dayChange);//日均涨跌额

		//涨跌额
		if (info.changeisOnZeor) {
			viewHolder.zhangdieE.setTextColor(Color.parseColor("#ff0000"));
			viewHolder.zhangdieF.setTextColor(Color.parseColor("#ff0000"));
			viewHolder.zhangdieF.setText("↑"+info.changeRate+"%");
		}else {
			viewHolder.zhangdieE.setTextColor(Color.parseColor("#2ac22e"));
			viewHolder.zhangdieF.setTextColor(Color.parseColor("#2ac22e"));
			
			viewHolder.zhangdieF.setText("↓"+info.changeRate+"%");
		}
		
		//日均涨跌额
		if (!info.changeRateisDownZeor) {
			viewHolder.every_zhangdieE.setTextColor(Color.parseColor("#ff0000"));
			viewHolder.every_zhangdieF.setTextColor(Color.parseColor("#ff0000"));
			viewHolder.every_zhangdieF.setText("↑"+info.dayChangeRate+"%");
		}else {
			viewHolder.every_zhangdieE.setTextColor(Color.parseColor("#2ac22e"));
			viewHolder.every_zhangdieF.setTextColor(Color.parseColor("#2ac22e"));
			viewHolder.every_zhangdieF.setText("↓"+info.dayChangeRate+"%");
		}
		
		if (!info.isShow) {
			viewHolder.bottomLayout.setVisibility(View.VISIBLE);
			viewHolder.trangleImageView.setImageResource(R.drawable.detail_up);
		}else {
			viewHolder.bottomLayout.setVisibility(View.GONE);
			viewHolder.trangleImageView.setImageResource(R.drawable.detail_down);
		}
		
		viewHolder.titleLayout.setOnClickListener(new ItemClickLinstener(viewHolder,info));
		
		return convertView;
	}
	
	public class ItemClickLinstener implements OnClickListener{
		
		ViewHolder viewHolder;
		MarketPriceDetailInfo marketPriceDetailInfo;
		
		public ItemClickLinstener(ViewHolder viewHolder,MarketPriceDetailInfo marketPriceDetailInfo){
			this.viewHolder = viewHolder;
			this.marketPriceDetailInfo = marketPriceDetailInfo;
		}

		@Override
		public void onClick(View v) {
			
			if (viewHolder.bottomLayout.getVisibility() == View.VISIBLE) {
				marketPriceDetailInfo.isShow = true;
				viewHolder.bottomLayout.setVisibility(View.GONE);
				viewHolder.trangleImageView.setImageResource(R.drawable.detail_down);
			}else {
				
				marketPriceDetailInfo.isShow = false;
				viewHolder.bottomLayout.setVisibility(View.VISIBLE);
				viewHolder.trangleImageView.setImageResource(R.drawable.detail_up);
			}
		}
	}
	
	final class ViewHolder{
		
		ImageView trangleImageView;
		
		TextView yearView;
		TextView priceView;
		TextView zhangdieE;
		TextView zhangdieF;
		
		TextView every_zhangdieE;
		TextView every_zhangdieF;
		
		LinearLayout titleLayout;
		LinearLayout bottomLayout;
		
		
	}

}
