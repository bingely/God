package com.meetrend.haopingdian.widget;

import com.meetrend.haopingdian.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DataCenterItemView extends FrameLayout{
	
	private TextView moneyNumView;

	public DataCenterItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		View view = LayoutInflater.from(context).inflate(R.layout.datacenter_item_layout, null);
		
		ImageView iconImageView = (ImageView) view.findViewById(R.id.datacenter_img);
		TextView titleView = (TextView) view.findViewById(R.id.title_text_view);
		moneyNumView = (TextView) view.findViewById(R.id.moneynum_view);
				
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DataItem);
		
		//图片
		if (typedArray.hasValue(R.styleable.DataItem_data_icon)) {
			int imgId = typedArray.getResourceId(R.styleable.DataItem_data_icon,R.drawable.loading_default);
			iconImageView.setImageResource(imgId);
		}
		
		//标题
		if (typedArray.hasValue(R.styleable.DataItem_data_title)) {
			String titleStr = typedArray.getString(R.styleable.DataItem_data_title);
			titleView.setText(titleStr);
		}
		
		//money
		if (typedArray.hasValue(R.styleable.DataItem_data_money)) {
			String money = typedArray.getString(R.styleable.DataItem_data_money);
			moneyNumView.setText(money);
		}
		
		typedArray.recycle();
		
		addView(view);
	
	}
	
	public void setNumView(String money){
		
		moneyNumView.setText(money);
	}

}