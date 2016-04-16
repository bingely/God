package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.JsonArray;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.fragment.ProductFastOrderFragment;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;

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
 * 
 * 刷卡 和 记账 结账
 * 
 *
 */
public class SwipingNumActivity extends BaseActivity{
	
	private final static String TAG = ProductFastOrderFragment.class.getName();
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	@ViewInject(id = R.id.remark)
	EditText mEditText;
	
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
	
	@ViewInject(id = R.id.commitbtn,click = "commitclick")
	Button mCommitBtn;
	
	@ViewInject(id = R.id.list_layout)
	LinearLayout listLayout;
	
	
	AjaxParams ajaxParams;
	
	String orderId;
	String payType;
	String remark;
	String ysMoney;
	double integralamountMoney;//积分金额
	String shipMethod;
	String totalMoney;
	String points;//扣除的积分
	String function;//实收金额
	String hejiMoney;//合计
	double discountAmount;//优惠券
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipingnum);
		FinalActivity.initInjectedView(this);
		Intent intent = getIntent();
		ajaxParams = App.ajaxParams;
		String paytype = intent.getStringExtra("paytype");
		
		if (paytype.equals("keepaccounts")) {
			mBarTitle.setText("记账结账");
		}else {
			mBarTitle.setText("刷卡结账");
		}
			
		orderId = intent.getStringExtra("orderid");
		payType = intent.getStringExtra("paytype");
		ysMoney = intent.getStringExtra("ys");//应收
		integralamountMoney = intent.getDoubleExtra("integralamount",0.0);//积分金额
		shipMethod = intent.getStringExtra("shipMethod");//送货方式
		totalMoney = intent.getStringExtra("total");//总金额
		points = intent.getStringExtra("points");//积分
		function = intent.getStringExtra("faction");//实收金额
		hejiMoney = intent.getStringExtra("incomeAmount");//合计金额
		discountAmount = intent.getDoubleExtra("discountAmount", 0.0);
		
		allproductMoneyView.setText(NumerUtil.getNum(totalMoney)+"");
		yhqView.setText("- " + discountAmount);
		pointsView.setText("- " + (points.equals("") ? "0" :points));
		hejiView.setText(NumerUtil.getNum(hejiMoney));//合计
		ysView.setText(NumerUtil.getNum(ysMoney));//应收
	}
	
	//提交
	public void commitclick(View view){
		
		showDialog();
		
		confimOrder();
	}
	
	//结束
	public void onClickHome(View view){
		finish();
	}
	
	/**
	 * 结账
	 * 
	 */
	public void confimOrder() {
		
		AjaxParams mAjaxParams = new AjaxParams();
	
		mAjaxParams.put("token", SPUtil.getDefault(SwipingNumActivity.this).getToken());
		mAjaxParams.put("storeId", SPUtil.getDefault(SwipingNumActivity.this).getStoreId());
		
		mAjaxParams.put("remark", mEditText.getText().toString());//备注
		mAjaxParams.put("orderid", orderId);
		mAjaxParams.put("paytype", payType);
		mAjaxParams.put("faction", "0");//实收
		mAjaxParams.put("changenote","0");//找零
		mAjaxParams.put("integralamount", integralamountMoney+"");//积分金额
		mAjaxParams.put("discountamount", discountAmount+"");//优惠金额
		mAjaxParams.put("shipMethod", shipMethod);//送货方式
		
		Callback callback = new Callback(tag, SwipingNumActivity.this) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (data.get("ret") != null) {
					
					if (data.get("ret").getAsBoolean()) {
						
						SPUtil.getDefault(SwipingNumActivity.this).setOrderId(orderId);//保存订单id
						Intent intent = new Intent(SwipingNumActivity.this, PayResultActivity.class);
						startActivity(intent);
						SwipingNumActivity.this.finish();
						dimissDialog();
						
					}else {
						
						if (data.has("msg")) {
							String msg = data.get("msg").getAsString();
							//Toast.makeText(SwipingNumActivity.this, msg, 200).show();
							showToast(msg);
						}
					}
				}else {
					showToast("服务器异常");
					///Toast.makeText(SwipingNumActivity.this, "服务器异常", 200).show();
				}
				
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.CURRENT_MONEY_PAY, mAjaxParams,callback);
	}

}