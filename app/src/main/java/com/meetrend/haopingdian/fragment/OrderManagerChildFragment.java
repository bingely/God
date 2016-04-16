package com.meetrend.haopingdian.fragment;

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
import com.meetrend.haopingdian.activity.OnlineOrderCRUDActivity;
import com.meetrend.haopingdian.adatper.NewOrderListAdapter;
import com.meetrend.haopingdian.bean.OrderEntity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 订单管理对应 tagFragment 对应的Fragment
 * 
 * @author 肖建斌
 * 
 * 综代码调试：只会销毁view 并不会释放变量所保存的值
 *
 */
public class OrderManagerChildFragment extends BaseChildOrderListFragment {
	
	private final static String TAG = OrderManagerChildFragment.class.getName();
	
	/**
	 *  状态
	 * 	Unconfirmed                待确认
	 *	UnPay                      待付款
	 *	Payed                      待发货
	 *	Finished                   已完成
	 *	Canceled                   已取消
	 */
	private String mStatus;
	
	/**
	 * 查询方案
	 * 
	 * orderSource  整数型             （0：全部 1：门店，2：线上）
	 */
	private String  mOrderSource;
	
	private int mPageIndex = 0;
	private int mPageCount;
	private List<OrderEntity> mList;
	
	
	@ViewInject(id = R.id.ordermanagerlistview)
	PullToRefreshListView mListView;
	
	private ListView listView;
	
	/**
	 * 内容为空显示ui
	 */
	@ViewInject(id = R.id.orderlist_empty)
	TextView mEmptyView;
	
	
	
	private NewOrderListAdapter mOrderListAdapter = null ;
	
	private int dex;
	
	/**
	 * 加载模式 1 标识刷新 2 标识 加载更多
	 */
	private int loadType = 2;
	
	//第一次加载数据
	private boolean iSfirstLoad = true;
	
	//数据为空
	View emptyFooterView;

	public OrderManagerChildFragment(String status,String orderSource,int dex){
		mStatus = status;
		mOrderSource = orderSource;
		this.dex = dex;
	}
	
	private View rootView = null;
	
	private boolean isPrepare;//做好准备
	private boolean hasLoad = false;//是否加载过 
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mActivity = this.getActivity();
		
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.ordermanagerchildfragment_layout, container, false);
			FinalActivity.initInjectedView(this, rootView);
			
			mList = new ArrayList<OrderEntity>();
			mOrderListAdapter = new NewOrderListAdapter(getActivity(), mList,mStatus,mOrderSource);
		
			SwingBottomInAnimationAdapter swingRightInAnimationAdapter = new SwingBottomInAnimationAdapter(mOrderListAdapter);
			swingRightInAnimationAdapter.setAbsListView(mListView.getRefreshableView());
			mListView.getRefreshableView().setAdapter(swingRightInAnimationAdapter);
			
			mListView.setMode(Mode.BOTH);
			//mListView.setAdapter(mOrderListAdapter);
			mListView.setOnRefreshListener(listener2);
			mListView.setOnItemClickListener(orderItemClickListener);
			
			listView = mListView.getRefreshableView();
			
			isPrepare = true;
			requestList();
		}
		
		//Log.i("## dex", "---------onCreateView------"+dex);
		
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent != null) {
			parent.removeView(rootView);
		}
		
		return rootView;
	}
	
	OnRefreshListener2<ListView> listener2 = new OnRefreshListener2<ListView> (){
		
		//刷新
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			
			hasLoad = false;//这样标志可以加载数据了
			
			loadType = 1;
			mPageIndex = 0;
			
			if (mList.size() > 0) {
				mList.clear();
			}
			
			requestList();
		}

		//加载更多
		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			
			hasLoad = false;
			
			loadType = 2;
			requestList();
		}			
	};
	
	OnItemClickListener orderItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			OrderEntity entity = (OrderEntity) parent.getItemAtPosition(position);
			if (null == entity) {
				return;
			}
			
			Intent intent = new Intent(mActivity, OnlineOrderCRUDActivity.class);
			intent.putExtra("orderid", entity.id);//订单id
			intent.putExtra("orderSource", entity.orderSource);//来源
			mActivity.startActivity(intent);
			//mActivity.overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
		}
	};
	
	
	/**
	 * 
	 * 列表请求
	 * 
	 */
	@Override
	public void requestList() {
		//Log.i(TAG, "---当前页--"+dex+"----isPrepare----"+ isPrepare +"----isVisible---"+isVisible+"------hasLoad----"+"----"+hasLoad);
		
		if (!isPrepare || !isVisible || hasLoad) {
			return;
		}
		
		if (iSfirstLoad) {
			showDialog();
			iSfirstLoad = false;
		}
		
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("pageSize", 15+"");
		params.put("orderSource", mOrderSource);
		params.put("status", mStatus);
		params.put("pageIndex", (++mPageIndex)+"");
		
		Callback callback = new Callback(tag,getActivity()) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				hasLoad = true;
				
				if (mListView.isRefreshing()) {
					mListView.onRefreshComplete();
				}
				
				dimissDialog();
				
				if (isSuccess) {
					mPageIndex = data.get("pageIndex").getAsInt();
					mPageCount = data.get("pageCount").getAsInt();
					Log.i(TAG, "## mPageIndex == " + mPageIndex);
					Log.i(TAG, "## mPageCount == " + mPageCount);
					JsonArray dataArray = data.get("records").getAsJsonArray();
					
					if (dataArray.size() > 0) {
						Gson gson = new Gson();
						List<OrderEntity> tempList = gson.fromJson(dataArray, 
								new TypeToken<List<OrderEntity>>() {}.getType());
						mList.addAll(tempList);
						mOrderListAdapter.setList(mList);
						mOrderListAdapter.notifyDataSetChanged();
					}
					
					
					if (mList.size() == 0) {
						
						listView.removeFooterView(emptyFooterView);
						
						//mEmptyView.setVisibility(View.VISIBLE);
						
						emptyFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.emptyview_layout, null);
						listView.addFooterView(emptyFooterView);
						
					}else{
						
						//if (mEmptyView.getVisibility() == View.VISIBLE) {
						//	mEmptyView.setVisibility(View.GONE);
						//}
						listView.removeFooterView(emptyFooterView);
					}
					
					if (mPageIndex >= mPageCount) {
						mListView.setMode(Mode.PULL_FROM_START);
					}else {
						mListView.setMode(Mode.BOTH);
					}
					
				}else {
					if (data.has("msg")){
						String msgStr = data.get("msg").getAsString();
						showToast(msgStr);
					}

				}
				
				if (loadType == 1) {
					loadType = 2;//重置为加载更多模式
				}
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hasLoad = false;
				dimissDialog();
			}
		};
		
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.ORDER_MANAGER_LIST, params, callback);
	}
}