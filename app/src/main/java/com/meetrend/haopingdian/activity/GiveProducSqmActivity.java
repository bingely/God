package com.meetrend.haopingdian.activity;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.InputSQMFragment;

import android.os.Bundle;

/**
 * 赠送商品 输入授权码赠送
 * 
 * @author 肖建斌
 *
 */
public class GiveProducSqmActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_giveproductsqm);
		
		
		Bundle bundle = new Bundle();
		bundle.putInt("fromtype", 1);
		InputSQMFragment inputSQMFragment = new InputSQMFragment();
		inputSQMFragment.setArguments(bundle);
		
		this.getSupportFragmentManager()
		.beginTransaction()
		//.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,R.anim.push_right_in, R.anim.push_right_out)
		.add(R.id.give_product_container,inputSQMFragment).commit();
		
	}
}