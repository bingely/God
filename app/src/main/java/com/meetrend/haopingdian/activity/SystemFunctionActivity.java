package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.FeedBackFragment;
import com.meetrend.haopingdian.fragment.FunctionDetailFragment;
import com.meetrend.haopingdian.fragment.SystemNotifyFragment;

public class SystemFunctionActivity extends BaseActivity {

	
	public static final String type = "type"; 
	
    public static final int TYPE_FEEDBACK = 1;
	
	public static final int TYPE_SYS_NOTIFY = 2;
	
	public static final int TYPE_APP_DETAIL = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_fragment);
		
		Intent intent = getIntent();
		int actionType = intent.getIntExtra(type, 0);
		Fragment fragment = null;
		switch(actionType){
			case TYPE_FEEDBACK:{
				fragment = new FeedBackFragment();
				break;
			}
			
			//系统通知
			case TYPE_SYS_NOTIFY:{
				fragment = new SystemNotifyFragment();
				break;
			}

			case TYPE_APP_DETAIL:{
				fragment = new FunctionDetailFragment();
				break;
			}
		}
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.add(R.id.frame_container, fragment);
		ft.commit();
	}


	public void homeClick(View v) {
		this.finish();
	}
	
	
}