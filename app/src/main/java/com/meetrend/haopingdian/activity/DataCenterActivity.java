package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.widget.DataCenterItemView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 数据中心 
 * 
 * @author 肖建斌
 *
 */
public class DataCenterActivity extends BaseActivity{
	
	//ActionBar
	@ViewInject(id = R.id.actionbar_home,click ="finishActivity")
	ImageView back;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	@ViewInject(id = R.id.actionbar_action,click = "incommeLookClick")
	TextView lookView;
	
	//今日收入
	@ViewInject(id = R.id.todayincommeview,click = "onTodayIncommeClick")
	DataCenterItemView todayincomeview;
	
	//今日下单
	@ViewInject(id = R.id.todayorderview,click = "onTodayOrdersClick")
	DataCenterItemView todayorderView;
	
	//今日访客
	@ViewInject(id = R.id.todaycomstomerview,click = "onTodayCustomerClick")
	DataCenterItemView todaycomstomerview;
	
	//今日新增客户
	@ViewInject(id = R.id.todaynewaddcomview,click = "onTodayAddCustomerClick")
	DataCenterItemView todaynewaddcomview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datacenter);
		FinalActivity.initInjectedView(this);
		
		titleView.setText("数据中心");
		lookView.setText("收入总览");
		
		requestData();
		
		
	}
	
	public void onTodayIncommeClick(View view){
		Intent intent = new Intent(DataCenterActivity.this, TodayIncomeActivity.class);
		startActivity(intent);
	}
	
	public void onTodayOrdersClick(View view){
		Intent intent = new Intent(DataCenterActivity.this, TodayOrderListActivity.class);
		startActivity(intent);
	}
	
	public void onTodayCustomerClick(View view){
		
		Intent intent = new Intent(DataCenterActivity.this, TodayCustomersActivity.class);
		startActivity(intent);
	}
	
	public void onTodayAddCustomerClick(View view){
		Intent intent = new Intent(DataCenterActivity.this, TodayNewAddCustomersActivity.class);
		startActivity(intent);
	}
	
	public void incommeLookClick(View view){
		Intent	intent = new Intent(DataCenterActivity.this,IncomeActivity.class);
		startActivity(intent);
		DataCenterActivity.this.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	public void finishActivity(View view){
		finish();
	}
	
	
	private void requestData(){
		
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(DataCenterActivity.this).getToken());
		
		Callback callback = new Callback(tag, DataCenterActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				dimissDialog();
				
				if (isSuccess) {

					String todayPayed = data.get("todayPayed").getAsString();
					String todayNum = data.get("todayNum").getAsString();
					String todayVissit = data.get("visitNum").getAsString();
					String todayAddNum = data.get("addNum").getAsString();

					todayincomeview.setNumView(NumerUtil.formatTwoDecimalsWithDivide(todayPayed));
					todayorderView.setNumView(NumerUtil.formatNumStrhDivide(todayNum));
					todaycomstomerview.setNumView(NumerUtil.formatNumStrhDivide(todayVissit));
					todaynewaddcomview.setNumView(NumerUtil.formatNumStrhDivide(todayAddNum));
				} else {
					String msg = data.get("msg").getAsString();
					showToast(msg);
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.DATACENTER, params,callback);
	}

}