package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.UISpinnerAdapter;
import com.meetrend.haopingdian.bean.Schema;
import com.meetrend.haopingdian.fragment.AllOrderFragment;
import com.meetrend.haopingdian.fragment.BaseFragment;
import com.meetrend.haopingdian.fragment.OnLineOrderFragment;
import com.meetrend.haopingdian.fragment.StoreOrderFragment;
import com.meetrend.haopingdian.widget.BaseTopPopWindow;
import com.meetrend.haopingdian.widget.OrdersListTopPopWindow;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

/**
 * 功能：新版 订单管理
 * 
 * @author 肖建斌
 * 
 * @date 2015/9/24
 * 
 */
public class NewOrderManagerActivity extends BaseActivity {

	private final static String TAG = NewOrderManagerActivity.class.getName();


	/**
	 * 标题栏
	 */
	@ViewInject(id = R.id.ordernamager_title_layout)
	ViewGroup mtitleBar;

	/**
	 * 返回
	 */
	@ViewInject(id = R.id.actionbar_home, click = "back")
	ImageView mBackImageView;

	/**
	 * 标题
	 */
	@ViewInject(id = R.id.actionbar_title, click = "popListViewClick")
	TextView mTitleView;

	@ViewInject(id = R.id.ordermanager_container)
	FrameLayout container;

	@ViewInject(id = R.id.popwindow_container)
	FrameLayout popContainer;


	/**
	 * 三角形向下图片标志
	 */
	private Drawable triangleDown;

	/**
	 * 三角形向上标识
	 */
	private Drawable triangleUp;

	/**
	 * 标题栏下弹出的窗口
	 */
	private PopupWindow mPopup;

	/**
	 * 订单种类
	 */
	private final static String[] ORDER_TYPES = new String[] { "全部订单", "线上订单",
			"店内订单" };

	/**
	 * 查询方案集合
	 */
	private List<Schema> mSmList;

	// 查询方案对应的Fragment
	private AllOrderFragment allOrderFragment = null;
	private StoreOrderFragment storeOrderFragment = null;
	private OnLineOrderFragment onLineOrderFragment = null;

	private BaseFragment[] fragmentArray ;
	private int curIndex = 0;//当前显示的index

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newordermanager);
		FinalActivity.initInjectedView(this);

		fragmentArray = new BaseFragment[3];

		initViews();

		initDatas();
	}

	private void initDatas() {
		Schema allchSchema = new Schema(ORDER_TYPES[0], "", true);
		Schema onlineSchSchema = new Schema(ORDER_TYPES[1], "", false);
		Schema storeChSchema = new Schema(ORDER_TYPES[2], "", false);
		mSmList = new ArrayList<Schema>();
		mSmList.add(allchSchema);
		mSmList.add(onlineSchSchema);
		mSmList.add(storeChSchema);
		mTitleView.setText("订单管理");
		mTitleView.setCompoundDrawables(null, null, triangleDown, null);

		// 初始化视图
		allOrderFragment = new AllOrderFragment();
		storeOrderFragment = new StoreOrderFragment();
		onLineOrderFragment = new OnLineOrderFragment();

		fragmentArray[0] = allOrderFragment;
		fragmentArray[1] = onLineOrderFragment;
		fragmentArray[2] = storeOrderFragment;

		this.getSupportFragmentManager().beginTransaction()
				.add(R.id.ordermanager_container, fragmentArray[curIndex]).commit();

	}

	private void initViews() {
		// 确定三角形符号的大小
		triangleDown = this.getResources().getDrawable(R.drawable.spinner_down);
		triangleDown.setBounds(0, 0, triangleDown.getMinimumWidth(),
				triangleDown.getMinimumHeight());
		triangleUp = this.getResources().getDrawable(R.drawable.spinner_up);
		triangleUp.setBounds(0, 0, triangleUp.getMinimumWidth(),
				triangleUp.getMinimumHeight());
	}
	

	OrdersListTopPopWindow popWindow = null;
	private boolean toggle;

	public void popListViewClick(View v){

		if (null == popWindow){

			popWindow = new OrdersListTopPopWindow(this, mTitleView,mSmList);
			FrameLayout.LayoutParams poParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			popContainer.addView(popWindow,0,poParams);

			//控制三角形的隐藏和现实
			if (popWindow.getPopContentViewVisibility() == View.VISIBLE)
				mTitleView.setCompoundDrawables(null, null, triangleDown, null);
			else
				mTitleView.setCompoundDrawables(null, null, triangleUp, null);

			popWindow.startInAnimation();
			popWindow.setCallback(new BaseTopPopWindow.ToggleValueCallback() {
				@Override
				public void callback(boolean value) {
					mTitleView.setCompoundDrawables(null, null, triangleDown, null);
					toggle = value;
				}
			});

			popWindow.setSwitchCallBack(new OrdersListTopPopWindow.SwitchCallBack() {
				@Override
				public void switchPosition(int position) {
					String text = mSmList.get(position).text;
					if (text.equals("全部订单")){
						mTitleView.setText("订单管理");
					}else{
						mTitleView.setText(text);
					}

					mTitleView.setCompoundDrawables(null, null, triangleDown, null);
					toggle = false;
					switchFragment(position);
				}
			});

			toggle = true;

		}else{

			if (popWindow.getPopContentViewVisibility() == View.VISIBLE)
				mTitleView.setCompoundDrawables(null, null, triangleDown, null);
			else
				mTitleView.setCompoundDrawables(null, null, triangleUp, null);

			if (!toggle){
				popWindow.startInAnimation();
			}else{
				popWindow.StartOutAnimation();
			}

			toggle = !toggle;
		}
	}

	private void switchFragment(int position){

		if (curIndex == position)
			return;

		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		//隐藏当前的fragment
		ft.hide(fragmentArray[curIndex]);

		switch (position) {
			// 所有订单
			case 0:

				if (!fragmentArray[0].isAdded()){
					ft.add(R.id.ordermanager_container, fragmentArray[0]);
				}
				ft.show(fragmentArray[0]).commitAllowingStateLoss();
				curIndex = 0;

				break;

			// 线上订单
			case 1:

				if (!fragmentArray[1].isAdded()){
					ft.add(R.id.ordermanager_container, fragmentArray[1]);
				}
				ft.show(fragmentArray[1]).commitAllowingStateLoss();
				curIndex = 1;
				break;

			// 店内订单
			case 2:
				if (!fragmentArray[2].isAdded()){
					ft.add(R.id.ordermanager_container, fragmentArray[2]);
				}
				ft.show(fragmentArray[2]).commitAllowingStateLoss();
				curIndex = 2;

				break;

			default:
				break;
		}
	}


//	public void popListViewClick(View v) {
//
//		View view = null;
//		if (mPopup != null) {
//			if (mPopup.isShowing()) {
//				mPopup.dismiss();
//				mTitleView.setCompoundDrawables(null, null, triangleDown, null);
//			} else {
//				mPopup.showAsDropDown(mtitleBar);
//				mTitleView.setCompoundDrawables(null, null, triangleUp, null);
//			}
//		} else {
//			View rootView = LayoutInflater.from(this).inflate(
//					R.layout.ordermager_titlebottom_pop_layout, null);
//			ListView mPopListView = (ListView) rootView
//					.findViewById(R.id.lv_popup);
//			// 阴影
//			 view = rootView.findViewById(R.id.dummy_popup);
//
//			mPopListView.setAdapter(new UISpinnerAdapter(
//					NewOrderManagerActivity.this, mSmList, mTitleView));
//			mPopListView
//					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//						@Override
//						public void onItemClick(AdapterView<?> parent,
//												View view, int position, long id) {
//							mPopup.dismiss();
//
//							// 标题随着切换更改
//							if (mSmList.get(position).getText().toString().equals("全部订单")) {
//								mTitleView.setText("订单管理");
//							} else {
//								mTitleView.setText(mSmList.get(position).getText());
//							}
//
//
//							switch (position) {
//								// 所有订单
//								case 0:
//
//									if (!allOrderFragment.isAdded()) {
//
//										if (storeOrderFragment.isVisible()) {
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.hide(storeOrderFragment).commit();
//										}
//
//										if (onLineOrderFragment.isVisible()) {
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.hide(onLineOrderFragment).commit();
//										}
//
//
//										NewOrderManagerActivity.this
//												.getSupportFragmentManager()
//												.beginTransaction()
//												.add(R.id.ordermanager_container,
//														allOrderFragment).commit();
//										NewOrderManagerActivity.this
//												.getSupportFragmentManager()
//												.beginTransaction()
//												.show(allOrderFragment).commit();
//									} else {
//										if (allOrderFragment.isHidden()) {
//
//											if (storeOrderFragment.isVisible()) {
//												NewOrderManagerActivity.this
//														.getSupportFragmentManager()
//														.beginTransaction()
//														.hide(storeOrderFragment)
//														.commit();
//											}
//
//											if (onLineOrderFragment.isVisible()) {
//												NewOrderManagerActivity.this
//														.getSupportFragmentManager()
//														.beginTransaction()
//														.hide(onLineOrderFragment)
//														.commit();
//											}
//
//
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.show(allOrderFragment)
//													.commit();
//
//										}
//									}
//
//									break;
//								// 线上订单
//								case 1:
//
//									if (!onLineOrderFragment.isAdded()) {
//
//										if (allOrderFragment.isVisible()) {
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.hide(allOrderFragment).commit();
//										}
//
//
//										if (storeOrderFragment.isVisible()) {
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.hide(storeOrderFragment).commit();
//										}
//
//
//										NewOrderManagerActivity.this
//												.getSupportFragmentManager()
//												.beginTransaction()
//												.add(R.id.ordermanager_container,
//														onLineOrderFragment)
//												.commit();
//										NewOrderManagerActivity.this
//												.getSupportFragmentManager()
//												.beginTransaction()
//												.show(onLineOrderFragment).commit();
//									} else {
//
//										if (onLineOrderFragment.isHidden()) {
//
//											if (storeOrderFragment.isVisible()) {
//												NewOrderManagerActivity.this
//														.getSupportFragmentManager()
//														.beginTransaction()
//														.hide(storeOrderFragment)
//														.commit();
//											}
//
//											if (allOrderFragment.isVisible()) {
//												NewOrderManagerActivity.this
//														.getSupportFragmentManager()
//														.beginTransaction()
//														.hide(allOrderFragment)
//														.commit();
//											}
//
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.show(onLineOrderFragment)
//													.commit();
//
//										}
//									}
//									break;
//
//								// 店内订单
//								case 2:
//									if (!storeOrderFragment.isAdded()) {
//
//										if (allOrderFragment.isVisible()) {
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.hide(allOrderFragment).commit();
//										}
//
//										if (onLineOrderFragment.isVisible()) {
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.hide(onLineOrderFragment).commit();
//										}
//
//										NewOrderManagerActivity.this
//												.getSupportFragmentManager()
//												.beginTransaction()
//												.add(R.id.ordermanager_container,
//														storeOrderFragment)
//												.commit();
//										NewOrderManagerActivity.this
//												.getSupportFragmentManager()
//												.beginTransaction()
//												.show(storeOrderFragment).commit();
//									} else {
//
//										if (storeOrderFragment.isHidden()) {
//
//											if (onLineOrderFragment.isVisible()) {
//												NewOrderManagerActivity.this
//														.getSupportFragmentManager()
//														.beginTransaction()
//														.hide(onLineOrderFragment)
//														.commit();
//											}
//
//											if (allOrderFragment.isVisible()) {
//												NewOrderManagerActivity.this
//														.getSupportFragmentManager()
//														.beginTransaction()
//														.hide(allOrderFragment)
//														.commit();
//											}
//
//											NewOrderManagerActivity.this
//													.getSupportFragmentManager()
//													.beginTransaction()
//													.show(storeOrderFragment)
//													.commit();
//
//										}
//									}
//
//									break;
//
//								default:
//									break;
//							}
//
//						}
//					});
//			mPopup = new PopupWindow(rootView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//			mPopup.setTouchable(true);
//			mPopup.setFocusable(true);
//			mPopup.setOutsideTouchable(true);
//			mPopup.setAnimationStyle(R.style.mypopwindow_anim_style);
//			mPopup.setOnDismissListener(new OnDismissListener() {
//				@Override
//				public void onDismiss() {
//					mTitleView.setCompoundDrawables(null, null, triangleDown,
//							null);
//				}
//			});
//			view.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					mPopup.dismiss();
//					//setWindowGrayBg(1.0f);///////////////////////
//				}
//			});
//			mTitleView.setCompoundDrawables(null, null, triangleUp, null);
//			mPopup.setBackgroundDrawable(new ColorDrawable(0xb0000000));
//			mPopup.showAsDropDown(mtitleBar);
//		}
//	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onBackPressed() {
		if (toggle){
			popWindow.StartOutAnimation();
			toggle = false;
			return;
		}
		super.onBackPressed();
	}
}