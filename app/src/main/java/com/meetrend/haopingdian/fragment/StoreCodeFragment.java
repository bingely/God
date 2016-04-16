package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.MeDetail;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ProgressDialog;

public class StoreCodeFragment extends BaseFragment {
	
	
	//
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	//
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	//
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	
	@ViewInject(id = R.id.my_photo)
	ImageView mMyPhoto;
	//
	@ViewInject(id = R.id.store_code_pic)
	ImageView mStoreCodePic;
	//
	@ViewInject(id = R.id.pd_member_info)
	ProgressDialog mDialog;
	
	//----------------------------------------
	
	private MeDetail mMe = null;;
	
	private FinalBitmap loader = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_store_code, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		loader = FinalBitmap.create(mActivity);
		mMe = App.meDetail;
		
		mBarTitle.setText(R.string.store_code);
		if(mMe!=null){
			loader.display(mMyPhoto, Server.BASE_URL + mMe.avatarId);
		}
		getInfos();
		return rootView;
	}

	/**
	 * 返回
	 * @author bob
	 * */
	public void onClickHome(View v){
		mActivity.finish();
	}
	
	/**
	 * 获取个人信息
	 */
	public void getInfos() {
		mDialog.setVisibility(View.VISIBLE);
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());

		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
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
				if (isSuccess) {
					mHandler.sendEmptyMessage(Code.SUCCESS);
				} else {
					mHandler.sendEmptyMessage(Code.FAILED);
				}			
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_MY_CODE, params, callback);
	}

	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mDialog.setVisibility(View.GONE);
			switch (msg.what) {
				case Code.FAILED:{
					showToast("操作失败,请重试。。。");
					break;
				}
				case Code.SUCCESS:{
					break;
				}
			}
		}
	};

}
