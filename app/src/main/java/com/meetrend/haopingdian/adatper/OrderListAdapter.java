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

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OrderEntity;
import com.meetrend.haopingdian.enumbean.OrderStatus;
import com.meetrend.haopingdian.enumbean.PageStatus;
import com.meetrend.haopingdian.enumbean.PayStatus;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.StringUtil;

public class OrderListAdapter extends BaseAdapter {
	private static final String TAG = OrderListAdapter.class.getSimpleName();
	private FinalBitmap loader;
	private List<OrderEntity> list;
	private LayoutInflater mLayoutInflater;
	private PageStatus pageStatus;

	public OrderListAdapter(Context context, List<OrderEntity> list) {
		mLayoutInflater = LayoutInflater.from(context);
		this.list = list;
		loader = FinalBitmap.create(context);
		loader.configBitmapMaxHeight(50);
		loader.configBitmapMaxWidth(50);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
//		this.type = type;
	}

	public void setType(PageStatus pageStatus){
		this.pageStatus = pageStatus;
	}
	
	public void setList(List<OrderEntity> list){
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_order, null);
			holder = new ViewHolder();
			holder.id = (TextView) convertView.findViewById(R.id.item_order_id);
			holder.create_time = (TextView) convertView.findViewById(R.id.item_order_create_time);
			holder.image = (ImageView) convertView.findViewById(R.id.item_order_status);

			holder.product_image = (ImageView) convertView.findViewById(R.id.item_order_product_image);
			holder.product_id = (TextView) convertView.findViewById(R.id.item_order_product_name);
			holder.order_user_id = (TextView) convertView.findViewById(R.id.item_order_user);
			holder.execute_user_id = (TextView) convertView.findViewById(R.id.item_order_executor);
			holder.amount = (TextView) convertView.findViewById(R.id.item_order_amount);
			holder.total_price = (TextView) convertView.findViewById(R.id.item_order_total_price);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		OrderEntity data = list.get(position);
		holder.id.setText("订单编号:" + data.orderName);
		holder.create_time.setText("下单时间:" + data.crateTime);
		
		OrderStatus status = OrderStatus.get(data.status);
		PayStatus payStatus = PayStatus.get(data.payStatus);		
		int resId = 0;		
		
	//	LogUtil.d(TAG, "OrderStatus : " + status + ", PayStatus : " + payStatus);
		//参考ios的objective-c代码而来
		if( PageStatus.INCOME==pageStatus){
			resId = payStatus.getResourceId();
		}else{
//			resId = status.getResourceId();
//			if(status == OrderStatus.CONFIRMED){
//				if(payStatus == payStatus.UNPAY){
//					resId = payStatus.UNPAY.getResourceId();
//				}else if(payStatus == payStatus.PAYED){
//					resId = payStatus.PAYED.getResourceId();
//				}
//			}
		}
		
		holder.image.setImageResource(resId);		
		loader.display(holder.product_image, Server.BASE_URL + data.avatarId);
		holder.product_id.setText(data.name + "  " + data.productPici);
		holder.order_user_id.setText("下单人:" + data.userId);
		holder.execute_user_id.setText("执行人:" + data.executeUserId);

		if (data.unitName.equals("")) {
			holder.amount.setText("数量: " + data.offerPieceQty+data.unitName);
		} else {
			holder.amount.setText("数量: " + data.quantity+data.unitName);
		}
		
		holder.total_price.setText("总价: ¥ " + data.receivableAmount);
		return convertView;
	}

	final static class ViewHolder {
		public TextView id;
		public TextView create_time;
		public ImageView image;

		public ImageView product_image;
		public TextView product_id;
		public TextView order_user_id;
		public TextView execute_user_id;
		public TextView amount;
		public TextView total_price;
	};

}