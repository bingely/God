package com.meetrend.haopingdian.adatper;

import java.util.List;

import com.meetrend.haopingdian.bean.TeaProductEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 库存搜索
 * 
 * @author 肖建斌
 *
 */
public class KucunSearchAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<TeaProductEntity> rightStr;
	private LayoutInflater inflator;
	
	public KucunSearchAdapter(Context mContext,List<TeaProductEntity> rightStr){
		this.mContext = mContext;
		this.rightStr = rightStr;
		inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
	
	

}
