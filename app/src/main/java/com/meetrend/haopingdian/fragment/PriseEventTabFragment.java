package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.NewEventDetailActivity;
import com.meetrend.haopingdian.bean.EnrollUser;
import com.meetrend.haopingdian.bean.PriseBean;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.fragment.ApplayEventTabFragment.ItemClickListener;
import com.meetrend.haopingdian.fragment.ApplayEventTabFragment.ViewHolder;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.TimeUtil;
import com.meetrend.haopingdian.widget.RoundImageView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 活动详情
 * 
 * 赞
 * 
 * @author 肖建斌
 * 
 */
public class PriseEventTabFragment extends BaseChildOrderListFragment {

	private boolean isPrepare;// 做好准备
	private boolean hasLoad = false;// 是否加载过
	View rootView;
	
	@ViewInject(id = R.id.listview)
	PullToRefreshListView mListView;
	
	@ViewInject(id = R.id.numview)
	TextView headTextView;
	
	@ViewInject(id = R.id.hintview)
	TextView headHintView;
	
	private String eventId;
	private List<PriseBean> priseList;
	private PriseAdapter priseAdapter;
	
	//刷新、加载
	private int pageIndex = 0;
	private int pagecount;
	private final static int PAGESIZE = 20;
	private  boolean isReFreshMode;
	
	//第一次加载数据
	private boolean firstIn = true;
	
	private ListView listView;
	//数据为空
	View emptyFooterView;
	
	@ViewInject(id = R.id.numviewLayout)
	RelativeLayout numviewLayout;
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			 rootView = inflater.inflate(R.layout.fragment_prise_fragment,
					container, false);
			FinalActivity.initInjectedView(this, rootView);
			headTextView.setText("0");
			headHintView.setText("个赞");
			
			eventId = ((NewEventDetailActivity)getActivity()).eventID;
			
			priseList = new ArrayList<PriseBean>();
			priseAdapter = new PriseAdapter(getActivity());
			mListView.setAdapter(priseAdapter);
			mListView.setOnRefreshListener(listener2);
			listView = mListView.getRefreshableView();
			
			isPrepare = true;
			requestList();
		}

		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void requestList() {
		if (!isPrepare || !isVisible || hasLoad) {
			return;
		}
		
		applyRequestList();
	}
	
	//数据为空时的点击刷新
	public void emptyClick(View view){
		pageIndex = 1;
		applyRequestList();
	}
	
	OnRefreshListener2<ListView> listener2 = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			hasLoad = false;
			//刷新
			isReFreshMode = true;
			pageIndex = 1;
			applyRequestList();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			hasLoad = false;
			
			isReFreshMode = false;
			++pageIndex;
			applyRequestList();
		}
	};
	
	
	/**
	 * 点赞列表数据请求
	 * 
	 */
	 private void applyRequestList(){
		 
		 if (firstIn) {
				showDialog();
				firstIn = false;
			}
		 
			AjaxParams params = new AjaxParams();
			params.put("token", SPUtil.getDefault(getActivity()).getToken());
			params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
			params.put("bonusId", eventId);
			params.put("pageSize", PAGESIZE + "");
			params.put("pageIndex", (pageIndex) + "");
			
			Callback callback = new Callback(tag,getActivity()) {

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					dimissDialog();
				}

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					
					dimissDialog();
					
					if (mListView.isRefreshing()) {
						mListView.onRefreshComplete();
					}
					
					try {
						
						if (isSuccess) {
							
							hasLoad = true;
							
							Gson gson = new Gson();
							JsonArray jsonArray = data.get("ret_array").getAsJsonArray();//报名
							pagecount = data.get("pageCount").getAsInt();//总页数
							pageIndex = data.get("pageIndex").getAsInt();//当前页码
							
							List<PriseBean>  datas = gson.fromJson(jsonArray, new TypeToken<List<PriseBean>>() {}.getType());
							if (isReFreshMode) {
								
								if (priseList.size() > 0) {
									priseList.clear();
									priseList.addAll(datas);
								}
								
							}else {
								priseList.addAll(datas);
							}
							
							if (priseList.size() == 0) {
								numviewLayout.setVisibility(View.GONE);
								//数据为空
								listView.removeFooterView(emptyFooterView);
								emptyFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.emptyview_layout, null);
								listView.addFooterView(emptyFooterView);
								
							}else {
								numviewLayout.setVisibility(View.VISIBLE);
								listView.removeFooterView(emptyFooterView);
								
								priseAdapter.notifyDataSetChanged();
								headTextView.setText(priseList.size() +"");//评论的个数
							}
							
							if (pageIndex >= pagecount || priseList.size() < PAGESIZE) {
								mListView.setMode(Mode.PULL_FROM_START);
							}
							
							
						}else {
							//数据加载失败
							Toast.makeText(getActivity(), "数据加载失败，请重试", Toast.LENGTH_SHORT).show();
							mListView.setMode(Mode.PULL_FROM_START);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			};

			CommonFinalHttp finalHttp = new CommonFinalHttp();
			finalHttp.get(Server.BASE_URL + Server.PRISE_LIST, params, callback);
		 }
	
	//适配器
	public class PriseAdapter extends BaseAdapter {
		
		LayoutInflater mLayoutInflater = null;
		Context mContext = null;
		FinalBitmap loader = null;
		
		public PriseAdapter(Context mContext) {
			mLayoutInflater = LayoutInflater.from(mContext);
			this.mContext = mContext;
			loader = FinalBitmap.create(getActivity());
		}

		@Override
		public int getCount() {
			return priseList.size();
		}

		@Override
		public PriseBean getItem(int position) {
			return priseList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("static-access")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
			
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mLayoutInflater.from(mContext).inflate(R.layout.prise_item_layout, null);
				viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.prise_head_view);
				viewHolder.nameView = (TextView) convertView.findViewById(R.id.prise_name_view);
				viewHolder.timeView = (TextView) convertView.findViewById(R.id.prise_time_view);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			final PriseBean priseBean = priseList.get(position);
//			loader.display(viewHolder.imageView, Server.BASE_URL + priseBean.avatarid);
			viewHolder.imageView.setImageURI(Uri.parse(Server.BASE_URL + priseBean.avatarid));
			viewHolder.nameView.setText(priseBean.name);
			
			String time = priseBean.uptime;
			String hintTime = TimeUtil.judgeTime(time);
			viewHolder.timeView.setText(hintTime);
			
			return convertView;
		}
		
	}
	
	class ViewHolder {
		SimpleDraweeView imageView;
		TextView nameView;
		TextView timeView;
	}

}
