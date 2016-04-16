package com.meetrend.haopingdian.widget;

import com.meetrend.haopingdian.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupNameLayout extends FrameLayout {

	private LinearLayout groupView = null;
	public String name;
	private TextView textView;
	
	public GroupNameLayout(Context context,AttributeSet attrs) {
		super(context, attrs);
		groupView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_memberlist_alphabet, null);
		textView = (TextView) groupView.findViewById(R.id.tv_alphabet);
		addView(groupView);
	}

//	终止向子view传递touch的事件
	public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		textView.setText(name);
	}

}
