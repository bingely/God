package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.MemberInfoFragment;
import com.umeng.socialize.utils.Log;

public class MemberInfoActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_fragment);
	
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		Fragment fragment = new MemberInfoFragment();
		fragment.setArguments(this.getIntent().getExtras());
		ft.replace(R.id.frame_container, fragment);
		ft.commit();
	}
}