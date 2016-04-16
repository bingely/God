package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.OrderHistoryAdapter;
import com.meetrend.haopingdian.bean.OrderHistoryEntity;
import com.meetrend.haopingdian.bean.PageInfo;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

public class InventoryHistoryFragment extends BaseFragment {
	private View rootView;
	private PullToRefreshListView mListView;

	private List<OrderHistoryEntity> mOrderList;
	private PageInfo mPageInfo;
	private BaseAdapter mAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		rootView = inflater.inflate(R.layout.fragment_inventory_history, container, false);
		TextView empty = (TextView) rootView.findViewById(R.id.lv_inventory_history_empty);
		mListView = (PullToRefreshListView) rootView.findViewById(R.id.lv_inventory_history);
		
		mOrderList = new ArrayList<OrderHistoryEntity>();
		mPageInfo = new PageInfo();
		mAdapter = new OrderHistoryAdapter(mActivity, mOrderList);
		mListView.setAdapter(mAdapter);
		mListView.setEmptyView(empty);
		mListView.setOnRefreshListener(listener);		
		mListView.setRefreshing();
		return rootView;
	}

	private OnRefreshListener<ListView> listener = new OnRefreshListener<ListView>() {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			SPUtil util = SPUtil.getDefault(mActivity);
			String productId = util.getProductId();
			
			AjaxParams params = new AjaxParams();
			params.put("token", SPUtil.getDefault(getActivity()).getToken());
			params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
			params.put("id", productId);

			Callback callback = new Callback(tag,getActivity()) {
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					mHandler.sendEmptyMessage(Code.FAILED);
				}

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					LogUtil.v(tag, "order history list info : " + t);
					int pageIndex = data.get("pageIndex").getAsInt();
					int pageCount = data.get("pageCount").getAsInt();
					String jsonArrayStr = data.get("records").toString();

					if (!isSuccess) {
						mHandler.sendEmptyMessage(Code.FAILED);
					} else if (pageCount != 0) {
						Gson gson = new Gson();
						List<OrderHistoryEntity> list = gson.fromJson(jsonArrayStr, new TypeToken<List<OrderHistoryEntity>>() {
						}.getType());
						mOrderList.addAll(list);
						mPageInfo = new PageInfo(pageIndex, pageCount);
						mHandler.sendEmptyMessage(Code.SUCCESS);
					} else if (pageCount == 0) {
						mHandler.sendEmptyMessage(Code.EMPTY);
					}
				}
			};

			FinalHttp finalHttp = new FinalHttp();
			finalHttp.get(Server.BASE_URL + Server.ORDER_HISTORY_URL, params, callback);
		}
		
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mListView.onRefreshComplete();	
			LogUtil.d(tag, mPageInfo.toString());
			switch (msg.what) {
			case Code.FAILED:
				showToast("获取数据失败");
				break;
			case Code.SUCCESS:
				mAdapter.notifyDataSetChanged();
				++mPageInfo.index;
				if (mPageInfo.index > mPageInfo.total) {
					mListView.setMode(Mode.DISABLED);
				}
				break;
			case Code.EMPTY:
				mListView.setMode(Mode.DISABLED);
				break;
			}
		}
	};
}
