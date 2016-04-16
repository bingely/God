package com.meetrend.haopingdian.activity;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.StorePrintFragment;

import android.os.Bundle;

/**
 * 打印小票的宿主
 * @author 肖建斌
 *
 */
public class PrintActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print);
		
		getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.print_container, new StorePrintFragment()).commit();
	}

}