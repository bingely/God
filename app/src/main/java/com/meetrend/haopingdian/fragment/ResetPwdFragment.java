package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;

public class ResetPwdFragment extends BaseFragment {
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_action, click = "actionClick")
	TextView mAction;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;

	@ViewInject(id = R.id.et_reset_pwd)
	TextView mPwd;
	@ViewInject(id = R.id.layout_reset_wait)
	ViewGroup mWait;

	private Bundle mBundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBundle = this.getArguments();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reset_pwd, container, false);
		FinalActivity.initInjectedView(this, rootView);
		mActivity = this.getActivity();
		mTitle.setText("重置密码");
		mAction.setText("完成");
		return rootView;
	}

	public void homeClick(View view) {
		mActivity.getSupportFragmentManager().popBackStack();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.FAILED:
				mWait.setVisibility(View.GONE);
				showToast("重置密码失败");
				break;
			case Code.INIT:
				mWait.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	public void actionClick(View view) {
		String password = mPwd.getText().toString();
		if (password.equals("")) {
			showToast("密码不能为空");
			return;
		}

		AjaxParams params = new AjaxParams();
		params.put("account", mBundle.getString("account"));
		params.put("resetPassword", password);

		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.i(tag, "reset password : " + t);
				if (!isSuccess) {
					Message msg = new Message();
					msg.what = Code.FAILED;
					msg.obj = data.get("msg").getAsString();
					mHandler.sendMessage(msg);
					return;
				}

				mWait.setVisibility(View.GONE);
				showToast("重置密码成功");
				FragmentManager manager = mActivity.getSupportFragmentManager();
				manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				Fragment fragment = new LoginFragment();
				FragmentTransaction ft = manager.beginTransaction();
				ft.add(R.id.frame_container, fragment);
				ft.commit();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.RESET_PASSWORD_URL, params, callback);
		mHandler.sendEmptyMessage(Code.INIT);
	}
}
