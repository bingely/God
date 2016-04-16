package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.BaseFragment;
import com.meetrend.haopingdian.fragment.MonthFragment;
import com.meetrend.haopingdian.fragment.QuarterFragment;
import com.meetrend.haopingdian.fragment.YearFragment;
import com.meetrend.haopingdian.widget.PagerSlidingTabStrip;

/**
 * 收入总览
 * @author 肖建斌
 *
 */
public class IncomeActivity extends BaseActivity {

	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	@ViewInject(id = R.id.pagerslidingtabstrip)
	PagerSlidingTabStrip mPagerSlidingTabStrip;
	@ViewInject(id = R.id.pager)
	ViewPager mViewPager;
	
	private String[] titles;
	private List<BaseFragment> mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_income);
		FinalActivity.initInjectedView(this);

		mBarTitle.setText("收入总览");
		titles = this.getResources().getStringArray(R.array.income_titles);

		
		mList = new ArrayList<BaseFragment>();
		mList.add(new MonthFragment());
		mList.add(new QuarterFragment());
		mList.add(new YearFragment());
		
		setTabsStyle(mPagerSlidingTabStrip, getResources().getDisplayMetrics());
		mViewPager.setAdapter(new TabAdapter(IncomeActivity.this.getSupportFragmentManager()));
		mViewPager.setOffscreenPageLimit(1);
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setSelectPosition(0);
	}
	
	public  void setTabsStyle(PagerSlidingTabStrip pagertab, DisplayMetrics dm) {
		pagertab.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		pagertab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
		pagertab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
		pagertab.setIndicatorColor(Color.parseColor("#02bc00"));
		pagertab.setTextColor(Color.parseColor("#000000"));
	}

	
	public class TabAdapter extends FragmentStatePagerAdapter {

		public TabAdapter(FragmentManager fm) {
			super(getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public String getPageTitle(int position) {
			return titles[position];
		}

	}
	

	
	public void onClickHome(View v){
		finish();
	}
}