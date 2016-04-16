package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.NewEventDetailActivity;
import com.meetrend.haopingdian.bean.EnrollUser;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.TimeUtil;
import com.meetrend.haopingdian.util.ExpandCollapseHelper;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 详情
 * 报名
 * @author 肖建斌
 *
 */
public class ApplayEventTabFragment extends BaseChildOrderListFragment{
	
	View rootView = null;
	
	private boolean isPrepare;//做好准备
	private boolean hasLoad = false;//是否加载过 
	
	@ViewInject(id = R.id.listview)
	PullToRefreshListView mListView;
	@ViewInject(id = R.id.empty_layout,click = "emptyClick")
	LinearLayout emptylayout;
	
	@ViewInject(id = R.id.numview)
	TextView headTextView;
	
	@ViewInject(id = R.id.hintview)
	TextView headHintView;
	
	//活动id
	private String eventId;
	private List<EnrollUser> applyList = null;
	private ApplyAdapter applyAdapter = null;
	
	//数据为空
	View emptyFooterView;
	private ListView listView;
	
	@ViewInject(id = R.id.numviewLayout)
	RelativeLayout numviewLayout;

	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_applyeventtabfragment, container, false);
			FinalActivity.initInjectedView(this, rootView);
			
			headTextView.setText("0");
			headHintView.setText("个报名");
			
			eventId = ((NewEventDetailActivity)getActivity()).eventID;
			applyList = new ArrayList<EnrollUser>();
			applyAdapter = new ApplyAdapter(getActivity());
			mListView.setAdapter(applyAdapter);
			mListView.setMode(com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode.DISABLED);
			listView = mListView.getRefreshableView();
			listView.setDividerHeight(1);
			listView.setDivider(getResources().getDrawable(R.color.listview_line_color));
			
			
			
			isPrepare = true;
			requestList();
		}
		
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent != null) {
			parent.removeView(rootView);
		}
		
		return rootView;
	}
	
	

	@Override
	public void requestList() {
		if (!isPrepare || !isVisible || hasLoad) {
			return;
		}
		applyRequestList(eventId);
	}
	
	//数据为空时的点击刷新
	public void emptyClick(View view){
		hasLoad = false;
		applyRequestList(eventId);
	}
	
	
	//适配器
	private List<Long> mVisibleIds;//存储itemid
	private Map<Long, View> mItemViewContainerMap;//存储item中的隐藏和显示的部分
	private int mLimit = 1;//限制只展示一个view
	
	public class ApplyAdapter extends BaseAdapter {
		
		LayoutInflater mLayoutInflater = null;
		Context mContext = null;
		
		public ApplyAdapter(Context mContext) {
			mLayoutInflater = LayoutInflater.from(mContext);
			this.mContext = mContext;
			
			mVisibleIds = new ArrayList<Long>();
			mItemViewContainerMap = new HashMap<Long, View>();
		}

		@Override
		public int getCount() {
			return applyList.size();
		}

		@Override
		public EnrollUser getItem(int position) {
			return applyList.get(position);
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
				convertView = mLayoutInflater.from(mContext).inflate(R.layout.apply_item_layout, null);
				viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.apply_head_view);
				viewHolder.nameView = (TextView) convertView.findViewById(R.id.apply_name_view);
				viewHolder.timeView = (TextView) convertView.findViewById(R.id.apply_time_view);
				viewHolder.phoneView = (TextView) convertView.findViewById(R.id.apply_phone_view);
				viewHolder.callLayout = (RelativeLayout) convertView.findViewById(R.id.call_layout_view);
				viewHolder.msgLayout = (RelativeLayout) convertView.findViewById(R.id.msg_layout_view);
				viewHolder.relative_top_layout = (RelativeLayout) convertView.findViewById(R.id.relative_top);
				viewHolder.connactLayout = (LinearLayout) convertView.findViewById(R.id.connect_layout);
				viewHolder.needPayTextView = (TextView) convertView.findViewById(R.id.is_evvent_need_paytext);
				viewHolder.needPayNumView = (TextView) convertView.findViewById(R.id.event_needpay_num);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			final EnrollUser enrollUser = applyList.get(position);
			viewHolder.imageView.setImageURI(Uri.parse(Server.BASE_URL + enrollUser.head));
			viewHolder.nameView.setText(enrollUser.userName);
			viewHolder.phoneView.setText("手机：" + enrollUser.userMobile);
			viewHolder.needPayTextView.setText(enrollUser.chargeName);
			viewHolder.needPayNumView.setText("¥"+enrollUser.money);
			
			String time = enrollUser.erolltime;
			String hintTime = TimeUtil.judgeTime(time);
			viewHolder.timeView.setText(hintTime);
			
			//操作逻辑部分
			if (mLimit > 0) {
				if (mVisibleIds.contains(getItemId(position))) {
					mItemViewContainerMap.put(getItemId(position), convertView);
				} else if (mItemViewContainerMap.containsValue(convertView) && !mVisibleIds.contains(getItemId(position))) {
					mItemViewContainerMap.remove(getItemId(position));
				}
			}
			
			viewHolder.connactLayout.setVisibility(mVisibleIds.contains(getItemId(position)) ? View.VISIBLE : View.GONE);
			viewHolder.connactLayout.setTag(getItemId(position));
			
			//title click事件
			viewHolder.relative_top_layout.setOnClickListener(new ItemClickListener(viewHolder.connactLayout));
			
			//打电话
			viewHolder.callLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent();

					//系统默认的action，用来打开默认的电话界面
					intent.setAction(Intent.ACTION_DIAL);
					//需要拨打的号码
					intent.setData(Uri.parse("tel:"+enrollUser.userMobile));
					startActivity(intent);
				}
			});
			//发信息
			viewHolder.msgLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent();
					//系统默认的action，用来打开默认的短信界面
					intent.setAction(Intent.ACTION_SENDTO);
					//需要发短息的号码
					intent.setData(Uri.parse("smsto:"+ enrollUser.userMobile));
					startActivity(intent);
				}
			});
			
			return convertView;
		}
		
	}
	
	
	
	class ViewHolder {
		SimpleDraweeView imageView;
		TextView nameView;
		TextView timeView;
		TextView phoneView;
		RelativeLayout relative_top_layout;
		LinearLayout connactLayout; 
		RelativeLayout callLayout;
		RelativeLayout msgLayout;
		TextView needPayTextView;
		TextView needPayNumView;
	}
	
	//联系人点击事件
	public class ItemClickListener implements OnClickListener {
		
		private View contentview = null;
		
		public ItemClickListener(View contentview){
			this.contentview = contentview;
		}

		@Override
		public void onClick(View v) {
				
			//if (viewHolder.connactLayout.getVisibility() == View.GONE) {
			//	viewHolder.connactLayout.setVisibility(View.VISIBLE);
			//}else {
			//	viewHolder.connactLayout.setVisibility(View.GONE);
			//}
			
			boolean isVisible = contentview.getVisibility() == View.VISIBLE;
			if (!isVisible && mLimit > 0 && mVisibleIds.size() >= mLimit) {
				Long firstId = mVisibleIds.get(0);
				View firstEV = mItemViewContainerMap.get(firstId);
				if (firstEV != null) {
					ViewHolder firstVH = ((ViewHolder) firstEV.getTag());
					ViewGroup contentParent = firstVH.connactLayout;
					ExpandCollapseHelper.animateCollapsing(contentParent);//隐藏内容动画效果
					mItemViewContainerMap.remove(mVisibleIds.get(0));
				}
				mVisibleIds.remove(mVisibleIds.get(0));
			}

			if (isVisible) {
				ExpandCollapseHelper.animateCollapsing(contentview);
				mVisibleIds.remove(contentview.getTag());
				mItemViewContainerMap.remove(contentview.getTag());
			} else {
				
				ExpandCollapseHelper.animateExpanding(contentview);
				mVisibleIds.add((Long) contentview.getTag());

				if (mLimit > 0) {
					View parent = (View) contentview.getParent();
					mItemViewContainerMap.put((Long) contentview.getTag(), parent);
				}
				
			}
		}
		
	}
	
	/**
	 * 报名列表数据请求
	 * 
	 * @param eventId 活动id
	 */
	 private void applyRequestList(String eventId){
		 
			AjaxParams params = new AjaxParams();
			params.put("token", SPUtil.getDefault(getActivity()).getToken());
			params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
			params.put("bonusId", eventId);
			
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
							JsonArray enrollArray = data.get("enrollUser").getAsJsonArray();//报名
							List<EnrollUser>  enrollist = gson.fromJson(enrollArray, new TypeToken<List<EnrollUser>>() {}.getType());
							if (null != enrollist && enrollist.size() > 0) {
								applyList.addAll(enrollist);
							}
							
							if (applyList.size() == 0) {
								
								numviewLayout.setVisibility(View.GONE);
								//数据为空
								//emptylayout.setVisibility(View.VISIBLE);
								listView.removeFooterView(emptyFooterView);
								emptyFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.emptyview_layout, null);
								listView.addFooterView(emptyFooterView);
								
								listView.setDivider(null);
							    //listView.setDividerHeight(0);
								
							}else {
								
								 //以下两句代码的顺序不能调换，否则没有效果
								listView.setDivider(getResources().getDrawable(R.color.listview_line_color));
							    listView.setDividerHeight(1);
								
								numviewLayout.setVisibility(View.VISIBLE);
								//emptylayout.setVisibility(View.GONE);
								listView.removeFooterView(emptyFooterView);
								
								applyAdapter.notifyDataSetChanged();
								headTextView.setText(applyList.size() +"");//评论的个数
							}
						}else {
							//数据加载失败
							if (data.has("msg")){
								Toast.makeText(getActivity(),data.get("msg").getAsString(), Toast.LENGTH_SHORT).show();
							}else
							Toast.makeText(getActivity(), "数据加载失败，请重试", Toast.LENGTH_SHORT).show();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			};

			CommonFinalHttp finalHttp = new CommonFinalHttp();
			finalHttp.get(Server.BASE_URL + Server.JOIN_ARRIVE, params, callback);
		 }

	
	 
}