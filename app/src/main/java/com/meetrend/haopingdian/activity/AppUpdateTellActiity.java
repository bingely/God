package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ProgressWebView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class AppUpdateTellActiity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	
	@ViewInject(id = R.id.my_web)
	ProgressWebView mMyWeb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_update_tell);
		FinalActivity.initInjectedView(this);
		mBarTitle.setText("升级公告");
		
		WebSettings settings = mMyWeb.getSettings();
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true);

//		http://weixin.meetrend.com/test1/Wx.WxUpgradeNotice.jdp
		mMyWeb.loadUrl("http://www.haopingdian.cn/saas/Wx.WxUpgradeNotice.jdp" +
				"?token=" + SPUtil.getDefault(AppUpdateTellActiity.this).getToken());

		mMyWeb.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
	            return true;
	        }
	       });
	}
	
	public void onClickHome(View v){
		finish();
	}

}