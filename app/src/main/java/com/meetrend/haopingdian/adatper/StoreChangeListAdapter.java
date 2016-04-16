package com.meetrend.haopingdian.adatper;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.StoreInfo;

/**
 * 门店适配器切换适配器
 * @author 肖建斌
 *
 */
public class StoreChangeListAdapter extends  BaseAdapter{
	
	private static final String TAG = StoreChangeListAdapter.class.getSimpleName();
	
	private LayoutInflater mLayoutInflater;
	private List<StoreInfo> list = null;
	private String flag;

	public StoreChangeListAdapter(Context context,List<StoreInfo> list,String flag) {
		mLayoutInflater = LayoutInflater.from(context);
		this.list = list;
		this.flag = flag;
	}

	public void setFlag(String flag){
		this.flag = flag;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		StoreInfo storeInfo = list.get(list.size()-1-position);
		ViewHolder holder = null;
		
		if (convertView == null || convertView.getTag()==null) { 
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_store_change, null);
			holder.store_radio = (ImageView) convertView.findViewById(R.id.store_check_img);
			holder.tv_store_name = (TextView) convertView.findViewById(R.id.tv_store_name);
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_store_name.setText(storeInfo.storeName);
		
		if (flag.equals(storeInfo.storeId+storeInfo.shanghuNum)) 
			holder.store_radio.setImageResource(R.drawable.check_on);
		else
			holder.store_radio.setImageResource(R.drawable.check_off);

		return convertView;
	}
	
	
	public void notifyData(){
		this.notifyDataSetChanged();
	}

	final  class ViewHolder {
		public ImageView store_radio;
		public TextView tv_store_name;
	}
}
