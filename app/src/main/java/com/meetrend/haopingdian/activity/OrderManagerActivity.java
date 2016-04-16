package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.enumbean.PageStatus;
import com.meetrend.haopingdian.event.FinishOrderManagerEvent;
import com.meetrend.haopingdian.fragment.IncomeFragment;
import com.meetrend.haopingdian.fragment.OnlineOrderListFragment;
import com.meetrend.haopingdian.fragment.ProductFastOrderFragment;
import com.meetrend.haopingdian.fragment.StorePlaceOrderFragment;
import com.meetrend.haopingdian.tool.SPUtil;

import de.greenrobot.event.EventBus;

/**
 * 1 订单管理
 * 
 * 2 店内下单  
 *
 */
public class OrderManagerActivity extends BaseActivity {

	
	public static final String type = "type"; 
	
    public static final int TYPE_ONLINE_ORDER = 1;//订单管理
	
	
	public static final int TYPE_INCOME = 2;//收入总览
	
	public static final int TYPE_STORE_PLACE = 3;//门店下单
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_fragment);
		Intent intent = getIntent();
		int actionType = intent.getIntExtra(type, 0);
		Fragment fragment = null;
		Bundle bundle = new Bundle();
		switch(actionType){
			case TYPE_ONLINE_ORDER:{
				//订单管理
				//fragment = new OnlineOrderListFragment();
				//bundle.putSerializable(OnlineOrderListFragment.STATUS, PageStatus.ONLINE_UNCONFIRM);
				Intent  orderManagerIntent = new Intent(OrderManagerActivity.this, NewOrderManagerActivity.class);
				startActivity(orderManagerIntent);
				finish();
				break;
			}
			case TYPE_INCOME:{
				//收入总览
				fragment = new IncomeFragment();
				
				FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
				fragment.setArguments(bundle);
				ft.add(R.id.frame_container, fragment);
				ft.commit();
				break;
			}
			case TYPE_STORE_PLACE:{
				//店内收银
				fragment = new ProductFastOrderFragment();
				FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
				ft.add(R.id.frame_container, fragment);
				ft.commit();
				break;
			}
		}
		
	}


	public void homeClick(View v) {
		this.finish();
	}
	
	
	
	
}