package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ProgressWebView;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 始终签到
 * @author 肖建斌
 *
 */
public class FuncationActivity extends BaseActivity{
	
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
		setContentView(R.layout.activity_lookevent);
		FinalActivity.initInjectedView(this);
		mBarTitle.setText("功能介绍");
		
		WebSettings settings = mMyWeb.getSettings(); 
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true); //设置WebView属性，能够执行Javascript脚本
		mMyWeb.loadUrl(Server.BASE_URL + Server.FUNCATION_DES +"?token=" + SPUtil.getDefault(FuncationActivity.this).getToken());
		
		mMyWeb.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	              //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	             view.loadUrl(url);
	            return true;
	        }
	       });
	}
	
	public void onClickHome(View v){
		finish();
	}

}