package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.XCFlowLayout;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class MarketMainActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	
	@ViewInject(id = R.id.flowlayout)
	XCFlowLayout mFlowLayout;
	
	@ViewInject(id = R.id.tea_searchbtn,click = "searchClick")
	TextView searchView;
	@ViewInject(id = R.id.tea_search_edit)
	TextView mEditText;
	
	//
	@ViewInject(id = R.id.hot_text_view)
	TextView hotView;
	
	private List<String> mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marketmain);
		FinalActivity.initInjectedView(this);
		
		init();
		
		requestGuanJianChars();
	}
	
	
	
	private void init(){
		mTitle.setText("市场参考价");
		mList = new ArrayList<String>();
	}
	
	public void searchClick(View view){
		
		if (mEditText.getText().toString().equals("")) {
			showToast("请输入商品名称");
			return;
		}
		
		Intent intent = new Intent(MarketMainActivity.this, MarketReferenceActivity.class);
		intent.putExtra("key", mEditText.getText().toString());
		startActivity(intent);
	}
	
	/**
	 * 关键字
	 */
	private void requestGuanJianChars(){
		
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(MarketMainActivity.this).getToken());
		
		Callback callback = new Callback(tag,MarketMainActivity.this) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				if (null != strMsg) {
					showToast(strMsg);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					
					JsonArray keys = data.get("data").getAsJsonArray();
					for (int i =0; i< keys.size();i++) {
						JsonObject jsonObject = (JsonObject) keys.get(i);
						mList.add(jsonObject.get("name").getAsString());
					}
					
					if (null == mList || mList.size() == 0) {
						hotView.setVisibility(View.GONE);
					}else {
						hotView.setVisibility(View.VISIBLE);
					}
					
					MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		               
			        lp.leftMargin = 0;
			        lp.rightMargin = 50;
			        lp.topMargin = 0;
			        lp.bottomMargin = 40;
			        
//			        mList = new ArrayList<String>();
//			        mList.add("你好实打实地方");
//			        mList.add("我是这方面的专家");
//			        mList.add("我是这方面的专家山东省第三代");
//			        mList.add("我是");
//			        mList.add("我是这方面的专家");
//			        mList.add("我是这方面的专家山东省第三代");
//			        mList.add("我是");
//			        mList.add("我是这方面的专家");
//			        mList.add("我是这方面的专家山东省第三代");
//			        mList.add("我是");
			        
			        LayoutInflater layoutInflater = LayoutInflater.from(MarketMainActivity.this);
			        int size = mList.size();
			        for(int i = 0; i < size; i ++){
			        	
			        	View childView = layoutInflater.inflate(R.layout.market_tag_char_layout, null);
			        	TextView tagView = (TextView) childView.findViewById(R.id.tag_char_view);
			        	tagView.setText(mList.get(i));
			        	tagView.setTag(mList.get(i));
			        	
			            mFlowLayout.addView(childView,lp);
			        	 
			            mFlowLayout.getChildAt(i).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
								Intent intent = new Intent(MarketMainActivity.this, MarketReferenceActivity.class);
								intent.putExtra("key", v.getTag().toString());
								startActivity(intent);
							}
						});
			        }
					
				} else {
					
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}else {
						showToast("加载失败");
					}
					
				}
				
			}
		};

		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.KEYCHARLIST, params, callback);
		
	}
	
	public void homeClick(View view){
		finish();
	}

}