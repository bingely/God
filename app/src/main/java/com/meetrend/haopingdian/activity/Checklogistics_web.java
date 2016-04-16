package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class Checklogistics_web extends BaseActivity {

    private WebView myWebview;
    private ImageView mHome;
    private String webUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklogistics_web);
        TextView titleview = (TextView) this.findViewById(R.id.actionbar_title);
        myWebview = (WebView) this.findViewById(R.id.my_webview);
        mHome = (ImageView) findViewById(R.id.actionbar_homeddd);
        titleview.setText("物流跟踪");
        mHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        getCheckLogisticsDetail();
    }

    public void homeClick(View view) {
        finish();
    }

    @Override
    public void handleMessage(Message msg) {

        super.handleMessage(msg);
        switch (msg.what) {
            case Code.FAILED: {
                showToast("操作失败,请重试...");
                break;
            }
            case Code.SUCCESS: {

                WebSettings settings = myWebview.getSettings();
                settings.setSupportZoom(true);
                settings.setBuiltInZoomControls(true);
                settings.setJavaScriptEnabled(true); //设置WebView属性，能够执行Javascript脚本

                myWebview.loadUrl(webUrl + "&isApp=1");
                myWebview.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                        view.loadUrl(url);
                        return true;
                    }
                });
                break;
            }
        }

    }

    public void getCheckLogisticsDetail() {
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(Checklogistics_web.this)
                .getToken());
        params.put("code", App.onlineOrderDetail.expressCode);
        params.put("wuliu", "");

        Callback callback = new Callback(tag, this) {
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
                isSuccess = Boolean.parseBoolean(root.get("success")
                        .getAsString());
                if (isSuccess) {
                    webUrl = data.get("url").getAsString();
                    mHandler.sendEmptyMessage(Code.SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(Code.FAILED);
                }
            }
        };

        FinalHttp finalHttp = new FinalHttp();
        finalHttp
                .get(Server.BASE_URL + Server.CHECKLOGISTICS, params, callback);
    }

}