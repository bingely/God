package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableSecondLayout extends RelativeLayout implements Checkable {
	
	private static final String TAG = CheckableLastLayout.class.getSimpleName();
	private Checkable child;
	
	public CheckableSecondLayout(Context context) {
		this(context, null);
	}

	public CheckableSecondLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckableSecondLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setChecked(boolean checked) {
		if (child == null) {
			throw new NullPointerException("child shouldn't be null");
		}
		child.setChecked(checked);
//		this.refreshDrawableState();
	}
	
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RelativeLayout pLayout = (RelativeLayout) getChildAt(0);//第一个控件  组名
        child = (Checkable)pLayout.getChildAt(1);
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
//		this.refreshDrawableState();
	}
}