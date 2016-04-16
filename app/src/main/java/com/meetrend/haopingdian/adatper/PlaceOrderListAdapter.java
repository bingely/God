package com.meetrend.haopingdian.adatper;

import java.util.List;
import android.content.Context;
import android.content.Intent;
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
import com.meetrend.haopingdian.activity.GiveProducSqmActivity;
import com.meetrend.haopingdian.bean.PlaceOrderEntity;
import com.meetrend.haopingdian.fragment.InputSQMFragment;
import com.meetrend.haopingdian.util.NumerUtil;

public class PlaceOrderListAdapter extends BaseAdapter {
	private static final String TAG = PlaceOrderListAdapter.class
			.getSimpleName();
	private LayoutInflater mLayoutInflater;
	private List<PlaceOrderEntity> list = null;
	private final int length = 3;//保留三位小数

	public int currentPosition = -1;

	public OnClickListener onStoreClickLister;
	public OnClickListener onAdditionListener;
	public OnClickListener onSubTractionListener;
	public OnClickListener onDelOnClickListener;
	public OnClickListener onGiftClickListener;

	public interface TextChangeListener {
		public void changCount(String count, int position);
	}
	
	public TextChangeListener textChangeListener;

	public void setTextChangeListener(TextChangeListener textChangeListener) {
		this.textChangeListener = textChangeListener;
	}

	private Context mcontext;
	public PlaceOrderListAdapter(Context context, List<PlaceOrderEntity> list,
			OnClickListener onStoreClickLister,
			OnClickListener onAdditionListener,
			OnClickListener onSubTractionListener,OnClickListener onDelOnClickListener, OnClickListener onGiftClickListener) {
		mLayoutInflater = LayoutInflater.from(context);
		this.list = list;
		this.onStoreClickLister = onStoreClickLister;
		this.onAdditionListener = onAdditionListener;
		this.onSubTractionListener = onSubTractionListener;
		this.onDelOnClickListener = onDelOnClickListener;
		this.onGiftClickListener = onGiftClickListener;
		mcontext = context;
	}

	public void refresh(List<PlaceOrderEntity> list) {
		this.list = list;
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
		
		PlaceOrderEntity order = list.get(position);
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_place_order,null);
					
			holder.tv_teaname = (TextView) convertView
					.findViewById(R.id.tv_teaname);
			holder.img_subtraction = (ImageView) convertView
					.findViewById(R.id.img_subtraction);
			holder.img_addition = (ImageView) convertView
					.findViewById(R.id.img_addition);
			holder.ed_contacts = (EditText) convertView
					.findViewById(R.id.ed_contacts);
			holder.teaPriceView = (TextView) convertView
					.findViewById(R.id.one_price_view);
			holder.delRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.del_layout);
			holder.triangleImg = (ImageView) convertView.findViewById(R.id.triangle_icon);
			holder.giftLayout = (RelativeLayout) convertView.findViewById(R.id.gift_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		
		if (NumerUtil.isFloat(order.count+"") || NumerUtil.isInteger(order.count+"")) {
			 String formatNum = NumerUtil.saveThreeDecimal(order.count+"");
			 holder.ed_contacts.setText(formatNum);
		 }else{
			 textChangeListener.changCount("0.000", position);
			 holder.ed_contacts.setText("0.000");
		 }
		
		//赠品标识显示
		if (order.pici != null && order.pici.isGift) {
			holder.triangleImg.setVisibility(View.VISIBLE);
			holder.giftLayout.setVisibility(View.GONE);
		}else {
			holder.triangleImg.setVisibility(View.GONE);
			holder.giftLayout.setVisibility(View.VISIBLE);
		}
		
		// 产品名称
		holder.tv_teaname.setTag(position);
		
		if (order.pici != null) {
			
			String piciName = "";
			String piciPrice = "";
			if (null != order.pici  ) {
				if (null == order.pici.model1Name) {
					order.pici.model1Name = "";
				}
				
				if (null == order.pici.model2Name) {
					order.pici.model2Name = "";
				}
				
				piciName = order.pici.model1Name + order.pici.model2Name;
				
				piciPrice = order.pici.price;
			}else {
				piciName = "";
				piciPrice = "";
			}
			
			holder.tv_teaname.setText(order.pici.fullName);
			// 单价
			//holder.teaPriceView.setText("¥"+NumerUtil.setSaveTwoDecimals(piciPrice));
			holder.teaPriceView.setText("¥"+NumerUtil.setSaveTwoDecimals(piciPrice)+"/"+ order.pici.unitName);
		}else {
			holder.tv_teaname.setText("");
			// 单价
			holder.teaPriceView.setText("");
		}

		holder.tv_teaname.setOnClickListener(onStoreClickLister);

		// 添加数量
		holder.img_addition.setTag(position);
		holder.ed_contacts.setEnabled(false);
		if (!TextUtils.isEmpty(holder.tv_teaname.getText())) {
			holder.img_addition.setOnClickListener(onAdditionListener);
			holder.ed_contacts.setEnabled(true);
		}

		// 减数量
		holder.img_subtraction.setTag(position);
		holder.img_subtraction.setOnClickListener(onSubTractionListener);

		// 编辑框监听数量
		holder.ed_contacts.addTextChangedListener(new ChangeListener(holder.ed_contacts, position));
		
		//删除自己
		holder.delRelativeLayout.setTag(position);
		holder.delRelativeLayout.setOnClickListener(onDelOnClickListener);
		
		//赠送
		holder.giftLayout.setTag(position);
		if (!TextUtils.isEmpty(holder.tv_teaname.getText())) {
			holder.giftLayout.setEnabled(true);
			holder.giftLayout.setOnClickListener(onGiftClickListener);
		}

		return convertView;
	}

	public void notifyData() {
		this.notifyDataSetChanged();
	}

	

	
	class ChangeListener  implements TextWatcher {

		private EditText editText;
		private int position;

		public ChangeListener(EditText editText, int postion) {
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
					 //editText.setText(formatNum);
				 }else{
					 textChangeListener.changCount("0.000", position);
					 //editText.setText("0.000");
				 }
				 
			}
		}
	}
	
	class ViewHolder{

		public ImageView img_subtraction;
		public ImageView img_addition;
		public TextView tv_teaname;
		public EditText ed_contacts;
		public TextView teaPriceView;
		public RelativeLayout delRelativeLayout;
		
		ImageView triangleImg;//赠品标识
		RelativeLayout giftLayout;//赠送btn
	}
}