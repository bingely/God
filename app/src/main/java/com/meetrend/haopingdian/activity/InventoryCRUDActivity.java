package com.meetrend.haopingdian.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.InventoryInfoFragment;

public class InventoryCRUDActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_inventory_crud);
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		Fragment fragment = new InventoryInfoFragment();
		ft.add(R.id.frame_inventory, fragment);
		ft.commit();
	}
}