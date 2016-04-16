package com.meetrend.haopingdian.adatper;

import java.util.ArrayList;
import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.util.NumerUtil;
/**
 * 订单详情产品列表
 *
 */
public class NewOrderInfoListItemAdapter extends BaseAdapter {

	private ArrayList<JsonArray> jsonArray;
	private LayoutInflater mInflater;
	FinalBitmap loader;
	
	public NewOrderInfoListItemAdapter(Context context,ArrayList<JsonArray> jsonArray) {
		super();
		this.jsonArray = jsonArray;
		mInflater = LayoutInflater.from(context);
		loader = FinalBitmap.create(context);
	}


	@Override
	public int getCount() {
		return jsonArray.size();
	}


	@Override
	public Object getItem(int arg0) {
		return jsonArray.get(arg0);
	}


	@Override
	public long getItemId(int arg0) {
		return arg0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.orderinfo_list_item_layout, null);
			holder.order_info_avatar = (SimpleDraweeView) convertView.findViewById(R.id.order_info_avatar);
			holder.order_info_teaname = (TextView) convertView.findViewById(R.id.order_info_teaname);
			holder.order_info_price = (TextView) convertView.findViewById(R.id.order_info_price);
			holder.order_info_num = (TextView) convertView.findViewById(R.id.order_info_num);
			holder.product_guige_view = (TextView) convertView.findViewById(R.id.product_guige_view);
			holder.order_product_forvorable = (TextView) convertView.findViewById(R.id.order_product_forvorable);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		JsonArray item = jsonArray.get(position);
		holder.order_info_avatar.setImageURI(Uri.parse(Server.BASE_URL + jsonArray.get(position).avatarId));
		holder.order_info_teaname.setText(item.name + "  " + jsonArray.get(position).productPici);
		holder.order_info_price.setText("¥ "+ item.incomeAmount);
		holder.order_info_num.setText("x" + NumerUtil.saveThreeDecimal(item.quantity));
		holder.order_product_forvorable.setText("-"+NumerUtil.setSaveTwoDecimals(item.discountAmount));//优惠的金额
		
		if (null == item.model1Value) {
			item.model1Value = "";
		}
		if (null == item.model2Value) {
			item.model2Value = "";
		}
		holder.product_guige_view.setText("规格："+ item.model1Value + item.model2Value);
		return convertView;
	}
	
	final static class ViewHolder { 
		public SimpleDraweeView order_info_avatar;//产品图片
		public TextView order_info_teaname;//茶品名称
		public TextView order_info_price;//单价
		public TextView order_product_forvorable;//优惠
		public TextView order_info_num;//数量
		public TextView product_guige_view;//产品规格
		
	}
}
