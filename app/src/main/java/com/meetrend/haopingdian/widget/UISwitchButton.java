package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetrend.haopingdian.R;

public class UISwitchButton extends RelativeLayout {
	private static final String TAG = UISwitchButton.class.getSimpleName();
	private TextView tv;
	private View vw;
	private boolean isVisible;

	public UISwitchButton(Context context) {
		this(context, null);
	}

	public UISwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.widget_uibtn, this, true);
		tv = (TextView) this.findViewById(R.id.uibtn_tv);
		
		vw = this.findViewById(R.id.uibtn_vw);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UISwitchButton, 0, 0);
		try {
			String content = array.getString(R.styleable.UISwitchButton_switch_text);
			tv.setText(content);
		} finally {
			array.recycle();
		}
		isVisible = false;
		vw.setVisibility(INVISIBLE);
		tv.setTextColor(this.getResources().getColor(R.color.green));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			isVisible = !isVisible;
			vw.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
			tv.setTextColor(isVisible ? this.getResources().getColor(R.color.green) : this.getResources().getColor(R.color.black));
			this.performClick();//产生点击事件
			break;
		}
		return true;
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return true;
	}
	
	public boolean isChecked() {
		return isVisible;
	}
	public void setChecked(boolean flag) {
		isVisible = flag;
		vw.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
		tv.setTextColor(isVisible ? this.getResources().getColor(R.color.green) : this.getResources().getColor(R.color.black));
	}
	public void setText(String text) {
		tv.setText(text);
	}
}
