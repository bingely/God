package com.meetrend.haopingdian.activity;


import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.event.OrderInfoSendTypeEvent;
import com.meetrend.haopingdian.event.PayTypeEvent;
import com.meetrend.haopingdian.event.PeiSongTypeEvent;

import de.greenrobot.event.EventBus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StoreOrderChooseTypeListActivity extends BaseActivity{
	
	private final static String TAG = NewBuidEventActivity.class.getName();
	
	@ViewInject(id = R.id.actionbar_home,click = "backClick")
	ImageView backImageView;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	
	@ViewInject(id = R.id.orederstore_type_listview)
	ListView mListView;
	
	private String sendTypeValue;//发送值
	private int chooseType;//1 配送方式    2支付方式 3 订单详情送货确认配送方式
	private String[] values;
	
	//public enum PAYTYPE{现金,刷卡,记账,微信}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storeordertypelist);
		FinalActivity.initInjectedView(this);
		
		Intent intent =getIntent();
		sendTypeValue = intent.getStringExtra("chooseValue");
		chooseType = intent.getIntExtra("chooseType",-1);
		
		if (chooseType == 1) {
			titleView.setText("配送方式");
			values = new String[]{"现货","自提","快递"};
		}else if (chooseType == 2) {
			titleView.setText("支付方式");
			values = new String[]{"现金","刷卡","记账","微信","余额"};
		}else {
			//订单详情
			titleView.setText("配送方式");
			values = new String[]{"现货","自提","快递"};
		}
		
		BaseAdapter mAdapter = new TypeChooseListAdapter(this, values, sendTypeValue);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent();
				String result = null;
				if (chooseType == 1) {
					
					result = values[position];
					PeiSongTypeEvent peiSongTypeEvent = new PeiSongTypeEvent();
					peiSongTypeEvent.typeVlaue = result;
					EventBus.getDefault().post(peiSongTypeEvent);
					
				}else if (chooseType == 2) {
					
					String value = values[position];
					
					if (value.equals("现金")) {
						
						result =  "cash";
						
					}else if(value.equals("刷卡")) {
						
						result =  "swipecard";
						
					}else if (value.equals("记账")) {
						
						result =   "keepaccounts";
						
					}else if (value.equals("余额")) {
						result = "yue";
					}else {
						//微信支付
						result =  "weixinswipecard";
					}
					
					
					
					PayTypeEvent payTypeEvent = new PayTypeEvent();
					payTypeEvent.typeText = values[position];
					payTypeEvent.typeVlaue = result;
					EventBus.getDefault().post(payTypeEvent);
					
				}else {
					
					//订单详情选择配送方式
					result = values[position];
					OrderInfoSendTypeEvent orderInfoSendTypeEvent = new OrderInfoSendTypeEvent();
					orderInfoSendTypeEvent.typeVlaue = result;
					EventBus.getDefault().post(orderInfoSendTypeEvent);
				}
				
				finish();
			}
		});
	}
	
	public void backClick(View view){
		this.finish();
	}
	
	public class TypeChooseListAdapter extends BaseAdapter {

		private Context context;
		private String  id;
		private String[] types;

		public TypeChooseListAdapter(Context context, String[] types, String id) {
			this.context = context;
			this.types = types;
			this.id = id;
		}
		
		public void setEventTypeId(String id){
			this.id = id;
		}

		@Override
		public int getCount() {
			return types.length;
		}

		@Override
		public String getItem(int position) {
			return types[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.list_popup_simple, parent, false);
				holder.title = (TextView) convertView.findViewById(R.id.list_popup_title_name);
				holder.select = (ImageView) convertView.findViewById(R.id.list_popup_title_img);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.title.setText(types[position]);
			if (types[position].equals(id)) {
				holder.title.setTextColor(Color.parseColor("#e33b3d"));
				holder.select.setVisibility(View.VISIBLE);
			} 
			else {
				holder.title.setTextColor(Color.parseColor("#252525"));
				holder.select.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}

		 class ViewHolder {
			public TextView title;
			public ImageView select;
		}
	}


}