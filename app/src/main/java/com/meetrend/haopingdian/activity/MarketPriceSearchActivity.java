package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.MarketPriceAdapter;
import com.meetrend.haopingdian.bean.MarketPriceInfo;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.EditTextWatcher;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * 市场价搜索
 * 
 * @author 肖建斌
 *
 */
public class MarketPriceSearchActivity extends BaseActivity{
	private final static String TAG = MarketPriceSearchActivity.class.getName();
	
	@ViewInject(id = R.id.actionbar_home,click = "finishActivity")
	ImageView backImgView;
	
	@ViewInject(id = R.id.actionbar_title)
	TextView tittleView;
	
	@ViewInject(id = R.id.search_listview)
	ListView mListView;
	
	@ViewInject(id = R.id.tea_search_edit)
	EditText searchEdit;
	@ViewInject(id = R.id.tea_searchbtn,click = "searchClick")
	TextView searchBtn;
	@ViewInject(id = R.id.clearbtn,click = "clearClick")
	ImageView clear;
	
	private List<MarketPriceInfo> mList;
	private MarketPriceAdapter marketPriceAdapter = null;
	
	private int mPageSize;
	private int mPageIndex;
	
	private View emptyView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market_search);
		FinalActivity.initInjectedView(this);
		
		init();
	}
	
	private void init(){
		
		tittleView.setText("搜索");
		
		emptyView = LayoutInflater.from(MarketPriceSearchActivity.this).inflate(R.layout.emptyview_layout, null);
		
		mList = new ArrayList<MarketPriceInfo>();
		marketPriceAdapter = new  MarketPriceAdapter(MarketPriceSearchActivity.this, mList);
		mListView.setAdapter(marketPriceAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MarketPriceInfo marketPriceInfo = (MarketPriceInfo) mListView.getItemAtPosition(position);
				if (marketPriceInfo != null) {
					
					Intent intent = new Intent(MarketPriceSearchActivity.this, MarketPreferenceDetailActivity.class);
					intent.putExtra("id", marketPriceInfo.id);
					startActivity(intent);
				}
				
			}
		});
		
		 searchEdit.addTextChangedListener(new EditTextWatcher() {
				public void afterTextChanged(Editable s) {
					
					if (s.length() == 0) {
						clear.setVisibility(View.GONE);
						searchBtn.setTextColor(Color.parseColor("#ff0000"));
						searchBtn.setText("取消");
					} else {
						searchBtn.setTextColor(Color.parseColor("#02bc00"));
						searchBtn.setText("搜索");
						clear.setVisibility(View.VISIBLE);
					}
				}
			});
	}
	
	public void searchClick(View view){
		
		if (searchBtn.getText().toString().equals("搜索")) {
			
			loadData(searchEdit.getText().toString());
			
		}else {
			finish();
		}
		
	}
	
	public void clearClick(View view){
		searchEdit.setText("");
	}
	
	public void loadData(String key) {
		
		showDialog("搜索中...");
		
		AjaxParams params = new AjaxParams();
		
		params.put("token", SPUtil.getDefault(MarketPriceSearchActivity.this).getToken());
		params.put("year", "");
		params.put("technology", "");
		params.put("type", "");
		params.put("keys", key);
		params.put("pageSize", "100");
		params.put("pageIndex", "1");

		Callback callback = new Callback(tag,this) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(TAG, "data is : " + t);
				
				List<MarketPriceInfo> dataList = null;
				if (isSuccess) {
					
						JsonArray jsonArr = data.get("dataList").getAsJsonArray();
						//mPageSize = data.get("pageSize").getAsInt();
						//mPageIndex = data.get("pageIndex").getAsInt();
						
						Gson gson = new Gson();
						dataList = gson.fromJson(jsonArr,new TypeToken<List<MarketPriceInfo>>() {}.getType());
					
						if (mList.size() > 0) {
							mList.clear();
						}
						
						if (null != dataList) {
							mList.addAll(dataList);
						}
					
						marketPriceAdapter.notifyDataSetChanged();
						
						if (mList.size() == 0) {
							mListView.removeFooterView(emptyView);
							mListView.addFooterView(emptyView);
						}else {
							mListView.removeFooterView(emptyView);
						}
						
					
				} else {
					
					if (data.get("msg") != null) {
						showToast(data.get("msg").getAsString());
					}
					
				}
				dimissDialog();
				
			}
		};
		
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.MARKET_PRICE_URL, params, callback);
	}
	
	public void finishActivity(View view){
		finish();
	}

}