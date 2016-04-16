package com.meetrend.haopingdian.widget;

//import u.aly.co;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/** 
 * 反弹ScrollView
 */  
public class BounceScrollView extends ScrollView {

	private final static int MAX_SCROLL_Y_DIS = 1000;
	
	private int newOverScrol_Y_DIS;
	
	private Context mContext;
	
	public BounceScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}  
	
	@SuppressLint("NewApi") private void init(Context context){
		this.mContext = context;
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		newOverScrol_Y_DIS = (int)displayMetrics.density * MAX_SCROLL_Y_DIS;
		
		//false:隐藏ScrollView的滚动条。  
        this.setVerticalScrollBarEnabled(false);  
          
        //不管装载的控件填充的数据是否满屏，都允许橡皮筋一样的弹性回弹。  
        this.setOverScrollMode(ScrollView.OVER_SCROLL_ALWAYS); 
	}
	
	/**
	 * 
	 */
	@SuppressLint("NewApi") @Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		
		return super.overScrollBy(deltaX/2, deltaY/3, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, newOverScrol_Y_DIS, isTouchEvent);
	}
   
	

}