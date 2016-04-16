package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;

public class IndentifyCodeFragment extends BaseFragment {
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_action, click = "actionClick")
	TextView mAction;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;

	@ViewInject(id = R.id.et_identify_code)
	EditText mCode;
	@ViewInject(id = R.id.btn_resend, click = "resendClick")
	Button mResend;
	@ViewInject(id = R.id.tv_countdown)
	TextView mCountDown;

	@ViewInject(id = R.id.layout_code_wait)
	ViewGroup mWait;
	
	private Bundle mBundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBundle = this.getArguments();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_indentify_code, container, false);
		FinalActivity.initInjectedView(this, rootView);
		mTitle.setText("重置密码");
		mAction.setText("下一步");
		mResend.setEnabled(false);
		new Thread(timerTask).start();
		return rootView;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.COUNTDOWN:
				if (msg.arg1 == 0) {
					mResend.setEnabled(true);
				}
				mCountDown.setText(msg.arg1 + "");
				break;
			case Code.FAILED:
				mWait.setVisibility(View.GONE);
				if (msg.obj == null) {
					showToast("获取验证码失败");
				} else {
					showToast((String) msg.obj);
				}
				break;
			}

		}
	};

	private Runnable timerTask = new Runnable() {
		private int total = 60000;
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

	public void homeClick(View view) {
		mActivity.getSupportFragmentManager().popBackStack();
	}

	public void resendClick(View view) {
		AjaxParams params = new AjaxParams();
		LogUtil.d(tag, "resendClick : " + mBundle.getString("account"));
		params.put("account", mBundle.getString("account"));

		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "get identify code : " + t);
				if (!isSuccess) {
					mHandler.sendEmptyMessage(Code.FAILED);
					return;
				}

				JsonElement randNumber = data.get("randNumber");
				JsonElement msg = data.get("msg");
				if (randNumber == null) {
					Message message = new Message();
					message.obj = msg.getAsString();
					message.what = Code.FAILED;
					mHandler.sendMessage(message);
				} else if (msg == null) {
					mBundle.putString("randNumber", randNumber.getAsString());
					mWait.setVisibility(View.GONE);
					showToast(R.string.request_success);
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
		mWait.setVisibility(View.VISIBLE);
	}

	public void actionClick(View view) {
		String code = mCode.getText().toString();
		String randNumber = mBundle.getString("randNumber");
		LogUtil.d(tag, "actionClick : " + randNumber);
		if (!code.equals(randNumber)) {
			showToast("验证码输入错误!");
			return;
		}

		mWait.setVisibility(View.GONE);
		FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
		Fragment fragment = new ResetPwdFragment();
		fragment.setArguments(mBundle);
		ft.add(R.id.frame_container, fragment);
		ft.addToBackStack(null);
		ft.commit();
	}
}
