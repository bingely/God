package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Inviter;
import com.meetrend.haopingdian.bean.RecommendGridItemAdapter;
import com.meetrend.haopingdian.bean.RecommendPerson;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.RoundImageView;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendFragment extends BaseFragment{
	
	@ViewInject(id = R.id.join_ex_listview)
	ExpandableListView exListView;
	
	private PeopelsExpandableAdapter adapter;
	private String eventId;
	private int lastVisibleItemIndex;
	private int mCurrentIndex =1;
	private int mPageCount;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recomment, container,false);
		FinalActivity.initInjectedView(this, view);
		
		if (getArguments() != null) {
			Bundle bundle = getArguments();
			eventId = bundle.getString("id");
		}
		adapter = new PeopelsExpandableAdapter();
		exListView.setAdapter(adapter);
		requestList(eventId);
		exListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastVisibleItemIndex == adapter.getGroupCount() && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				
					if (mCurrentIndex <= mPageCount) {
						// footerView.setVisibility(View.VISIBLE);
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
					lastVisibleItemIndex = firstVisibleItem + visibleItemCount - 1;
			}
		});
		return view;
	}
	
	/**
	 * 
	 * 列表数据请求
	 * */
	 private void  requestList(String eventId){
		 
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("bonusId", eventId);
		params.put("pageSize", "20");
		params.put("pageIndex", mCurrentIndex+"");
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					Gson gson = new Gson();
					JsonArray jsonArray = data.get("userArray").getAsJsonArray();
					List<RecommendPerson>  dataList = gson.fromJson(jsonArray, new TypeToken<List<RecommendPerson>>() {}.getType());
					adapter.setList(dataList);
					adapter.notifyDataSetChanged();
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.JOIN_RECOMMENT, params, callback);
	 }
	
	 class PeopelsExpandableAdapter extends BaseExpandableListAdapter {
		 
		 private LayoutInflater layoutInflater;
		 private FinalBitmap loader;
		 
		 private List<RecommendPerson> list = new ArrayList<RecommendPerson>();
	
		 public PeopelsExpandableAdapter(){
			 layoutInflater = LayoutInflater.from(getActivity());
			 loader = FinalBitmap.create(getActivity());
			 loader.configLoadingImage(R.drawable.loading_default);
			 loader.configLoadfailImage(R.drawable.loading_failed);
		 }
		 
		 public void setList(List<RecommendPerson> list){
			 this.list = list;
		 }
		 
		@Override
		public Inviter getChild(int arg0, int arg1) {
			return list.get(arg0).invite.get(arg1);
		}
	
		@Override
		public long getChildId(int arg0, int arg1) {
			return 0;
		}
	
		@Override
		public int getChildrenCount(int arg0) {
			return 1;
		}
	
		@Override
		public RecommendPerson getGroup(int arg0) {
			return list.get(arg0);
		}
	
		@Override
		public int getGroupCount() {
			return list.size();
		}
	
		@Override
		public long getGroupId(int arg0) {
			
			return 0;
		}
	
		@Override
		public View getChildView(int groupPosition, int childPosition,boolean arg2, View childView, ViewGroup arg4) {
			
			ChildHolder childHolder = null;
			if (childView == null) {
				childHolder = new ChildHolder();
				childView = layoutInflater.inflate(R.layout.recommend_child_item, null);
				childHolder.gridView = (com.meetrend.haopingdian.widget.GridView) childView.findViewById(R.id.recomment_gridview);
				childView.setTag(childHolder);
			}else {
				childHolder = (ChildHolder) childView.getTag();
			}
				childHolder.gridView.setAdapter(new RecommendGridItemAdapter(getActivity(), list.get(groupPosition).invite));
			return childView;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean arg1, View groupView,ViewGroup arg3) {
			
			GroupHoler groupHoler = null;
			
			if (groupView == null) {
				groupHoler = new GroupHoler();
				groupView = layoutInflater.inflate(R.layout.recommend_parent_list_item, null);
				groupHoler.parentImg = (RoundImageView) groupView.findViewById(R.id.parent_img);
				groupHoler.checkOff = (ImageView) groupView.findViewById(R.id.check_off_img);
				groupHoler.parentName = (TextView) groupView.findViewById(R.id.parent_name);
				groupHoler.peopleNum = (TextView) groupView.findViewById(R.id.people_num_tv);
				groupHoler.phoneView = (TextView) groupView.findViewById(R.id.phone);
				groupView.setTag(groupHoler);
			}else {
				groupHoler = (GroupHoler) groupView.getTag();
			}
			RecommendPerson person = getGroup(groupPosition);
			loader.display(groupHoler.parentImg, Server.BASE_URL + person.head);
			
			
			groupHoler.parentName.setText(person.userName);
			if (person.isSign.equals("true")) {
				groupHoler.checkOff.setVisibility(View.VISIBLE);
			}else {
				groupHoler.checkOff.setVisibility(View.GONE);
				
			   //灰色头像设置
//			   Drawable mDrawable = groupHoler.parentImg.getDrawable();
//		       mDrawable.mutate();  
//		       ColorMatrix cm = new ColorMatrix();  
//		       cm.setSaturation(0);  
//		       ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);  
//		       mDrawable.setColorFilter(cf); 
//		       groupHoler.parentImg.setImageDrawable(mDrawable);
			}
			
			groupHoler.peopleNum.setText(person.inviteNum);//人数
			groupHoler.phoneView.setText(person.userMobile);//电话
			return groupView;
		}
	
		@Override
		public boolean hasStableIds() {
			return true;
		}
	
		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}
	 }
	 
	 public class GroupHoler{
		 
		 RoundImageView parentImg;
		 ImageView checkOff;
		 TextView parentName;
		 TextView phoneView;
		 TextView peopleNum;
	 }
	 
	 public class ChildHolder{
		 com.meetrend.haopingdian.widget.GridView gridView;
	 }
}
