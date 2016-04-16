package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Income;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

public class IncomeFragment extends BaseFragment {
	//
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	//
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	// 收入
	@ViewInject(id = R.id.tv_yesterday)
	TextView mYesterDayIncome;
	@ViewInject(id = R.id.tv_receivable)
	TextView mReceivableIncome;
	@ViewInject(id = R.id.tv_total)
	TextView mTotalIncome;
	@ViewInject(id = R.id.tv_month)
	TextView mMonthIncome;
	@ViewInject(id = R.id.tv_store)
	TextView mStoreIncome;
	@ViewInject(id = R.id.tv_online)
	TextView mOnlineIncome;
	
	Income income = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_income, container, false);
		FinalActivity.initInjectedView(this, rootView);
		mBarTitle.setText("收入总览");
		getIncome();
		return rootView;
	}
		
	public void onClickHome(View v){
		mActivity.finish();
	}
	


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {			
			case Code.FAILED:
				showToast("无法获取收入数据");
				break;
			case Code.SUCCESS:	 
				break;
			case Code.EMPTY:
			break;
			} 
		}
	};




	//收入

	private void getIncome() {
		AjaxParams params = new AjaxParams();
//		params.put("token", App.token);
//		params.put("storeId", App.storeId);
		
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
//		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());

		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "income info : " + t);

				if (!isSuccess) {
					mHandler.sendEmptyMessage(Code.FAILED);
					return;
				}

				Gson gson = new Gson();
				income = gson.fromJson(data, Income.class);
				mHandler.sendEmptyMessage(Code.SUCCESS);
			}
 
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
				
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.INCOME_ACCOUNT_URL, params, callback);
	}
	
	//eventbus
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}


}
