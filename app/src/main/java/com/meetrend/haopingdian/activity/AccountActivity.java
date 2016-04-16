package com.meetrend.haopingdian.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ViewDragHelper;
import android.view.Window;
import android.widget.Button;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.LoginFragment;
import com.meetrend.haopingdian.tool.Utils;

import java.util.jar.JarEntry;

public class AccountActivity extends BaseActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_fragment);

		tintManager.setStatusBarTintResource(R.color.white);
		Utils.getScreenWidth(this);
		Utils.getScreenHeight(this);

		//String signType = getIntent().getStringExtra(LoginFragment.SIGNUP);

		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		Fragment fragment = new LoginFragment();
		//Bundle bundle = new Bundle();
		//bundle.putString(LoginFragment.SIGNUP,signType);
		//fragment.setArguments(bundle);
		ft.replace(R.id.frame_container, fragment);
		ft.commit();
	}
}