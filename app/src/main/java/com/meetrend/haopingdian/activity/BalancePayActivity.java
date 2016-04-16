package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.fragment.ProductFastOrderFragment;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 余额支付
 * @author 肖建斌
 */
public class BalancePayActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home, click = "finishActivity")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	//商品金额
	@ViewInject(id = R.id.balance_all_product_money)
	TextView productMoneyTv;
	//优惠金额
	@ViewInject(id = R.id.balance_youhuiquan_money)
	TextView yhMoneyTv;
	
	//积分金额
	@ViewInject(id = R.id.balance_points)
	TextView jfMoneyTv;
	
	//合计金额
	@ViewInject(id = R.id.balance_heji)
	TextView hjMoneyTv;
	
	//应收金额
	@ViewInject(id = R.id.balance_yingshou)
	TextView ysMoneyTv;
	
	//账户余额
	@ViewInject(id = R.id.zhanghu_balance)
	TextView zhMoneyTv;
	
	//付款码
	@ViewInject(id = R.id.fkm_view)
	EditText fkmEdit;
	
	//timebtn
	@ViewInject(id = R.id.timebutton,click = "timeBtnClick")
	TextView timeBtn;
	
	//提交按钮
	@ViewInject(id = R.id.commitbtn,click = "commitbtnClick")
	Button commitBtn;
	
	//timeBtn是否是重新发送模式 true 重新发送，false 记时
	private boolean isSendAgain;
	
	private int time;
	
	private Thread timeThread = null;
	
	//订单id
	private String orderId;
	
	//应收money
	private String ysMoney;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balancepay);
		FinalActivity.initInjectedView(this);
		mBarTitle.setText(getResources().getString(R.string.title_balancepay_txt));
		
		Intent intent = getIntent();
		orderId = intent.getStringExtra("orderid");
		//都保留两位小数
		productMoneyTv.setText(intent.getStringExtra("total"));
		
		String yhMoney = intent.getStringExtra("discountAmount");
		//Log.i("---------------BalancePayActivity", yhMoney == null ? "为空" : yhMoney);
		yhMoneyTv.setText(yhMoney);
		
		jfMoneyTv.setText(intent.getStringExtra("integralamount"));
		hjMoneyTv.setText(intent.getStringExtra("incomeAmount"));
		ysMoney = intent.getStringExtra("yishou");
		ysMoneyTv.setText(ysMoney);
		zhMoneyTv.setText(intent.getStringExtra("ye"));
		
		timeThread = new TimeThread();
		timeThread.start();
	}
	
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 0:
				 //Log.i("--------------------time -------------", time +"S");
				 timeBtn.setText(time +"S");
				break;
			case 1:
				timeBtn.setText("重新发送");
				break;
			case 3:
				 time = 0;
				 isSendAgain = false;
				 
				 timeThread = new TimeThread();
				 timeThread.start();
				break;

			default:
				break;
			}
			 
		}
	};
	
	
	public void timeBtnClick(View view){
		
		//不是重新发送模式就取消点击效果
		if (!isSendAgain) {
			return;
		}
		
		aginGetPayCode();
	}
	
	//支付
	public void commitbtnClick(View view){
		yuePayRequest();
	}
	
	public void finishActivity(View view){
		finish();
	}
	
	
	public class TimeThread extends Thread{
		
		@Override
		public void run() {
			
			 while (!isSendAgain) {
				 
				 	// 中断线程
//					if (isInterrupted()) {
//						return;
//					}
				   
				   try {
					   Thread.sleep(1000);
				    } catch (InterruptedException e) {
				    	e.printStackTrace();
				    }
				   ++ time;
				   handler.sendEmptyMessage(0);
				   
				   if (time == 60) {
					   isSendAgain = true;
					   handler.sendEmptyMessage(1);
				   }
				   
					
				   
				}
			}
	}
	
	//余额支付
	private void yuePayRequest(){
		
		if (fkmEdit.getText().toString().trim().equals("")) {
			showToast("请输入付款码");
			return;
		}
		
		showDialog("支付中...");
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(BalancePayActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(BalancePayActivity.this).getStoreId());
		params.put("payCode", fkmEdit.getText().toString().trim());
		params.put("orderId", orderId);
		
		Callback callback = new Callback(tag, BalancePayActivity.this) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					
					showToast("支付成功");
					SPUtil.getDefault(BalancePayActivity.this).saveShiShouMoney(ysMoney);
					Intent intent = new Intent(BalancePayActivity.this, PayResultActivity.class);
					startActivity(intent);
					finish();
					
				}else {
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}
				}
				
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
			
		};
		
		CommonFinalHttp commonFinalHttp = new CommonFinalHttp();
		commonFinalHttp.get(Server.BASE_URL + Server.YUEPAY, params, callback);
		
	}
	
	private void aginGetPayCode(){
		
		showDialog("获取中...");
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(BalancePayActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(BalancePayActivity.this).getStoreId());
		params.put("orderId", orderId);
		
		Callback callback = new Callback(tag, BalancePayActivity.this) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					
					showToast("正在发送...");
					handler.sendEmptyMessage(3);
					
				}else {
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}
				}
				
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
			
		};
		
		CommonFinalHttp commonFinalHttp = new CommonFinalHttp();
		commonFinalHttp.get(Server.BASE_URL + Server.AGIN_GET_PAYCODE, params, callback);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//中断线程
//		if (null != timeThread) {
//			timeThread.interrupted();
//			timeThread = null;
//		}
		
		if (!isSendAgain) {
			isSendAgain = true;
		}
	}

}