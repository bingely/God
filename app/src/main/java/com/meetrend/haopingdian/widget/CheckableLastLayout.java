package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CheckableLastLayout extends RelativeLayout implements Checkable {
	private static final String TAG = CheckableLastLayout.class.getSimpleName();
	private Checkable child;
	
	public CheckableLastLayout(Context context) {
		this(context, null);
	}

	public CheckableLastLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckableLastLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setChecked(boolean checked) {
		if (child == null) {
			throw new NullPointerException("child shouldn't be null");
		}
		child.setChecked(checked);
		this.refreshDrawableState();
	}
	
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LinearLayout parent = (LinearLayout)this.getChildAt(0);
        child = (Checkable) parent.getChildAt(0);
    }

	@Override
	public boolean isChecked() {
		if (child == null) {
			throw new NullPointerException("child shouldn't be null");
		}
		return child.isChecked();
	}

	@Override
	public void toggle() {
		if (child == null) {
			throw new NullPointerException("child shouldn't be null");
		}
		child.toggle();
		this.refreshDrawableState();
	}
	

}
