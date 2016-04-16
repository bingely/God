package com.meetrend.haopingdian.adatper;

import java.util.List;
import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OrderEntity;
import com.meetrend.haopingdian.enumbean.OrderStatus;
import com.meetrend.haopingdian.enumbean.PageStatus;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.listviewanimations.ArrayAdapter;
import com.meetrend.haopingdian.util.NumerUtil;

public class NewOrderListAdapter extends BaseAdapter {
	
	private static final String TAG = NewOrderListAdapter.class.getName();
	private FinalBitmap loader;
	private List<OrderEntity> list;
	private LayoutInflater mLayoutInflater;
	
	/**
	 * 订单状态
	 */
	private String mStatus;
	
	/**
	 * 0全部，1店内，2线上
	 */
	private String mOrderType;

	public NewOrderListAdapter(Context context, List<OrderEntity> list,String status,String OrderType) {
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
		loader = FinalBitmap.create(context);
		loader.configBitmapMaxHeight(50);
		loader.configBitmapMaxWidth(50);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
		mStatus = status;
		mOrderType = OrderType;
	}

	
	public void setList(List<OrderEntity> list){
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public OrderEntity getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.ordermanager_item_layout, null);
			holder = new ViewHolder();
			holder.id = (TextView) convertView.findViewById(R.id.item_order_id);
			holder.create_time = (TextView) convertView.findViewById(R.id.item_order_create_time);
			holder.image = (ImageView) convertView.findViewById(R.id.item_order_status);

			holder.product_image = (SimpleDraweeView) convertView.findViewById(R.id.item_order_product_image);
			holder.product_id = (TextView) convertView.findViewById(R.id.item_order_product_name);
			holder.order_user_id = (TextView) convertView.findViewById(R.id.item_order_user);
			holder.execute_user_id = (TextView) convertView.findViewById(R.id.item_order_executor);
			holder.amount = (TextView) convertView.findViewById(R.id.item_order_amount);
			holder.total_price = (TextView) convertView.findViewById(R.id.item_order_total_price);
			holder.orderStatusView = (TextView) convertView.findViewById(R.id.order_stutas_text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		OrderEntity data = list.get(position);
		
		if (mOrderType.equals("0")) {
			holder.orderStatusView.setVisibility(View.VISIBLE);
			if (data.orderSource.equals("1")) {
				//店内
				holder.orderStatusView.setText("店内");
				holder.orderStatusView.setTextColor(Color.parseColor("#02bc00"));
				holder.orderStatusView.setBackgroundResource(R.drawable.oderstatus_instore_bg_style);
			}else  
				
			{
				//线上
				holder.orderStatusView.setText("线上");
				holder.orderStatusView.setTextColor(Color.parseColor("#e05f00"));
				holder.orderStatusView.setBackgroundResource(R.drawable.oderstatus_online_bg_style);
			}
		}else {
			holder.orderStatusView.setVisibility(View.GONE);
		}
		
		
		holder.id.setText("订单编号:" + data.orderName);
		holder.create_time.setText("下单时间:" + data.crateTime);
		
		int resId = 0;		
		//订单状态
		OrderStatus status = OrderStatus.get(data.status);
		switch (status) {
		//待确认
		case UN_CONFIRMED:
			resId = OrderStatus.UN_CONFIRMED.getResourceId();
			break;
		//待付款
		case WAIT_APY:
			resId = OrderStatus.WAIT_APY.getResourceId();
			break;
			//待发货
		case HAVE_PAY:
			resId = OrderStatus.HAVE_PAY.getResourceId();
			
			break;
		//已完成
		case FINISHED:
			resId = OrderStatus.FINISHED.getResourceId();
			break;
		//已取消
		case CANCELED:
			resId = OrderStatus.CANCELED.getResourceId();
			break;
		}
		
		holder.image.setImageResource(resId);		
		holder.product_image.setImageURI(Uri.parse(Server.BASE_URL + data.avatarId));
		
		if (null == data.productPici) {
			holder.product_id.setText(data.name);
		}else {
			holder.product_id.setText(data.name + "  " + data.productPici);
		}
		
		
		holder.order_user_id.setText("客户:" + data.userId);
//		if (data.userId.length() >= 1) {
//			String familyName = data.userId.substring(0, 1);
//			familyName = familyName +"**";
//			holder.order_user_id.setText("客户:" + familyName);
//		}else {
//			holder.order_user_id.setText("客户:");
//		}
		
		
		holder.execute_user_id.setText("送货人:" + data.executeUserId);

		//if (data.unitName.equals("")) {
		//	holder.amount.setText("数量: " + NumerUtil.saveThreeDecimal(data.offerPieceQty)+data.unitName);
		//} else {
		//	holder.amount.setText("数量: " + data.quantity+data.unitName);
		//}
		
		holder.amount.setText("数量: " + data.quantity+data.unitName);
		holder.total_price.setText("总价: ¥ " + NumerUtil.setSaveTwoDecimals(data.receivableAmount));// 应收/改价后的总金额
		
		
	
		
		return convertView;
	}

	final static class ViewHolder {
		public TextView id;
		public TextView create_time;
		public ImageView image;

		public SimpleDraweeView product_image;
		public TextView product_id;
		public TextView order_user_id;
		public TextView execute_user_id;
		public TextView amount;
		public TextView total_price;
		
		//在"全部订单"中有 有"店内"，"线上"，两种标识
		public TextView orderStatusView;
	};

}
