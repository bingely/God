package com.meetrend.haopingdian.activity;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.zxing.activity.CaptureActivity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeiXinPayingActivity extends BaseActivity{
	
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	@ViewInject(id = R.id.img)
	ImageView img;
	
	@ViewInject(id = R.id.time)
	TextView timeView;
	
	@ViewInject(id = R.id.hint)
	TextView hintView;
	
	private String orderid;//订单号
	private	String auth_code;//用户刷卡码
	
	private final static int MAX = 65; //65秒，最多轮询65秒
	private boolean hasResult;//已经成功返回结果
	
	int i = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weixinpaying);
		FinalActivity.initInjectedView(this);
		
		mBarTitle.setText("结果获取中...");
		
		Intent intent = getIntent();
		orderid = intent.getStringExtra("orderid");
		auth_code = intent.getStringExtra("auth_code");
		
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());// 加速减速
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setRepeatCount(-1);//循环次数
		rotateAnimation.setRepeatMode(Animation.RESTART);//循环模式
		img.setAnimation(rotateAnimation);
		rotateAnimation.startNow();//开启动画
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (i== MAX || i < MAX) {
					
					if (i == 65) {
						handler.sendEmptyMessage(1);
						break;
					}
					
					handler.sendEmptyMessage(3);
					
					
					if (hasResult) {
						
						handler.sendEmptyMessage(0);
						
						break;
					}
					
					
					if (i % 5 == 0) {
						handler.sendEmptyMessage(2);
						
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					++i;
				}
			}
		}).start();
		
		
		
	}
	
	Handler handler= new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 0:
				img.clearAnimation();
				break;
			case 1:
				Toast.makeText(WeiXinPayingActivity.this, "交易失败", 200).show();
				img.clearAnimation();
				finish();
				break;
			case 2:
				requestPayResult();
				break;
			case 3:
				timeView.setText(i +"秒");
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 根据电话号码获取客户名字
	 * 
	 */
	private void requestPayResult() {

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(WeiXinPayingActivity.this).getToken());
		params.put("orderid", orderid);
		params.put("auth_code", auth_code);

		Callback callback = new Callback(tag, WeiXinPayingActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				String msgString = data.get("msg").toString();
				
				//Toast.makeText(WeiXinPayingActivity.this, msgString, 200).show();
				
				if (isSuccess) {
					
					if (data.get("ret").getAsBoolean()) {
						hasResult = true;
						Intent intent = new Intent(WeiXinPayingActivity.this, PayResultActivity.class);
						startActivity(intent);
						finish();
					}
					
					
				}else {
					finish();
				}
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_PAY_RESULT, params,callback);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		i = 70;
		img.clearAnimation();
	}
	
	/**
	 * 返回
	 * @param view
	 */
	public void onClickHome(View view) {
		finish();
	}
	

}