package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.GiveProducSqmActivity;
import com.meetrend.haopingdian.activity.TeaTypeListActivity;
import com.meetrend.haopingdian.adatper.PlaceOrderListAdapter;
import com.meetrend.haopingdian.adatper.PlaceOrderListAdapter.TextChangeListener;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.PlaceOrderEntity;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.event.FinishOrderManagerEvent;
import com.meetrend.haopingdian.event.GiftRefreshEvent;
import com.meetrend.haopingdian.event.OrderRefreshEvent;
import com.meetrend.haopingdian.event.SendTeaListEvent;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.zxing.activity.CaptureActivity;

import de.greenrobot.event.EventBus;
/**
 * 
 * 店内下单 之 确认下单
 *
 */
public class StorePlaceOrderFragment extends BaseFragment {
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	@ViewInject(id = R.id.scan_icon)
	ImageView scanImg;
	
	
	@ViewInject(id = R.id.lv_order_list)
	ListView mOrderListView;
	
	//扫描
	@ViewInject(id = R.id.scan_icon,click = "scanClick")
	ImageView scanImageView;
	
	/**
	 * 确认下单
	 */
	@ViewInject(id = R.id.confirm_order, click = "onClickConfirmOrder")
	TextView mConfirmOrder;
	
	/**
	 * 商品金额
	 */
	@ViewInject(id = R.id.bottom_should_givew_money_view)
	TextView bottomGiveMoneyView;
	
	PlaceOrderListAdapter mAdapter = null;
	
	
	/**
	 * 购买的产品 集合
	 */
	private ArrayList<PlaceOrderEntity> list = new ArrayList<PlaceOrderEntity>();
	
	//选择茶品索引
	int position ;
	
	String userId = "";
	String userName = "";
	String phone = "";
	
	/**
	 * 茶品列表中已经有该茶
	 */
	private boolean hasSelect;
	
	private int fromType = 0;
	
	//添加
	RelativeLayout addRelativeLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_store_place_order, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		mBarTitle.setText("店内收银");
		
		//footerview 添加茶品
		LayoutInflater	mLayoutInflater = LayoutInflater.from(mActivity);
		View v = mLayoutInflater.inflate(R.layout.layout_store_add_order, null);
		addRelativeLayout = (RelativeLayout) v.findViewById(R.id.addlayout);
		mOrderListView.addFooterView(v);
		
		Bundle bundle = getArguments();
		if (null != bundle) {
			fromType = bundle.getInt("from_pay");
			int empty = 0;
			try {
					empty = bundle.getInt("isEmpty");
			} catch (Exception e) {
				
			}
			
			if (empty == 1) {
				list.add(new PlaceOrderEntity());
			}
			
			List<PlaceOrderEntity> tempList = null;
			try {
				 tempList = bundle.getParcelableArrayList("list");
		    } catch (Exception e) {}
			
			if (tempList != null) {
				list.addAll(tempList);
			}
			
			//
			if (fromType == 1) {
				
				if ((null != App.onlineOrderDetail.inputPoint && !App.onlineOrderDetail.inputPoint.equals("")) 
						|| null == App.onlineOrderDetail.voucherRecordId) {
					mConfirmOrder.setEnabled(false);
					mConfirmOrder.setBackgroundColor(Color.parseColor("#f05b72"));
					addRelativeLayout.setBackgroundColor(Color.parseColor("#f7f7f7"));
					addRelativeLayout.setEnabled(false);
				}else {
					mConfirmOrder.setEnabled(true);
					mConfirmOrder.setBackgroundResource(R.drawable.pay_btn_bg);
				}
			}
			
			allPriceChange(list);
			
		  }

		mAdapter = new PlaceOrderListAdapter(mActivity,list,onStoreClickListener,
				onAdditionListener,onSubTractionListener,ondelTeaClickListener,onGiftOnClickListener);
		mOrderListView.setAdapter(mAdapter);
		ListViewUtil.setListViewHeightBasedOnChildren(mOrderListView);
		//添加
		addRelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				PlaceOrderEntity placeOrderEntity = new PlaceOrderEntity();
				placeOrderEntity.pici = null;
				placeOrderEntity.products = null;
				
				list.add(placeOrderEntity);
				//mAdapter.refresh(list);
				mAdapter.notifyDataSetChanged();
				ListViewUtil.setListViewHeightBasedOnChildren(mOrderListView);
			}
		});
		
		//监听输入框的数量输入
		mAdapter.setTextChangeListener(new TextChangeListener() {
			
			@Override
			public void changCount(String count,int position) {
				
				if (null == count || "".equals(count)) {
					count ="0.0";
				}
				
				list.get(position).count = Double.parseDouble(NumerUtil.saveThreeDecimal(count));
				allPriceChange(list);
			}
		});
		
		return rootView;
	}
	
	//扫描商品
	public void scanClick(View view){
		Intent intent = new Intent(getActivity(), CaptureActivity.class);
		intent.putExtra("type", 3);
		startActivity(intent);
	}
	
	/**
	 * 下单 或 完成订单 按钮点击事件
	 * 
	 * */
	public void onClickConfirmOrder(View v){
		
		for (PlaceOrderEntity entity : list) {
			if (entity.products == null || entity.count == 0) {
				showToast("请选择购买产品");
				return;
			}
		}
		
		Bundle bundle = new Bundle();
		bundle.putInt("from_pay", fromType);//标识从该界面跳转到结账界面的
		bundle.putParcelableArrayList("tealist", list);
		
		//发送消息至订单详情
		SendTeaListEvent sendTeaListEvent = new SendTeaListEvent();
		sendTeaListEvent.setBundle(bundle);
		EventBus.getDefault().post(sendTeaListEvent);
		
		//结束
		getActivity().getSupportFragmentManager().popBackStack();
	}
	
	/**
	 * 微信支付成功，进PayResultActivity,点击完成结束
	 * @param event
	 */
	public void onEventMainThread(FinishOrderManagerEvent event) {
		
		getActivity().getSupportFragmentManager().popBackStack();
	}
	
	public void onEventMainThread(OrderRefreshEvent event){
		mActivity.finish();
	}
	
	
	
	/**
	 * 
	 * 商品赠送按钮
	 * */
	OnClickListener onGiftOnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			position = (Integer)v.getTag();
			Intent intent = new Intent(getActivity(), GiveProducSqmActivity.class);
			startActivity(intent);
		}
	};
	
	
	
	/**
	 * 
	 * 添加数量按钮监听
	 * */
	OnClickListener onAdditionListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			position = Integer.parseInt(v.getTag().toString());//添加按钮记录标识
			++list.get(position).count;
			mAdapter.refresh(list);
			mAdapter.notifyDataSetChanged();
			
			allPriceChange(list);
		}
	};
	
	/**
	 * 
	 * 减少数量按钮监听
	 * */
	OnClickListener onSubTractionListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			position = Integer.parseInt(v.getTag().toString());
			if (list.get(position).count == 0) {
				Toast.makeText(getActivity(), "当前茶品数量为零", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(list.get(position).count>0)
			--list.get(position).count;
			
			//if (list.get(position).count == 0) {
				//list.get(position).products = null;
				//list.get(position).pici = null;
			//}
			
			mAdapter.refresh(list);
			mAdapter.notifyDataSetChanged();
			allPriceChange(list);
		}
	};
	
	
	/**
	 *  删除茶品
	 */
	OnClickListener ondelTeaClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			position = Integer.parseInt(v.getTag().toString());
			if (position >=0 && list.size() > 0) {
				list.remove(position);
				mAdapter.refresh(list);
				mAdapter.notifyDataSetChanged();
				allPriceChange(list);
			}
			
		}
	};
	
	
	/**
	 * 
	 * 选择茶品监听
	 * */
	OnClickListener onStoreClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent(mActivity,TeaTypeListActivity.class);
			position = Integer.parseInt(v.getTag().toString());
			startActivity(intent);
		}
	};
	
	
	//赠送商品时接收消息刷新列表
	public void onEventMainThread(GiftRefreshEvent event){
		
		PlaceOrderEntity teaProductEntity = list.get(position);
		Pici wantToPici = teaProductEntity.pici;
		wantToPici.isGift = true;
		
		list.remove(position);
		
		for(int i = 0;i < list.size(); i++){
			
			PlaceOrderEntity poe = list.get(i);
			
			if (null == poe.pici) {
				continue;
			}
			
			//如果同名，都是礼物，则删除当前想要赠送的产品
			if(poe.pici.fullName.equals(wantToPici.fullName) && poe.pici.isGift == true){
				++poe.count;
				break;
			}
			
		 }
		
		if (list.size() == 0) {
			list.add(teaProductEntity);
		}
		
		refreshList();
	}
	

	/**
	 * 接收选茶品列表传递的信息
	 * 
	 * 有两种来源  1 茶列表 2 搜索列表 
	 * 
	 * 
	 * @param event
	 */
	public void onEventMainThread(TeaAddEvent event) {

		Pici eventPici = event.pici;
		Products eventProduct = event.products;
		boolean isGift = event.isGift;//执行赠送产品操作
		boolean isScanBarCode = event.isScanBarCode;//扫描条形码

		//正对条形码扫描展示产品
		if (list.size() == 0) {
			PlaceOrderEntity  placeOrderEntity = new PlaceOrderEntity();
			placeOrderEntity.pici = event.pici;
			placeOrderEntity.products = event.products;
			placeOrderEntity.count = event.count;
			list.add(placeOrderEntity);
		}

		 if (list.size() == 1){
			PlaceOrderEntity firstOrderEntity = list.get(0);
			if (null == firstOrderEntity.pici){
				firstOrderEntity.products = eventProduct;
				firstOrderEntity.pici = eventPici;
				firstOrderEntity.count = 1.000;
			}else{
				if (isGift) {
					firstOrderEntity.pici.isGift = isGift;//是赠送礼物行为
				}else{
					    //选fullName相同和不同都将数量设为1
						firstOrderEntity.products = eventProduct;
						firstOrderEntity.pici = eventPici;
					    if (isScanBarCode){
							firstOrderEntity.count = event.count;
						}else{
							firstOrderEntity.count = 1.000;
						}
				}
			}

		}else{
			PlaceOrderEntity  listItem = null;
			for(int i = 0;i < list.size(); i++){
				listItem = list.get(i);
				if (!isGift){
					//首先要判断pici为null项,有可能有多个选项为null
					if (null == listItem.pici){
						if (i == position){
							listItem.pici = eventPici;
							listItem.products = eventProduct;
							if (isScanBarCode){
								listItem.count = event.count;
							}else{
								listItem.count = 1.000;
							}
							break;
						}
					}else{
							if ( eventPici.fullName.equals(listItem.pici.fullName) ){
								if (isScanBarCode){
									//产品扫码
									listItem.count = event.count;
									listItem.products = event.products;
									listItem.pici = event.pici;
									break;
								}else{

									if (position != i){
										//fullName相同，但是不是在同一个item里去选择商品，所以商品数量加一
										if (listItem.pici.isGift == isGift){
											++listItem.count;
											break;
										}
									}else{
										listItem.count = 1.000;
										break;//此处添加-------------------------------------------
									}

								}
							}else{
								//产品条码扫描(找不到相同名字的商品)
								if (isScanBarCode){
									PlaceOrderEntity placeOrderEntity = new PlaceOrderEntity();
									placeOrderEntity.count = event.count;
									placeOrderEntity.pici = eventPici;
									placeOrderEntity.products = eventProduct;
									list.add(placeOrderEntity);
									break;
								}else{
									//名字不同，但是是同一个选项
									if (position == i){
										listItem.count = 1;
										listItem.pici = eventPici;
										listItem.products = eventProduct;
									}
									//else{
									//不同名字，又不同选项，就不管
									//}
								}

							}

					}
				}else{
					//是执行送礼物操作

					//相同的礼物合并
					PlaceOrderEntity selectEntity = list.get(position);
					if ( listItem.pici.isGift ){
						if (listItem.pici.fullName.equals(selectEntity.pici.fullName)){
							listItem.count = listItem.count + selectEntity.count;//同名的礼物合并
							list.remove(position);
							break;
						}
					}else{
						//不是礼物（但是即将成为礼物），但是没有和列表当中商品同名的
						selectEntity.pici.isGift = true;
						break;
					}
				}
			}
		}

		refreshList();

		//Pici pici = event.pici;//产品的批次
		
		//当没有数据的时候
//		if (list.size() == 0) {
//			PlaceOrderEntity  placeOrderEntity = new PlaceOrderEntity();
//			placeOrderEntity.pici = event.pici;
//			placeOrderEntity.products = event.products;
//			placeOrderEntity.count = event.count;
//			list.add(placeOrderEntity);
//		}
		
//		for(int i = 0;i < list.size(); i++){
//
//			PlaceOrderEntity poe = list.get(i);
//			if (null == poe.pici) {
//				continue;
//			}
//			if (null == poe.pici.model1Name) {
//				poe.pici.model1Name = "";
//			}
//			if (null == poe.pici.model2Name) {
//				poe.pici.model2Name = "";
//			}
//			//如果同名，都是礼物，则删除当前想要赠送的产品
//			if(poe.pici.fullName.equals(pici.fullName) && poe.pici.isGift == pici.isGift){
//				++poe.count;
//				hasSelect = true;
//				break;
//			} else{
//				hasSelect = false;
//			}
//		}
//
//		//如果之前没有选择该茶品
//		if (!hasSelect) {
//			PlaceOrderEntity  placeOrderEntity = new PlaceOrderEntity();
//			placeOrderEntity.pici = event.pici;
//			placeOrderEntity.products = event.products;
//			placeOrderEntity.count = event.count;
//			list.add(placeOrderEntity);
//		}
		
		//refreshList();
	}
	
	private void refreshList(){
		
		//删除Pici为null的产品
		List<PlaceOrderEntity> tempList = new ArrayList<PlaceOrderEntity>();
		Pici tempPici = null;
		for (int i = 0; i < list.size(); i++) {
			PlaceOrderEntity placeOrderEntity = list.get(i);
			tempPici = placeOrderEntity.pici;
			if (null == tempPici) {
				tempList.add(placeOrderEntity);
			}
		}
		list.removeAll(tempList);

		hasSelect = false;//重置
		mAdapter.refresh(list);
		mAdapter.notifyDataSetChanged();
		ListViewUtil.setListViewHeightBasedOnChildren(mOrderListView);
		//计算总价
		allPriceChange(list);
	}

	/**
	 * 合并相同的产品
	 * @param mList
	 */
	private void combineSamePlaceOrderEntity(List<PlaceOrderEntity> mList){

	}
	
	/**
	 * @param list 计算当前选中茶品的总价
	 */
	private void allPriceChange(List<PlaceOrderEntity> list){
		try {
				double mTotal = 0.0;
				for(int i = 0;i<list.size();i++){
					double count = Double.valueOf(list.get(i).count);
					if(count!=0){
						
						PlaceOrderEntity entity = list.get(i);
						
						//如果当前商品时赠品则不计算总金额
						if (entity.pici.isGift) {
							continue;
						}
						
						Pici p = entity.pici;//批次
						String price = p.price;
						mTotal += Double.parseDouble(price) * count;//所有茶叶数量
					}
				}
				bottomGiveMoneyView.setText("总金额: ¥ " + NumerUtil.setSaveTwoDecimals(mTotal+""));//金额去分处理
				
		} catch (Exception e) {
			e.printStackTrace();
			showToast("异常");
		}
	}
	
	public void onClickHome(View v){
		getActivity().getSupportFragmentManager().popBackStack();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}