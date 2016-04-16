package com.meetrend.haopingdian.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.OrderInfoFragment;

/**
 * 订单详情
 *
 */
public class OnlineOrderCRUDActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_fragment);
	
		String orderId = getIntent().getStringExtra("orderid");
		String orderSource = getIntent().getStringExtra("orderSource");
		
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		Fragment fragment = new OrderInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putString("orderSource", orderSource);
		bundle.putString("orderid", orderId);
		fragment.setArguments(bundle);
		ft.add(R.id.frame_container, fragment);
		ft.commit();
	}
}