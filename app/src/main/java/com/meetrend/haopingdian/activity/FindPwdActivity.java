package com.meetrend.haopingdian.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.FindPwdFragment;

public class FindPwdActivity extends BaseActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    	this.setContentView(R.layout.activity_fragment);
	        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			Fragment fragment = new FindPwdFragment();
			ft.add(R.id.frame_container, fragment);
			ft.commit();
	        
	}
	
	
}