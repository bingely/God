package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetrend.haopingdian.R;

public class TriangleSpinner extends RelativeLayout {
	private static final String TAG = TriangleSpinner.class.getSimpleName();
	private TextView mTitle;
	private ImageView mTriangle;
	private RotateAnimation rotatedown, rotateup;
	private boolean flag = false;

	public TriangleSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.widget_triangle_spinner, this, true);
		mTitle = (TextView) this.findViewById(R.id.tv_triangle_spinner);
		mTriangle = (ImageView) this.findViewById(R.id.iv_triangle_spinner);
		
		rotatedown = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotatedown.setDuration(100);
		rotatedown.setFillAfter(true);

		rotateup = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateup.setDuration(100);
		rotateup.setFillAfter(true);
		
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TriangleSpinner, 0, 0);
		try {
			float size = array.getDimension(R.styleable.TriangleSpinner_spinner_text_size, 24);
			int color = array.getColor(R.styleable.TriangleSpinner_spinner_text_color, Color.WHITE);
			mTitle.setTextSize(size);
			mTitle.setTextColor(color);
		} finally {
			array.recycle();
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
			mTriangle.startAnimation(flag ? rotateup : rotatedown);
			flag = !flag;
			this.performClick();
			break;
		}

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return true;
	}

	public void resetTriangle() {
		mTriangle.startAnimation(rotateup);
		flag = false;
	}

	public void setText(String title) {
		mTitle.setText(title);
	}
	public String getText() {
		return mTitle.getText().toString();
	}

}
