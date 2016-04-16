package com.meetrend.haopingdian.adatper;

import java.util.List;
import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.SelectExcutorActivity;
import com.meetrend.haopingdian.bean.Executor;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.SPUtil;

public class ExecutorListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<Executor> list;
	private FinalBitmap loader;
	private int currentChildePosition = -1;
	private Context context;
	private Handler mHandler;
	private String clickstatus;
	private String pages;
	private int clickposition;
	private int getposition;

	/**
	 * 
	 * @param context
	 * @param list
	 * @param mHandler
	 * @param pages
	 *            从哪个页面跳转
	 */
	public ExecutorListAdapter(Context context, List<Executor> list,
			Handler mHandler, String pages) {
		this.list = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		loader = FinalBitmap.create(context);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
		this.mHandler = mHandler;
		this.pages = pages;
	}


	public void setDate(int getposition) {
		this.getposition = getposition;
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
				convertView = mInflater.inflate(R.layout.item_executor, null);
				holder.avatar = (ImageView) convertView
						.findViewById(R.id.iv_executor_avatar);
				holder.name = (TextView) convertView
						.findViewById(R.id.tv_executorr_name);
				holder.addr = (TextView) convertView
						.findViewById(R.id.tv_executor_addr);
				holder.store = (TextView) convertView
						.findViewById(R.id.tv_executor_store);
				holder.rb_execute = (RadioButton) convertView
						.findViewById(R.id.rb_execute);
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
			loader.display(holder.avatar, Server.BASE_URL
					+ item.entity.avatarId);
			holder.name.setText(item.entity.userName);
			holder.rb_execute.setTag(R.layout.item_executor, position);

			if ("changes".equals(pages)) {
				if (App.onlineOrderDetail.executeUserName.equals(item.entity.userName)) {
					holder.rb_execute.setChecked(true);
				}
			} else {
				if (position == SPUtil.getDefault(context).getPotion()) {
					holder.rb_execute.setChecked(true);
				}
			}
			convertView.setOnClickListener(new myClick(holder.rb_execute));
			if ("ok".equals(clickstatus)) {
				if (currentChildePosition != -1
						&& currentChildePosition == position) {
					if ("true".equals(clickstatus)) {
						holder.rb_execute.setChecked(false);
					} else {
						holder.rb_execute.setChecked(true);
					}
				} else {
					holder.rb_execute.setChecked(false);
				}
			} else if ("cf".equals(clickstatus)) {
				holder.rb_execute.setChecked(false);
				currentChildePosition = -9;
			}

			break;
		}
		return convertView;
	}

	public class myClick implements OnClickListener {

		RadioButton rb;

		public myClick(RadioButton rb) {
			super();
			this.rb = rb;
		}

		@Override
		public void onClick(View arg0) {

			RadioButton radbtn = (RadioButton) arg0
					.findViewById(R.id.rb_execute);
			clickposition = Integer.parseInt(radbtn.getTag(
					R.layout.item_executor).toString());

			if (currentChildePosition == clickposition) {
				
				clickstatus = "cf";
				mHandler.sendEmptyMessage(-1);
			
			} else {
				
				currentChildePosition = clickposition;
				mHandler.sendEmptyMessage(currentChildePosition);
				clickstatus = "ok";
				
			}
		}

	}

	class ViewHolder {

		public ImageView avatar;
		public TextView name;
		public TextView addr;
		public TextView store;
		public TextView alphabet;
		public RadioButton rb_execute;

	}

}
