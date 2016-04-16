package com.meetrend.haopingdian.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.CheckBean;
import com.meetrend.haopingdian.bean.CheckDetailListItemBean;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.widget.MyListView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 盘点详情
 * 
 * @author 肖建斌
 *
 */
public class StoreCheckDetailActivity extends BaseActivity{
	
	private final static String TAG = StoreCheckDetailActivity.class.getName();
	
	@ViewInject(id = R.id.actionbar_home,click = "finishActivity")
	ImageView backView;
	
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	
	@ViewInject(id = R.id.actionbar_action,click = "editOnclick")
	TextView editBtn;
	
	//盘点
	@ViewInject(id = R.id.check_id)
	TextView checkNumView;
	@ViewInject(id = R.id.check_excutor)
	TextView checkExcutorView;
	@ViewInject(id = R.id.check_time)
	TextView checkTimeView;
	
	//金额
	@ViewInject(id = R.id.shoukuan_money_view)
	TextView shouquanMoneyView;
	@ViewInject(id = R.id.xianjin_money_view)
	TextView xianjinMoneyView;
	@ViewInject(id = R.id.shuaka_money_view)
	TextView shuakaMoneyView;
	@ViewInject(id = R.id.jizhang_money_view)
	TextView jiZhangMoneyView;
	@ViewInject(id = R.id.weixin_money_view)
	TextView wxMoneyView;
	
	//备注
	@ViewInject(id = R.id.beizhu_view)
	TextView beiZhuView;
	
	//
	@ViewInject(id = R.id.check_list)
	MyListView mListView;
	
	
	private ArrayList<CheckDetailListItemBean> mList;
	private CheckListAdapter checkListAdapter;
	private String id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storecheckdetail);
		FinalActivity.initInjectedView(this);
		
		id = getIntent().getStringExtra("id");//详情id
		initViews();
		initDatas();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		requestDetailData(id);
	}	
	
	private void initViews(){
		titleView.setText("盘点详情");
		editBtn.setText("  编辑");
	}
	
	private void initDatas(){
		mList = new ArrayList<CheckDetailListItemBean>();
		
		checkListAdapter = new CheckListAdapter(mList);
		mListView.setAdapter(checkListAdapter);
		
	}
	
	public void editOnclick(View view){
		Intent intent = new Intent(StoreCheckDetailActivity.this,AddStoreCheckActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("list", mList);//盘点的数据
		bundle.putString("id", id);
		bundle.putInt("mode", 2);
		
		bundle.putString("sk", shouquanMoneyView.getText().toString());
		bundle.putString("xj", xianjinMoneyView.getText().toString());
		bundle.putString("ska", shuakaMoneyView.getText().toString());
		bundle.putString("jz", jiZhangMoneyView.getText().toString());
		bundle.putString("wx", wxMoneyView.getText().toString());
		
		bundle.putString("bz", beiZhuView.getText().toString());
		
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
	
	public void finishActivity(View view){
		finish();
	}
	
	//盘点列表详情
	private void requestDetailData(String detailId) {
		
		showDialog("加载中...");

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(StoreCheckDetailActivity.this).getToken());
		params.put("id", detailId);
		
		Callback callback = new Callback(tag, StoreCheckDetailActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					Gson gson = new Gson();
					
					String checkNum = data.get("serialNumber").getAsString();
					String checkExcutor = data.get("createUserId").getAsString();
					String checkTime = data.get("checkTime").getAsString();
					String desText = data.get("description").getAsString();//备注
					
					int isCanModify = data.get("is").getAsInt();//是否可以修改
					//1标识可以修改0不可以
					if (isCanModify == 0) {
						editBtn.setVisibility(View.GONE);
					}
					
					
					checkNumView.setText(checkNum);
					checkExcutorView.setText("经办人："+checkExcutor);
					String formatTime = checkTime.substring(0, 16);
					checkTimeView.setText(formatTime);
					
					beiZhuView.setText(desText);
					
					String shuaKaMoney = data.get("cardMoney").getAsString();
					String xianJinMoney = data.get("cashMoney").getAsString();
					String jiZhangMoney = data.get("tallyMoney").getAsString();
					String shoukuangMoney = data.get("totalMoney").getAsString();
					String wxMoney = data.get("weChatMoney").getAsString();
					
					shuakaMoneyView.setText(NumerUtil.setSaveTwoDecimals(shuaKaMoney));
					xianjinMoneyView.setText(NumerUtil.setSaveTwoDecimals(xianJinMoney));
					jiZhangMoneyView.setText(NumerUtil.setSaveTwoDecimals(jiZhangMoney));
					shouquanMoneyView.setText(NumerUtil.setSaveTwoDecimals(shoukuangMoney));
					wxMoneyView.setText(NumerUtil.setSaveTwoDecimals(wxMoney));
					
					JsonArray jsonArrayStr = data.get("list").getAsJsonArray();
					List<CheckDetailListItemBean> tlist = gson.fromJson(jsonArrayStr,
							new TypeToken<List<CheckDetailListItemBean>>() {}.getType());
					if (mList.size() > 0) {
						mList.clear();
					}
					mList.addAll(tlist);
					checkListAdapter.notifyDataSetChanged();
				}else {
					
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}else {
						showToast("数据加载失败，请重试");
					}
				}
				
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.STORE_LIST_DETAIL, params,callback);
	}
	
	
	
	
	private class CheckListAdapter extends BaseAdapter {
		
		private List<CheckDetailListItemBean> list;
		private LayoutInflater mLayoutInflater;
		
		public CheckListAdapter(List<CheckDetailListItemBean> list){
			this.list = list;
			mLayoutInflater = LayoutInflater.from(StoreCheckDetailActivity.this);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public CheckDetailListItemBean getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			CheckDetailListItemBean checkBean = getItem(position);
			
			ViewHolder viewHolder = null;
			
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.check_detail_list_item_layout, null);
				viewHolder.productImgView = (SimpleDraweeView) convertView.findViewById(R.id.product_icon);
				viewHolder.productNameView = (TextView) convertView.findViewById(R.id.product_name_view);
				viewHolder.productNumView = (TextView) convertView.findViewById(R.id.product_num_view);
				
				convertView.setTag(viewHolder);
				
			}else {
				
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			viewHolder.productImgView.setImageURI(Uri.parse(Server.BASE_URL + checkBean.picture));
			viewHolder.productNameView.setText(checkBean.productName);
			viewHolder.productNumView.setText("实盘数量("+ "盒"+")："+ checkBean.amount);
			
			
			return convertView;
		}
		
		final class ViewHolder{
			SimpleDraweeView productImgView;
			TextView productNameView;
			TextView productNumView;
		}
		
	}
}