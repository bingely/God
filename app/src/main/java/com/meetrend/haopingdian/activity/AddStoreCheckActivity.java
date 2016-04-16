package com.meetrend.haopingdian.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.CheckDetailListItemBean;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.StoreCheckEvent;
import com.meetrend.haopingdian.event.StoreCheckRefreshEvent;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;

import de.greenrobot.event.EventBus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 添加店内盘点界面
 * 
 * @author 肖建�?
 *
 */
public class AddStoreCheckActivity extends BaseActivity implements OnClickListener{
	
	ImageView backView;
	TextView titleView;
	
	//添加商品
	RelativeLayout addProductLayout;
	
	//�?个产�?
	LinearLayout oneProductLayout;
	SimpleDraweeView oneTeaImageView;
	TextView oneTeaNameView;
	TextView oneTeaNum;
	
	//多个产品
	RelativeLayout moreProductLayout;
	LinearLayout imgsContainer;
	TextView teaCountView;
	
	//金额
	TextView shouquanMoneyView;
	EditText xianjinMoneyView;
	EditText shuakaMoneyView;
	EditText jiZhangMoneyView;
	EditText wxMoneyView;
	
	//备注
	TextView beiZhuView;
	
	//保存
	TextView saveBtn;
	
	private ArrayList<CheckDetailListItemBean> mList = new ArrayList<CheckDetailListItemBean>();
	
	private int mode;//1 从商品列�? 2从商品盘点详情的编辑
	private String id;//详情id
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcheckstore);
		FinalActivity.initInjectedView(this);
		
		initViews();
		
		init();
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		//正对添加盘点处理
		if (null == bundle) {
			return;
		}
		
		 mList = bundle.getParcelableArrayList("list");
		 id = bundle.getString("id");
		 mode = bundle.getInt("mode");
		 
		 shouquanMoneyView.setText(bundle.getString("sk"));
		 xianjinMoneyView.setText(bundle.getString("xj"));
		 shuakaMoneyView.setText(bundle.getString("ska"));
		 jiZhangMoneyView.setText(bundle.getString("jz"));
		 String wxStr = bundle.getString("wx");
		 wxMoneyView.setText(wxStr);
		 wxMoneyView.setSelection(wxStr.length());


		beiZhuView.setText(bundle.getString("bz"));
		
		 if(mList.size() == 1){
			oneProductLayout.setVisibility(View.VISIBLE);
			addProductLayout.setVisibility(View.GONE);
			
			CheckDetailListItemBean bean = mList.get(0);
			oneTeaImageView.setImageURI(Uri.parse(Server.BASE_URL + bean.picture));
			oneTeaNameView.setText(bean.productName);
			oneTeaNum.setText("数量 "+ NumerUtil.saveThreeDecimal(bean.amount+""));//件数
		 }
		 
		 else {
			 
			 moreProductLayout.setVisibility(View.VISIBLE);
			 addProductLayout.setVisibility(View.GONE);
			 
			 int count = mList.size();
			 teaCountView.setText(NumerUtil.saveOneDecimal(count+""));
			 if (count > 3) {
				count = 3;
			 }
				
			for (int i = 0; i < count; i++) {
				//图片的显�?
				SimpleDraweeView childImg = (SimpleDraweeView) imgsContainer
						.getChildAt(i);
				childImg.setVisibility(View.VISIBLE);
				childImg.setImageURI(Uri.parse(Server.BASE_URL + mList.get(i).picture));
			}
			
		 }
		 
	}
	
	private void initViews(){
		backView = (ImageView) this.findViewById(R.id.actionbar_home);
		titleView = (TextView) this.findViewById(R.id.actionbar_title);
		
		//添加产品
		addProductLayout = (RelativeLayout) this.findViewById(R.id.add_check_tea_layout);
		
		//�?个产�?
		oneProductLayout = (LinearLayout) this.findViewById(R.id.one_check_product_layout);
		oneTeaImageView = (SimpleDraweeView) this.findViewById(R.id.tea_imgview);
		oneTeaNameView = (TextView) this.findViewById(R.id.tea_name_view);
		oneTeaNum = (TextView) this.findViewById(R.id.tea_num_view);
		
		//多个产品
		moreProductLayout = (RelativeLayout) this.findViewById(R.id.more_check_product_container);
		imgsContainer = (LinearLayout) this.findViewById(R.id.more_product_img_layout);
		teaCountView = (TextView) this.findViewById(R.id.product_count_view);
		
		//金额
		shouquanMoneyView = (TextView) this.findViewById(R.id.shoukuan_money_view);
		
		xianjinMoneyView = (EditText) this.findViewById(R.id.xianjin_money_view);
		shuakaMoneyView = (EditText) this.findViewById(R.id.shuaka_money_view);
		jiZhangMoneyView = (EditText) this.findViewById(R.id.jizhang_money_view);
		wxMoneyView = (EditText) this.findViewById(R.id.weixin_money_view);
		
		//备注
		beiZhuView = (TextView) this.findViewById(R.id.beizhu_view);
		
		//保存
		saveBtn = (TextView) this.findViewById(R.id.savebtn);
		
		setonClickLinsteners();
		
		
		xianjinMoneyView.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				
				calculateAllNum();
			}
		});
		
		shuakaMoneyView.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				
				calculateAllNum();
			}
		});
		
		jiZhangMoneyView.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				
				calculateAllNum();
			}
		});
		
		
		wxMoneyView.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				
				calculateAllNum();
			}
		});
		
	}
	
	//计算总金�?
	double xinjinNum = 0.00;
	double shuakaNum = 0.00;
	double jizhangNum = 0.00;
	double wxNum = 0.00;
	
	private void calculateAllNum(){
		String xinjinStr = xianjinMoneyView.getText().toString();
		String shuakaStr = shuakaMoneyView.getText().toString();
		String jizhangStr = jiZhangMoneyView.getText().toString();
		String wxStr = wxMoneyView.getText().toString();
		
		
		if (NumerUtil.isFloat(xinjinStr) || NumerUtil.isInteger(xinjinStr)) {
			
			 xinjinNum = Double.parseDouble(xinjinStr);
		}else {
			xinjinNum = 0.00;
		}
		
		if (NumerUtil.isFloat(shuakaStr) || NumerUtil.isInteger(shuakaStr)) {
			
			shuakaNum = Double.parseDouble(shuakaStr);
		}else {
			 shuakaNum = 0.00;
		}
		
		if (NumerUtil.isFloat(jizhangStr) || NumerUtil.isInteger(jizhangStr)) {
			
			jizhangNum = Double.parseDouble(jizhangStr);
			
		}else {
			
			jizhangNum = 0.00;
		}
		
		if (NumerUtil.isFloat(wxStr) || NumerUtil.isInteger(wxStr)) {
			
			wxNum = Double.parseDouble(wxStr);
			
		}else {
			 wxNum = 0.00;
		}
		
		String num = NumerUtil.setSaveTwoDecimals((xinjinNum + shuakaNum + jizhangNum + wxNum)+"");
		shouquanMoneyView.setText(num);
	}
	
	
	private void setonClickLinsteners(){
		addProductLayout.setOnClickListener(this);
		oneProductLayout.setOnClickListener(this);
		moreProductLayout.setOnClickListener(this);
		backView.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_home:
			finish();
			break;
			
		case R.id.add_check_tea_layout:
			
			Intent addintent = new Intent(AddStoreCheckActivity.this, StoreCheckListActivity.class);
			Bundle addbundle = new Bundle();
			addbundle.putInt("isEmpty", 1);//标识数据为空
			addintent.putExtras(addbundle);
			startActivity(addintent);
			
		break;
		
		case R.id.one_check_product_layout:
			
			Intent oneintent = new Intent(AddStoreCheckActivity.this, StoreCheckListActivity.class);
			Bundle onebundle = new Bundle();
			onebundle.putParcelableArrayList("list", mList);
			oneintent.putExtras(onebundle);
			startActivity(oneintent);
			
		break;
		
		case R.id.more_check_product_container:
			
			Intent moreintent = new Intent(AddStoreCheckActivity.this, StoreCheckListActivity.class);
			Bundle morebundle = new Bundle();
			morebundle.putParcelableArrayList("list", mList);
			moreintent.putExtras(morebundle);
			startActivity(moreintent);
			
		break;
		
		case R.id.savebtn:
			 addStoreCheck();
			 
			break;
		
		default:
			break;
		}
		
	}
	
	public void onEventMainThread(StoreCheckEvent event) {
		
		 mList = event.arrayList;
		 
		 mode = event.mode;
		
		 if(mList.size() == 1){
			    moreProductLayout.setVisibility(View.GONE);
				oneProductLayout.setVisibility(View.VISIBLE);
				addProductLayout.setVisibility(View.GONE);
				
				CheckDetailListItemBean bean = mList.get(0);
				oneTeaImageView.setImageURI(Uri.parse(Server.BASE_URL + bean.picture));
				oneTeaNameView.setText(bean.productName);// 茶品�?
				oneTeaNum.setText("数量:"+ NumerUtil.saveThreeDecimal(bean.amount+""));//件数
			 }
			 
			 else {
			 oneProductLayout.setVisibility(View.GONE);
				 moreProductLayout.setVisibility(View.VISIBLE);
				 addProductLayout.setVisibility(View.GONE);
				 
				 int count = mList.size();
				 teaCountView.setText(NumerUtil.saveOneDecimal(count+""));
				 if (count > 3) {
					count = 3;
				 }
					
				for (int i = 0; i < count; i++) {
					//图片的显�?
					
					SimpleDraweeView childImg = (SimpleDraweeView) imgsContainer
							.getChildAt(i);
					childImg.setVisibility(View.VISIBLE);
					childImg.setImageURI(Uri.parse(Server.BASE_URL + mList.get(i).picture));
				}
				
			 }
	}
	
	private void init(){
		titleView.setText("店内盘点");
	}
	
	
	private void addStoreCheck() {

		showDialog();
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(AddStoreCheckActivity.this).getToken());
		
		JsonObject jsonObject = new JsonObject();
		//编辑模式
		if (mode == 2) {
			jsonObject.addProperty("id", id);
		}
		
		//时间	
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		String date = sDateFormat.format(new java.util.Date());
		String formatTime = date.substring(0, 16);
		
		JsonArray jsonArray = new JsonArray();
		for (CheckDetailListItemBean bean : mList) {
			JsonObject obj = new JsonObject();
			obj.addProperty("producId", bean.productId);
			obj.addProperty("amount", bean.amount+"");
			jsonArray.add(obj);
		}
		
		jsonObject.add("details", jsonArray);
		jsonObject.addProperty("checkTime", formatTime);
		jsonObject.addProperty("cardMoney", shuakaNum);
		jsonObject.addProperty("cashMoney", xinjinNum);
		jsonObject.addProperty("tallyMoney", jizhangNum);
		jsonObject.addProperty("totalMoney", shouquanMoneyView.getText().toString());
		jsonObject.addProperty("weChatMoney", wxNum);
		jsonObject.addProperty("description", beiZhuView.getText().toString());
		
		params.put("args", jsonObject.toString());
		
		Callback callback = new Callback(tag, AddStoreCheckActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					if (mode == 1) 
					  showToast("新增盘点成功");
					else 
					  showToast("编辑盘点成功");
					EventBus.getDefault().post(new StoreCheckRefreshEvent());
					finish();

				} else {
					String msg = data.get("msg").getAsString();
					Toast.makeText(AddStoreCheckActivity.this, msg, Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		if (mode == 2) 
			finalHttp.get(Server.BASE_URL + Server.STORECHECK_EDIT, params,callback);//编辑
		else 
			finalHttp.get(Server.BASE_URL + Server.STORECHECK_ADD, params,callback);//添加
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}