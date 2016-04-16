package com.meetrend.haopingdian.adatper;

import java.util.List;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.InCommeBean;
import com.meetrend.haopingdian.util.NumerUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InCommeAdapter extends BaseAdapter{
	
	private List<InCommeBean> mList;
	private LayoutInflater mLayoutinInflater;
	
	private int type;//1收入，2支付笔数，3客单价 三种显示模式
	
	public InCommeAdapter(List<InCommeBean> list,Context context){
		mList = list;
		mLayoutinInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}
	

	@Override
	public InCommeBean getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHelper viewHelper = null;
		
		if (convertView == null) {
			viewHelper = new ViewHelper();
			convertView = mLayoutinInflater.inflate(R.layout.adapter_incomme_item_layout, null);//收入
			viewHelper.timeTv = (TextView) convertView.findViewById(R.id.date_tv);
			viewHelper.inCommeTv = (TextView) convertView.findViewById(R.id.incomme_tv);
			viewHelper.payNumTv = (TextView) convertView.findViewById(R.id.pay_num_tv);
			viewHelper.priceTv = (TextView) convertView.findViewById(R.id.price__tv);
			viewHelper.titleLayout = (LinearLayout) convertView.findViewById(R.id.titlelayout);
			viewHelper.bottomLayout = (LinearLayout) convertView.findViewById(R.id.bottomlayout);
			viewHelper.triangleImg = (ImageView) convertView.findViewById(R.id.triangle_img);
			convertView.setTag(viewHelper);
		}else {
			viewHelper = (ViewHelper) convertView.getTag();
		}
		
		InCommeBean inCommeBean = getItem(position);
		
		viewHelper.timeTv.setText(inCommeBean.Date);
		viewHelper.payNumTv.setText("支付笔数："+(int)Math.floor(inCommeBean.Num));

		String avg = null;
		if (NumerUtil.isFloat(inCommeBean.AVG + "") || NumerUtil.isInteger(inCommeBean.AVG+""))
			avg = NumerUtil.setSaveTwoDecimals(inCommeBean.AVG + "");
		else
			avg = "0.00";
		viewHelper.priceTv.setText("客单价：" + avg);


		String payed = null;
		if (NumerUtil.isFloat(inCommeBean.Payed + "") || NumerUtil.isInteger(inCommeBean.Payed+""))
			payed = NumerUtil.setSaveTwoDecimals(inCommeBean.Payed + "");
		else
			payed = "0.00";

		String pay = "收入："+payed;


		int contentLength = pay.length();
		SpannableStringBuilder style = new SpannableStringBuilder(pay);
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3b2d")), 3, 
				contentLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色    
		viewHelper.inCommeTv.setText(style);
		
		
		if (!inCommeBean.bottomIsShow) {
			viewHelper.bottomLayout.setVisibility(View.VISIBLE);
			viewHelper.triangleImg.setImageResource(R.drawable.icon_collapse);
		}else {
			viewHelper.bottomLayout.setVisibility(View.GONE);
			viewHelper.triangleImg.setImageResource(R.drawable.icon_unfold);
		}
		
		viewHelper.titleLayout.setOnClickListener(new TitleClickLitener(viewHelper.bottomLayout, inCommeBean,viewHelper.triangleImg));
		
		return convertView;
	}
	
	public class TitleClickLitener implements OnClickListener{
		
		LinearLayout layout;
		InCommeBean inCommeBean;
		ImageView imageView;
		public TitleClickLitener(LinearLayout layout,InCommeBean inCommeBean,ImageView imageView){
			this.layout = layout;
			this.inCommeBean = inCommeBean;
			this.imageView = imageView;
		}

		@Override
		public void onClick(View v) {
			
			if (layout.getVisibility() == View.GONE) {
				layout.setVisibility(View.VISIBLE);
				inCommeBean.bottomIsShow = false;
				imageView.setImageResource(R.drawable.icon_collapse);
			}else {
				layout.setVisibility(View.GONE);
				inCommeBean.bottomIsShow = true;
				imageView.setImageResource(R.drawable.icon_unfold);
			}
			
		}
		
	}
	
	class ViewHelper{
		LinearLayout titleLayout;
		LinearLayout bottomLayout;
		TextView timeTv;
		TextView inCommeTv;
		TextView payNumTv;
		TextView priceTv;
		ImageView triangleImg;
	}

}
