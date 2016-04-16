package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.event.FinishOrderManagerEvent;
import com.meetrend.haopingdian.fragment.ProductFastOrderFragment;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;

import de.greenrobot.event.EventBus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PayResultActivity extends BaseActivity{
	
	private final static String TAG = ProductFastOrderFragment.class.getName();

	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	/**
	 * 完成
	 */
	@ViewInject(id = R.id.finish,click = "finishClick")
	Button finishBtn;
	
	/**
	 * 显示金额
	 */
	@ViewInject(id = R.id.moneyview)
	TextView moneyView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payresult);
		FinalActivity.initInjectedView(this);
		mBarTitle.setText("操作成功");
		mBarHome.setVisibility(View.GONE);
		
		String shishouMoney = SPUtil.getDefault(PayResultActivity.this).getShiShouMoney();
		if (TextUtils.isEmpty(shishouMoney)) {
			shishouMoney = "位置";
		}
		moneyView.setText("¥"+NumerUtil.getNum(shishouMoney));
		
	}
	
	/**
	 * 完成
	 * 
	 * 跳转到管理页面，必须衔结束 OrderManagerActivity
	 * 
	 */
	public void finishClick(View view){
		Intent intent = new Intent(PayResultActivity.this, PrintActivity.class);
		startActivity(intent);
		finish();
	}
}