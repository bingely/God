package com.meetrend.haopingdian.adatper;

import java.util.List;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.CheckDetailListItemBean;
import com.meetrend.haopingdian.util.NumerUtil;

/**
 *  盘点添加列表
 * @author 肖建斌
 *
 */
public class CheckListAdapter extends BaseAdapter {
	private static final String TAG = CheckListAdapter.class.getSimpleName();
			
	private LayoutInflater mLayoutInflater;
	private List<CheckDetailListItemBean> list = null;
	private final int length = 3;//保留三位小数

	public int currentPosition = -1;

	public OnClickListener onStoreClickLister;
	public OnClickListener onAdditionListener;
	public OnClickListener onSubTractionListener;
	public OnClickListener onDelOnClickListener;

	public interface ChangeListener {
		public void changCount(String count, int position);
	}
	
	public ChangeListener textChangeListener;

	public void setTextChangeListener(ChangeListener textChangeListener) {
		this.textChangeListener = textChangeListener;
	}

	public CheckListAdapter(Context context, List<CheckDetailListItemBean> list,
			OnClickListener onStoreClickLister,
			OnClickListener onAdditionListener,
			OnClickListener onSubTractionListener,OnClickListener onDelOnClickListener) {
		mLayoutInflater = LayoutInflater.from(context);
		this.list = list;
		this.onStoreClickLister = onStoreClickLister;
		this.onAdditionListener = onAdditionListener;
		this.onSubTractionListener = onSubTractionListener;
		this.onDelOnClickListener = onDelOnClickListener;
	}

	public void refresh(List<CheckDetailListItemBean> list) {
		this.list = list;
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
		
		CheckDetailListItemBean order = list.get(position);
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_storecheck_list_layout,null);
					
			holder.tv_teaname = (TextView) convertView
					.findViewById(R.id.tv_teaname);
			holder.img_subtraction = (ImageView) convertView
					.findViewById(R.id.img_subtraction);
			holder.img_addition = (ImageView) convertView
					.findViewById(R.id.img_addition);
			holder.ed_contacts = (EditText) convertView
					.findViewById(R.id.ed_contacts);
			holder.delRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.del_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		
		if (NumerUtil.isFloat(order.amount +"") || NumerUtil.isInteger(order.amount +"")) {
			 String formatNum = NumerUtil.saveThreeDecimal(order.amount +"");
			 holder.ed_contacts.setText(formatNum);
		 }else{
			 textChangeListener.changCount("0.000", position);
			 holder.ed_contacts.setText("0.000");
		 }
		
		// 产品名称
		holder.tv_teaname.setTag(position);
		holder.tv_teaname.setText(order.productName);
		holder.tv_teaname.setOnClickListener(onStoreClickLister);

		// "+"数量
		holder.img_addition.setTag(position);
		holder.ed_contacts.setEnabled(false);
		if (!TextUtils.isEmpty(holder.tv_teaname.getText())) {
			holder.img_addition.setOnClickListener(onAdditionListener);
			holder.ed_contacts.setEnabled(true);
		}

		// "-"数量
		holder.img_subtraction.setTag(position);
		holder.img_subtraction.setOnClickListener(onSubTractionListener);

		// 编辑框监听数量
		holder.ed_contacts.addTextChangedListener(new MyChangeListener(holder.ed_contacts, position));
		
		//删除自己
		holder.delRelativeLayout.setTag(position);
		holder.delRelativeLayout.setOnClickListener(onDelOnClickListener);

		return convertView;
	}

	public void notifyData() {
		this.notifyDataSetChanged();
	}

	class MyChangeListener  implements TextWatcher {

		private EditText editText;
		private int position;

		public MyChangeListener(EditText editText, int postion) {
			this.editText = editText;
			this.position = postion;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
					
			
		}
		

		@Override
		public void afterTextChanged(Editable s) {
			
			if (textChangeListener != null) {
				
				 if (NumerUtil.isFloat(s.toString()) || NumerUtil.isInteger(s.toString())) {
					 String formatNum = NumerUtil.saveThreeDecimal(s.toString());
					 textChangeListener.changCount(formatNum, position);
				 }else{
					 textChangeListener.changCount("0.000", position);
				 }
				 
			}
		}
	}
	
	class ViewHolder{
		public ImageView img_subtraction;
		public ImageView img_addition;
		public TextView tv_teaname;
		public EditText ed_contacts;
		public RelativeLayout delRelativeLayout;
	}
}
