package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.AppUpdateTellActiity;
import com.meetrend.haopingdian.activity.SystemFunctionActivity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.SystemUtil;

/**
 * 关于
 *
 */
public class AboutFragment extends BaseFragment {
	
	private final static String TAG = AboutFragment.class.getName();
	
	//
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	//
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	//
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	
	@ViewInject(id = R.id.feedback, click = "onClickFeedBack")
	LinearLayout mFeedBack;
	//版本更新
	@ViewInject(id = R.id.version_name, click = "onClickUpdate")
	TextView mVersionName;
	/**
	 * 系统消息
	 */
	@ViewInject(id = R.id.sys_notify, click = "onClickSysNotify")
	LinearLayout mSysNotify;
	//
	@ViewInject(id = R.id.app_detail, click = "onClickAppDetail")
	LinearLayout mAppDetail;
	
	//显示版本号
	@ViewInject(id = R.id.versionnameview)
	TextView mVersionView;
	
	@ViewInject(id = R.id.update_tell, click = "updateTellClick")
	LinearLayout updateTellLayout;
	
	@ViewInject(id = R.id.update_icon)
	ImageView redIconImgView;
	
	public String versionName = "";
	public boolean isNeedUpdate = false;
	public String url = "";
	private Boolean isUpdate;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		mBarTitle.setText(R.string.about);
		
		PackageInfo pkg;
		try {
			
			 pkg = getActivity().getPackageManager().getPackageInfo(getActivity().getApplication().getPackageName(), 0);
			 mVersionView.setText("v"+pkg.versionName);
			 
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
        
		
		
		getSystemVersion();
		return rootView;
	}
	
	public void updateTellClick(View view){
		
		Intent intent = new Intent(mActivity,AppUpdateTellActiity.class);
		startActivity(intent);
	}

	/**
	 * 返回
	 * */
	public void onClickHome(View v){
		mActivity.finish();
	}
	
	/**
	 * 系统消息
	 * */
	public void onClickSysNotify(View v){
		Intent intent = new Intent(mActivity,SystemFunctionActivity.class);
		intent.putExtra(SystemFunctionActivity.type, SystemFunctionActivity.TYPE_SYS_NOTIFY);
		startActivity(intent);
		
	}
	
	/**
	 * 问题反馈
	 * */
	public void onClickFeedBack(View v){
		Intent intent = new Intent(mActivity,SystemFunctionActivity.class);
		intent.putExtra(SystemFunctionActivity.type, SystemFunctionActivity.TYPE_FEEDBACK);
		startActivity(intent);
	}
	
	
	/**
	 * 功能介绍
	 * */
	public void onClickAppDetail(View v){
		Intent intent = new Intent(mActivity,SystemFunctionActivity.class);
		intent.putExtra(SystemFunctionActivity.type, SystemFunctionActivity.TYPE_APP_DETAIL);
		startActivity(intent);
	}
	
	/**
	 * 获取系统版本
	 */
	public void getSystemVersion() {
		
		showDialog();
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
//		params.put("storeId", SPUtil.getDefault(SearchActivity.this).getStoreId());
		params.put("deviceType", "5");
		params.put("name","O2OAPP");

		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				Log.i(TAG +"version name ", t);
				
				JsonParser parser = new JsonParser();
            	JsonObject root = parser.parse(t).getAsJsonObject();
            	data = root.get("data").getAsJsonObject();
            	LogUtil.d(tag, "success " + root.get("success").getAsString());
            	isSuccess = Boolean.parseBoolean(root.get("success").getAsString());
				if (isSuccess) {
					isNeedUpdate = data.get("isNeedUpdate").getAsBoolean();
					versionName = data.get("version").getAsString(); 
					url = data.get("url").getAsString();
					mHandler.sendEmptyMessage(Code.SUCCESS);
				} else {
					mHandler.sendEmptyMessage(Code.FAILED);
				}			
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.SYSTEM_UPDATE, params, callback);
	}

	public void onClickUpdate(View v){
		if(isUpdate){
			Intent intent = new Intent();         
			intent.setAction("android.intent.action.VIEW");    
			Uri content_url = Uri.parse(url);   
			intent.setData(content_url);  
			startActivity(intent);
		}else {
			showToast("已经是最新版本");
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dimissDialog();
			switch (msg.what) {
				case Code.FAILED:{
					showToast("操作失败,请重试...");
					break;
				}
				case Code.SUCCESS:{
					String appVersion = SystemUtil.getAppVersionName(mActivity);
					isUpdate = !versionName.equals(appVersion);
					if(isUpdate){
						mVersionName.setText("下载最新版本");
						mVersionName.setTextColor(getResources().getColor(R.color.gray_1));
					}else{
						mVersionName.setText(versionName);
					}
					break;
				}
			}
		}
	};

}