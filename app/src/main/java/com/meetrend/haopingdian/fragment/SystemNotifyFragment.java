package com.meetrend.haopingdian.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.SystemNotify;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

/**
 * 系统消息
 *
 */
public class SystemNotifyFragment extends BaseFragment {
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	/**
	 * expandableListView
	 */
	@ViewInject(id = R.id.ex_listview)
	ExpandableListView exListView = null;
	
	List<SystemNotify> systemNotifyList = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_system_notify, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mBarTitle.setText(R.string.title_sys_notify);
		
		
		getSystemNotify();
		
	}
	
	
	/**
	 * 获取系统通知
	 */
	public void getSystemNotify() {
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				JsonParser parser = new JsonParser();
            	JsonObject root = parser.parse(t).getAsJsonObject();
            	data = root.get("data").getAsJsonObject();
            	isSuccess = Boolean.parseBoolean(root.get("success").getAsString());
            	
				if (isSuccess) {
					JsonArray jsonArr = data.get("jsonArray").getAsJsonArray();
					systemNotifyList =  new Gson().fromJson(jsonArr,  new TypeToken<List<SystemNotify>>() {}.getType());
					
					exListView.setAdapter(new MessAgeExpandableAdapter(getActivity()));
					
					dimissDialog();
	                
				} else {
					dimissDialog();
				}			
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.SYSTEM_NOTIFY, params, callback);
	}
	
	class MessAgeExpandableAdapter extends BaseExpandableListAdapter {

		private LayoutInflater mLayoutInflater = null;
		SimpleDateFormat simpleFormat = null;
		
		public MessAgeExpandableAdapter(Context context){
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		

		@Override
		public String getChild(int arg0, int arg1) {
			return systemNotifyList.get(arg0).content;
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
		public SystemNotify getGroup(int arg0) {
			return systemNotifyList.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return systemNotifyList.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,boolean isExpanded, View childView, ViewGroup arg4) {
			 
			 ChildHolder cHolder = null;
			 
			 if (childView == null || childView.getTag() == null) {
				 cHolder = new ChildHolder();
				 childView = mLayoutInflater.inflate(R.layout.sys_msg_child_layout_item, null);
				 cHolder.tvContent = (TextView) childView.findViewById(R.id.content_view);
			     childView.setTag(cHolder);
			 }else {
				 cHolder = (ChildHolder)childView.getTag();
			 }
			 
			 SystemNotify systemNotify = systemNotifyList.get(groupPosition);
			 
			 if (TextUtils.isEmpty(systemNotify.content)) 
				 cHolder.tvContent.setText("暂无具体信息");
			 else 
				 cHolder.tvContent.setText(systemNotify.content);
			
			 
			return childView;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View parentView,ViewGroup arg3) {
			
			ParentHolder pHolder = null;
			
			if (parentView == null || parentView.getTag() == null) {
				pHolder = new ParentHolder();
				parentView = mLayoutInflater.inflate(R.layout.item_system_notify, null);
				pHolder.tvSubject = (TextView) parentView.findViewById(R.id.tv_subject);
				pHolder.tvTime = (TextView) parentView.findViewById(R.id.tv_message_time);
				pHolder.indiImg = (ImageView) parentView.findViewById(R.id.mGroupimage);
				
				parentView.setTag(pHolder);
			} else {
				pHolder = (ParentHolder) parentView.getTag();
			}
			
			if(isExpanded){
				pHolder.indiImg.setImageResource(R.drawable.boottom_arrow);
	        }else{
	        	pHolder.indiImg.setImageResource(R.drawable.right_arrow);
	        }
			
			SystemNotify systemNotify = systemNotifyList.get(groupPosition);
			pHolder.tvSubject.setText(systemNotify.subject);
			pHolder.tvTime.setText(simpleFormat.format(new Date(Long.parseLong(systemNotify.sendTime))));
				 
			return parentView;
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
	
	
	private class ParentHolder {
		public TextView tvSubject;
		public TextView tvTime;
		public ImageView indiImg;
	}
	
	private class ChildHolder{
		public TextView tvContent;
	}
	
	
	/**
	 * 返回
	 * 
	 */
	public void onClickHome(View v){
		mActivity.finish();
	}
	
	

}
