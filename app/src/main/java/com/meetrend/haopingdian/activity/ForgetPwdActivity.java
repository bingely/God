package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.fragment.LoginFragment;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.widget.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 修改登录密码
 *
 */
public class ForgetPwdActivity extends BaseActivity{

	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	@ViewInject(id = R.id.actionbar_action)
	TextView mBarAction;
	
	@ViewInject(id = R.id.new_pwd)
	EditText mNewPwd;
	@ViewInject(id = R.id.confim_pwd)
	EditText mConfimPwd;
	//
	@ViewInject(id = R.id.btn_pwd_ok, click = "resendClick")
	Button mPwdOk;
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		FinalActivity.initInjectedView(this);
		
		mBarTitle.setText("忘记密码");
		intent = getIntent();
	}
	
	
	public void resendClick(View view) {
		
		String password = mConfimPwd.getText().toString();
		String newPwd = mNewPwd.getText().toString();
		if(!password.equals(newPwd)){
			showToast("密码不一致");
			return;
		}
		if (password.equals("")) {
			showToast("密码不能为空");
			return;
		}

		showDialog("正在重置...");
		AjaxParams params = new AjaxParams();
		params.put("account", intent.getStringExtra("account"));
		params.put("resetPassword", password);

		Callback callback = new Callback(tag,this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (!isSuccess) {
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}
					return;
				}

				showToast("重置密码成功");
				startActivity(new Intent(ForgetPwdActivity.this,AccountActivity.class));
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (strMsg != null) {
					showToast(strMsg);
				}else {
					showToast("获取验证码失败");
				}
			}
		};

		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.RESET_PASSWORD_URL, params, callback);
		//mHandler.sendEmptyMessage(Code.INIT);
	}
	
	public void onClickHome(View v){
		this.finish();
	}
	
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.push_right_in,0);
	}
	

}