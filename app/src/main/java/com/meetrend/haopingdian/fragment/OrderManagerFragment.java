package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.DataCenterActivity;
import com.meetrend.haopingdian.activity.IncomeActivity;
import com.meetrend.haopingdian.activity.MarketMainActivity;
import com.meetrend.haopingdian.activity.MarketReferenceActivity;
import com.meetrend.haopingdian.activity.NewBuidEventActivity;
import com.meetrend.haopingdian.activity.NewOrderManagerActivity;
import com.meetrend.haopingdian.activity.OrderManagerActivity;
import com.meetrend.haopingdian.activity.ProductMangerActivity;
import com.meetrend.haopingdian.activity.ProductReportoryActivity;
import com.meetrend.haopingdian.activity.SignRecordsActivity;
import com.meetrend.haopingdian.activity.StoreCheckActivity;
import com.meetrend.haopingdian.activity.StorePayCodeListActivity;
import com.meetrend.haopingdian.activity.TeaEventListActivity;
import com.meetrend.haopingdian.activity.ShouQuanMaActivity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ComItemLayoutView;
import com.meetrend.haopingdian.widget.ShouquanMa_InputWidget;
import com.meetrend.haopingdian.widget.ShouquanMa_InputWidget.InputCompeleListener;

/**
 * 主界面     管理模块
 * 
 * @author 肖建斌
 *
 */
public class OrderManagerFragment extends BaseFragment {
	
	private final static String TAG = OrderManagerFragment.class.getName();
	
	//订单管理
	@ViewInject(id = R.id.online_order, click = "onItemClick")
	ComItemLayoutView mOnlineOrder;
	//快速下单
	@ViewInject(id = R.id.x_order, click = "onItemClick")
	ComItemLayoutView mXorder;
	//数据中心
	@ViewInject(id = R.id.datacenterview, click = "onItemClick")
	ComItemLayoutView mDateCenterView;
	//新建活动
	@ViewInject(id = R.id.newactivity, click = "onItemClick")
	ComItemLayoutView mNewActivities;
	//活动总览
	@ViewInject(id = R.id.activities, click = "onItemClick")
	ComItemLayoutView mActivities;
	
	//其他\门店授权
	@ViewInject(id = R.id.shouquan_view, click = "onItemClick")
	ComItemLayoutView shouquanView;
	
	//优惠券核销
	//@ViewInject(id = R.id.yhqhx_view, click = "onItemClick")
	//ComItemLayoutView yhqHxView;

	@ViewInject(id = R.id.product_pandian, click = "onItemClick")
	ComItemLayoutView mStorePandian;
	

	//市场参考价
	@ViewInject(id = R.id.marketprice_view, click = "onItemClick")
	ComItemLayoutView marketviewprice;
	
	//签到
	@ViewInject(id = R.id.map_sign, click = "onItemClick")
	ComItemLayoutView mapSignBtn;

	//收款码管理
	@ViewInject(id = R.id.paycode_manager_view, click = "onItemClick")
	ComItemLayoutView payCodeManagerView;

	//商品管理
	@ViewInject(id = R.id.product_manager_view, click = "onItemClick")
	ComItemLayoutView productManagerView;


	private Dialog inputDialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_order_manager, container, false);
		FinalActivity.initInjectedView(this, rootView);
		return rootView;
	}
	
	
	/**
	 * 
	 * 单击事件
	 * @param view
	 * @author bob
	 * 
	 * */
	
	public void onItemClick(View view){
		switch(view.getId()){
			case R.id.online_order:{
				Intent intent = new Intent(mActivity,NewOrderManagerActivity.class);
				//intent.putExtra(OrderManagerActivity.type, OrderManagerActivity.TYPE_ONLINE_ORDER);
				startActivity(intent);
				//getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			}
			case R.id.x_order:{
				Intent	intent = new Intent(mActivity,OrderManagerActivity.class);
				intent.putExtra(OrderManagerActivity.type, OrderManagerActivity.TYPE_STORE_PLACE);
				startActivity(intent);
				break;
			}

			
			//授权码，生成授权码对话框
			case R.id.shouquan_view:{
				createShouQuanMaInputDialog();
				showPassDialog();
				break;
			}
			
			case R.id.newactivity:{
				//NewActivity
				Intent	intent = new Intent(mActivity,NewBuidEventActivity.class);
				intent.putExtra("fromtype", 1);//标识从新建界面
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				break;
			}
			case R.id.activities:{
				Intent	intent = new Intent(mActivity,TeaEventListActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_left_in,R.anim.push_left_out);
				break;
			}
			//优惠券核销
			//case R.id.yhqhx_view:{
				
				
			//	break;
			//}
			
			//店内盘点
			case R.id.product_pandian: {
				
				Intent intent = new Intent(mActivity,StoreCheckActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_left_in,R.anim.push_left_out);
				
				break;
			}
			
			case R.id.datacenterview:
			{
				Intent intent = new Intent(mActivity,DataCenterActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_left_in,R.anim.push_left_out);
				break;
			}
			
//			case R.id.kucun_view:
//			{
//				Intent intent = new Intent(mActivity,ProductReportoryActivity.class);
//				startActivity(intent);
//				getActivity().overridePendingTransition(R.anim.activity_left_in,R.anim.push_left_out);
//				break;
//
//			}
			//市场参考价
			case R.id.marketprice_view:{
				
				Intent intent = new Intent(mActivity,MarketMainActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_left_in,R.anim.push_left_out);
				break;
			}
			
			//签到
			case R.id.map_sign:
				
				Intent intent = new Intent(mActivity,SignRecordsActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_left_in,R.anim.push_left_out);
				
				break;
			case R.id.paycode_manager_view:

				Intent paycodeIntent = new Intent(mActivity,StorePayCodeListActivity.class);
				startActivity(paycodeIntent);
				getActivity().overridePendingTransition(R.anim.activity_left_in,R.anim.push_left_out);
				break;
			//商品管理
			case R.id.product_manager_view:
				Intent productOrgIntent = new Intent(mActivity,ProductMangerActivity.class);
				startActivity(productOrgIntent);
				break;
		}
	}
	
	/**
	 * 授权码密码对话框
	 */
	private void createShouQuanMaInputDialog(){
		
		inputDialog = new Dialog(getActivity(), R.style.dialog_theme);
		inputDialog.setContentView(R.layout.shouquanma_dialog);
		
		ImageView cancel = (ImageView) inputDialog.findViewById(R.id.cancelView);
		final ShouquanMa_InputWidget inputWidget = (ShouquanMa_InputWidget) inputDialog.findViewById(R.id.input_widget);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				disMissPassDailog();
				//强制隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput( InputMethodManager.HIDE_IMPLICIT_ONLY, 0 );
			}
		});
		
		inputWidget.setOnEditListenr(new InputCompeleListener() {
			
			@Override
			public void compeleteCallBack(boolean isComepelete) {
				if (isComepelete) {
						disMissPassDailog();
						showDialog();
						
						//强制隐藏软键盘
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput( InputMethodManager.HIDE_IMPLICIT_ONLY, 0 );
						
						Log.i(TAG+"授权码", inputWidget.getPassword());
						processToShowShouQuanMa(inputWidget.getPassword());
					return;
				}
					
			}
		});
	}
	
	private void disMissPassDailog(){
		
		if (inputDialog != null) {
			inputDialog.dismiss();
		}
	}
	
	private void showPassDialog(){
		
		if (inputDialog != null) {
			inputDialog.show();
		}
	}
	

	/**
	 * 查看授权码
	 * @param passward 授权密码 ，只有老板才有授权密码
	 */
	private void processToShowShouQuanMa(final String passward){
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("password", passward);
		params.put("getStatus", 0+"");//0默认，1重新获取
		
		  
		
		Callback callback = new Callback(tag,getActivity()) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					String shouQunCode = data.get("code").getAsString();//授权码
					Intent intent = new Intent(getActivity(), ShouQuanMaActivity.class);
					intent.putExtra("sqcode", shouQunCode);
					intent.putExtra("pass", passward);
					startActivity(intent);
				}else{
					String msgStr = data.get("msg").getAsString();
					showToast(msgStr);
				}
				

				
				dimissDialog();
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_SHOUQUANMA, params, callback);
	}
	

}