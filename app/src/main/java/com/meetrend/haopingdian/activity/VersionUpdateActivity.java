package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.meetrend.haopingdian.R;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class VersionUpdateActivity extends BaseActivity {

	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.my_webview)
	WebView myWebview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_update);
		FinalActivity.initInjectedView(this);
		mTitle.setText("版本更新");
		myWebview.loadUrl(getIntent().getStringExtra("url"));
		myWebview.getSettings().setJavaScriptEnabled(true);
		myWebview.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});
	}
	
	public void homeClick(View v){
		this.finish();
	}

}