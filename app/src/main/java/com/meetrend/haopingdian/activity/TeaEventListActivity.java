package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.EventTypesSpinnerAdapter;
import com.meetrend.haopingdian.adatper.OverViewAdapter;
import com.meetrend.haopingdian.bean.EventTypeBean;
import com.meetrend.haopingdian.bean.OverViewEntity;
import com.meetrend.haopingdian.bean.PageInfo;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.BaseTopPopWindow;
import com.meetrend.haopingdian.widget.OrdersListTopPopWindow;
import com.meetrend.haopingdian.widget.TeaEventThemesListPopWindow;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 活动总览
 * 
 */
public class TeaEventListActivity extends BaseActivity{

	@ViewInject(id = R.id.ordernamager_title_layout)
	RelativeLayout titleBarLayout;
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title,click = "popListViewClick")
	TextView mTitle;
	
	@ViewInject(id = R.id.lv_activity)
	PullToRefreshListView mListView = null;

	@ViewInject(id = R.id.popwindow_container)
	FrameLayout popwindowContainer ;

	List<OverViewEntity> mList;

	// 分页
	List<OverViewEntity> mAllList = new ArrayList<OverViewEntity>();// 全部的数据
	private int pageIndex = 1;
	int mPageCount;
	int mPageIndex;
	
	//三角形
	private Drawable triangleDown;
	private Drawable triangleUp;

	// 活动分类集合
	private List<EventTypeBean> eventTypeList = null;

	//活动主题列表适配器
	private EventTypesSpinnerAdapter eventTypesSpinnerAdapter;

	//活动列表适配器
	OverViewAdapter overViewAdapter = null;
	
	
	//活动主题列表的数据是否已加载
	private boolean hasLoad;
	
	//是否是下拉刷新
	private boolean pullStart;
	
	//开始加载活动列表的数据
	private int startLoad = 0x247;
	
	//所有的加载过的数据，避免每次都去网络请求
	private List<List<OverViewEntity>> allEventList;
	//当前选中
	private int currentSelect;
	//保存数据分页信息
	private List<PageInfo> pageInfoList;
	
	//标识当前列表数据为空，点击刷新
	private boolean emptyFresh;
	
	//数据为空
	private View emptyFooterView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_overview);
		FinalActivity.initInjectedView(this);

		mTitle.setEnabled(false);
		allEventList = new ArrayList<List<OverViewEntity>>();
		eventTypeList = new ArrayList<EventTypeBean>();
		pageInfoList = new ArrayList<PageInfo>();
		
		initView();
		
		mListView.setMode(Mode.BOTH);
		mListView.setTag(true);
		mListView.setOnRefreshListener(listener2);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				OverViewEntity entity = (OverViewEntity) parent.getItemAtPosition(position);
				if (null == entity) {
					return;
				}

				Intent intent = new Intent(TeaEventListActivity.this,
						NewEventDetailActivity.class);
				intent.putExtra("id", entity.entityId);
				intent.putExtra("ischarge",entity.isCharge);
				startActivity(intent);
			}
		});
		
		//活动主题类型请求
		eventTypeListRequest();
	}

	OnRefreshListener2<ListView> listener2 = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			pullStart = true;
			pageInfoList.get(currentSelect).index = 1;
			getActivityOverView(currentSelect);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			pullStart = false;
			++pageInfoList.get(currentSelect).index;
			getActivityOverView(currentSelect);
		}
	};
	
	
	//刷新
	public void reFreshDataClick(View view){
		emptyFresh = true;
		getActivityOverView(currentSelect);
	}
	

	
	
	//overViewAdapter = new OverViewAdapter(this, mList);
	//SwingRightInAnimationAdapter swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(new SwipeDismissAdapter(overViewAdapter, this));
	//swingRightInAnimationAdapter.setAbsListView(mListView.getRefreshableView());
	//mListView.getRefreshableView().setAdapter(swingRightInAnimationAdapter);
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		
		switch (msg.what) {
			case Code.FAILED: {
				showToast(null == msg.obj.toString()   ? "获取数据，失败请重试" : msg.obj.toString());
				break;
			}
			
			case 0x247:
				//第一个主题的数据请求
				getActivityOverView(currentSelect);
			break;
		}
	}

	/**
	 * 活动类型接口
	 */
	public void eventTypeListRequest() {
		
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(TeaEventListActivity.this)
				.getToken());
		params.put("parentEntityId", "ee348e50-c000-475d-a5d3-494177335707");// 该参数固定不变
		params.put("pageIndex", pageIndex + "");

		Callback callback = new Callback(tag, this) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dimissDialog();
				if (strMsg != null) {
					showToast(strMsg);
				}
				super.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					JsonArray jsonArray = data.get("classify").getAsJsonArray();
					Gson gson = new Gson();
					eventTypeList = gson.fromJson(jsonArray,
							new TypeToken<List<EventTypeBean>>() {
							}.getType());
					
					EventTypeBean eventTypeBean = new EventTypeBean();
					eventTypeBean.FText = "全部活动";
					eventTypeBean.FValue = "0";
					eventTypeList.add(0, eventTypeBean);
					
					//eventTypesSpinnerAdapter = new EventTypesSpinnerAdapter(TeaEventListActivity.this, eventTypeList, mTitle);
					
					if (pageInfoList.size() > 0) {
						pageInfoList.clear();
					}
					
					//添加分页信息对象
					for (int i = 0; i < eventTypeList.size(); i++) {
						pageInfoList.add(new PageInfo());
						allEventList.add(new ArrayList<OverViewEntity>());
					}
					
					pageInfoList.get(currentSelect).index =1;
					overViewAdapter = new OverViewAdapter(TeaEventListActivity.this, allEventList.get(currentSelect));
					//动画效果
					SwingBottomInAnimationAdapter swingRightInAnimationAdapter = new SwingBottomInAnimationAdapter(overViewAdapter);
					swingRightInAnimationAdapter.setAbsListView(mListView.getRefreshableView());
					mListView.getRefreshableView().setAdapter(swingRightInAnimationAdapter);
					
					if (eventTypeList.get(currentSelect).FText.equals("全部活动")) {
						mTitle.setText("活动总览");
					}else {
						mTitle.setText(eventTypeList.get(currentSelect).FText);
					}
					
					mTitle.setEnabled(true);
					hasLoad = true;
					mHandler.sendEmptyMessage(startLoad);
				} else {
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
					} else {
						showToast("数据加载失败");
					}
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.EVENTTYPE_LIST, params, callback);
	}

	//列表数据加载
	public void getActivityOverView(int position) {
		
		if (emptyFresh) {
			showDialog();
			emptyFresh = false;
		}
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(TeaEventListActivity.this)
				.getToken());
		params.put("storeId", SPUtil.getDefault(TeaEventListActivity.this)
				.getStoreId());
		params.put("classify", eventTypeList.get(currentSelect).FValue);
		params.put("pageSize", "15");
		params.put("pageIndex", pageInfoList.get(position).index + "");//当前页吗

		Callback callback = new Callback(tag, this) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (null != strMsg) {
					showToast(strMsg);
				}else {
					showToast("加载失败");
				}
				
				mListView.onRefreshComplete();
				
				//mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					
					String entityList = data.get("entityList").toString();
					pageInfoList.get(currentSelect).total = data.get("pageCount").getAsInt();
					pageInfoList.get(currentSelect).index = data.get("pageIndex").getAsInt();
					
					Gson gson = new Gson();
					List<OverViewEntity> list = gson.fromJson(entityList,new TypeToken<List<OverViewEntity>>() {}.getType());
					
					if (pullStart) {
						if (allEventList.get(currentSelect).size() > 0) {
							allEventList.get(currentSelect).clear();
						}
					}
					//请求结果添加到当前组
					allEventList.get(currentSelect).addAll(list);
					
					overViewAdapter.setList(allEventList.get(currentSelect));
					overViewAdapter.notifyDataSetChanged();
					
					
					//完成刷新
					mListView.onRefreshComplete();
					if (pageInfoList.get(currentSelect).index >= pageInfoList.get(currentSelect).total) {
						mListView.setMode(Mode.PULL_FROM_START);
					}else {
						mListView.setMode(Mode.BOTH);
					}
					
					if (allEventList.get(currentSelect).size() == 0) {
						//emptyLayout.setVisibility(View.VISIBLE);
						//mListView.setVisibility(View.GONE);
						
						mListView.getRefreshableView().removeFooterView(emptyFooterView);
						emptyFooterView = LayoutInflater.from(TeaEventListActivity.this).inflate(R.layout.emptyview_layout, null);
						emptyFooterView.setBackgroundColor(Color.parseColor("#eaeaea"));
						mListView.getRefreshableView().addFooterView(emptyFooterView);
						
					}else {
						
						mListView.getRefreshableView().removeFooterView(emptyFooterView);
						//emptyLayout.setVisibility(View.GONE);
						//mListView.setVisibility(View.VISIBLE);
					}
					
				} else {
					
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
					}else {
						showToast("数据请求失败，请重试");
					}
					
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.ACTIVITYS, params, callback);
	}

	public void initView() {
		triangleDown = this.getResources().getDrawable(R.drawable.spinner_down);
		triangleDown.setBounds(0, 0, triangleDown.getMinimumWidth(),
				triangleDown.getMinimumHeight());
		triangleUp = this.getResources().getDrawable(R.drawable.spinner_up);
		triangleUp.setBounds(0, 0, triangleUp.getMinimumWidth(),
				triangleUp.getMinimumHeight());
		mTitle.setCompoundDrawables(null, null, triangleDown, null);
		
		//显示头部出现分割线
		mListView.getRefreshableView().setHeaderDividersEnabled(false);
		//禁止底部出现分割线 
		mListView.getRefreshableView().setFooterDividersEnabled(false);
	}

	public void homeClick(View v) {
		this.finish();
	}

	
	
	//private PopupWindow mPopup;
	
	//titleView的click事件
	TeaEventThemesListPopWindow popWindow = null;
	private boolean toggle;

	public void popListViewClick(View v) {
		
		if (!hasLoad) {
			showToast("没有数据");
			return;
		}

		if (null == popWindow){

			popWindow = new TeaEventThemesListPopWindow(this, mTitle,eventTypeList);
			FrameLayout.LayoutParams poParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			popwindowContainer.addView(popWindow,0,poParams);

			//控制三角形的隐藏和现实
			if (popWindow.getPopContentViewVisibility() == View.VISIBLE)
				mTitle.setCompoundDrawables(null, null, triangleDown, null);
			else
				mTitle.setCompoundDrawables(null, null, triangleUp, null);

			popWindow.startInAnimation();
			popWindow.setCallback(new BaseTopPopWindow.ToggleValueCallback() {
				@Override
				public void callback(boolean value) {
					mTitle.setCompoundDrawables(null, null, triangleDown, null);
					toggle = value;
				}
			});

			popWindow.setSwitchCallBack(new TeaEventThemesListPopWindow.SwitchCallBack() {
				@Override
				public void switchPosition(int position) {

					toggle = false;

					currentSelect = position;

					if (pageInfoList.get(currentSelect).index == 0) {
						//动画效果
						SwingBottomInAnimationAdapter swingRightInAnimationAdapter = new SwingBottomInAnimationAdapter(overViewAdapter);
						swingRightInAnimationAdapter.setAbsListView(mListView.getRefreshableView());
						mListView.getRefreshableView().setAdapter(swingRightInAnimationAdapter);

						pageInfoList.get(currentSelect).index = 1;
						showDialog();
						getActivityOverView(currentSelect);
					}else {
						overViewAdapter.setList(allEventList.get(currentSelect));
						overViewAdapter.notifyDataSetChanged();

						if (allEventList.get(currentSelect).size() == 0) {
							mListView.setVisibility(View.GONE);
						}else {
							mListView.setVisibility(View.VISIBLE);
						}
					}

					if (eventTypeList.get(currentSelect).FText.equals("全部活动")) {
						mTitle.setText("活动总览");
					}else {
						mTitle.setText(eventTypeList.get(currentSelect).FText);
					}

					//刷新标题
					//mTitle.setText(eventTypeList.get(position).FText);
					mTitle.setCompoundDrawables(null, null, triangleDown, null);
				}
			});

			toggle = true;

		}else{

			if (popWindow.getPopContentViewVisibility() == View.VISIBLE)
				mTitle.setCompoundDrawables(null, null, triangleDown, null);
			else
				mTitle.setCompoundDrawables(null, null, triangleUp, null);

			if (!toggle){
				popWindow.startInAnimation();
			}else{
				popWindow.StartOutAnimation();
			}

			toggle = !toggle;
		}
	}

	@Override
	public void onBackPressed() {
		if (toggle){
			popWindow.StartOutAnimation();
			toggle = false;
			return;
		}
		super.onBackPressed();
	}
}