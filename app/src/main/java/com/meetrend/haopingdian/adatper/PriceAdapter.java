package com.meetrend.haopingdian.adatper;

import java.util.List;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.InCommeAdapter.TitleClickLitener;
import com.meetrend.haopingdian.adatper.InCommeAdapter.ViewHelper;
import com.meetrend.haopingdian.bean.InCommeBean;
import com.meetrend.haopingdian.util.NumerUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PriceAdapter extends BaseAdapter{
	
	private List<InCommeBean> mList;
	private LayoutInflater mLayoutinInflater;
	
	public PriceAdapter(List<InCommeBean> list,Context context){
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
			convertView = mLayoutinInflater.inflate(R.layout.adapter_incomme_price_layout, null);//收入
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

		String incomme = null;
		if (NumerUtil.isFloat(inCommeBean.Payed + "") || NumerUtil.isInteger(inCommeBean.Payed+""))
			incomme = NumerUtil.setSaveTwoDecimals(inCommeBean.Payed + "");
		else
			incomme = "0.00";
		viewHelper.inCommeTv.setText("收入："+incomme);


		String price = null;
		if (NumerUtil.isFloat(inCommeBean.AVG + "") || NumerUtil.isInteger(inCommeBean.AVG+""))
			price = NumerUtil.setSaveTwoDecimals(inCommeBean.AVG + "");
		else
			price = "0.00";
		
		String pay = "客单价："+ price;
		int contentLength = pay.length();
		SpannableStringBuilder style = new SpannableStringBuilder(pay);
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3b2d")), 4, 
				contentLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色    
		viewHelper.priceTv.setText(style);
		
		
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