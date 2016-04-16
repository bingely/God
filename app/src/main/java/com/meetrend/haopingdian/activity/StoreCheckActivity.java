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
import com.meetrend.haopingdian.bean.CheckBean;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.event.StoreCheckEvent;
import com.meetrend.haopingdian.event.StoreCheckRefreshEvent;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * 
 * 店内盘点
 * 
 */
public class StoreCheckActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home, click = "finishActivity")
	ImageView mBarHome;
	
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	//添加
	@ViewInject(id = R.id.actionbar_action,click = "addClick")
	ImageView addBtn;
	
	@ViewInject(id = R.id.checklistview)
	PullToRefreshListView pullToRefreshListView;
	
	private ListView listView;
	private List<CheckBean> mList;
	private StoreCheckAdapter storeCheckAdapter = null;
	
	//分页
	private int mPageCount;
	private int mPageIndex = 1;
	
	private View emptyFooterView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storecheck);
		EventBus.getDefault().register(this);
		FinalActivity.initInjectedView(this);
		
		initViews();
		initDatas();
		
		requestCheckList();


	}

	//刷新数据
	public void onEventMainThread(StoreCheckRefreshEvent freshEvent) {
		if (mList.size() > 0) {
			mList.clear();
		}

		mPageIndex = 1;
		requestCheckList();
	}
	

	private void initViews(){
		mBarTitle.setText("店内盘点");
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);
		listView = pullToRefreshListView.getRefreshableView();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					
				CheckBean checkBean = (CheckBean) listView.getAdapter().getItem(position);
				Intent intent = new Intent(StoreCheckActivity.this, StoreCheckDetailActivity.class);
				intent.putExtra("id", checkBean.id);
				startActivity(intent);
			}
		});
	}
	
	private void initDatas(){
		mList = new ArrayList<CheckBean>();
		storeCheckAdapter = new StoreCheckAdapter(mList);
		listView.setAdapter(storeCheckAdapter);
		pullToRefreshListView.setOnRefreshListener(listener2);
	}
	
	//添加
	public void addClick(View view){
		
		Intent intent = new Intent(StoreCheckActivity.this, AddStoreCheckActivity.class);
		startActivity(intent);
	}
	
	OnRefreshListener2<ListView> listener2 = new OnRefreshListener2<ListView> (){
		
		//刷新
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			
		}

		//加载更多
		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			
			++ mPageIndex;
			requestCheckList();
			
		}			
	};
	
	
	public  void finishActivity(View view){
		finish();
	}
	
	
	//盘点列表选择
	private void requestCheckList() {
		
		showDialog("加载中...");

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(StoreCheckActivity.this).getToken());
		params.put("pageSize", 15+"");
		params.put("pageIndex", mPageIndex +"");

		Callback callback = new Callback(tag, StoreCheckActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (pullToRefreshListView.isRefreshing()) {
					pullToRefreshListView.onRefreshComplete();
				}
				
				if (isSuccess) {
					Gson gson = new Gson();
					mPageIndex = data.get("pageIndex").getAsInt();
					mPageCount = data.get("pageCount").getAsInt();
					JsonArray jsonArrayStr = data.get("list").getAsJsonArray();
					List<CheckBean> tlist = gson.fromJson(jsonArrayStr,new TypeToken<List<CheckBean>>() {}.getType());
					
					if (mPageIndex >= mPageCount) {
						pullToRefreshListView.setMode(Mode.DISABLED);
					}
					
					
					mList.addAll(tlist);	
					
					if (mList.size() == 0) {
						listView.removeFooterView(emptyFooterView);
						emptyFooterView = LayoutInflater.from(StoreCheckActivity.this).inflate(R.layout.emptyview_layout, null);
						listView.addFooterView(emptyFooterView);
					}else{
						listView.removeFooterView(emptyFooterView);
						storeCheckAdapter.notifyDataSetChanged();
					}
					
					if (mPageIndex >= mPageCount) {
						pullToRefreshListView.setMode(Mode.DISABLED);
					}else {
						pullToRefreshListView.setMode(Mode.PULL_FROM_END);
					}
					
				}else {
					
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}else {
						showToast("数据加载失败，请重试");
					}
					
				}
				
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.STORE_ORDER_LIST, params,callback);
	}
	
	
	private class StoreCheckAdapter extends BaseAdapter {
		
		private List<CheckBean> list;
		LayoutInflater mLayoutInflater;
		
		public StoreCheckAdapter(List<CheckBean> list){
			this.list = list;
			mLayoutInflater = LayoutInflater.from(StoreCheckActivity.this);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public CheckBean getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			CheckBean checkBean = getItem(position);
			
			ViewHolder viewHolder = null;
			
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.storecheck_list_item_layout, null);
				viewHolder.checkIdView = (TextView) convertView.findViewById(R.id.check_id);
				viewHolder.checkTimeView = (TextView) convertView.findViewById(R.id.check_time);
				viewHolder.checkExcutorView = (TextView) convertView.findViewById(R.id.check_excutor);
				
				convertView.setTag(viewHolder);
				
			}else {
				
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			viewHolder.checkIdView.setText(checkBean.num);
			String time = checkBean.time.substring(0, 16);
			viewHolder.checkTimeView.setText(time);
			viewHolder.checkExcutorView.setText("经办人：" + checkBean.name);
			
			return convertView;
		}
		
		class ViewHolder{
			TextView checkIdView;
			TextView checkTimeView;
			TextView checkExcutorView;
		}
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}