package com.meetrend.haopingdian.adatper;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.bean.People;
import com.meetrend.haopingdian.widget.CheckableImageView;

public class PeopleAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<People> list;
	private int checkableVisibility = View.GONE;//checkview 状态
	
	public PeopleAdapter(Context context, List<People> list) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public People getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	// 混合布局
	enum ITEM_TYPE {
		ALPHABET, MEMBER;
	}
	private static final int ITEM_TYPE_COUNAT = ITEM_TYPE.values().length;
		
	@Override
	public boolean isEnabled(int position) {
		return getType(position) != ITEM_TYPE.ALPHABET;
	}
	
	@Override
	public int getItemViewType(int position) {
		return getType(position) == ITEM_TYPE.ALPHABET ? 0 : 1;
	}
	
	private ITEM_TYPE getType(int position) {
		People member = list.get(position);
		boolean flag = (member.pinyin.length() == 1);
		return flag ? ITEM_TYPE.ALPHABET : ITEM_TYPE.MEMBER;
	}
	
	@Override
	public int getViewTypeCount() {
		return ITEM_TYPE_COUNAT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = this.getItemViewType(position);
		People people = list.get(position); 
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case 0:
				convertView = mInflater.inflate(R.layout.member_select_list_item_layout, null);
				holder.alphabet = (TextView) convertView.findViewById(R.id.tv_alphabet);
				break;
			case 1:
				convertView = mInflater.inflate(R.layout.item_cantact_add, null);
				holder.contactRadio = (ImageView) convertView.findViewById(R.id.contact_radio);
				holder.status = (TextView) convertView.findViewById(R.id.tv_status);//状态view
				holder.name = (TextView) convertView.findViewById(R.id.tv_name);
				break;
			}
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		switch (type) {
			case 0:
				holder.alphabet.setText(people.pinyin);
				break;
			case 1:
				holder.name.setText(people.name);
				//是否显示状态
				if (people.getStatus() == 1) {
					holder.contactRadio.setVisibility(View.GONE);
					holder.status.setVisibility(View.VISIBLE);
				}else {
					holder.contactRadio.setVisibility(View.VISIBLE);
					holder.status.setVisibility(View.GONE);
				}

				if (people.isCheck){
					holder.contactRadio.setImageResource(R.drawable.check_on);
				}else{
					holder.contactRadio.setImageResource(R.drawable.check_off);
				}

				break;
		}
		return convertView;
	}
	
	public void setList(List<People> list){
	   this.list = list;	
	}

	public static final class ViewHolder {
		public TextView name;
		public ImageView contactRadio;
		public TextView status;
		public TextView alphabet;
	}
	
	public void setCheckableViewVisibility(int visibility) {
		checkableVisibility = visibility;
		this.notifyDataSetChanged();
	}

}