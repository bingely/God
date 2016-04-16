package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.meetrend.haopingdian.R;

public class UIToggleButton extends RelativeLayout {
	private ToggleButton btn;
	private CheckedTextView tv;

	public UIToggleButton(Context context) {
		this(context, null);
	}

	public UIToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.widget_uitogglebtn, this, true);
		btn = (ToggleButton) this.findViewById(R.id.btn_uitoggle);
		tv = (CheckedTextView) this.findViewById(R.id.tv_uitoggle);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UIToggleButton, 0, 0);
		try {
			String content = a.getString(R.styleable.UIToggleButton_toggle_text);	
			int resId = a.getResourceId(R.styleable.UIToggleButton_toggle_background, R.drawable.loading_default);
			tv.setText(content);
			btn.setBackgroundResource(resId);
		} finally {
			a.recycle();
		}
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
			this.performClick();
			break;
		}
		return true;
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	public void setChecked(boolean flag) {
		tv.setChecked(flag);
		btn.setChecked(flag);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return true;
	}
}