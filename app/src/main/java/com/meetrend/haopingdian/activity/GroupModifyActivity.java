package com.meetrend.haopingdian.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.GroupModifyFragment;

public class GroupModifyActivity extends BaseActivity {
	GroupModifyFragment fragment = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_fragment);
		Bundle group = getIntent().getExtras();
		fragment = new GroupModifyFragment();
		fragment.setArguments(group);
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.add(R.id.frame_container, fragment);
		ft.commit();
	}
	
}