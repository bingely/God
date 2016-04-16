package com.meetrend.haopingdian.adatper;

import java.util.List;
import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Executor;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.widget.RoundImageView;

public class NewExecutorListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<Executor> list;
	private Context context;
	

	public NewExecutorListAdapter(Context context, List<Executor> list) {
			
		this.list = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}


	// 混合布局
	enum ITEM_TYPE {
		ALPHABET, MEMBER;
	}

	private static final int ITEM_TYPE_COUNAT = ITEM_TYPE.values().length;

	@Override
	public int getItemViewType(int position) {
		return getType(position) == ITEM_TYPE.ALPHABET ? 0 : 1;
	}

	@Override
	public boolean isEnabled(int position) {
		return getType(position) != ITEM_TYPE.ALPHABET;
	}

	private ITEM_TYPE getType(int position) {
		Executor executor = list.get(position);
		boolean flag = (executor.pinyinName.length() == 1);
		return flag ? ITEM_TYPE.ALPHABET : ITEM_TYPE.MEMBER;
	}

	@Override
	public int getViewTypeCount() {
		return ITEM_TYPE_COUNAT;
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Executor item = list.get(position);
		
		int type = this.getItemViewType(position);
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case 0:
				convertView = mInflater.inflate(
						R.layout.member_select_list_item_layout, null);
				holder.alphabet = (TextView) convertView
						.findViewById(R.id.tv_alphabet);
				break;
			case 1:
				convertView = mInflater.inflate(R.layout.new_item_executor, null);
				//头像
				holder.avatar = (SimpleDraweeView) convertView
						.findViewById(R.id.iv_executor_avatar);
				//执行人姓名
				holder.name = (TextView) convertView
						.findViewById(R.id.tv_executorr_name);
				//选中标识
				holder.rb_execute = (ImageView) convertView
						.findViewById(R.id.select_img);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (type) {
		case 0:
			holder.alphabet.setText(item.pinyinName);
			break;
		case 1:
			holder.avatar.setImageURI(Uri.parse( Server.BASE_URL + item.entity.avatarId));
			holder.name.setText(item.entity.userName);
			
			if (item.isSelected) {
				holder.rb_execute.setImageResource(R.drawable.check_on);
			}else {
				holder.rb_execute.setImageResource(R.drawable.check_off);
			}
			
			break;
		}
		return convertView;
	}


	class ViewHolder {

		public SimpleDraweeView avatar;
		public TextView name;
		public TextView alphabet;
		public ImageView rb_execute;

	}

}
