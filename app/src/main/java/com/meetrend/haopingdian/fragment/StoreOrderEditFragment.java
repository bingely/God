package com.meetrend.haopingdian.fragment;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.CheckOutEntity;
import com.meetrend.haopingdian.bean.PayType;
import com.meetrend.haopingdian.bean.RoomEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.OrderRefreshEvent;
import com.meetrend.haopingdian.event.StoreChangeEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.StringUtil;

import de.greenrobot.event.EventBus;

public class StoreOrderEditFragment extends BaseFragment {
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	// header
	@ViewInject(id = R.id.store_order_avatar)
	ImageView mPhoto;
	@ViewInject(id = R.id.store_order_number_edit)
	TextView mOrderNumber;
	@ViewInject(id = R.id.store_order_user_name)
	TextView mOrderUserName;
	@ViewInject(id = R.id.store_order_time_edit)
	TextView mOrderTime;
	// 包间
	@ViewInject(id = R.id.store_order_detail_amount)
	TextView mAmount;
	@ViewInject(id = R.id.store_order_room_select, click = "roomClick")
	TextView mRoom;
	@ViewInject(id = R.id.store_order_room_price)
	TextView mPrice;
	@ViewInject(id = R.id.store_order_room_time_select)
	TextView mTime;
	@ViewInject(id = R.id.store_order_room_cost)
	TextView mCost;
	// body
	@ViewInject(id = R.id.store_order_discount)
	TextView mDiscount;
	@ViewInject(id = R.id.store_order_total_amount)
	TextView mTotal;
	@ViewInject(id = R.id.store_order_pay_select, click = "payMethodClick")
	TextView mPayMethod;
	@ViewInject(id = R.id.btn_pay, click = "payClick")
	Button mPayBtn;
	// footer
	// 数据
	private List<RoomEntity> mRoomList;
	private boolean isGetDataFailed;
	private CheckOutEntity mDetail;
	private PayType mPayType;
	private List<PayType> mPayTypeList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle bundle = this.getArguments();		
		mDetail = (CheckOutEntity)bundle.getSerializable(CheckOutEntity.class.getSimpleName());
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	public void onEventMainThread(StoreChangeEvent event) {
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		getRoom();
		View view = inflater.inflate(R.layout.fragment_store_order_edit, container, false);
		FinalActivity.initInjectedView(this, view);
		init();
		return view;
	}
	
	private void init() {
		FinalBitmap loader = FinalBitmap.create(mActivity);
		loader.display(mPhoto, Server.BASE_URL + mDetail.avatarId);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
		
		mTitle.setText("结账");
		mOrderNumber.setText("订单编号" + mDetail.orderName);
		mOrderUserName.setText("下单人: " + mDetail.userName);
		String date = "下单时间: " + StringUtil.formatDateStr(mDetail.orderDate);
		mOrderTime.setText(date);
		  
		mRoom.setText(mDetail.roomName);
		mPrice.setText(mDetail.roomPrice);
		mAmount.setText(mDetail.detailAmount);
		
		if (TextUtils.isEmpty(mDetail.roomHour)) {
			mTime.setText(1 + "");
		} else {
			mTime.setText(mDetail.roomHour);
		}
		
		double hour = Double.parseDouble(mTime.getText().toString());
		double price = Double.parseDouble(mPrice.getText().toString());
		String cost = (hour * price) + "";
		mCost.setText(cost);
		
		mDiscount.setText(App.storeOrderDetail.discount);
		mTotal.setText(App.storeOrderDetail.receivableAmount);	
	}
	
	private void getRoom() {		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "room info : " + t);
				Gson gson = new Gson();
				JsonObject data_data = data.get("data").getAsJsonObject();
				String jsonArrayStr = data_data.get("records").toString();
				
				if (!isSuccess) {
					isGetDataFailed = true;
				} else {
					isGetDataFailed = false;		
					mRoomList = gson.fromJson(jsonArrayStr, new TypeToken<List<RoomEntity>>() {}.getType());
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				isGetDataFailed = true;
			}			
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.ROOM_LIST_URL, params, callback);
	}
	
	public void payMethodClick(View view) {
		Gson gson = new Gson();
		mPayTypeList = gson.fromJson(mDetail.payArrays, new TypeToken<List<PayType>>() {}.getType());
		int length = mPayTypeList.size();
		if (length == 0) {
			showToast("没有支付方式");
			return;
		}		
		String[] strArray = new String[length];
		for (int i = 0; i < length; ++i) {
			strArray[i] = mPayTypeList.get(i).text;
		}
		
		showDialog("支付方式", strArray, mPayMethod);
	}
	
	public void roomClick(View view) {
		if (isGetDataFailed) {
			showToast("无法获取包间数据");
			getRoom(); //重新获取
			return;
		}
		
		int length = mRoomList.size();
		if (length == 0) {
			showToast("包间信息为空");
			return;
		}	
		String[] strArray = new String[length];
		for (int i = 0; i < length; ++i) {
			strArray[i] = mRoomList.get(i).roomName;
		}		
		showDialog("选择包间", strArray, mRoom);
	}
	
	public void timeClick(View view) {
		String[] strArray = new String[12];
		for (int i = 0; i < 12; ++i) {
			strArray[i] = (i + 1) + "";
		}
		showDialog("选择包间用时", strArray, mTime);
	}

	public void payClick(View view) {
		if (TextUtils.isEmpty(mPayMethod.getText())) {
			showToast("请选择支付方式");
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("请确定信息无误");
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendConfirmAction();
			}
		});
		builder.setNegativeButton("取消操作", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void sendConfirmAction() {
		String discount = mDiscount.getText().toString();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("orderId", App.storeOrderDetail.orderId);  
		params.put("detailAmount", mDetail.detailAmount);
	
		params.put("roomId", mDetail.roomId);
		params.put("rooPrice", mDetail.roomPrice);
		params.put("roomHour", mDetail.roomHour);

		params.put("perHourPrice", mDetail.perHourPrice);
		params.put("discount", discount);
		params.put("totalAmount", mDetail.totalAmount);	
		params.put("paymentType", mPayType.value);
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "结账信息 : " + t);
				if (isSuccess) {
					showToast("结账成功");
					EventBus.getDefault().post(new OrderRefreshEvent());
					mActivity.finish();				
				} else {
					showToast("结账失败");	
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast("结账失败");
			}			
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.CONFIRM_CHECKOUT_URL, params, callback);
	}

	public void homeClick(View view) {
		mActivity.finish();
	}
	
	/**
	 * 弹窗选择模板
	 * 
	 * @param title
	 * @param items
	 * @param tv
	 */
	public void showDialog(final String title, final CharSequence[] items, final TextView tv) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(title);
		builder.setItems(items, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tv.setText(items[which]);
				if (title.equals("选择包间")) {
					RoomEntity entity = mRoomList.get(which);
					mDetail.roomName = entity.roomName;
					mDetail.roomId = entity.roomId;
					mDetail.perHourPrice = entity.perHourPrice;
					mPrice.setText(mDetail.perHourPrice);
				}
				
				if (title.equals("支付方式")) {
					mPayType = mPayTypeList.get(which);
				}

				if (title.equals("选择包间用时")) {
					double hour = Double.parseDouble((String) items[which]);
					mDetail.roomHour = items[which].toString();
					double price = Double.parseDouble(mDetail.perHourPrice);
					mDetail.detailAmount = price * hour + "";		
					mCost.setText(mDetail.detailAmount);
				}
			}
		});

		builder.create().show();
	}

}
