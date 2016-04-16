package com.meetrend.haopingdian.widget;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.util.NumerUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * 数据中心数据tab
 *
 */
public class CommonTabView extends FrameLayout{

	private LinearLayout firstTabLayout;
	private LinearLayout secondTabLayout;
	private LinearLayout thirdTabLayout;
	
	private View firstDivierView;
	private View secondDivierView;
	private View thirdDivierView;
	
	private TextView firstTopView;
	private TextView firstBottomView;
	
	private TextView secTopView;
	private TextView secBottomView;
	
	private TextView thirdTopView;
	private TextView thirdBottomView;
	
	//当前选中的position
	private int currentPosition = 1;
	
	//颜色id
	int redColor;
	int blackColor;
	
	public interface CheckLinstener {
	     public void checkPosition(int position);	
	}
	
	public CheckLinstener checkLinstener ;
	
	public void setCheckLintener(CheckLinstener checkLinstener){
		this.checkLinstener = checkLinstener;
	}
	
	public CommonTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		redColor = Color.parseColor("#ff3b2d");
		blackColor = Color.parseColor("#252525");
		
	    initViews(context);
	    
	    showTabStyle(currentPosition);
	}
	
	private void initViews(Context context){
		
		View rootView = LayoutInflater.from(context).inflate(R.layout.commontab_layout, null);
	    firstTabLayout = (LinearLayout) rootView.findViewById(R.id.first_tab);
	    secondTabLayout = (LinearLayout) rootView.findViewById(R.id.sec_tab);
	    thirdTabLayout = (LinearLayout) rootView.findViewById(R.id.third_tab);
	    
	    TabOnClickLinstener tabOnClickLinstener = new TabOnClickLinstener();
	    firstTabLayout.setOnClickListener(tabOnClickLinstener);
	    secondTabLayout.setOnClickListener(tabOnClickLinstener);
	    thirdTabLayout.setOnClickListener(tabOnClickLinstener);
	    
	    firstTopView = (TextView) rootView.findViewById(R.id.frst_tab_topview);
	    firstBottomView =  (TextView) rootView.findViewById(R.id.frst_tab_bottom_view);
	    
	    secTopView = (TextView) rootView.findViewById(R.id.sec_tab_topview);
	    secBottomView =  (TextView) rootView.findViewById(R.id.sec_tab_bottom_view);
	    
	    thirdTopView = (TextView) rootView.findViewById(R.id.third_top_topview);
	    thirdBottomView =  (TextView) rootView.findViewById(R.id.third_tab_bottom_view);
	    
	    firstDivierView = (View) rootView.findViewById(R.id.first_diviver);
	    secondDivierView = (View) rootView.findViewById(R.id.sec_diviver);
	    thirdDivierView = (View) rootView.findViewById(R.id.third_diviver);
	    
	    addView(rootView);
	}

	private int cPosition;
	
	private void showTabStyle(int position){


		if (position == 1) {
			firstTopView.setTextColor(redColor);
			firstBottomView.setTextColor(redColor);
			firstDivierView.setVisibility(View.VISIBLE);

			secTopView.setTextColor(blackColor);
			secBottomView.setTextColor(blackColor);
			secondDivierView.setVisibility(View.GONE);

			thirdTopView.setTextColor(blackColor);
			thirdBottomView.setTextColor(blackColor);
			thirdDivierView.setVisibility(View.GONE);


		}else if(position == 2){

			firstTopView.setTextColor(blackColor);
			firstBottomView.setTextColor(blackColor);
			firstDivierView.setVisibility(View.GONE);

			secTopView.setTextColor(redColor);
			secBottomView.setTextColor(redColor);
			secondDivierView.setVisibility(View.VISIBLE);

			thirdTopView.setTextColor(blackColor);
			thirdBottomView.setTextColor(blackColor);
			thirdDivierView.setVisibility(View.GONE);

		}else {

			firstTopView.setTextColor(blackColor);
			firstBottomView.setTextColor(blackColor);
			firstDivierView.setVisibility(View.GONE);

			secTopView.setTextColor(blackColor);
			secBottomView.setTextColor(blackColor);
			secondDivierView.setVisibility(View.GONE);

			thirdTopView.setTextColor(redColor);
			thirdBottomView.setTextColor(redColor);
			thirdDivierView.setVisibility(View.VISIBLE);
		}
	}
	
	public void setFirstBottomViewValue(String value){
		firstBottomView.setText(value);
	}
	
	public void setSecBottomViewValue(String value){
		secBottomView.setText(value);
	}
	
	public void setThirdBottomViewValue(String value){
		thirdBottomView.setText(value);
	}
	
	private class TabOnClickLinstener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.first_tab:
				currentPosition = 1;
				showTabStyle(currentPosition);
				
				break;
			case R.id.sec_tab:
				currentPosition = 2;
				showTabStyle(currentPosition);

				break;
			case R.id.third_tab:
				currentPosition = 3;
				showTabStyle(currentPosition);

				break;

			default:
				break;
			}

			if (cPosition == currentPosition)
				return;

			if (checkLinstener != null) {
				checkLinstener.checkPosition(currentPosition);
			}
			cPosition = currentPosition;
		}
	}
	
	//显示所有的标题数据
	public void setTopDadas(String[][] topDatas){
		
		firstTopView.setText(topDatas[0][0]);
		if (NumerUtil.isFloat(topDatas[0][1]) || NumerUtil.isInteger(topDatas[0][1]))
			firstBottomView.setText(NumerUtil.setSaveTwoDecimals(topDatas[0][1]));
		else
			firstBottomView.setText("0.00");
		
		secTopView.setText(topDatas[1][0]);
		secBottomView.setText((int)Math.floor(Double.parseDouble(topDatas[1][1]))+"");

		thirdTopView.setText(topDatas[2][0]);
		if (NumerUtil.isFloat(topDatas[2][1]) || NumerUtil.isInteger(topDatas[2][1]))
			thirdBottomView.setText(NumerUtil.setSaveTwoDecimals(topDatas[2][1]));
		else
			thirdBottomView.setText("0.00");
	}
}
