package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.meetrend.haopingdian.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityInfoFragment extends BaseFragment {

	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.actionbar_action, click = "actionClick")
	TextView mAction;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_activity_info,container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		mTitle.setText("活动详情");
		mAction.setText("编辑");
		return rootView;
	}
	
	
	public void actionClick(View v){
		
	}
	
	public void homeClick(View v){
		mActivity.finish();
	}
}
