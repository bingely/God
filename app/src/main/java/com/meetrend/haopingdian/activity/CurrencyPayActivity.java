package com.meetrend.haopingdian.activity;

import java.math.BigDecimal;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.ActivityInfoEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.fragment.ProductFastOrderFragment;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * 现金结账
 *
 */
public class CurrencyPayActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	//商品金额
	@ViewInject(id = R.id.all_product_money)
	TextView allproductMoneyView;
	
	//优惠券view
	@ViewInject(id = R.id.youhuiquan_money)
	TextView yhqView;
	
	//积分
	@ViewInject(id = R.id.points)
	TextView pointsView;
	
	//合计
	@ViewInject(id = R.id.heji)
	TextView hejiView;
	
	//应收
	@ViewInject(id = R.id.yingshou)
	TextView ysView;
	
	//实收
	@ViewInject(id = R.id.shihou)
	EditText shView;
	
	//零钱
	@ViewInject(id = R.id.small_money_view)
	TextView smallView;
	
	//确认
	@ViewInject(id = R.id.confirm_order)
	TextView sureView;
	
	String orderId;
	String payType;
	String remark;
	String integralamountMoney;
	String shipMethod;
	String totalMoney;
	String points;//扣除的积分
	String faction;//实收金额
	
	String hejiMoney;//合计金额
	String yinshouMoney;//应收
	
	 private double smallMoney;//找零
	 private double discountAmount;//优惠金额
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitycurentcypay);
		FinalActivity.initInjectedView(this);
		
		mBarTitle.setText("现金结账");
		
		Intent intent = getIntent();
		
		try {
				orderId = intent.getStringExtra("orderid");
				payType = intent.getStringExtra("paytype");
				remark = intent.getStringExtra("remark");
				hejiMoney = intent.getStringExtra("incomeAmount");//合计
				integralamountMoney = intent.getStringExtra("integralamount");//积分金额
				shipMethod = intent.getStringExtra("shipMethod");//送货方式
				totalMoney = intent.getStringExtra("total");//总金额
				points = intent.getStringExtra("points");//积分
				yinshouMoney = intent.getStringExtra("yishou");
				discountAmount = intent.getDoubleExtra("discountAmount",0.0);//优惠金额
				allproductMoneyView.setText(NumerUtil.getNum(totalMoney)+"");
				yhqView.setText("-"+NumerUtil.getNum(discountAmount+""));
				pointsView.setText("-"+points);
				hejiView.setText(NumerUtil.getNum(hejiMoney));//合计
				ysView.setText(NumerUtil.getNum(yinshouMoney));//应收
				shView.setText(NumerUtil.getNum(faction));//实收
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		
		sureView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentPayRequest();
			}
		});
		
		//实收框监听
		shView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				 if (NumerUtil.isFloat(s.toString()) || NumerUtil.isInteger(s.toString())) {
					 
					 smallMoney =Double.parseDouble(s.toString()) - Double.parseDouble(yinshouMoney);
					 BigDecimal decimal = new BigDecimal(smallMoney);
					 double result = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
					 
					 smallView.setText("找零：¥ "+ result);
					 shView.setTextColor(Color.parseColor("#797979"));
					 sureView.setBackgroundResource(R.drawable.pay_btn_bg);
					 sureView.setEnabled(true);
					 
				 }else {
					 
					    sureView.setEnabled(false);
					    sureView.setBackgroundColor(Color.parseColor("#f05b72"));
					    shView.setTextColor(Color.RED);
				}
			}
		});
		
	}
	
	
	public void currentPayRequest() {
		
		if (shView.getText().toString().equals("")) {
			Toast.makeText(CurrencyPayActivity.this, "实收金额不能为空", 200).show();
			return;
		}
		
		showDialog("正在提交...");
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(CurrencyPayActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(CurrencyPayActivity.this).getStoreId());
		params.put("orderid", orderId);
		params.put("paytype", payType);//支付方式
		params.put("remark", remark);//备注
		params.put("faction", shView.getText().toString());//实收数据
		params.put("changenote", smallMoney+"");//找零
		params.put("discountamount", discountAmount+"");//优惠金额
		params.put("integralamount", integralamountMoney);//积分金额
		params.put("shipMethod", shipMethod);//送货方式

		Callback callback = new Callback(tag, this) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				
				dimissDialog();
				
				if (null != strMsg) {
					Toast.makeText(CurrencyPayActivity.this, strMsg, 200).show();
				}
				
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
					dimissDialog();

					boolean ret = data.get("ret").getAsBoolean();
					
					if (ret) {
						SPUtil.getDefault(CurrencyPayActivity.this).saveShiShouMoney(yinshouMoney);//保存应收的金额
						Toast.makeText(CurrencyPayActivity.this, "提交成功", 200).show();
						//SPUtil.getDefault(CurrencyPayActivity.this).saveShiShouMoney(shView.getText().toString());
						Intent intent = new Intent(CurrencyPayActivity.this, PayResultActivity.class);
						startActivity(intent);
						finish();
					}else {
						Toast.makeText(CurrencyPayActivity.this, data.get("msg").getAsString(), 200).show();
					}
					
			}
		};
		FinalHttp request = new FinalHttp();
		request.get(Server.BASE_URL + Server.CURRENT_MONEY_PAY, params, callback);
	}
	
	public void onClickHome(View view) {
		finish();
	}
	
}