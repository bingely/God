package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ForgetPwdActivity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.util.DialogUtil;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;
import com.meetrend.haopingdian.util.DialogUtil.FinishListener;
/**
 * 修改授权密码 之   获取验证码并校验
 * 
 * @author 肖建斌
 *
 */
public class ModifySqmGetYZMFragment extends BaseFragment {
	
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	
	/**
	 * 用户名框
	 */
	@ViewInject(id = R.id.et_account)
	EditText mAccount;
	
	/**
	 * 下一步按钮
	 */
	@ViewInject(id = R.id.btn_next,click = "actionClick")
	Button mNext;
	
	/**
	 * 发送验证码
	 */
	@ViewInject(id = R.id.btn_send_captcha,click = "captchaClick")
	Button mcaptcha;
	
	/**
	 * 验证码编辑框
	 */
	@ViewInject(id = R.id.et_captcha)
	EditText mEcaptcha;
	
	/**
	 * 时间显示layout
	 */
	@ViewInject(id = R.id.ll_time)
	LinearLayout time_layout;
	
	/**
	 * 时间数值显示
	 */
	@ViewInject(id = R.id.tv_countdown)
	TextView mCountdown;
	
	/**
	 * 用户名框删除按钮
	 */
	@ViewInject(id = R.id.iv_clear,click = "clearClick")
	ImageButton mClear;
	
	/**
	 * 验证码框删除按钮
	 */
	@ViewInject(id = R.id.iv_clear1,click = "clearClick1")
	ImageButton mClear1;
	
	String randNumber;
	String account;
	String mobile;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity(); 
		View rootView = inflater.inflate(R.layout.fragment_modify_sqm_get_yzm, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		init();
		
		return rootView;
	}
	
	private void init(){
		
		mTitle.setText("修改授权密码");
		time_layout.setVisibility(View.GONE);
		
		//用户名编辑框监听
		mAccount.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				
				if(mAccount.getText().length() == 0){
					mClear.setVisibility(View.GONE);
				}else{
					mClear.setVisibility(View.VISIBLE);
				}
			}
		});
		
		//验证码编辑框监听
		mEcaptcha.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				
				if(mEcaptcha.getText().length() == 0){
					mClear1.setVisibility(View.GONE);
				}else{
					mClear1.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.FAILED:
				dimissDialog();
				
				if (msg.obj == null) {
					showToast("获取验证码失败");
				} else {
					showToast((String) msg.obj);
				}
				break;
			case Code.COUNTDOWN:
				if (msg.arg1 == 0) {
					time_layout.setVisibility(View.GONE);
					mcaptcha.setVisibility(View.VISIBLE);
					return;
				}
				mCountdown.setText(msg.arg1+"");
				time_layout.setVisibility(View.VISIBLE);
				mcaptcha.setVisibility(View.GONE);
				break;
			case Code.COUNTTIME:
				if (msg.arg1 == 0) {
					randNumber = "";
					return;
				}
				break;
			}
		}
		
	};
	
	/**
	 * 发送验证码按钮的click事件
	 * @param v
	 */
	public void captchaClick(View v){
		final String account = mAccount.getText().toString();
		total = 120000;
		if (TextUtils.isEmpty(account)) {
			showToast(R.string.str_empty_phonenumber);
			return;
		}
		getAccount();
	}
	
	/**
	 * 下一步操作
	 * @param view
	 */
	public void actionClick(View view) {
		
//		String captcha = mEcaptcha.getText().toString();//获取编辑框的验证码
//		
//		if(TextUtils.isEmpty(mEcaptcha.getText().toString())){
//			showToast("请输入验证码");
//			return;
//		}else if(mEcaptcha.getText().toString().equals(randNumber)){
//			
//			mActivity.getSupportFragmentManager().beginTransaction()
//			.add(R.id.shoquanma_container, new ModifySqmPassFragment())
//			.addToBackStack(null)
//			.commit();
//			
//		}else{
//			showToast("验证码不正确");
//		}
		//临时
		mActivity.getSupportFragmentManager().beginTransaction()
		.add(R.id.shoquanma_container, new ModifySqmPassFragment())
		.addToBackStack(null)
		.commit();
	}
	
	/**
	 * 用户名删除按钮事件
	 * @param v
	 */
	public void clearClick(View v){
		mAccount.getText().clear();
	}
	
	
	/**
	 * 验证码删除按钮事件
	 * @param v
	 */
	public void clearClick1(View v){
		mEcaptcha.getText().clear();
	}
	
	
	
	
	
	/**
	 * 10分钟后验证码不可使用
	 */
	
	private int mtotal = 600000;
	private Runnable mtimerTask = new Runnable() {
		private static final int ONE_SECOND = 1000;

		@Override
		public void run() {
	
			while (true) {
				try {
					Thread.sleep(ONE_SECOND);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mtotal -= ONE_SECOND;
				Message msg = new Message();
				msg.what = Code.COUNTTIME;
				msg.arg1 = mtotal / 1000;
				mHandler.sendMessage(msg);
				if (mtotal == 0) {
					break;
				}
			}
		}
	};
	
	
	/**
	 * 2分钟后可再点击
	 */
	private int total = 120000;
	private Runnable timerTask = new Runnable() {
		private static final int ONE_SECOND = 1000;

		@Override
		public void run() {
	
			while (true) {
				try {
					Thread.sleep(ONE_SECOND);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				total -= ONE_SECOND;
				Message msg = new Message();
				msg.what = Code.COUNTDOWN;
				msg.arg1 = total / 1000;
				mHandler.sendMessage(msg);
				if (total == 0) {
					break;
				}
			}
		}
	};
	
	
	
	/**
	 * 获取验证码
	 */
	private void getAccount(){
		
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("account", mAccount.getText().toString());
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.i(tag, "get identify code : " + t);
				if (!isSuccess) {
					mHandler.sendEmptyMessage(Code.FAILED);
					return;
				}else{
					dimissDialog();
				}
				
				JsonElement msg = data.get("msg");
				if (msg == null) {
					dimissDialog();
					
					account = data.get("account").getAsString();
					randNumber = data.get("randNumber").getAsString();
					mobile = data.get("mobile").getAsString();
					DialogUtil dialog = new DialogUtil(mActivity);
					dialog.findpwdDialog("已将验证码短信发送至\n"+mobile, "取消", "确认");
					dialog.setListener(new FinishListener() {
						
						@Override
						public void finishView() {
							new Thread(timerTask).start();
							new Thread(mtimerTask).start();
						}
					});
					
				}else{
					showToast(msg.toString());
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}			
		};
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.FORGET_PASSWORD_URL, params, callback);
		
		dimissDialog();
	}
	
	
	/**
	 * 返回
	 * @param view
	 */
	public void homeClick(View view){
		getActivity().getSupportFragmentManager().popBackStack();
	}

}