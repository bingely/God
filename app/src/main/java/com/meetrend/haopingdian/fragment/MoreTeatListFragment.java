package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.annotation.view.ViewInject;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray;
import com.meetrend.haopingdian.bean.PlaceOrderEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.util.NumerUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 打印小票阶段
 * 
 * 订单详情 更多茶品列表
 * @author 肖建斌
 *
 */
public class MoreTeatListFragment extends BaseFragment {

	private final static String TAG = MoreTeatListFragment.class.getName();

	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;

	@ViewInject(id = R.id.moretea_listview)
	ListView mListView;
	
	public ArrayList<PlaceOrderEntity> teaList;
	
	public ArrayList<JsonArray> jsonArraysList;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_moretealist,
				container, false);
		FinalActivity.initInjectedView(this, rootView);
		mBarTitle.setText("商品清单");
		mListView.setFooterDividersEnabled(true);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	
		
	  Bundle bundle = getArguments();
	  
	  
	  int type = -1;//1 从订单详情触发 2从打印小票界面进入
	  type = bundle.getInt("type");
	  if (type == 1) {
		  
		  	teaList =  bundle.getParcelableArrayList("list");
			if (teaList != null || teaList.size() > 0) {
				mListView.setAdapter(new TeaAdapter(getActivity()));
			}
	  }
	  //从打印小票界面进入
	  else {
		
		  jsonArraysList = bundle.getParcelableArrayList("list");
		  Tea2Adapter tea2Adapter = new Tea2Adapter(getActivity());
		  mListView.setAdapter(tea2Adapter);
		  
	  }
	}

	public void onClickHome(View view) {
		getActivity().getSupportFragmentManager().popBackStack();
	}

	/**
	 * 列表适配器
	 * 
	 */
	public class TeaAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater = null;
		private FinalBitmap finalBp = null;

		public TeaAdapter(Context context) {
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			finalBp = FinalBitmap.create(context);
			finalBp.configLoadingImage(R.drawable.loading_default);
			finalBp.configLoadfailImage(R.drawable.loading_failed);
			finalBp.configBitmapMaxHeight(60);
			finalBp.configBitmapMaxWidth(60);
		}

		@Override
		public int getCount() {
			return teaList.size();
		}

		@Override
		public PlaceOrderEntity getItem(int position) {

			return teaList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder = null;

			if (convertView == null) {
				holder = new Holder();
				convertView = layoutInflater.inflate(
						R.layout.more_tea_list_item, null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.more_tea_imgview);
				holder.teaNameTv = (TextView) convertView
						.findViewById(R.id.more_tea_name_view);
				holder.priceTv = (TextView) convertView
						.findViewById(R.id.more_tea_price);
				holder.teaNumView = (TextView) convertView
						.findViewById(R.id.more_tea_num_view);
				holder.allPriceView = (TextView) convertView
						.findViewById(R.id.more_tea_total_price_view);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			try {
				PlaceOrderEntity entity = teaList.get(position);
				finalBp.display(holder.imageView, Server.BASE_URL
						+ entity.pici.productImage);// 茶图片
				holder.teaNameTv.setText(entity.products.productName);
				holder.priceTv.setText("单价：¥ " + entity.pici.price);
				holder.teaNumView.setText("数量：" + entity.count);
				double itemAllMoney = Double.parseDouble(entity.pici.price) * entity.count ;
				holder.allPriceView.setText("总价：¥ " + NumerUtil.getNum(itemAllMoney+"") + "");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

	}
	
	/**
	 * 正对jsonarraylist适配
	 * 
	 */
	public class Tea2Adapter extends BaseAdapter {

		private LayoutInflater layoutInflater = null;
		private FinalBitmap finalBp = null;

		public Tea2Adapter(Context context) {
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			finalBp = FinalBitmap.create(context);
			finalBp.configLoadingImage(R.drawable.loading_default);
			finalBp.configLoadfailImage(R.drawable.loading_failed);
			finalBp.configBitmapMaxHeight(60);
			finalBp.configBitmapMaxWidth(60);
		}

		@Override
		public int getCount() {
			return jsonArraysList.size();
		}

		@Override
		public JsonArray getItem(int position) {

			return jsonArraysList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder = null;

			if (convertView == null) {
				holder = new Holder();
				convertView = layoutInflater.inflate(
						R.layout.more_tea_list_item, null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.more_tea_imgview);
				holder.teaNameTv = (TextView) convertView
						.findViewById(R.id.more_tea_name_view);
				holder.priceTv = (TextView) convertView
						.findViewById(R.id.more_tea_price);
				holder.teaNumView = (TextView) convertView
						.findViewById(R.id.more_tea_num_view);
				holder.allPriceView = (TextView) convertView
						.findViewById(R.id.more_tea_total_price_view);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			try {
				JsonArray entity = jsonArraysList.get(position);
				finalBp.display(holder.imageView, Server.BASE_URL
						+ entity.avatarId);// 茶图片
				holder.teaNameTv.setText(entity.name);
				holder.priceTv.setText("单价：¥ " + entity.piecePrice);
				holder.teaNumView.setText("数量：" + entity.offerPieceQty);
				double  itemAllMoney = Double.parseDouble(entity.piecePrice) * Double.parseDouble(entity.offerPieceQty);
				holder.allPriceView.setText("总价：¥ " + NumerUtil.getNum(itemAllMoney+""));
						

			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

	}

	private class Holder {

		ImageView imageView;
		TextView teaNameTv;
		TextView priceTv;
		TextView teaNumView;
		TextView allPriceView;

	}

}