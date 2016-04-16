package com.meetrend.haopingdian.fragment;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseChildOrderListFragment extends BaseFragment{
	
	public  boolean isVisible;//fragment是否可见
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(getUserVisibleHint()) {
			isVisible = true;
			visible();
		} else {
			isVisible = false;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void visible(){
		requestList();
	}
	
	public abstract void requestList() ;

}