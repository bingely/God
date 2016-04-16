package com.meetrend.haopingdian.fragment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.SearchEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.widget.TasksCompletedView;

import de.greenrobot.event.EventBus;

/**
 * 
 * 年份
 * @author 
 *
 */
public class YearFragment extends BaseChildOrderListFragment {
	
	@ViewInject(id = R.id.income_view)
	TasksCompletedView mIncomeView;
	@ViewInject(id = R.id.back_date, click = "backClick")
	TextView mBackDate;
	@ViewInject(id = R.id.current_date)
	TextView mCurrentDate;
	@ViewInject(id = R.id.forward_date, click = "forwardClick")
	TextView mForwardDate;
	
	@ViewInject(id = R.id.online_amount)
	TextView mOnlineAcount;
	@ViewInject(id = R.id.order_amount)
	TextView mOrderAmount;
	@ViewInject(id = R.id.online_yuan)
	TextView mOnlineYuan;
	@ViewInject(id = R.id.order_yuan)
	TextView mOrderYuan;
	
	int year;
	Calendar cc;
	String mDate;
	int mYear;
	
	private View rootView = null;

	@SuppressLint("SimpleDateFormat") 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		EventBus.getDefault().register(this);
		if (rootView == null) {
			
			rootView = inflater.inflate(R.layout.fragment_date_income, container, false);
			FinalActivity.initInjectedView(this, rootView);
			
			isPrepare = true;
			
		    cc=Calendar.getInstance();
			year =	cc.get(Calendar.YEAR);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			mDate = sdf.format(new Date());
			mYear = Integer.parseInt(mDate.substring(0, 4));
			
			initData();
			
			requestList();
			
		}
		
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent != null) {
			parent.removeView(rootView);
		}
		
		return rootView;
	}
	
	private boolean isPrepare;//做好准备
	private boolean hasLoad = false;//是否加载过 
	
	@Override
	public void requestList() {
		if (!isPrepare || !isVisible || hasLoad) {
			return;
		}
		
		load(year);
	}
	
	public void initData(){
		mForwardDate.setVisibility(View.VISIBLE);
		mBackDate.setText(year-1+"年");
		mForwardDate.setText(year+1+"年");
		if(year >= mYear){
			mForwardDate.setVisibility(View.GONE);
		}
		mCurrentDate.setText(year+"年");
	}
	
	public void load(int year){
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("year", year+"");
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "login info : " + t);
				if (isSuccess) {
					JsonObject jsonObj = data.get("args").getAsJsonObject();
					String storeAmount = jsonObj.get("storeAmount").getAsString();
					String onlineAmount = jsonObj.get("onlineAmount").getAsString();
					String sumAmount = jsonObj.get("sumAmount").getAsString();
					float sum = Float.parseFloat(sumAmount);
					float online =Float.parseFloat(onlineAmount);
					float order =Float.parseFloat(storeAmount);
					if(sum!=0){
						mIncomeView.setProgress(online, order,sumAmount);
						float onlinePress = online / sum *100;
						float orderPress = order / sum *100;
						mOnlineAcount.setText(getFloatScale(2,onlinePress)+"%");
						mOrderAmount.setText(getFloatScale(2,orderPress)+"%");
						mOnlineYuan.setText(NumerUtil.setSaveTwoDecimals(onlineAmount)+"元");
						mOrderYuan.setText(NumerUtil.setSaveTwoDecimals(storeAmount) + "元");
					}else{
						mIncomeView.setProgress(0, 0, "0");
						mOnlineAcount.setText("0%");
						mOrderAmount.setText("0%");
						mOnlineYuan.setText(NumerUtil.setSaveTwoDecimals(onlineAmount)+"元");
						mOrderYuan.setText(NumerUtil.setSaveTwoDecimals(storeAmount) + "元");
					}
				} else {
					Message msg = new Message();
					msg.what = Code.FAILED;
					msg.obj = data.get("msg").getAsString();
					mHandler.sendMessage(msg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.NEW_INCOME_ACCOUNT, params, callback);
	}
	
	public void backClick(View v){
		--year;
		load(year);
		initData();
	}
	
	public void forwardClick(View v){
		++year;
		
		if(year > mYear){
			year = mYear;
		}
		
		load(year);
		initData();
	}
	
	public float getFloatScale(int scale,float onlinePross){
		BigDecimal   b  =   new BigDecimal(onlinePross);  
		float   f1   =  b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();  
		return f1;
	}
	
	
	public void onEventMainThread(SearchEvent event) {
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}


	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.FAILED:
				showToast("获取数据失败");
				break;
			case Code.SUCCESS:
				break;
			case Code.EMPTY:
				break;
			}
		}
	};

	

}
