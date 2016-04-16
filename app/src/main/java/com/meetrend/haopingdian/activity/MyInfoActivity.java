package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.event.MeChangeEvent;
import com.meetrend.haopingdian.fragment.AboutFragment;
import com.meetrend.haopingdian.fragment.MeInfoFragment;
import com.meetrend.haopingdian.fragment.UserPwdModifyFragment;

import de.greenrobot.event.EventBus;


public class MyInfoActivity extends BaseActivity {
	
	public static final String type = "type";
	
	public static final int TYPE_MY_INFO = 1;
	public static final int TYPE_MY_PHOTO = 2;
	public static final int TYPE_PWD_MODIFY = 4;
	public static final int TYPE_ABOUT_DAYI = 6;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_fragment);
		Intent intent = getIntent();
		
		int actionType = intent.getIntExtra(type, 0);
		Fragment fragment = null;
		switch(actionType){

			//登录用户详情
			case TYPE_MY_INFO:
			case TYPE_MY_PHOTO:
				fragment = new MeInfoFragment();
				FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
				ft.add(R.id.frame_container, fragment);
				ft.commit();
				break;

			//用户登录密码修改
			case TYPE_PWD_MODIFY:{
				fragment = new UserPwdModifyFragment();
				FragmentTransaction ftModify = this.getSupportFragmentManager().beginTransaction();
				ftModify.add(R.id.frame_container, fragment);
				ftModify.commit();
				break;
			}

			//关于
			case TYPE_ABOUT_DAYI:{
				fragment = new AboutFragment();
				FragmentTransaction ftAbout = this.getSupportFragmentManager().beginTransaction();
				ftAbout.add(R.id.frame_container, fragment);
				ftAbout.commit();
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().post(new MeChangeEvent());
	}
	
	
}