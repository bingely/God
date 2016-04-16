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
import com.meetrend.haopingdian.adatper.MarketPriceDetailAdapter;
import com.meetrend.haopingdian.bean.MarketPriceDetailInfo;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshScrollView;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.MyListView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * 市场参考价详情
 * 
 * @author 肖建斌 
 *
 */
public class MarketPreferenceDetailActivity extends BaseActivity{
	
	private final static String TAG = MarketPreferenceDetailActivity.class.getName();
	
	@ViewInject(id = R.id.actionbar_home,click = "finishActivity")
	ImageView backImgView;
	
	@ViewInject(id = R.id.actionbar_title)
	TextView tittleView;
	
	@ViewInject(id = R.id.detailname_tv)
	TextView detailName;
	
	@ViewInject(id = R.id.detail_price_tv)
	TextView price_tv;
	
	@ViewInject(id = R.id.year_tv)
	TextView yearTv;
	
	@ViewInject(id = R.id.pici_tv)
	TextView piciTv;
	
	@ViewInject(id = R.id.techs_tv)
	TextView techsTv;
	
	@ViewInject(id = R.id.guige_tv)
	TextView guigeTv;
	
	@ViewInject(id = R.id.pulllistview)
	MyListView mListView;
	
	@ViewInject(id = R.id.pull_to_scrollview)
	PullToRefreshScrollView pullToRefreshScrollView;
	
	
	private List<MarketPriceDetailInfo> mList;
	
	private int mPageSize;
	private int mPageIndex;
	
	public String id;
	
	MarketPriceDetailAdapter detailAdapter;
	ScrollView scrollView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marketpreference_detail);
		FinalActivity.initInjectedView(this);
		
		id = getIntent().getStringExtra("id");
		mList = new ArrayList<MarketPriceDetailInfo>();
		
		pullToRefreshScrollView.setMode(Mode.DISABLED);
		scrollView = pullToRefreshScrollView.getRefreshableView();
		
		
		detailAdapter = new MarketPriceDetailAdapter(mList, MarketPreferenceDetailActivity.this);
		mListView.setAdapter(detailAdapter);
		
		
		showDialog();
		loadData(id);
	}
	
	
	OnRefreshListener2<ScrollView> listener2 = new OnRefreshListener2<ScrollView> (){
		
		//刷新
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
			
		}

		//加载更多
		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
			
			loadData(id);
		}			
	};
	
	public void loadData(String id) {
		
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(MarketPreferenceDetailActivity.this).getToken());
		params.put("pageSize", "50");
		params.put("pageIndex", ++mPageIndex +"");
		params.put("id", id);

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
				dimissDialog();
				
				List<MarketPriceDetailInfo> dataList = null;
				if (isSuccess) {
					
						JsonArray jsonArr = data.get("dataList").getAsJsonArray();
						mPageSize = data.get("pageSize").getAsInt();
						mPageIndex = data.get("pageIndex").getAsInt();
						
						String name = data.get("name").getAsString();
						detailName.setText(name);
						
						tittleView.setText(name);
						
						String price = data.get("price").getAsString();
						price_tv.setText("¥" + price);
						
						String year = data.get("year").getAsString();
						yearTv.setText("年份："+year);
						
						String pici = data.get("pici").getAsString();
						piciTv.setText("批次："+pici);
						
						String technology = data.get("technology").getAsString();
						techsTv.setText("生产工艺："+ technology);
						
						String standard = data.get("standard").getAsString();
						guigeTv.setText("规格："+standard);
						
						
						Gson gson = new Gson();
						dataList = gson.fromJson(jsonArr,new TypeToken<List<MarketPriceDetailInfo>>() {}.getType());
					
						for (MarketPriceDetailInfo info : dataList) {
							
							if (Double.parseDouble(info.change) > 0) {
								info.changeisOnZeor = true;
							}else {
								info.changeisOnZeor = false;
							}
							
							if (Double.parseDouble(info.dayChange) > 0) {
								info.changeRateisDownZeor = false;
							}else {
								info.changeRateisDownZeor = true;
							}
						}
						
						if (null != dataList) {
							mList.addAll(dataList);
						}
						
						detailAdapter.notifyDataSetChanged();
						scrollView.smoothScrollTo(0, 0);
						dimissDialog();
						
				} else {
					
					if (data.get("msg") != null) {
						showToast(data.get("msg").getAsString());
					}
					
				}
			}
		};
		
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.MARKET_DETAIL, params, callback);
	}
	
	public void finishActivity(View view){
		finish();
	}

}