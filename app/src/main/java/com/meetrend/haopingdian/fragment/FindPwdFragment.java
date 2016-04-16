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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.BaseActivity;
import com.meetrend.haopingdian.activity.ForgetPwdActivity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.util.DialogUtil;
import com.meetrend.haopingdian.util.DialogUtil.FinishListener;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;

/**
 * 修改密码
 *
 */
public class FindPwdFragment extends BaseFragment {
	
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_action)
	TextView mAction;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	
	@ViewInject(id = R.id.et_account)
	EditText mAccount;
	@ViewInject(id = R.id.btn_next,click = "actionClick")
	Button mNext;
	@ViewInject(id = R.id.btn_send_captcha,click = "captchaClick")
	Button mcaptcha;
	@ViewInject(id = R.id.et_captcha)
	EditText mEcaptcha;
	@ViewInject(id = R.id.ll_time)
	LinearLayout time_layout;
	@ViewInject(id = R.id.tv_countdown)
	TextView mCountdown;
	
	@ViewInject(id = R.id.iv_clear,click = "clearClick")
	ImageButton mAcountClear;
	
	
	
	@ViewInject(id = R.id.shanghu_edit)
	EditText mShEdit;
	
	@ViewInject(id = R.id.sh_clear_btn,click = "shClearClick")
	ImageButton mShClearBtn;
	
	private boolean isContinue = true;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity(); 
		View rootView = inflater.inflate(R.layout.fragment_findpwd, container, false);
		FinalActivity.initInjectedView(this, rootView);
		mTitle.setText("忘记密码");
		time_layout.setVisibility(View.GONE);
		initView();
		return rootView;
	}
	
	public void initView(){
		
		mAccount.addTextChangedListener(new TextWatcherChangeListener() {
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
				if(mAccount.getText().length() == 0){
					mAcountClear.setVisibility(View.GONE);
				}else{
					mAcountClear.setVisibility(View.VISIBLE);
				}
			}
		});
		
		mShEdit.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable arg0) {
				
				if(mShEdit.getText().length() == 0){
					mShClearBtn.setVisibility(View.GONE);
				}else{
					mShClearBtn.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	public void clearClick(View v){
		mAccount.getText().clear();
	}
	
	public void shClearClick(View v){
		mShEdit.getText().clear();
	}
	

	public void homeClick(View view) {
		mActivity.finish();
	}
	
	//发送验证码
	public void captchaClick(View v){
		
		String account = mAccount.getText().toString();
		total = 120000;
		
		if (TextUtils.isEmpty(account)) {
			showToast("请输入用户名");
			return;
		}
		
		if (TextUtils.isEmpty(mShEdit.getText().toString())) {
			showToast("请输入商户号");
			return;
		}
		
		getServerPath(mShEdit.getText().toString());
	}
	
	//下一步
	public void actionClick(View view) {
		
		String captcha = mEcaptcha.getText().toString();
		
		if(TextUtils.isEmpty(mEcaptcha.getText().toString())){
			showToast("请输入验证码");
			return;
		}
		
		if(mEcaptcha.getText().toString().equals(randNumber)){
			Intent intent = new Intent(getActivity(),ForgetPwdActivity.class);
			intent.putExtra("account", account);
			//((BaseActivity) getActivity()).personalStartActivity(intent);
		}else{
			showToast("验证码不正确");
		}
		
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
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
				
			case REQUEST_SERVERPATH_SUCCESS:
				
				getAccount();
				break;
			}
		}
		
	};
	
	String randNumber;
	String account;
	String mobile;
	
	private void getAccount(){
		
		AjaxParams params = new AjaxParams();
		params.put("account", mAccount.getText().toString());
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (!isSuccess) {
					
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}
					return;
					
				}else {
					
					account = data.get("account").getAsString();
					randNumber = data.get("randNumber").getAsString();
					mobile = data.get("mobile").getAsString();
					DialogUtil dialog = new DialogUtil(mActivity);
					dialog.findpwdDialog("已将验证码短信发送至\n" + mobile, "取消", "确认");
					
					dialog.setListener(new FinishListener() {
						
						@Override
						public void finishView() {
							new Thread(timerTask).start();
							new Thread(mtimerTask).start();
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (strMsg != null) {
					
					showToast(strMsg);
				}else {
					
					showToast("获取验证码失败");
				}
				
			}			
		};
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.FORGET_PASSWORD_URL, params, callback);
	}
	
	private int mtotal = 600000;
	/**
	 * 10分钟后验证码不可使用
	 */
	private Runnable mtimerTask = new Runnable() {
		private static final int ONE_SECOND = 1000;

		@Override
		public void run() {
	
			while (isContinue) {
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
	
	private int total = 120000;
	/**
	 * 2分钟后可再点击
	 */
	private Runnable timerTask = new Runnable() {
		private static final int ONE_SECOND = 1000;

		@Override
		public void run() {
	
			while (isContinue) {
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
	
	private void requestByPost(String account) {
		
		AjaxParams params = new AjaxParams();
		params.put("account", account);
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.i(tag, "get identify code : " + t);
				if (!isSuccess) {
					mHandler.sendEmptyMessage(Code.FAILED);
					return;
				}else{
				}
				
				JsonElement msg = data.get("msg");
				if (msg == null) {
					Bundle bundle = new Bundle();
					bundle.putString("account",  data.get("account").getAsString());
					bundle.putString("randNumber", data.get("randNumber").getAsString());
					showToast("验证码:"+data.get("randNumber").getAsString());
					FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
					Fragment fragment = new IndentifyCodeFragment();
					fragment.setArguments(bundle);
					ft.add(R.id.frame_container, fragment);
					ft.addToBackStack(null);
					ft.commit();
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
	}
	
	/**
	 * 通过商户号获取服务器地址
	 * @param businessNum 商户号
	 */
	
	private String busynessUrl;
	private final static int REQUEST_SERVERPATH_SUCCESS = 0X157;
	
	private void getServerPath(String businessNum){
		
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("number", businessNum);
		
		Callback callback = new Callback(tag,getActivity()) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				dimissDialog();
				
				if (isSuccess) {
					busynessUrl = data.get("host").getAsString();
					
					if (busynessUrl.lastIndexOf("/") != busynessUrl.length()-1) {
						busynessUrl = busynessUrl +"/";
					}
					
					Server.BASE_URL = busynessUrl;
					
					mHandler.sendEmptyMessage(REQUEST_SERVERPATH_SUCCESS);
				}else {
					if (data.has("msg")) 
						showToast(data.get("msg").getAsString());
					else 
					showToast("获取商户号失败");
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
		
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BUSINESS_URL, params, callback);
		
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isContinue = false;
	}
}