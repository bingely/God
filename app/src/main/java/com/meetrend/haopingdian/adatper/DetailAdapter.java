package com.meetrend.haopingdian.adatper;

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
import com.meetrend.haopingdian.bean.StoreOrderDetail;
import com.meetrend.haopingdian.env.Server;

public class DetailAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private FinalBitmap mLoader;

	public DetailAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		mLoader = FinalBitmap.create(context);
		mLoader.configBitmapMaxHeight(50);
		mLoader.configBitmapMaxWidth(50);
		mLoader.configLoadingImage(R.drawable.loading_default);
		mLoader.configLoadfailImage(R.drawable.loading_failed);
	}

	@Override
	public int getCount() {
		return App.detailList.size();
	}

	@Override
	public Object getItem(int position) {
		return App.detailList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_detail, null);
			holder.name = (TextView) convertView.findViewById(R.id.detail_product_name);
			holder.amount = (TextView) convertView.findViewById(R.id.detail_product_amount);
			holder.price = (TextView) convertView.findViewById(R.id.detail_product_price);
			holder.total = (TextView) convertView.findViewById(R.id.detail_product_total);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		StoreOrderDetail.Detail item = App.detailList.get(position);
		String url = Server.BASE_URL + item.productStorePicture;
		mLoader.display(holder.image, url);

		double price = Double.parseDouble(item.unitPrice);
		double amount = Double.parseDouble(item.productNumber);

		holder.name.setText(item.productName);
		holder.amount.setText("数量: " + item.productNumber);
		holder.price.setText("单价: ¥ " + item.unitPrice);
		holder.total.setText("总价: ¥ " + (price * amount));
		return convertView;
	}

	static class ViewHolder {
		public ImageView image;
		public TextView name;
		public TextView amount;
		public TextView price;
		public TextView total;
	}
}