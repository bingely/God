package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.ProductFastOrderFragment;
import android.os.Bundle;

/**
 * 		从订单详情进入（立即付款）
 * 		intent.putExtra("from_pay", 1);//标识从改界面跳转进行支付
 * 		intent.putExtra("fromdetail", 1);//标识
 * 
 * @author 肖建斌
 * 
 * 
 *
 */
public class QuikPayActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quikpay);
		FinalActivity.initInjectedView(this);
		
		int fromPay = getIntent().getIntExtra("from_pay", -1);
		int fromDetail = getIntent().getIntExtra("fromdetail", -1);
		
		ProductFastOrderFragment orderTeaDetailFragment = new ProductFastOrderFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("from_pay", fromPay);
		bundle.putInt("fromdetail", fromDetail);
		orderTeaDetailFragment.setArguments(bundle);
		
		getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.quikpay_container, orderTeaDetailFragment)
		.commit();
	}
}