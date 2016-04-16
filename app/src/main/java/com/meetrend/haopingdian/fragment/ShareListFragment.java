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
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.ShareGridAdapter;
import com.meetrend.haopingdian.bean.SharePeople;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.GridView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShareListFragment extends BaseFragment{
	@ViewInject(id = R.id.share_gridview)
	GridView shareGridView;
	@ViewInject(id = R.id.emptyview)
	TextView emptyView;
	
	String eventId;
	private List<SharePeople> shareList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		    View rootView = inflater.inflate(R.layout.fragment_sharelist, container, false);
		    FinalActivity.initInjectedView(this,rootView);
		    Bundle bundle = getArguments();
		    eventId = bundle.getString("eventId");
		    shareList = new ArrayList<SharePeople>(); 
		    requestList(eventId);
		return rootView;
	}
	
	//获得分享人列表
	 private void  requestList(String eventId){
		 
			AjaxParams params = new AjaxParams();
			params.put("token", SPUtil.getDefault(getActivity()).getToken());
			params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
			params.put("bonusId", eventId);
			
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
						JsonArray jsonArray = data.get("userList").getAsJsonArray();
						shareList = gson.fromJson(jsonArray, new TypeToken<List<SharePeople>>() {}.getType());
						if (shareList.size() == 0) {
							emptyView.setVisibility(View.VISIBLE);
							return;
						}
						shareGridView.setAdapter(new ShareGridAdapter(getActivity(), shareList));
					}else {
						emptyView.setVisibility(View.VISIBLE);
						emptyView.setText("亲，出现异常了!");
						showToast("分享人列表加载失败");
					}
				}
			};
			FinalHttp finalHttp = new FinalHttp();
			finalHttp.get(Server.BASE_URL + Server.SHARE_PEOPLE_LIST, params, callback);
		 }
	
	

}
