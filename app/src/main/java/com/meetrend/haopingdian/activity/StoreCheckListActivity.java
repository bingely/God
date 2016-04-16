package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.CheckListAdapter;
import com.meetrend.haopingdian.adatper.CheckListAdapter.ChangeListener;
import com.meetrend.haopingdian.bean.CheckDetailListItemBean;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.event.StoreCheckEvent;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.zxing.activity.CaptureActivity;
import de.greenrobot.event.EventBus;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 店内盘点 添加产品
 * 
 * @author 肖建斌
 *
 */
public class StoreCheckListActivity extends BaseActivity{
	
	//title
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	@ViewInject(id = R.id.scan_icon)
	ImageView scanImg;
	//扫描
	@ViewInject(id = R.id.scan_icon,click = "scanClick")
	ImageView scanImageView;
	
	@ViewInject(id = R.id.lv_order_list)
	ListView mOrderListView;
	
	//确定
	@ViewInject(id = R.id.confirm_order, click = "onClickConfirmOrder")
	TextView mConfirmOrder;
	
	
	CheckListAdapter mAdapter = null;
	
	//产品 集合
	private ArrayList<CheckDetailListItemBean> list = new ArrayList<CheckDetailListItemBean>();
	
	int position ;
	
	String userId = "";
	String userName = "";
	String phone = "";
	
	/**
	 * 茶品列表中已经有该茶
	 */
	private boolean hasSelect;
	
	private int fromType = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storechecklist);
		FinalActivity.initInjectedView(this);
		
		
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		if (null != bundle) {
			
				fromType = bundle.getInt("from_pay");
				
				int empty = 0;
				try {
					  empty = bundle.getInt("isEmpty");
					  
				} catch (Exception e) {}
				
				if (empty == 1) {
					list.add(new CheckDetailListItemBean());
				}
				
				
				List<CheckDetailListItemBean> tempList = null;
				try {
					 tempList = bundle.getParcelableArrayList("list");
			    } catch (Exception e) {}
				
				if (tempList != null) {
					list.addAll(tempList);
				}
		  }
		
		  init();
		
			
	}
	
	private void init(){
		
		mBarTitle.setText("盘点库存");
		
		//footerview 添加茶品
		LayoutInflater	mLayoutInflater = LayoutInflater.from(StoreCheckListActivity.this);
		View v = mLayoutInflater.inflate(R.layout.layout_store_add_order, null);
		mOrderListView.addFooterView(v);
		
		//添加
		RelativeLayout addRelativeLayout = (RelativeLayout) v.findViewById(R.id.addlayout);
		addRelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CheckDetailListItemBean bean = new CheckDetailListItemBean();
				bean.amount = 0.000;
				bean.productName = "";
				list.add(bean);
				mAdapter.notifyDataSetChanged();
				ListViewUtil.setListViewHeightBasedOnChildren(mOrderListView);
			}
		});
		
		mAdapter = new CheckListAdapter(StoreCheckListActivity.this,list,onStoreClickListener,
				onAdditionListener,onSubTractionListener,ondelTeaClickListener);
		mOrderListView.setAdapter(mAdapter);
		ListViewUtil.setListViewHeightBasedOnChildren(mOrderListView);
	}
	
	//扫描商品
	public void scanClick(View view){
		Intent intent = new Intent(StoreCheckListActivity.this, CaptureActivity.class);
		intent.putExtra("type", 3);
		startActivity(intent);
	}
		
	//确定
	public void onClickConfirmOrder(View v){
		
		for (CheckDetailListItemBean entity : list) {
			if (entity.amount == 0) {
				showToast("请选择商品");
				return;
			}
		}
		
		StoreCheckEvent storeCheckEvent = new StoreCheckEvent();
		storeCheckEvent.arrayList = list;
		storeCheckEvent.mode = 1;
		EventBus.getDefault().post(storeCheckEvent);
		finish();
	}
		
		
		//添加数量按钮监听
		OnClickListener onAdditionListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				position = Integer.parseInt(v.getTag().toString());
				++list.get(position).amount;
				mAdapter.refresh(list);
				mAdapter.notifyDataSetChanged();
			}
		};
		
		// 减少数量按钮监听
		OnClickListener onSubTractionListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				position = Integer.parseInt(v.getTag().toString());
				if (list.get(position).amount == 0) {
					showToast("当前茶品数量为零");
					return;
				}
				
				if(list.get(position).amount > 0)
				 --list.get(position).amount;
				
				mAdapter.refresh(list);
				mAdapter.notifyDataSetChanged();
			}
		};
		
		//删除茶品
		OnClickListener ondelTeaClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				position = Integer.parseInt(v.getTag().toString());
				if (position >=0 && list.size() > 0) {
					list.remove(position);
					mAdapter.refresh(list);
					mAdapter.notifyDataSetChanged();
				}
			}
		};
		
		
		//选择茶品监听
		OnClickListener onStoreClickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(StoreCheckListActivity.this,TeaTypeListActivity.class);
				position = Integer.parseInt(v.getTag().toString());
				startActivity(intent);
			}
		};
		
		

		/**
		 * 接收选茶品列表传递的信息
		 * 
		 * 有两种来源  1 茶列表 2 搜索列表 
		 */
		public void onEventMainThread(TeaAddEvent event) {
			
			Pici pici = event.pici;//产品的批次
			
			//当没有数据的时候
			if (list.size() == 0) {
				
				CheckDetailListItemBean bean = new CheckDetailListItemBean();
				bean.productId = pici.productId;
				bean.productName = pici.fullName;
				bean.picture = pici.productImage;
				bean.amount = event.count;
				list.add(bean);
			}
			
			for(int i = 0;i < list.size(); i++){
				
				CheckDetailListItemBean bean = list.get(i);
				if (null == bean.productId) {
					continue;
				}
				
				if(bean.productName.equals(pici.fullName)){
					++bean.amount;
					hasSelect = true;
					break;
				}
			}
			
			//如果之前没有选择该茶品
			if (!hasSelect) {
				//CheckDetailListItemBean bean  = list.get(position);
				CheckDetailListItemBean bean  = new CheckDetailListItemBean();
				bean.productId = pici.productId;
				bean.productName = pici.fullName;
				bean.picture = pici.productImage;
				bean.amount = event.count;
				list.add(bean);
			}
			
			//删除productid为空的商品
			List<CheckDetailListItemBean> tempList = new ArrayList<CheckDetailListItemBean>();
			for (int i = 0; i < list.size(); i++) {
				CheckDetailListItemBean bean = list.get(i);
				if (TextUtils.isEmpty(bean.productId)) {
					tempList.add(bean);
				}
			}
			list.removeAll(tempList);
			hasSelect = false;//重置
			
			mAdapter.refresh(list);
			mAdapter.notifyDataSetChanged();
			ListViewUtil.setListViewHeightBasedOnChildren(mOrderListView);
			
			//监听输入框的数量输入
			mAdapter.setTextChangeListener(new ChangeListener() {
				
				@Override
				public void changCount(String count, int position) {
					
					if (null == count || "".equals(count)) {
						count ="0.000";
					}
					list.get(position).amount = Double.parseDouble(NumerUtil.saveThreeDecimal(count));
				}
			});
		}
		
		
		
		
		public void onClickHome(View v){
			StoreCheckListActivity.this.finish();
		}
		
		@Override
		public void onDestroy() {
			EventBus.getDefault().unregister(this);
			super.onDestroy();
		}

}