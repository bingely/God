package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.ShowShouQuanMaFragment;

public class ShouQuanMaActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_shouquanma);
		
		Intent intent = getIntent();
		
		ShowShouQuanMaFragment fragment = new ShowShouQuanMaFragment();
		Bundle bundle =  new Bundle();
		bundle.putString("sqcode", intent.getStringExtra("sqcode"));//授权码
		bundle.putString("pass", intent.getStringExtra("pass"));//获取授权码密码
		fragment.setArguments(bundle);
		
		getSupportFragmentManager().beginTransaction()
		.add(R.id.shoquanma_container, fragment)
		.commit();
		
	}
	
}