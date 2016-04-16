package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.AddCourierCodeEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

import de.greenrobot.event.EventBus;

/**
 * 录入快递单号
 * @author 肖建斌
 *
 */
public class CourierCodeActivity extends BaseActivity {
	
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.actionbar_action)
	TextView mAction;
	@ViewInject(id = R.id.ed_courier_code)
	EditText mCourierCode;
	@ViewInject(id = R.id.save, click = "saveClick")
	Button mSave;
	
	String orderId ="";
	//快递单号
	String orderNum = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_courier_code);
	        FinalActivity.initInjectedView(this);
	        
	        mTitle.setText("快递单号");
	        try {
	        	
	        		orderId = getIntent().getStringExtra("orderId");
	        		orderNum = getIntent().getStringExtra("num");
	        		
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	        mCourierCode.setText(orderNum);
	        if (null != orderNum) {
				mCourierCode.setSelection(orderNum.length());
			}
	}
	
	public void saveClick(View v){
		save();
	}
	
	public void homeClick(View v){
		finish();
	}
	
	public void save(){
		if(mCourierCode==null || "".equals(mCourierCode.getText().toString())){
			showToast("快递单号不能为空");
			return;
		}
			
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(CourierCodeActivity.this).getToken());
		params.put("orderId", orderId);
		params.put("expressCode", mCourierCode.getText().toString());
		
		Callback callback = new Callback(tag,this) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "login info : " + t);
				if (isSuccess) {
					mHandler.sendEmptyMessage(Code.SUCCESS);
				} else {
					Message msg = new Message();
					msg.what = Code.FAILED;
					msg.obj = data.get("msg");
					mHandler.sendMessage(msg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.ADD_ORDER_EXPRESSCODE, params, callback);
	}
	
	
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.SUCCESS:
				EventBus.getDefault().post(new AddCourierCodeEvent(mCourierCode.getText().toString()));
				showToast("快递单号已成功录入");
				finish();
				break;
			case Code.FAILED: 
				if (msg.obj == null) {
					showToast("添加失败");
				} else {
					showToast((String) msg.obj);
				}
				break;
			}
		}
	};
	

}