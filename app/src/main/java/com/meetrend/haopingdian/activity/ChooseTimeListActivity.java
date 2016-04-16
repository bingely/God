package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Time;
import com.meetrend.haopingdian.event.SendTimeListEvent;
import com.meetrend.haopingdian.widget.CheckableImageView;
import de.greenrobot.event.EventBus;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 星期选择
 *
 */
public class ChooseTimeListActivity extends BaseActivity {
	
	private final static String TAG = ChooseTimeListActivity.class.getSimpleName().toString();
	
	private ImageView backImg;
	private TextView titleView;
	private ListView listView;
	private String[] times = new String[]{"周一","周二","周三","周四","周五","周六","周天"}; 
	private List<Time> datas;
	private List<Time> selectDatas;
	private TimeAdapter timeAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_time_list);
		
		initViews();
		selectDatas = new ArrayList<Time>();
		timeAdapter = new TimeAdapter();
		listView.setAdapter(timeAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Time time = datas.get(position);
				if (time.ischeck) {
					time.ischeck = false;
					selectDatas.remove(datas.get(position));
				}else {
					time.ischeck = true;
					selectDatas.add(datas.get(position));
				}
				
				timeAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void initViews(){
		
		backImg = (ImageView) this.findViewById(R.id.actionbar_home);
		titleView = (TextView) this.findViewById(R.id.actionbar_title);
		titleView.setText("选择重复时间");
		TextView sureBtn = (TextView) this.findViewById(R.id.actionbar_action);
		sureBtn.setText("确定");
		listView = (ListView) this.findViewById(R.id.listview);
		
		datas = new ArrayList<Time>();
		for (int i = 0; i < times.length; i++) {
			Time time = new Time();
			time.stime = times[i];
			time.ischeck =false;
			datas.add(time);
		}
		
		sureBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    
			    if (selectDatas.size() == 0) {
			    	showToast("请选择时间");
					return;
				}
			    
			    List<String> tempList = new ArrayList<String>();
			    for (int i = 0; i < selectDatas.size(); i++) {
					tempList.add(selectDatas.get(i).stime);
				}
			   
				SendTimeListEvent sendTimeListEvent = new SendTimeListEvent();
				sendTimeListEvent.timeList = tempList;
				EventBus.getDefault().post(sendTimeListEvent);
				ChooseTimeListActivity.this.finish();
			}
		});
		
		backImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
	private class TimeAdapter extends BaseAdapter{

		LayoutInflater layoutInflater;
		
		public TimeAdapter() {
			layoutInflater = LayoutInflater.from(ChooseTimeListActivity.this);
		}
		
		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Time getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			convertView = layoutInflater.inflate(R.layout.choosetime_item_layout, null);
			
			ImageView imageView = (ImageView) convertView.findViewById(R.id.time_check);
			TextView timeTextView = (TextView) convertView.findViewById(R.id.time_view);
			timeTextView.setText(times[position]);
			
			Time time = datas.get(position);
			timeTextView.setText(time.stime);
			if (time.ischeck) {
				imageView.setImageResource(R.drawable.address_list_select_icon);
			}else {
				imageView.setImageResource(R.drawable.empty);
			}
			
			return convertView;
		}
		
	}
	
	public void finishActivity(View view){
		finish();
	}


}