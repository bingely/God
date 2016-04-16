package com.meetrend.haopingdian.adatper;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BasicAdapter<T> extends BaseAdapter{
	
	public List<T> mList;
	public  LayoutInflater mLayoutInflater;
	
	public BasicAdapter(List<T> list,Context context){
		this.mList = list;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public T getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return convertView;
	}

}