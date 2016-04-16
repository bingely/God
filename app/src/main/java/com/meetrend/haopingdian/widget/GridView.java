package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GridView extends android.widget.GridView{
	public GridView(Context context) {
		super(context);
	}

	public GridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override  
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
	            MeasureSpec.AT_MOST);  
	    super.onMeasure(widthMeasureSpec, expandSpec);  
	  
	}  
	
	// 禁止Grid滑动
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_MOVE)
			return true;
		return super.dispatchTouchEvent(ev);
	}
}
