package com.meetrend.haopingdian.adatper;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OrderHistoryEntity;
import com.meetrend.haopingdian.enumbean.OrderStatus;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.StringUtil;

public class OrderHistoryAdapter extends BaseAdapter {
	private FinalBitmap loader;
	private LayoutInflater mLayoutInflater;
	private List<OrderHistoryEntity> mList;

	public OrderHistoryAdapter(Context context, List<OrderHistoryEntity> list) {
		mLayoutInflater = LayoutInflater.from(context);
		loader = FinalBitmap.create(context);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_order, null);
			viewHolder = new ViewHolder();
			viewHolder.id = (TextView) convertView.findViewById(R.id.item_order_id);
			viewHolder.create_time = (TextView) convertView.findViewById(R.id.item_order_create_time);
			viewHolder.order_status = (ImageView) convertView.findViewById(R.id.item_order_status);

			viewHolder.product_image = (ImageView) convertView.findViewById(R.id.item_order_product_image);
			viewHolder.product_id = (TextView) convertView.findViewById(R.id.item_order_product_name);
			viewHolder.order_user_id = (TextView) convertView.findViewById(R.id.item_order_user);
			viewHolder.execute_user_id = (TextView) convertView.findViewById(R.id.item_order_executor);
			viewHolder.amount = (TextView) convertView.findViewById(R.id.item_order_amount);
			viewHolder.total_price = (TextView) convertView.findViewById(R.id.item_order_total_price);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		OrderHistoryEntity data = mList.get(position);
		viewHolder.id.setText("订单编号:" + data.orderName);
		viewHolder.create_time.setText("下单时间:" + StringUtil.formatDateStr(data.createTime));
		
		OrderStatus status = OrderStatus.get(data.status);		
		viewHolder.order_status.setImageResource(status.getResourceId());

		loader.display(viewHolder.product_image, Server.BASE_URL + data.avatarId);
		viewHolder.product_id.setText( data.name + " " + data.productPici);
		viewHolder.order_user_id.setText("下单人:" + data.userId);
		viewHolder.execute_user_id.setText("执行人:" + data.executeUserId);
		viewHolder.amount.setText("数量: " + data.offerPieceQty);
		viewHolder.total_price.setText("总价: ¥ " + data.detailAmount);

		return convertView;
	}

	private final static class ViewHolder {
		public TextView id;
		public TextView create_time;
		public ImageView order_status;

		public ImageView product_image;
		public TextView product_id;
		public TextView order_user_id;
		public TextView execute_user_id;
		public TextView amount;
		public TextView total_price;
	};

}
