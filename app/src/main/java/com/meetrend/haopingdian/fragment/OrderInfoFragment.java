package com.meetrend.haopingdian.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.google.gson.Gson;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.Checklogistics_web;
import com.meetrend.haopingdian.activity.CourierCodeActivity;
import com.meetrend.haopingdian.activity.NewSelectExcutorActivity;
import com.meetrend.haopingdian.activity.QuikPayActivity;
import com.meetrend.haopingdian.activity.SendConfirmActivity;
import com.meetrend.haopingdian.adatper.DiviceListAdapter;
import com.meetrend.haopingdian.adatper.NewOrderInfoListItemAdapter;
import com.meetrend.haopingdian.bean.Device;
import com.meetrend.haopingdian.bean.ExecutorEntity;
import com.meetrend.haopingdian.bean.OnlineOrderDetail;
import com.meetrend.haopingdian.bean.WorkState;
import com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray;
import com.meetrend.haopingdian.enumbean.OrderStatus;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.AddCourierCodeEvent;
import com.meetrend.haopingdian.event.OrderRefreshEvent;
import com.meetrend.haopingdian.event.ReFreshOrderExcutorEvent;
import com.meetrend.haopingdian.event.SendEnableMsgEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import de.greenrobot.event.EventBus;

/**
 * 订单详情
 * 
 */
public class OrderInfoFragment extends BaseFragment {
	
	private final static String TAG = OrderInfoFragment.class.getName();
	
	private OnlineOrderDetail mDetail;
	
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	
	@ViewInject(id = R.id.ll_orderinfo_head_lv)
	LinearLayout ll_orderinfo_head_lv;
	
	@ViewInject(id = R.id.emptyview)
	TextView emptyView;
	
	//订单编号
	@ViewInject(id = R.id.order_infos_number)
	TextView mOrdersName;
	
	//订单人
	@ViewInject(id = R.id.order_infos_dummy)
	TextView mOrdersTeaName;
	
	//订单时间
	@ViewInject(id = R.id.order_infos_create_time)
	TextView mOrdersTime;
	
	//订单状态
	@ViewInject(id = R.id.order_infos_status)
	ImageView mStatuss;
	
	//合计
	@ViewInject(id = R.id.heji_layout)
	LinearLayout hejiLayout;
	@ViewInject(id = R.id.heji_view)
	TextView hejiView;
	
	//应收
	@ViewInject(id = R.id.yinshou_layout)
	LinearLayout yinshouLayout;
	@ViewInject(id = R.id.yinshou_view)
	EditText yinshouView;
	@ViewInject(id = R.id.yinshou_top_view)
	TextView yinshouTopView;
	@ViewInject(id = R.id.yinshou_icon)
	ImageView yinshouIcon;
	
	//订单产品列表
	@ViewInject(id = R.id.order_listview)
	ListView orderListView;
	
	//详情
	//客户
	@ViewInject(id = R.id.customer_name_view)
	TextView  customerView;
	//联系方式
	@ViewInject(id = R.id.connact_type_view)
	TextView  connactTypeView;
	//送货方式
	@ViewInject(id = R.id.send_type_view)
	TextView  sendTypeView;
	//收货地址
	@ViewInject(id = R.id.recieve_address_view)
	TextView  recieveAddress;
	//配送方式
	@ViewInject(id = R.id.pay_type_view)
	TextView  payTypeView;
	
	//备注
	@ViewInject(id = R.id.express_view)
	TextView  beizhuView;
	
	//执行人
	@ViewInject(id = R.id.excutor_chooose_layout,click = "changeOrderClick")
	LinearLayout excutorChooseLayout;
	@ViewInject(id = R.id.excutor_name_view)
	TextView  excutorNameView;
	@ViewInject(id = R.id.excutor_arrow_icon)
	ImageView  excutorIcon;
	
	//快递单号,只有在订单完成的状态才显示录入快递单号
	@ViewInject(id = R.id.write_kuaidi_layout)
	LinearLayout writeKuaidiLayout;
	@ViewInject(id = R.id.write_kuaidi_layout)
	TextView  writeKuaidiView;
	@ViewInject(id = R.id.kuaidi_arrow_icon)
	ImageView  kuaidIcon;
	@ViewInject(id = R.id.write_kuaidi_view,click = "courierCodeClick")
	TextView  writeKaiView;
	
	//备注
	@ViewInject(id = R.id.beizhu_layout)
	LinearLayout beizhuLinearLayout;
	@ViewInject(id = R.id.beizhu_view)
	EditText desView;
	
	//底部按钮
	//取消订单
	@ViewInject(id = R.id.cancel_order_btn,click = "cancelOrderClick")
	Button cancelOrderBtn;
	//打印小票
	@ViewInject(id = R.id.print_btn,click = "printClick")
	Button printBtn;
	//录入快递单号
	@ViewInject(id = R.id.write_in_kuaidi_btn)
	Button writeKuaidiNumView;
	//送货确认
	@ViewInject(id = R.id.confirm_send_btn,click = "sendConfirmClick")
	Button comfirmSendBtn;
	//订单确认
	@ViewInject(id = R.id.comfir_order_btn,click = "confrimOrderClick")
	Button confirmOrder;
	//确认修改
	@ViewInject(id = R.id.comfir_modify_btn,click = "orderModifyClick")
	Button confirm_modify_btn;
	//查看物流
	@ViewInject(id = R.id.look_order_progress_btn, click = "checkLogisticsClick")
	Button lookOrderBtn;
	
	//立即付款
	@ViewInject(id = R.id.paybtn, click = "quickPay")
	Button payBtn;
	
	
	//底部layout
	@ViewInject(id = R.id.bottom_layout)
	LinearLayout bottomLayout;
	
	//积分layout
	@ViewInject(id = R.id.jifenlayout)
	LinearLayout jifenLayout;
	@ViewInject(id = R.id.jifen_view)
	TextView jifenView;
	
	//优惠layout
	@ViewInject(id = R.id.youhuilayout)
	LinearLayout youhuiLayout;
	@ViewInject(id = R.id.youhui_view)
	TextView youhuiView;
	
	//订单id
	String orderId;
	/**订单来源 “店内，线上 标识” 1店内2线上**/
	String orderSource;
	
	private PrintDialog printdialog = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_online_order_detail,null);
		FinalActivity.initInjectedView(this, rootView);
		mTitle.setText("订单详情");
		showDialog();
		
		if (null != getArguments()) {
			orderId = getArguments().getString("orderid");
			orderSource = getArguments().getString("orderSource");
		}
		
		requestOrderDetail(orderId);
		
		return rootView;
	}

	
	/**
	 * 通过订单id获取订单详情
	 * 
	 * @param orderId
	 */
	private void requestOrderDetail(String orderId){
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("oid", orderId);
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();

				if (!isSuccess) {
					if (data.get("msg") != null) {
						showToast(data.get("msg").getAsString());
					} else {
						showToast("无法获取订单数据");
					}
					return;
				}
				emptyView.setVisibility(View.GONE);
				Gson gson = new Gson();
				App.onlineOrderDetail = mDetail = gson.fromJson(data, OnlineOrderDetail.class);
				executorId = mDetail.executeUserId;
				
				Log.i("------二维码图片地址--footerUrl--------------", mDetail.footerUrl);
				String localPath = SPUtil.getDefault(getActivity()).getErWeiMaPath();
				//下载图片
				if (!TextUtils.isEmpty(mDetail.footerUrl)) {
					 
					 Bitmap localBitmap = BitmapFactory.decodeFile(localPath);
					
					if (TextUtils.isEmpty(localPath) || null == localBitmap) {
						DownLoadPictrueThread downLoadPictrueThread = new DownLoadPictrueThread(mDetail.footerUrl);
						downLoadPictrueThread.start();
					}
				}
				
				initView();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast("无法获取订单数据");
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL+Server.ONLINE_ORDER_DETAIL_URL, params, callback);
	}
	
	//图片下载线程
	public class DownLoadPictrueThread extends Thread{
		
		public String netPath;
		
		public DownLoadPictrueThread(String netPath){
			this.netPath = netPath;
		}
		
		@Override
		public void run() {
			
			try {
				 String path = Environment.getExternalStorageDirectory()+"/" + "com.meetrend.haopingdian.erweima";
				
				 byte[] bytes = com.meetrend.haopingdian.util.BitmapUtil.readImage(netPath);
				 
				 File file = new File(path);  
		         if(!file.exists()) {
		        	 file.mkdirs(); 
		         } 
		         String savePath = file.getPath()+"/"+"erweima.png";
				 FileOutputStream erweimaOutputStream = new FileOutputStream(savePath);
				 erweimaOutputStream.write(bytes);
				 
				 //保存图片的路径
				 SPUtil.getDefault(getActivity()).saveErweiMaPath(savePath);
				 Log.i("-----------图片下载完成----------------", "is over");
				 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initView() {
		FinalBitmap loader = FinalBitmap.create(mActivity);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
		
		hejiLayout.setVisibility(View.VISIBLE);
		hejiView.setText("¥"+ NumerUtil.setSaveTwoDecimals(mDetail.receivableAmount));//合计
		//总金额
		
		allmoeny = NumerUtil.setSaveTwoDecimals(mDetail.receivableAmount);
		yinshouTopView.setText("¥" + allmoeny);

		
		customerView.setText("客户："+ (mDetail.userName == null ? "" : mDetail.userName));
		
		//客户名称的显示处理
//		if ( null != mDetail.userName && mDetail.userName.length() >= 1) {
//			String familyName = mDetail.userName.substring(0, 1);
//			familyName = familyName +"**";
//			customerView.setText("客户："+familyName);
//		}else {
//			customerView.setText("客户：");
//		}
		
		connactTypeView.setText("联系电话："+mDetail.shipPhone);
		sendTypeView.setText("配送方式："+mDetail.shipMethod);
		recieveAddress.setText("收货地址："+ mDetail.obtainAddress);
		
		if (!TextUtils.isEmpty(mDetail.executeUserName)) {
			excutorNameView.setText(mDetail.executeUserName);
			excutorIcon.setVisibility(View.VISIBLE);
		}else{
			excutorNameView.setText("未设置");
			excutorIcon.setVisibility(View.VISIBLE);
		}
		
		beizhuView.setText("备注：" + mDetail.description);
		
		//订单状态
		OrderStatus status = OrderStatus.get(mDetail.status);
		//Log.i(TAG, "## OrderStatus == " + mDetail.status);
		
		int resId = -1;
		switch (status) {
		//待确认
		case UN_CONFIRMED:
			
			//显示"确认订单"、"取消订单"按钮，可更换 执行人
			resId = OrderStatus.UN_CONFIRMED.getResourceId();
			
			cancelOrderBtn.setVisibility(View.VISIBLE);
			confirmOrder.setVisibility(View.VISIBLE);
		
			excutorChooseLayout.setEnabled(true);
			excutorIcon.setVisibility(View.VISIBLE);
			
			//if (orderSource.equals("2")) {
				//线上
			//	beizhuView.setVisibility(View.GONE);
			//}
			
			break;
		//待付款
		case WAIT_APY:
			
			//显示"取消订单","立即付款"按钮（"立即订单"目前没有添加）
			resId = OrderStatus.WAIT_APY.getResourceId();
			if (orderSource.equals("2")) {
				
				//beizhuView.setVisibility(View.GONE);
				beizhuLinearLayout.setVisibility(View.VISIBLE);
				//线上
				cancelOrderBtn.setVisibility(View.VISIBLE);
				
				
				yinshouLayout.setVisibility(View.VISIBLE);
				yinshouIcon.setVisibility(View.VISIBLE);
				
				//确认修改
				confirm_modify_btn.setVisibility(View.VISIBLE);
				
				yinshouTopView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//授权码
						getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null)
						.add(R.id.frame_container, new InputSQMFragment()).commit();
					}
				});
				
			}else {
				
				excutorChooseLayout.setVisibility(View.GONE);
				
				payBtn.setVisibility(View.VISIBLE);
				//beizhuView.setVisibility(View.GONE);
				//店内
				cancelOrderBtn.setVisibility(View.VISIBLE);
				
				yinshouLayout.setVisibility(View.GONE);
			}
			
		
			
			excutorChooseLayout.setEnabled(true);
			excutorIcon.setVisibility(View.VISIBLE);
			
			cancelOrderBtn.setVisibility(View.VISIBLE);//取消订单
			
			break;
			//已付款
		case HAVE_PAY:
			
			jifenLayout.setVisibility(View.VISIBLE);
			youhuiLayout.setVisibility(View.VISIBLE);
			jifenView.setText("-"+ mDetail.integralAmount);
			youhuiView.setText("-"+mDetail.discountAmount);
			
			payTypeView.setText("支付方式："+mDetail.payType);
			
			yinshouLayout.setVisibility(View.VISIBLE);
			
			//显示变更执行人
			resId = OrderStatus.HAVE_PAY.getResourceId();
			
			printBtn.setVisibility(View.VISIBLE);
			//送货确认
//			if (mDetail.shipMethod.equals("快递")) {
//				comfirmSendBtn.setVisibility(View.VISIBLE);
//			}else {
//				comfirmSendBtn.setVisibility(View.GONE);
//			}
			
			comfirmSendBtn.setVisibility(View.VISIBLE);
			
			excutorChooseLayout.setEnabled(true);
			excutorIcon.setVisibility(View.VISIBLE);
			
			//if (orderSource.equals("1")) {
			//	beizhuView.setVisibility(View.VISIBLE);
			//	beizhuView.setText("备注："+mDetail.description);
			//}else {
				//线上
			
			//}
			
			//总金额不能按
			yinshouLayout.setEnabled(false);
			
			break;
		// 已完成
		case FINISHED:
			payTypeView.setText("支付方式："+mDetail.payType);
			
			jifenLayout.setVisibility(View.VISIBLE);
			youhuiLayout.setVisibility(View.VISIBLE);
			jifenView.setText("-"+ mDetail.integralAmount);
			youhuiView.setText("-"+ mDetail.discountAmount);
			
			yinshouLayout.setVisibility(View.VISIBLE);
			yinshouLayout.setEnabled(false);
			
			//显示录露快递单号view,打印小票
			resId = OrderStatus.FINISHED.getResourceId();
			printBtn.setVisibility(View.VISIBLE);
			lookOrderBtn.setVisibility(View.VISIBLE);
			
			//没有如果快递单号
			if (TextUtils.isEmpty(mDetail.expressCode)) {
				lookOrderBtn.setVisibility(View.GONE);
			}else {
				//快递单号只有在完成状态才显示
				writeKuaidiLayout.setVisibility(View.VISIBLE);
				writeKaiView.setText(mDetail.expressCode);
				lookOrderBtn.setVisibility(View.VISIBLE);
			}
			
			excutorChooseLayout.setEnabled(false);
			excutorIcon.setVisibility(View.GONE);
			
			//if (orderSource.equals("1")) {
			//	beizhuView.setVisibility(View.VISIBLE);
			//	beizhuView.setText("备注："+mDetail.description);
			//}else {
			
			//}
			
			//执行人
			if (TextUtils.isEmpty(mDetail.executeUserName)) {
				excutorNameView.setText("");
			}
			
			//总金额不能按
			yinshouLayout.setEnabled(false);
			
			break;
		//已取消
		case CANCELED:
			
			yinshouLayout.setVisibility(View.GONE);// 总金额
			
			resId = OrderStatus.CANCELED.getResourceId();
			bottomLayout.setVisibility(View.GONE);
			excutorChooseLayout.setEnabled(false);
			excutorIcon.setVisibility(View.GONE);
			
			break;
		}
		
		mOrdersName.setText("订单编号: " + mDetail.orderName);
		mOrdersTeaName.setText("订单人:" + mDetail.createUserName);
		mOrdersTime.setText("订单时间:" + mDetail.createTime);
		if (resId != -1) {
			mStatuss.setBackgroundResource(resId);
		}

		orderListView.setAdapter(new NewOrderInfoListItemAdapter(getActivity(),mDetail.jsonArray));
		ListViewUtil.setListViewHeightBasedOnChildren(orderListView);
		
		
		//beizhuView.setVisibility(View.GONE);
	}
	
	private String executorId;
	private String executorName;
	/**
	 * 变更执行人
	 */
	public void onEventMainThread(ReFreshOrderExcutorEvent event) {
		
		executorId = event.excutorId;
		executorName = event.excutorName;
		
		Log.i(TAG+"执行人id", executorId);
		Log.i(TAG+"执行人名字", executorName);
		
		//变更执行人接口
		modifyExcutorRequest(executorId);
	}
	
	//通过修改授权码改动总金额
	private String allmoeny;//总金额（应收）
	public void onEventMainThread(SendEnableMsgEvent event) {

		yinshouTopView.setVisibility(View.GONE);
		yinshouIcon.setVisibility(View.GONE);
		yinshouView.setVisibility(View.VISIBLE);
		yinshouView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				if (NumerUtil.isFloat(s.toString()) || NumerUtil.isInteger(s.toString())) {
					
					confirm_modify_btn.setEnabled(true);
					allmoeny = NumerUtil.setSaveTwoDecimals(s.toString());
					
				}else{
					confirm_modify_btn.setEnabled(false);
				}
				
			}
		});
		
		String foramtAllMoney = NumerUtil.setSaveTwoDecimals(mDetail.receivableAmount);
		yinshouView.setText(NumerUtil.setSaveTwoDecimals(mDetail.receivableAmount));
		allmoeny = foramtAllMoney;
	}

	//马上付款
	public void quickPay(View view){
		
		Intent intent = new Intent(getActivity(), QuikPayActivity.class);
		intent.putExtra("from_pay", 1);//标识从改界面跳转进行支付
		intent.putExtra("fromdetail", 1);//标识
		startActivity(intent);
	}
	
	// 变更执行人点击事件
	public void changeOrderClick(View view) {
		Intent intent = new Intent(getActivity(), NewSelectExcutorActivity.class);
		if (mDetail.executeUserId == null) {
			mDetail.executeUserId = "";
		}
		intent.putExtra("exename", mDetail.executeUserId);
		intent.putExtra("from", 2);
		startActivity(intent);
	}
	
	//送货确认
	public void sendConfirmClick(View view){
		Intent intent = new Intent(getActivity(), SendConfirmActivity.class);
		intent.putExtra("orderId", mDetail.id);
		intent.putExtra("ship", mDetail.shipMethod);//配送方式
		startActivity(intent);
	}

	//订单确认
	public void confrimOrderClick(View view) {
		
		if (TextUtils.isEmpty(executorId)) {
			showToast("所获人不能为空");
			return;
		}
		
		showDialog("订单确认中...");
		
		if (executorId != null) {
			AjaxParams mparams = new AjaxParams();
			mparams.put("token", SPUtil.getDefault(getActivity()).getToken());
			mparams.put("orderId", App.onlineOrderDetail.id);
			mparams.put("executeUserId", executorId);
			Callback mcallback = new Callback(tag, getActivity()) {

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					LogUtil.d(tag, "transfer info : " + t);
					
					dimissDialog();
					
					if (!isSuccess) {
						showToast(data.get("msg").getAsString());
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					dimissDialog();
					if (null != strMsg) {
						showToast(strMsg);
					}
				}

			};

			FinalHttp mfinalHttp = new FinalHttp();
			mfinalHttp.get(Server.BASE_URL + Server.CHANGE_EXECUTOR, mparams,
					mcallback);
		}

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("oid", mDetail.id);

		boolean flag = false;// 执行人列表与后台执行人对比是否存在
		for (ExecutorEntity item : App.executorList) {
			if (mDetail.executeUserName.equals(item.userName)) {
				LogUtil.d(tag, mDetail.executeUserId + "," + item.userName);
				params.put("eid", item.id);
				flag = true;
				break;
			}
		}
		if (flag == false) {
			showToast("没有选择执行人或执行人不存在");
			return;
		}

		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "confirm order : " + t);
				if (isSuccess == false) {
					String mMsg = data.get("msg").getAsString();
					if (TextUtils.isEmpty(mMsg)) {
						// showToast("取消订单失败");
						EventBus.getDefault().post(new OrderRefreshEvent());
						mActivity.finish();
						return;
					}
					showToast(data.get("msg").getAsString());
					return;
				}
				EventBus.getDefault().post(new OrderRefreshEvent());
				mActivity.finish();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast("确认订单失败");
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.FIREM_ORDER_URL, params,
				callback);
	}

	public void cancelOrderClick(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("确定取消订单");
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				requestCancel();
			}
		});
		builder.setNegativeButton("取消操作", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * 变更执行人请求
	 * 
	 */
	private void modifyExcutorRequest(String excutorId){
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("orderId", App.onlineOrderDetail.id);
		params.put("executeUserId", excutorId);//执行人id

		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				LogUtil.d(TAG, "变更执行人接口执行结果 : " + t);
				
				if (!isSuccess) {
					//
					if (data.has("msg")) {
						Toast.makeText(getActivity(), data.get("msg").toString(), Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(getActivity(), "变更执行人失败！", Toast.LENGTH_SHORT).show();
					}
					
				} else {
					//如果变更执行成功，则显示执行人名字
					excutorNameView.setText(executorName);
					Toast.makeText(getActivity(), "执行人修改成功！", Toast.LENGTH_SHORT).show();
				}
				
				dimissDialog();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
				showToast(strMsg);
			}
		};
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.CHANGE_EXECUTOR, params,callback);
	}
	
	//线上订单修改
	public void orderModifyClick(View view){
		modifyOnlineOrderRequest();
	}
	
	/**
	 * 线上订单修改
	 */
	private void modifyOnlineOrderRequest(){
		
		showDialog("订单修改中...");
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("orderId", mDetail.id);
		params.put("amount", allmoeny);//总金额
		params.put("description", desView.getText().toString());//描述
		params.put("executeUserId", executorId);
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.d(TAG,  t);
				dimissDialog();
				
				if (!isSuccess) {
					//
					if (data.has("msg")) {
						Toast.makeText(getActivity(), data.get("msg").toString(), Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(getActivity(), "订单失败！", Toast.LENGTH_SHORT).show();
					}
					
				} else {
					//如果变更执行成功，则显示执行人名字
					excutorNameView.setText(executorName);
					Toast.makeText(getActivity(), "订单修改成功！", Toast.LENGTH_SHORT).show();
					getActivity().finish();
				}
				
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
				showToast(strMsg);
			}
		};
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.ONLINE_ORDER_MODIFY, params,callback);
		
	}

	/**
	 * 取消订单
	 */
	private void requestCancel() {
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("orderId", mDetail.id);

		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess == false) {
					showToast(data.get("msg").getAsString());
					return;
				}
				boolean flag = Boolean.parseBoolean(data.get("success")
						.getAsString());
				if (flag) {
					showToast("取消订单成功");
					EventBus.getDefault().post(new OrderRefreshEvent());
				} else {
					showToast("取消订单失败");
				}

				mActivity.finish();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast("取消订单失败");
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.CANCEL_ORDER_URL, params,
				callback);
	}


	//查看物流
	public void checkLogisticsClick(View view) {
		startActivity(new Intent(getActivity(), Checklogistics_web.class));
	}


	public void homeClick(View view) {
		mActivity.finish();
	}

	public void onClickAction(View v) {
		mActivity.finish();
		EventBus.getDefault().post(new OrderRefreshEvent());
	}
	

	/**
	 * 录入快递单号
	 */
	public void courierCodeClick(View view) {
		Intent intent = new Intent(mActivity, CourierCodeActivity.class);
		intent.putExtra("orderId", mDetail.id);
		intent.putExtra("num", writeKaiView.getText().toString());
		startActivity(intent);
	}

	//快递单号的显示
	public void onEventMainThread(AddCourierCodeEvent event) {
		writeKaiView.setText(event.code);
	}
	
	
	/************************打印小票**********************************/
	
	/**
	 * 传递的端口号,定值
	 */
	private String sendPort = "10000";
	
	/**
	 * 传递的ip
	 */
	private String sendIp ;
	
	/**
	 * 网关ip
	 */
	private String wgIP;
	private UpDateDvListThread upDateDvListThread = null;
	private CheckHasDiviceThread checkHasDiviceThread = null;
	private DatagramPacket packet;
	private DatagramSocket socket;
	private DatagramPacket getpacket;
	private byte data2[] = new byte[4 * 1024];
	boolean sendDatagram = true;
	
	private final static int SEARCH_LUYOU_FAIALED = 0x189;//广播失败
	private final static int SEARCH_DEVICES_FAIALED = 0x188;//搜索打印设备失败
	private final static int PRINT_FINISH = 0x187;//打印成功
	
	private final static int INIT_DATA_SUCCESS = 0x182;//数据初始化成功
	private final static int DIVICE_EXCEPTION = 0x181;//数据初始化成功
	
	private final static int NO_DIVICE_CAN_CONNACT = 0x180;//没有可连接的设备
	
	private final static int START_PRINT = 0x458;//开始打印
	
	private String logoPath;
	
	private boolean isContinu = true;//是否继续搜索设备

	private List<Device> dvList = new ArrayList<Device>();

	private boolean isConnected=false;
	private Socket client;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	
	/**
	 * 小票打印
	 */
	public void printClick(View view){
		  
	   if (null == dvList || dvList.size() == 0) {
			
			showDialog("正在打印...");
			
			wgIP = getIp();
			// 发送广播消息 搜索设备
			String strIp = wgIP;
			strIp = strIp.substring(0, strIp.lastIndexOf('.'));
			strIp += ".255";
			
			searchDevice(strIp);
			dvList = new ArrayList<Device>();
			return;
		}else {
			if (TextUtils.isEmpty(sendIp)) {
				//显示
				printdialog = new PrintDialog(getActivity(), dvList,R.style.dialog_theme);
				printdialog.show();
				
			}else {
				//选择之前打印过的设备
				new PrintDatasThread().start();
			}
		}
		
	}
	
	/**
	 * 打印文本线程
	 *
	 */
	private class PrintDatasThread extends Thread{
		@Override
		public void run() {
			
			SendData();
		}
	}
	
	/**
	 * 获取网关IP地址
	 * 
	 */
	private String getIp() {
		WifiManager wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		DhcpInfo di = wm.getDhcpInfo();
		long getewayIpL = di.gateway;

		String ip = intToIp(getewayIpL);// 网关地址

		return ip;
	}

	private String intToIp(long i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
	
	/**
	 * 监听找到的设备
	 * 
	 * 发送UDP包
	 */
	private void searchDevice(String strIp) {
		try {
			
				//开启UDP接收数据监听，获取服务器IP
				upDateDvListThread = new UpDateDvListThread();
				upDateDvListThread.start();
				checkHasDiviceThread = new CheckHasDiviceThread();
				checkHasDiviceThread.start();
				
				// UDP发送数据建立UDP服务器通信
				socket = new DatagramSocket();
				InetAddress serverAddress = InetAddress.getByName(strIp);// 设置对方IP
				String str = "AT+FIND=?\r\n";// 设置要发送的报文
				byte data[] = str.getBytes();// 把字符串str字符串转换为字节数组
				packet = new DatagramPacket(data, data.length, serverAddress, 10002);// 设置发送数据，地址，端口
				new SendUdpPacketThread().start();
			
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(DIVICE_EXCEPTION);
		}
	}
	
	/**
	 * 发送数据包至路由器
	 *
	 */
	public class SendUdpPacketThread extends Thread {
		
		public void run() {
			int timeSpan = 0;
			
			while (sendDatagram) {
				try {
					//10秒后跳出循环停止发送数据报
					if (timeSpan++ > 20) {
						timeSpan = 0;
						break;
					}
					socket.send(packet);
					Thread.sleep(500);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				catch (InterruptedException e) {
					e.printStackTrace();
					dimissDialog();
				}
			}
		}
		
	}
	
	/**
	 * 
	 * 利用死循环 监听找到的设备
	 * 
	 */
	class UpDateDvListThread extends Thread {

		@Override
		public void run() {
			
			try {
				getpacket = new DatagramPacket(data2, data2.length);// 创建一个接收PACKET
			} catch (Exception e) {
			}
			
			while (true) {
				
				if (socket == null) {
					try {
						sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				  //中断线程  
                if (isInterrupted()) {  
                    return ;  
                } 
				
				try {
					
					socket.receive(getpacket);//获取UDP服务器发送数据信息，放入PACKET中
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 判断服务器是否发送数据
				if (getpacket.getAddress() != null) {
					// 获得服务器IP
					String ipStr = getpacket.getAddress().toString()
							.substring(1);
					String macAdd = new String(data2, 0, getpacket.getLength())
							.trim();

					Device d = new Device();
					d.deviceName = macAdd;//mac地址
					d.deviceAddress = ipStr;//ip地址
					if (wgIP.equals(d.deviceAddress)) {
						d.wkState = WorkState.AP;
					} else {
						d.wkState = WorkState.STA;
					}
					
					if (!checkData(dvList, d)) {
						
						if (TextUtils.isEmpty(sendIp)) {
							dvList.add(d);
						}
					}
				}
			}
		}
	}
	
	private boolean checkData(List<Device> list, Device d) {
		for (Device device : list) {
			if (device.deviceAddress.equals(d.deviceAddress)) {
				return true;
			}
		}
		return false;
	}
	
	private final static int UPDATE_LIST = 0X459;//更新列表
	//单独的线程检测是查询到设备
	private class CheckHasDiviceThread extends Thread{
		
		@Override
		public void run() {
			int time = 0;
			int time2 = 0;//用于显示设备的列表
			
			while (isContinu) {
				
				SystemClock.sleep(500);
				
				//10秒左右后没有接收到设备信息则跳出循环
				if (time++ > 20) {
					if (dvList.size() == 0) {
						handler.sendEmptyMessage(SEARCH_DEVICES_FAIALED);
					}
					time = 0;
					break;
				}
				
				//超过5秒则更新对话框列表
				if (time2++ > 5) {
					isContinu = false;
					handler.sendEmptyMessage(UPDATE_LIST);
				}
			}
		}
	}
		
		/**
		 * 打印
		 * 
		 */
	    private void SendData()
	    {
	    	try 
			{	
	    		connect(sendIp,sendPort);
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("1D 21 11"));//换行
	    		String companyName = mDetail.companyname;
	    		outputStream.write(("     " +  companyName).getBytes("GBk"));
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write("================================".getBytes("GBk"));//滑横线
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(("门店名称："+ mDetail.storeName).getBytes("GBk"));
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(("门店授权号："+ mDetail.mendianSQH).getBytes("GBk"));
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(("单号："+mDetail.orderName).getBytes("GBk"));//滑横线
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write("--------------------------------".getBytes());//滑横线
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write("名称    单价    数量    金额".getBytes("GBk"));
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write("--------------------------------".getBytes("GBk"));//滑横线
	    		
	    		int j = 1;
	    		for (int i = 0; i < mDetail.jsonArray.size(); i++) {
	    			JsonArray item = mDetail.jsonArray.get(i);
	    			
	    			outputStream.write(hexStringToBytes("1B 40"));//初始化
	        		outputStream.write(hexStringToBytes("0A"));//换行
	        		outputStream.write((item.name + item.productPici).getBytes("GBk"));//查名，批次
	        		
	        		outputStream.write(hexStringToBytes("1B 40"));//初始化
	        		//outputStream.write(hexStringToBytes("1B 61 01"));//居中
	        		outputStream.write(hexStringToBytes("0A"));//换行
	        		j = i+1;
	        		double itemAllMoney = Double.parseDouble(item.piecePrice) * Double.parseDouble(item.quantity);
	        		outputStream.write(("00"+(j)+"     "+item.piecePrice+"     "+item.quantity+"     "
	        		+ NumerUtil.setSaveTwoDecimals(itemAllMoney+"")).getBytes("GBk"));
				}
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write("--------------------------------".getBytes());//滑横线
	    		
	    		
	    		StringBuffer firstLineStr = new StringBuffer();
	    		String hejiStr = "合计:" + NumerUtil.setSaveTwoDecimals(mDetail.incomeAmount);
	    		String yhStr = "优惠:" + NumerUtil.setSaveTwoDecimals(mDetail.discountAmount);
	    		String jfStr = "积分:" + NumerUtil.setSaveTwoDecimals(mDetail.integralAmount); 
	    		
	    		firstLineStr.append(hejiStr);
	    		firstLineStr.append(yhStr);
	    		firstLineStr.append(jfStr);
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
	    		outputStream.write(firstLineStr.toString().getBytes("GBk"));
	    		
	    		
	    		StringBuffer secondLineStr = new StringBuffer();
	    		String ys = "应收:" + NumerUtil.setSaveTwoDecimals(mDetail.receivableAmount);
	    		String xj = "现金:"+ NumerUtil.setSaveTwoDecimals(mDetail.receivableAmount);
	    		String zl = "找零:" + NumerUtil.setSaveTwoDecimals(mDetail.changeAmount);
	    		
	    		secondLineStr.append(ys);
	    		secondLineStr.append(xj);
	    		secondLineStr.append(zl);
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
	    		outputStream.write(secondLineStr.toString().getBytes("GBk"));
	    		
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(("收银员：" + mDetail.checkoutname).getBytes("GBk"));
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		String ddate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	    		outputStream.write(("时间："+ddate).getBytes("GBk"));
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(("客服电话："+ mDetail.mobile).getBytes("GBk"));
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write("================================".getBytes("GBk"));
	    		
	    		try {
					  Thread.sleep(2000);//此处需要2s,缓冲池已满
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    		
	    		Bitmap printBitmap = BitmapFactory.decodeFile(SPUtil.getDefault(getActivity()).getErWeiMaPath());
	    		
	    		if (null != printBitmap) {
	    			
	    			//图片打印
	        		int h = printBitmap.getHeight();
	        		Bitmap resultBitmap = resizeImage(printBitmap, 384, h);
	        		byte[] lsendbuf = StartBmpToPrintCode(resultBitmap);
	        		outputStream.write(lsendbuf);
	        		
				}else {
					
					Log.i("pictrue", "bitmap is null");
					outputStream.write(hexStringToBytes("1B 40"));//初始化
		    		outputStream.write(hexStringToBytes("0A"));//换行
		    		outputStream.write(hexStringToBytes("0A"));//换行
		    		outputStream.write(hexStringToBytes("0A"));//换行
		         	outputStream.write(hexStringToBytes("1B 61 01"));//居中
		         	outputStream.write(hexStringToBytes("1D 21 01"));//纵向放大
		    		String printText = "      谢谢惠顾欢迎再次光临!";
		    		outputStream.write(printText.getBytes("GBk"));
		    		
		    		outputStream.write(hexStringToBytes("1B 40"));//初始化
		    		outputStream.write(hexStringToBytes("0A"));//换行
		    		outputStream.write("                   ".getBytes("GBk"));
		    		
		    		outputStream.write(hexStringToBytes("1B 40"));//初始化
		    		outputStream.write(hexStringToBytes("0A"));//换行
		    		outputStream.write(hexStringToBytes("1B 4D 11"));//换行
		    		String techName = "         技术支持：好评店 ";
		    		outputStream.write(techName.getBytes("GBk"));
		    		
		    		outputStream.write(hexStringToBytes("1B 40"));//初始化
		    		outputStream.write(hexStringToBytes("0A"));//换行
		    		outputStream.write(hexStringToBytes("1B 4D 11"));//换行
		    		String httpName = "   http://www.haopingdian.cn";
		    		outputStream.write(httpName.getBytes("GBk"));
				}
	    		
	    		
	    		outputStream.flush();//关闭流
	    		
	    		handler.sendEmptyMessage(PRINT_FINISH);
			} 
			catch (NumberFormatException e) 
			{
				handler.sendEmptyMessage(DIVICE_EXCEPTION);
			} 
			catch (IOException e) {
			
			}finally{
				disconnect();
			}
	    }
	    
		//图片转字节数组
		private byte[] StartBmpToPrintCode(Bitmap bitmap) {
			byte temp = 0;
			int j = 7;
			int start = 0;
			if (bitmap != null) {
				int mWidth = bitmap.getWidth();
				int mHeight = bitmap.getHeight();

				int[] mIntArray = new int[mWidth * mHeight];
				byte[] data = new byte[mWidth * mHeight];
				bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
				encodeYUV420SP(data, mIntArray, mWidth, mHeight);
				byte[] result = new byte[mWidth * mHeight / 8];
				for (int i = 0; i < mWidth * mHeight; i++) {
					temp = (byte) ((byte) (data[i] << j) + temp);
					j--;
					if (j < 0) {
						j = 7;
					}
					if (i % 8 == 7) {
						result[start++] = temp;
						temp = 0;
					}
				}
				if (j != 7) {
					result[start++] = temp;
				}

				int aHeight = 24 - mHeight % 24;
				byte[] add = new byte[aHeight * 48];
				byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * 48];
				System.arraycopy(result, 0, nresult, 0, result.length);
				System.arraycopy(add, 0, nresult, result.length, add.length);

				byte[] byteContent = new byte[(mWidth / 8 + 4)
						* (mHeight + aHeight)];// 打印数组
				byte[] bytehead = new byte[4];// 每行打印头
				bytehead[0] = (byte) 0x1f;
				bytehead[1] = (byte) 0x10;
				bytehead[2] = (byte) (mWidth / 8);
				bytehead[3] = (byte) 0x00;
				for (int index = 0; index < mHeight + aHeight; index++) {
					System.arraycopy(bytehead, 0, byteContent, index * 52, 4);
					System.arraycopy(nresult, index * 48, byteContent,
							index * 52 + 4, 48);

				}
				return byteContent;
			}
			return null;

		}

		//转换图片格式
		public void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width,
				int height) {
			////final int frameSize = width * height;
			int r, g, b, y;//, u, v;
			int index = 0;
			////int f = 0;
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					r = (rgba[index] & 0xff000000) >> 24;
					g = (rgba[index] & 0xff0000) >> 16;
					b = (rgba[index] & 0xff00) >> 8;
					
					// rgb to yuv
					y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
					/*u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
					v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;*/
					// clip y
					// yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 :
					// y));
					byte temp = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
					yuv420sp[index++] = temp > 0 ? (byte) 1 : (byte) 0;

					// {
					// if (f == 0) {
					// yuv420sp[index++] = 0;
					// f = 1;
					// } else {
					// yuv420sp[index++] = 1;
					// f = 0;
					// }

					// }

				}

			}
			////f = 0;
		}
	    
	  //缩放图片
		public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
			Bitmap BitmapOrg = bitmap;
			int width = BitmapOrg.getWidth();
			int height = BitmapOrg.getHeight();
			int newWidth = w;

			float scaleWidth = ((float) newWidth) / width;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleWidth);
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		}
	    
	    
	    private void disconnect(){
			if(this.inputStream!=null){
				try{
					this.inputStream.close();
				}catch(Exception e){
					
				}
			}
			if(this.outputStream!=null){
				try{
					this.outputStream.close();
				}catch(Exception e){
					
				}
			}
			this.inputStream=null;
			this.outputStream=null;
			if(client!=null){
				try{
					client.close();
				}catch(Exception e){
					
				}
			}
			client=null;
			isConnected = false;
		}
	    
		private void connect(String ipAddr,String mport) throws UnknownHostException, IOException {
			//if(isConnected){
			//	return;
			//}
			try {
				
				InetAddress serverAddr = InetAddress.getByName(ipAddr);// TCPServer.SERVERIP
				int port=Integer.valueOf(mport);
				SocketAddress my_sockaddr = new InetSocketAddress(serverAddr, port);
				client = new Socket();
				client.connect(my_sockaddr,5000);
				outputStream = client.getOutputStream();
				inputStream = client.getInputStream();
				isConnected = true;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	    /**
		 * 将字符串形式表示的十六进制数转换为byte数组
		 */
		public static byte[] hexStringToBytes(String hexString)
		{
			hexString = hexString.toLowerCase();
			String[] hexStrings = hexString.split(" ");
			byte[] bytes = new byte[hexStrings.length];
			for (int i = 0; i < hexStrings.length; i++)
			{
				char[] hexChars = hexStrings[i].toCharArray();
				bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
			}
			return bytes;
		}
		
		private static byte charToByte(char c)
		{
			return (byte) "0123456789abcdef".indexOf(c);
		}
		
		Handler handler = new Handler()
		{
			public void handleMessage(android.os.Message msg) {
				
				switch (msg.what) {
				
				case SEARCH_LUYOU_FAIALED:
					showToast("向路由器器发送广播异常");
					dimissDialog();
					break;
					
				case SEARCH_DEVICES_FAIALED:
					 upDateDvListThread.interrupt();//中断线程
					 dimissDialog();
					 showToast("没有搜索到可打印小票的设备");
					break;
					
				case START_PRINT:  
					printdialog.dismiss();
					//打印小票线程
					new PrintDatasThread().start();
					
					break;
				case PRINT_FINISH:
					
					printdialog = null;
					//sendIp = "";
					dimissDialog();
					isConnected = true;
					break;
					
				case INIT_DATA_SUCCESS:
					 dimissDialog();
					 showToast("打印数据初始化成功");
					break;
				case DIVICE_EXCEPTION:
					dimissDialog();
					showToast("没有打印设备或者打印异常");
					break;
					
				case NO_DIVICE_CAN_CONNACT:
					dimissDialog();
					showToast("没有可连接的设备");
					
					showDialog("正在搜索设备...");
					wgIP = getIp();
					// 发送广播消息 搜索设备
					String strIp = wgIP;
					strIp = strIp.substring(0, strIp.lastIndexOf('.'));
					strIp += ".255";
					
					searchDevice(strIp);
					dvList = new ArrayList<Device>();
					break;
					
				case UPDATE_LIST:
				
					dimissDialog();
					if (dvList.size() > 0) {
						printdialog = new PrintDialog(getActivity(), dvList,R.style.discount_dialog_theme);
						printdialog.show();
					}else {
						//没有搜索到设备
						showToast("没有搜索到设备");
					}
					
					break;

				default:
					break;
				}
				
			};
		};

		
	private String selectIp;
	//打印之前选中过的ip打印
	private String hasSelectIp;
	
	public class PrintDialog extends Dialog{
		
		public List<Device> diviceNameList;
		public Context mContext;
		public ListView diviceListView;
		private DiviceListAdapter mAdapter;
		private Context context;
		
		public PrintDialog(Context context,List<Device> diviceNameList, int theme) {
			super(context, theme);
			this.diviceNameList = diviceNameList;
			this.context = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_choose_divice_layout);
			
			getWindow().setGravity(Gravity.BOTTOM);
			//设置对话框的宽度
			WindowManager m = getWindow().getWindowManager();
	        Display d = m.getDefaultDisplay();
	        WindowManager.LayoutParams p = getWindow().getAttributes();
	        p.width = d.getWidth();
	        p.height = LayoutParams.WRAP_CONTENT;
	        getWindow().setAttributes(p);
			
			diviceListView = (ListView)this.findViewById(R.id.divices_listview);
			ImageView clearImageView = (ImageView) this.findViewById(R.id.clearbtn);
			Button printbtn = (Button) this.findViewById(R.id.printBtn);
			
			clearImageView.setOnClickListener(new ClearDialogClick());
			printbtn.setOnClickListener(new SurePrintClick());
			
			mAdapter = new DiviceListAdapter(context,diviceNameList, "");
			diviceListView.setAdapter(mAdapter);
			mAdapter.setAddress(hasSelectIp);
			
			diviceListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					selectIp = dvList.get(position).deviceAddress;
					sendIp = dvList.get(position).deviceAddress;
					mAdapter.setAddress(sendIp);
				}
			});
			
			
		}
		
		public void notifydiviceListView(List<Device> list){
			this.diviceNameList = list;
			mAdapter.notifyDataSetChanged();
		}
		
	  }
	
	public class ClearDialogClick implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			if (null != printdialog && printdialog.isShowing()) 
				printdialog.dismiss();
		}
	}
	
	//确定选择打印机按钮
	public class SurePrintClick implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			if (null != printdialog && printdialog.isShowing()) 
				printdialog.dismiss();
			
			showDialog("正在打印...");
			
			sendIp = selectIp;
			handler.sendEmptyMessage(START_PRINT);
		}
	}
	
		
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
}