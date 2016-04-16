package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.AccountActivity;
import com.meetrend.haopingdian.bean.MeDetail;
import com.meetrend.haopingdian.bean.StoreInfo;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.memberlistdb.StoreInfoOpetate;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.DaYiActivityManager;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.Utils;
import com.meetrend.haopingdian.widget.ProgressDialog;

/**
 * 修改登录密码成功
 */
public class UserPwdModifyFragment extends BaseFragment {
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	@ViewInject(id = R.id.actionbar_action)
	TextView mBarAction;
	@ViewInject(id = R.id.old_pwd)
	EditText mOldPwd;
	@ViewInject(id = R.id.new_pwd)
	EditText mNewPwd;
	@ViewInject(id = R.id.confim_pwd)
	EditText mConfimPwd;
	@ViewInject(id = R.id.pwd_ok, click = "onClicKPwdOk")
	TextView mPwdOk;
	
	private MeDetail mMe = null;
	private SPUtil mUtil = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_pwd_modify, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		mMe = App.meDetail;
		
		mBarTitle.setText(R.string.pwd_modify);
		try{
			Bundle bundle = getArguments();
			 String page = bundle.getString("page");
			 if(!TextUtils.isEmpty(page)){
				 if(page.equals("forget")){
					 mBarTitle.setText("忘记密码");
				 }
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		mUtil = SPUtil.getDefault(mActivity);
		return rootView;
	}

	public void onClickHome(View v){
		mActivity.finish();
	}

	public boolean checkArgs(int type,EditText pEidtText){
		if (TextUtils.isEmpty(pEidtText.getText().toString())){
			switch (type){
				case 1:
					showToast("旧密码不能为空");
					break;
				case 2:
					showToast("新密码不能为空");
					break;
				case 3:
					showToast("确认密码不能为空");
					break;
			}
			return false;
		}else{
			if (pEidtText.getText().length()<6){
				switch (type){
					case 1:
						showToast("旧密码不能小于6位数");
						break;
					case 2:
						showToast("新密码不能小于6位数");
						break;
					case 3:
						showToast("确认密码不能小于6位数");
						break;
				}
				return false;
			}

		}
		return true;
	}
	

	public void onClicKPwdOk(View v) {

		if (!checkArgs(1,mOldPwd)){
			return;
		}
		if (!checkArgs(2,mNewPwd)){
			return;
		}
		if (!checkArgs(3,mConfimPwd)){
			return;
		}

		showDialog("请稍候...");
		AjaxParams params = new AjaxParams();
		params.put("token",  SPUtil.getDefault(getActivity()).getToken());
		params.put("loginName", mUtil.getLoginName());
		params.put("currentPassword",mNewPwd.getText().toString());
		params.put("pastPassword", mOldPwd.getText().toString());
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
				mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.d(tag, "  my info : " + t);
				JsonParser parser = new JsonParser();
            	JsonObject root = parser.parse(t).getAsJsonObject();
            	data = root.get("data").getAsJsonObject();
            	LogUtil.d(tag, "success " + root.get("success").getAsString());
            	isSuccess = Boolean.parseBoolean(root.get("success").getAsString());

				dimissDialog();
				if (isSuccess) {
					showToast("登录密码修改成功");
					StoreInfoOpetate.getInstance().upDateStoreLoginPwd(getActivity(), mNewPwd.getText().toString(),App.storeInfo);
					SPUtil.getDefault(mActivity).savePwdstatus("");
					DaYiActivityManager.getWeCareActivityManager().popAllActivityExceptOne(null);
					context.startActivity(new Intent("com.meetrend.account"));
				} else {
					if(data.get("msg")!=null){
						String errorMsg = data.get("msg").toString();
						showToast(errorMsg);
					}
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.UPDATE_PWSSWORD, params, callback);
	}
}
