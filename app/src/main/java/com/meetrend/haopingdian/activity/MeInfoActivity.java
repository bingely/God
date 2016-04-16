package com.meetrend.haopingdian.activity;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.MeInfoFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class MeInfoActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meinfo);
		
		String userid = getIntent().getStringExtra("id");
		
		Bundle bundle = new Bundle();
		bundle.putString("id", userid);
		MeInfoFragment meInfoFragment = new MeInfoFragment();
		meInfoFragment.setArguments(bundle);
				
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.meinfocontainer, meInfoFragment);
		ft.commit();
	}

}