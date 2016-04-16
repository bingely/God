package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.OrderInfoSendTypeEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import de.greenrobot.event.EventBus;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 送货确认
 * 
 * @author 肖建斌
 *
 */
public class SendConfirmActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	
	@ViewInject(id = R.id.send_type_view,click = "sencTypeClick")
	TextView sendTypeView;
	
	@ViewInject(id = R.id.kuaidi_edit_view)
	EditText kuaidiView;
	
	@ViewInject(id = R.id.beizhu_edit_view)
	EditText beizhuView;
	
	@ViewInject(id = R.id.confirm_btn,click = "confirmClick")
	Button confirmBtn;
	
	@ViewInject(id = R.id.bottomline)
	View bottomLine;
	
	@ViewInject(id = R.id.kuaidilayout)
	LinearLayout kuaidiLayout;
	
	
	private String sendType;
	private String orderId;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_sendconfirm);
		FinalActivity.initInjectedView(this);
		
		orderId = getIntent().getStringExtra("orderId");
		sendType = getIntent().getStringExtra("ship");
		
		if (!sendType.equals("快递")) {
			kuaidiLayout.setVisibility(View.GONE);
			bottomLine.setVisibility(View.GONE);
		}
		
		mTitle.setText("送货确认");
		sendTypeView.setText(sendType);//配送方式
		
	}
	
	//支付方式选择返回值
	public void onEventMainThread(OrderInfoSendTypeEvent event) {
		sendType = event.typeVlaue;
		sendTypeView.setText(sendType);
	}
	
	//配送方式选择
	public void sencTypeClick(View view){
		
		Intent intent = new Intent(SendConfirmActivity.this, StoreOrderChooseTypeListActivity.class);
		intent.putExtra("chooseValue", sendTypeView.getText().toString());//配送方式
		intent.putExtra("chooseType", 3);//标识是配送方式
		startActivity(intent);
	}
	
	public void confirmClick(View view){
		sendRequest();
	}
	
	//送货确认
	private void sendRequest() {

		showDialog();

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(SendConfirmActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(SendConfirmActivity.this).getStoreId());
		params.put("id", orderId);
		params.put("shipMethod", sendType);
		params.put("expressCode", kuaidiView.getText().toString());
		params.put("description", beizhuView.getText().toString());
		

		Callback callback = new Callback(tag, this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {

					Toast.makeText(SendConfirmActivity.this, "送货确认成功", Toast.LENGTH_SHORT).show();
					finish();
					
				} else {
					if (data.get("msg") != null) {
						String msg = data.get("msg").getAsString();
						Toast.makeText(SendConfirmActivity.this, msg, Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(SendConfirmActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
					
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.SEND_PRODUCT_REQUEST, params,
				callback);

	}
	
	public void homeClick(View view){
		finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}