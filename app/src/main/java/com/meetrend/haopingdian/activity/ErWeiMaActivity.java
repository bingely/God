package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

public class ErWeiMaActivity extends BaseActivity{
	
	@ViewInject(id = R.id.sign_ew_layout,click ="erClick")
	RelativeLayout erLayout;
	@ViewInject(id = R.id.tg_ew_layout,click ="tgClick")
	RelativeLayout tgLayout;
	
	@ViewInject(id = R.id.actionbar_home,click ="finishActivity")
	ImageView back;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	
	private String signQrc;
	private String shareQrc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_erweima);
		FinalActivity.initInjectedView(this);
		titleView.setText("二维码");
		getQrc();
	}
	
	public void getQrc(){
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(ErWeiMaActivity.this).getToken());
		params.put("entityId", App.eventId);

		Callback callback = new Callback(tag,this) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.d(tag, "  my info : " + t);
				if (isSuccess) {
					signQrc = data.get("signQrc").getAsString();
					shareQrc = data.get("shareQrc").getAsString();
				} else {
					showToast("获取二维码失败");
				}			
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_QRC, params, callback);
	}
	
	public void erClick(View view){
		Intent intent = new Intent(ErWeiMaActivity.this, ShowErWeiMaActivity.class);
		intent.putExtra("ey", "1");
		intent.putExtra("signQrc", signQrc);
		startActivity(intent);
	}
	
	
	public void tgClick(View view){
		Intent intent = new Intent(ErWeiMaActivity.this, ShowErWeiMaActivity.class);
		intent.putExtra("ey", "2");
		intent.putExtra("shareQrc", shareQrc);
		startActivity(intent);
		
	}
	
	public void finishActivity(View view){
		this.finish();
	}

}