package com.meetrend.haopingdian.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.OnlineOrderCRUDActivity;
import com.meetrend.haopingdian.adatper.OrderListAdapter;
import com.meetrend.haopingdian.bean.OnlineOrderDetail;
import com.meetrend.haopingdian.bean.OrderEntity;
import com.meetrend.haopingdian.bean.PageInfo;
import com.meetrend.haopingdian.enumbean.PageStatus;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.OrderRefreshEvent;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.HttpFileUpTool;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.UISwitchButton;

import de.greenrobot.event.EventBus;

/**
 * 订单管理
 *
 */
public class OnlineOrderListFragment extends BaseFragment {
	
	private List<OrderEntity> mOrderList = new ArrayList<OrderEntity>();
	Map<PageStatus,PageInfo> mp = new HashMap<PageStatus,PageInfo>();
	private PageInfo mPageInfo;
	Map<PageStatus,List<OrderEntity>> listMp = new HashMap<PageStatus,List<OrderEntity>>();
	PageStatus mCurStatus = PageStatus.ONLINE_UNCONFIRM;
	private OrderListAdapter mAdapter;
	
	// 未确认
	@ViewInject(id = R.id.uitb_unconfirm, click = "mBtnClick")
	UISwitchButton mUnconfirm;
	//已确认
	@ViewInject(id = R.id.uitb_confirm, click = "mBtnClick")
	UISwitchButton mConfirm;
	//已完成按钮
	@ViewInject(id = R.id.uitb_pay, click = "mBtnClick")
	UISwitchButton mPay;
	// 显示，隐藏主体
	@ViewInject(id = R.id.layout_order_body)
	LinearLayout mOrderBodyLayout;
	
	//
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	//
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	//
	@ViewInject(id = R.id.actionbar_action)
	TextView mBarAction;
	
	@ViewInject(id = R.id.orderlist_empty)
	TextView mEmpty;
	
	@ViewInject(id = R.id.pd_orderlist)
	com.meetrend.haopingdian.widget.ProgressDialog mLoading;
	
	@ViewInject(id = R.id.lv_fragment_order)
	PullToRefreshListView mListView;
	
	public static final String STATUS = "status";
	
	public final String pageSize = "10";

	ExecutorService mExecutor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_onlineorderlist, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		mExecutor = Executors.newSingleThreadExecutor();
		mBarTitle.setText("订单管理");
		
		switchPageStatus(mCurStatus);
		switchListView();
		
		mListView.setMode(Mode.BOTH);
		mListView.setTag(true);
		mExecutor.submit(new DataRunnable(mCurStatus));
		
		return rootView;
	}
		
	/************************* 列表处理 *******************************/	
	private void switchListView() {
		if(mp.containsKey(mCurStatus)){
			mPageInfo = mp.get(mCurStatus);
		}else{
			mPageInfo = new PageInfo();
			mp.put(mCurStatus, mPageInfo);
		}
		
		getOrderList();
		
		mListView.setOnRefreshListener(listener2);
		mListView.setOnItemClickListener(orderItemClickListener);
		mAdapter = new OrderListAdapter(mActivity, mOrderList);
		mListView.setAdapter(mAdapter);
		mListView.setEmptyView(mEmpty);
		mListView.setRefreshing(); //触发listener2		
		mListView.setMode(Mode.PULL_FROM_START);
	} 
	
	OnRefreshListener2<ListView> listener2 = new OnRefreshListener2<ListView> (){
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			newLoadData();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			mExecutor.submit(new DataRunnable(mCurStatus));
		}			
	};
	
     public void newLoadData(){
    	mPageInfo = new PageInfo();
		mp.put(mCurStatus, mPageInfo);
		mListView.setMode(Mode.BOTH);
		mListView.setTag(true);
		mExecutor.submit(new DataRunnable(mCurStatus));
     }
	
	class DataRunnable implements Runnable{
		PageStatus pageStatus;
		public DataRunnable(PageStatus pageStatus){
			this.pageStatus = pageStatus;
		}
		
		@Override
		public void run() {
			 List <NameValuePair> params = new ArrayList <NameValuePair>();  
			 params.add(new BasicNameValuePair("token", SPUtil.getDefault(getActivity()).getToken()));
			 params.add(new BasicNameValuePair("storeId", SPUtil.getDefault(getActivity()).getStoreId()));
			 params.add(new BasicNameValuePair("status",pageStatus.ordinal()+""));
			 params.add(new BasicNameValuePair("pageIndex",mPageInfo.index+""));
			 params.add(new BasicNameValuePair("pageSize", pageSize));
			
			String url = Server.BASE_URL + Server.ORDER_LIST_URL;
			try {
				String result = HttpFileUpTool.post(url, params);
			    if(result!=null){
			    	LogUtil.v(tag, "order list info : " + result);
			    	JsonParser parser = new JsonParser();
			    	JsonObject jsonObj = parser.parse(result).getAsJsonObject();
			    	JsonObject data = jsonObj.getAsJsonObject("data");
					int pageIndex = data.get("pageIndex").getAsInt();
					int pageCount = data.get("pageCount").getAsInt();
					String jsonArrayStr = data.get("records").toString();
					
					mPageInfo.index = pageIndex;
					mPageInfo.total = pageCount;
					
					if (pageCount != 0) {
						Gson gson = new Gson();
						List<OrderEntity> list = gson.fromJson(jsonArrayStr, new TypeToken<List<OrderEntity>>() {
						}.getType());
						
						boolean flag= (Boolean)mListView.getTag();
						if (flag) {
							getOrderList();
							mOrderList.clear();
							flag = false;
							mListView.setTag(flag);
						}
						addOrderToList(list);	
						mOrderList = list;
						mHandler.sendEmptyMessage(Code.SUCCESS);
					} else if (pageCount == 0) {
						mHandler.sendEmptyMessage(Code.EMPTY);
					}
			    }else{
			    	mHandler.sendEmptyMessage(Code.FAILED);
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/***
	 * 
	 * 添加集合
	 * 
	 * @author bob
	 **/
	public void addOrderToList(List<OrderEntity> mOrderList){
		if(listMp.containsKey(mCurStatus) && listMp.get(mCurStatus)!=null){
			listMp.get(mCurStatus).addAll(mOrderList);
		}else{
			listMp.put(mCurStatus,mOrderList);
		}
	}

	
	/***
	 * 
	 * 获取集合
	 * 
	 * @author bob
	 **/
	public void getOrderList(){
		if(listMp.containsKey(mCurStatus) && listMp.get(mCurStatus)!=null){
			mOrderList = listMp.get(mCurStatus);
		}else{
			mOrderList = new ArrayList<OrderEntity>();
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mListView.onRefreshComplete();
			mLoading.setVisibility(View.GONE);
			switch (msg.what) {			
			case Code.FAILED:
				showToast("获取数据失败");
				break;
			case Code.SUCCESS:	 
				mAdapter.setType(mCurStatus);
				getOrderList();
				mAdapter.setList(mOrderList);
				mAdapter.notifyDataSetChanged();
				mPageInfo.index+=1;
				if (mPageInfo.index > mPageInfo.total) {
					mListView.setMode(Mode.PULL_FROM_START);
				}
				break;
			case Code.EMPTY:
				mOrderList.clear();
				mAdapter.notifyDataSetChanged();
				break;
			} 
		}
	};

	OnItemClickListener orderItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			OrderEntity entity = (OrderEntity) parent.getItemAtPosition(position);

			AjaxParams params = new AjaxParams();
			params.put("token", SPUtil.getDefault(getActivity()).getToken());
			params.put("oid", entity.id);
			Callback callback = new Callback(tag,getActivity()) {

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					LogUtil.v(tag, "order list item info : " + t);

					if (!isSuccess) {
						if (data.get("msg") != null) {
							showToast(data.get("msg").getAsString());
						} else {
							showToast("无法获取订单数据");
						}
						return;
					}

					Gson gson = new Gson();
					App.onlineOrderDetail = gson.fromJson(data, OnlineOrderDetail.class);
					mActivity.startActivity(new Intent(mActivity, OnlineOrderCRUDActivity.class));
					//mActivity.overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					showToast("无法获取订单数据");
				}
			};

			FinalHttp finalHttp = new FinalHttp();
			finalHttp.get(Server.BASE_URL+Server.ONLINE_ORDER_DETAIL_URL, params, callback);
		}
	};

	/**
	 * 
	 * 未确认/已确认/已完成
	 * @author bob
	 * 
	 * */
	public void mBtnClick(View v) {
		switch (v.getId()) {
		case R.id.uitb_unconfirm:
			mCurStatus =  PageStatus.ONLINE_UNCONFIRM;
			break;
		case R.id.uitb_confirm:
			mCurStatus = PageStatus.ONLINE_CONFIRM ;
			break;
		case R.id.uitb_pay:
			mCurStatus = mCurStatus.STORE_PAYED;
			break;
		}
		if(mp.containsKey(mCurStatus)){
			mPageInfo = mp.get(mCurStatus);
		}else{
			mPageInfo = new PageInfo();
			mp.put(mCurStatus, mPageInfo);
		}
//		mAdapter.setType(mCurStatus);
//		mAdapter.setList(mOrderList);
//		mAdapter.notifyDataSetChanged();
		mPageInfo.index = 1;
		switchPageStatus(mCurStatus);
//		switchListView();
		mListView.setMode(Mode.BOTH);
		mOrderList.clear();
		mExecutor.submit(new DataRunnable(mCurStatus));
	}
	

	private void switchPageStatus(PageStatus status) {
		mLoading.setVisibility(View.VISIBLE);
		switch (status) {
		case ONLINE_UNCONFIRM:{
			mUnconfirm.setChecked(true);
			mConfirm.setChecked(false);
			mPay.setChecked(false);
			break;
		}
		case ONLINE_CONFIRM:{
			mUnconfirm.setChecked(false);
			mConfirm.setChecked(true);
			mPay.setChecked(false);
			break;
		}
		case STORE_PAYED:
			mUnconfirm.setChecked(false);
			mConfirm.setChecked(false);
			mPay.setChecked(true);
			break;
		}
		
	}
	//收入
	private void showRetryDialog() {
		showToast("无法获取收入数据");
//		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//		builder.setTitle("获取收入信息失败，是否重新获取数据");
//		builder.setNegativeButton("放弃", null);
//		builder.setPositiveButton("重新获取数据", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				getIncome();				
//			}			
//		});
//		builder.create().show();		
	}

	
	//eventbus
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	public void onEventMainThread(OrderRefreshEvent event) {
		newLoadData();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onClickHome(View v){
		mActivity.finish();
	}

}