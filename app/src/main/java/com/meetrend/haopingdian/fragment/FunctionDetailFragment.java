package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ProgressDialog;
/**
 * 功能介绍
 *
 */
public class FunctionDetailFragment extends BaseFragment {
	
	
	//
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	//
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	//
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	//
	@ViewInject(id = R.id.my_web)
	WebView mMyWeb;

	String webUrl = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_function_detail, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		mBarTitle.setText(R.string.title_function_detail);
		mBarAction.setVisibility(View.GONE);
		
		getFunctionDetail();
		
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
	 * 提交
	 */
	public void getFunctionDetail() {
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		

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
            	isSuccess = root.get("success").getAsBoolean();
				if (isSuccess) {
					webUrl = data.get("url").getAsString();
					mHandler.sendEmptyMessage(0x258);
				} else {
					mHandler.sendEmptyMessage(0x259);
				}			
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.FUNCTION_DETAIL, params, callback);
	}
	
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0x259:{
					showToast("操作失败,请重试...");
					break;
				}
				case 0x258:{
					mMyWeb.loadUrl(webUrl);
					break;
				}
			}
		}
	};

}
