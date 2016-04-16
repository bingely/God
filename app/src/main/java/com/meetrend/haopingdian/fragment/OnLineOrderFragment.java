package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.widget.PagerSlidingTabStrip;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 线上订单
 * @author 肖建斌
 *
 */
public class OnLineOrderFragment extends BaseFragment{
	
private final String[] TASNAMES = new String[]{"待确认","待付款","待发货","已完成","已取消"};
	
	@ViewInject(id = R.id.tablayout)
	TabLayout tabLayout;
	
	@ViewInject(id = R.id.pager)
	ViewPager mViewPager;
	
	private List<BaseFragment> mList;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_onlineorder, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mList = new ArrayList<BaseFragment>();
		//待确认
		BaseFragment mWaitConfirmFragment = new OrderManagerChildFragment("Unconfirmed", 2+"",0);
		//待付款
		BaseFragment mWaitPayFragment = new OrderManagerChildFragment("UnPay", 2+"",1);
		//待发货
		BaseFragment mWaitSendFragment = new OrderManagerChildFragment("Payed", 2+"",2);
		//已完成
		BaseFragment mWaitFinishFragment = new OrderManagerChildFragment("Finished", 2+"",3);
		//已取消
		BaseFragment mWaitCancelFragment = new OrderManagerChildFragment("Canceled", 2+"",4);
		
		mList.add(mWaitConfirmFragment);
		mList.add(mWaitPayFragment);
		mList.add(mWaitSendFragment);
		mList.add(mWaitFinishFragment);
		mList.add(mWaitCancelFragment);
		//setTabsStyle(mPagerSlidingTabStrip, getResources().getDisplayMetrics());
		TabAdapter tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
		tabLayout.setTabsFromPagerAdapter(tabAdapter);
		mViewPager.setAdapter(tabAdapter);
		tabLayout.setupWithViewPager(mViewPager);
		//mPagerSlidingTabStrip.setSelectPosition(0);//初始化位置并刷新
	}

//	public  void setTabsStyle(PagerSlidingTabStrip pagertab, DisplayMetrics dm) {
//		pagertab.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
//		pagertab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
//		pagertab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
//		pagertab.setIndicatorColor(Color.parseColor("#02bc00"));
//		pagertab.setTextColor(Color.parseColor("#000000"));
//	}

	public class TabAdapter extends FragmentStatePagerAdapter {

		public TabAdapter(FragmentManager fm) {
			super(getChildFragmentManager());
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

}