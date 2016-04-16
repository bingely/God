package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.InventoryInfoAdapter;
import com.meetrend.haopingdian.bean.InventoryDetailEntity;
import com.meetrend.haopingdian.bean.InventoryItem;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ProgressDialog;

public class InventoryInfoFragment extends BaseFragment {
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_action)
	TextView mAction;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;

	@ViewInject(id = R.id.iv_inventory_info)
	ImageView mProdcutImage;
	@ViewInject(id = R.id.vp_inventory_info)
	ViewPager mPager;
	@ViewInject(id = R.id.detail_inventory_info)
	View detail;
	@ViewInject(id = R.id.history_inventory_info)
	View history;
	@ViewInject(id = R.id.layout_detail, click = "pageClick")
	RelativeLayout detail_layout;
	@ViewInject(id = R.id.layout_history, click = "pageClick")
	RelativeLayout history_layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_inventory_info, container, false);
		FinalActivity.initInjectedView(this, rootView);
		SPUtil util = SPUtil.getDefault(mActivity);
		util.setProductId(App.inventoryItem.productId);		
		initView();
		return rootView;
	}
	
	private void initView() {
		InventoryItem item = App.inventoryItem;
		String pici = "";
		if (item.productPici == null) {
			LogUtil.w(tag, "item.productPici == null");
		} else {
			pici = item.productPici;
		}
		String title = item.productName + "  " + pici;
		mTitle.setText(title);

		FinalBitmap loader = FinalBitmap.create(mActivity);
		loader.display(mProdcutImage, Server.BASE_URL + item.avatarId);

		String[] titles = mActivity.getResources().getStringArray(R.array.inventory_viewpager_indicator_title);
		FragmentPagerAdapter adapter = new InventoryPageAdapter(this.getChildFragmentManager(), titles);
		mPager.setAdapter(adapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {		
			}

			@Override
			public void onPageSelected(int position) {
				boolean flag = position == 0;
				detail.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
				history.setVisibility(!flag ? View.VISIBLE : View.INVISIBLE);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				
			}			
		});
	}	
	
	public void pageClick(View view) {
		switch (view.getId()) {
		case R.id.layout_detail:
			mPager.setCurrentItem(0);
		break;
		case R.id.layout_history:
			mPager.setCurrentItem(1);
		break;
		}
	}

	public void homeClick(View view) {
		mActivity.finish();
	}

}

class InventoryDetailFragment extends BaseFragment {
	private ProgressDialog mLoading;
	private ListView mListView;
	private String productId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_inventory_detail, container, false);
		mListView = (ListView) rootView.findViewById(R.id.lv_inventory_detail);
		mLoading = (ProgressDialog) rootView.findViewById(R.id.pd_inventory_detail);
//		mLoading.setReloadClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				getInventoryDetail();
//			}
//			
//		});
		
		SPUtil util = SPUtil.getDefault(mActivity);
		productId = util.getProductId();
		getInventoryDetail();
		return rootView;
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.FAILED:
				showToast("无法获取库存详情");
				mLoading.setReLoadVisibility(true);
			break;
			case Code.SUCCESS:				
				mLoading.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				mListView.setAdapter(new InventoryInfoAdapter(mActivity, App.inventoryDetail));
			break;
			}
		}
		
	};
	
	private void getInventoryDetail() {
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("id", productId);

		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "inventory detail info : " + t);
				if (!isSuccess) {
					mHandler.sendEmptyMessage(Code.FAILED);
					return;
				}
				
				Gson gson = new Gson();
				App.inventoryDetail = gson.fromJson(data, InventoryDetailEntity.class);
				mHandler.sendEmptyMessage(Code.SUCCESS);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(Code.FAILED);
			}

		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_INVENTORYBYID_URL, params, callback);
	}
}

class InventoryPageAdapter extends FragmentPagerAdapter {
	private String[] titles;

	public InventoryPageAdapter(FragmentManager fm, String[] titles) {
		super(fm);
		this.titles = titles;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0: 
			return new InventoryDetailFragment();
		case 1: 
			return new InventoryHistoryFragment();
		default:
			throw new IllegalArgumentException("position should be " + position); 
		}
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
}