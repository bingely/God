package com.meetrend.haopingdian.widget;

import com.meetrend.haopingdian.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * itemview
 * 
 * @author 肖建斌
 * 
 */
public class ComItemLayoutView extends FrameLayout {


	public ComItemLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);

		ViewGroup.LayoutParams lps = new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		RelativeLayout viewGroup = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.common_item_layout, null);
				
		TextView titleView = (TextView) viewGroup.findViewById(R.id.hint_title);
		ImageView iconImageView = (ImageView) viewGroup
				.findViewById(R.id.icon_img);
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Comitem_attr);
		
		//图片
		if (typedArray.hasValue(R.styleable.Comitem_attr_cicon)) {
			int imgId = typedArray.getResourceId(R.styleable.Comitem_attr_cicon,R.drawable.loading_default);
			iconImageView.setImageResource(imgId);
		}
		
		//标题
		if (typedArray.hasValue(R.styleable.Comitem_attr_ctitle)) {
			String titleStr = typedArray.getString(R.styleable.Comitem_attr_ctitle);
			titleView.setText(titleStr);
		}
		
		typedArray.recycle();
		
		viewGroup.setLayoutParams(lps);
		
		addView(viewGroup);
	}
	
}