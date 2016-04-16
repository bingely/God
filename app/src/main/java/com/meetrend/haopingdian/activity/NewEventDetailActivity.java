package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.ApplayEventTabFragment;
import com.meetrend.haopingdian.fragment.BaseFragment;
import com.meetrend.haopingdian.fragment.CommentEventTabFragment;
import com.meetrend.haopingdian.fragment.EventManagerEventTabFragment;
import com.meetrend.haopingdian.fragment.PriseEventTabFragment;
import com.meetrend.haopingdian.widget.PagerSlidingTabStrip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 新版 活动详情
 * @author 肖建斌
 *
 */
public class NewEventDetailActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home,click = "backClick")
	ImageView backImageView;
	@ViewInject(id = R.id.actionbar_title)
	public TextView titleView;
	
	
	@ViewInject(id = R.id.tablayout)
	TabLayout tabLayout;
	
	@ViewInject(id = R.id.pager)
	ViewPager mViewPager;
	
	private List<BaseFragment> mList = null;
	
	public String eventID;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neweventdetail);
		FinalActivity.initInjectedView(this);
		titleView.setText("活动详情");
		eventID = getIntent().getStringExtra("id");
		boolean ischarge = getIntent().getBooleanExtra("ischarge", false);
		
		//管理
		BaseFragment managerFragment = new EventManagerEventTabFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("ischarge",ischarge);
		managerFragment.setArguments(bundle);
		//报名
		BaseFragment applyFragment = new ApplayEventTabFragment();
		//评论
		BaseFragment commentFragment = new CommentEventTabFragment();
		//赞
		BaseFragment priseBaseFragment = new PriseEventTabFragment();
		
		mList = new ArrayList<BaseFragment>();
		mList.add(managerFragment);
		mList.add(applyFragment);
		mList.add(commentFragment);
		mList.add(priseBaseFragment);

		TabAdapter tabAdapter = new TabAdapter(NewEventDetailActivity.this.getSupportFragmentManager());
		tabLayout.setTabsFromPagerAdapter(tabAdapter);
		mViewPager.setAdapter(tabAdapter);
		tabLayout.setupWithViewPager(mViewPager);

		Intent intent = getIntent();
		String eventId = intent.getStringExtra("id");
	}

	private final String[] TASNAMES = new String[]{"活动管理","报名","评论","赞"};
	public class TabAdapter extends FragmentPagerAdapter {

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
			return TASNAMES[position];
		}
	}
	
	
	public void backClick(View view){
		finish();
	}

}