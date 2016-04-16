package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.MemberGroupFragment;

/**
 * 
 * @author  肖建斌
 * 
 * 群组列表
 *
 */
public class MemberGroupActivity extends BaseActivity {
	
	MemberGroupFragment fragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_fragment);
		
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		fragment = new MemberGroupFragment();
		ft.add(R.id.frame_container, fragment);
		ft.commit();
	}
	

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		fragment.onActivityResult(arg0, arg1, arg2);
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	
}