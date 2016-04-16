package com.meetrend.haopingdian.adatper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.CommonWebViewActivity;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品管理搜索
 */
public class ProductManagerSearchSectionedAdapter extends SectionedBaseAdapter {

	private Context mContext;
	private List<LeftItemBean> leftStr;
	private List<TeaProductEntity> rightStr;
	private LayoutInflater inflator;

	public ProductManagerSearchSectionedAdapter(Context context, List<LeftItemBean> leftStr, List<TeaProductEntity> rightStr){
		this.mContext = context;
		this.leftStr = leftStr;
		this.rightStr = rightStr;
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	//右边显示的数据
	@Override
	public Products getItem(int section, int position) {
		return rightStr.get(section).nameList.get(position);
	}

	@Override
	public long getItemId(int section, int position) {
		return position;
	}

	//有多少组
	@Override
	public int getSectionCount() {
		return leftStr.size();
	}

	//每组的数量
	@Override
	public int getCountForSection(int section) {
		return rightStr.get(section).nameList.size();
	}

	@Override
	public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {

		ViewHelper viewHelper = null;

		if (convertView == null) {
			viewHelper = new ViewHelper();
			convertView = (LinearLayout) inflator.inflate(R.layout.product_org_list_layout, null);
			viewHelper.parentLayout = (RelativeLayout) convertView.findViewById(R.id.parent_layout);
			viewHelper.imgeView = (SimpleDraweeView)  convertView.findViewById(R.id.parent_product_img);
			viewHelper.productNameView = (TextView) convertView.findViewById(R.id.parent_product_name_view);
			viewHelper.kucunNameView = (TextView) convertView.findViewById(R.id.parent_product_kucun_name_view);
			viewHelper.beizhuView = (TextView) convertView.findViewById(R.id.parent_product_mark_view);
			viewHelper.icon = (ImageView)  convertView.findViewById(R.id.parent_product_hint_icon);
			viewHelper.listview = (ListView) convertView.findViewById(R.id.item_listview);
			viewHelper.productpriceview = (TextView) convertView.findViewById(R.id.parent_product_price_view);
			viewHelper.moreBtn = (ImageButton) convertView.findViewById(R.id.more_btn);
			convertView.setTag(viewHelper);
		} else {
			viewHelper = (ViewHelper) convertView.getTag();
		}

		Products products =rightStr.get(section).nameList.get(position);
		List<Pici> picilist = products.productList;//批次数据集合

		Pici pici = null;
		float all = 0.0f;
		for (int i = 0; i < picilist.size(); i++) {
			pici = picilist.get(i);
			all = all + pici.number;
		}

		//一级菜单显示的名称
		if (picilist.size() == 1) {
			viewHelper.icon.setVisibility(View.GONE);
			viewHelper.productNameView .setText(picilist.get(0).fullName);//显示产品全名
			viewHelper.listview.setVisibility(View.GONE);

			//价格
			viewHelper.productpriceview.setVisibility(View.VISIBLE);
			viewHelper.productpriceview.setText("¥" + picilist.get(0).price);

			//备注
			viewHelper.beizhuView.setVisibility(View.VISIBLE);
			viewHelper.beizhuView.setText(picilist.get(0).description);
			viewHelper.listview.setVisibility(View.GONE);
			viewHelper.moreBtn.setVisibility(View.VISIBLE);

		}else {
			viewHelper.moreBtn.setVisibility(View.GONE);
			viewHelper.icon.setVisibility(View.VISIBLE);//显示
			viewHelper.productNameView.setText(products.productName);
			//价格
			viewHelper.productpriceview.setVisibility(View.GONE);
			//备注
			viewHelper.beizhuView.setVisibility(View.GONE);

			//三级菜单及指示器的显示状态
			if (products.isShowChildList) {
				viewHelper.icon.setImageResource(R.drawable.icon_collapse);
				viewHelper.listview.setVisibility(View.VISIBLE);
			}else{
				viewHelper.icon.setImageResource(R.drawable.icon_unfold);
				viewHelper.listview.setVisibility(View.GONE);
			}

			ProductOrgRightChildAdpater adpater = new ProductOrgRightChildAdpater(mContext, products.productList, products);
			viewHelper.listview.setAdapter(adpater);
			ListViewUtil.setListViewHeightBasedOnChildren(viewHelper.listview);

			adpater.setPopCallBack(new ProductOrgRightChildAdpater.PopCallBack() {
				@Override
				public void eidtCallback(int type, Pici pici) {
					if (popChilcallBack != null) {
						popChilcallBack.eidtchildCallback(type, pici);
					}
				}
			});
		}

		//一级菜单显示的图片
		viewHelper.imgeView.setImageURI(Uri.parse(Server.BASE_URL + picilist.get(0).productImage));

		//库存
		viewHelper.kucunNameView.setText("库存：" + NumerUtil.saveThreeDecimal(all + ""));//暂时用零去代替，可能需要将子项的累加起来

		//打开第三级菜单
		viewHelper.parentLayout.setOnClickListener(new ParentClick(mContext,viewHelper.listview, products,viewHelper.icon,picilist));

		//更多按钮pop
		viewHelper.moreBtn.setOnClickListener(new MoreBtnClick(viewHelper,products.productList.get(0)));

		return convertView;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {

		HeaderViewHolder headHolder = null;
		if (convertView == null) {
			headHolder = new HeaderViewHolder();
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.kucun_listheader_item, null);
			headHolder.headerView = (TextView) convertView.findViewById(R.id.textItem);
			convertView.setTag(headHolder);
		}else {
			headHolder = (HeaderViewHolder) convertView.getTag();
		}

		headHolder.headerView.setText(leftStr.get(section).name);

		return convertView;
	}

	class HeaderViewHolder{
		TextView headerView;
	}

	public class MoreBtnClick implements View.OnClickListener{

		public ViewHelper viewHelper;
		public Pici pici;

		public MoreBtnClick(ViewHelper viewHelper,Pici pici){
			this.pici = pici;
			this.viewHelper = viewHelper;
		}

		@Override
		public void onClick(View view) {

			MenuPopView menuPopView = new MenuPopView(mContext,pici);

			int[] location = new int[2];
			view.getLocationOnScreen(location);
			int width = menuPopView.getWidth();
			int height = menuPopView.getHeight();
			Log.i("----width---", width + "");
			Log.i("----height---",height + "");

			menuPopView.showAtLocation(view, Gravity.NO_GRAVITY,location[0]-915,location[1]-45);
		}
	}

	//item的点击事件
	private class ParentClick implements View.OnClickListener {

		Context mContext;
		List<Pici> piciList;
		ListView listView;
		Products products;
		ImageView iconImg;

		@SuppressWarnings("unused")
		public ParentClick(Context context,ListView listView,Products products,ImageView iconImg,List<Pici> piciList){
			this.mContext = context;
			this.listView = listView;
			this.products = products;
			this.iconImg= iconImg;
			this.piciList = piciList;
		}

		@Override
		public void onClick(View v) {

			if (piciList.size() == 1) {

				Intent intent = new Intent(mContext, CommonWebViewActivity.class);
				String url = SPUtil.getDefault(mContext).getCommonUrl()+"/"+"Crm.Product.Show.jdp?productId="
						+ piciList.get(0).productId+"&storeId="+SPUtil.getDefault(mContext).getStoreId();
				intent.putExtra("url",url);
				mContext.startActivity(intent);

			}else{

				if (products.isShowChildList) {
					products.isShowChildList = false;
					this.listView.setVisibility(View.GONE);
					this.iconImg.setImageResource(R.drawable.icon_unfold);
				}else {
					products.isShowChildList = true;
					this.listView.setVisibility(View.VISIBLE);
					this.iconImg.setImageResource(R.drawable.icon_collapse);
				}

			}

		}

	}

	class ViewHelper{
		RelativeLayout parentLayout;
		SimpleDraweeView imgeView;
		TextView productNameView;
		TextView kucunNameView;
		TextView beizhuView;
		TextView productpriceview;
		ImageView icon;
		ListView listview;
		ImageButton moreBtn;
	}

	public class MenuPopView extends PopupWindow {

		private int btnSytle;// 0 编辑，1上架 2下架 3删除4分享
		private int width;
		private int height;

		private LinearLayout editBtn;
		private LinearLayout putOnBtn;
		private LinearLayout putDownBtn;
		private LinearLayout popDelBtn;
		private LinearLayout shareBtn;

		private Pici pici;

		public MenuPopView(Context context,Pici pici) {
			super(context);

			this.pici = pici;
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View mPopContentView = inflater.inflate(R.layout.list_item_pop_layout, null);
			this.setContentView(mPopContentView);

			this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);//要设置大小
			this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			this.setFocusable(true);
			this.setAnimationStyle(R.style.pop_anim);//动画效果
			ColorDrawable dw = new ColorDrawable(000000);
			this.setBackgroundDrawable(dw);

			editBtn = (LinearLayout) mPopContentView.findViewById(R.id.pop_edit);
			putOnBtn = (LinearLayout) mPopContentView.findViewById(R.id.pop_puton);
			putDownBtn = (LinearLayout) mPopContentView.findViewById(R.id.pop_putdown);
			popDelBtn = (LinearLayout) mPopContentView.findViewById(R.id.pop_del);
			shareBtn = (LinearLayout) mPopContentView.findViewById(R.id.pop_share);

			BtnClick btnClick = new BtnClick();
			editBtn.setOnClickListener(btnClick);
			putOnBtn.setOnClickListener(btnClick);
			putDownBtn.setOnClickListener(btnClick);
			popDelBtn.setOnClickListener(btnClick);
			shareBtn.setOnClickListener(btnClick);
		}

		public class BtnClick implements View.OnClickListener {

			@Override
			public void onClick(View view) {

				dismiss();

				if (null == pici)
					return;
				//Pici pici = product.productList.get(0);

				switch (view.getId()) {

					case R.id.pop_edit:
						popallBack.eidtCallback(0,pici);
						break;
					case R.id.pop_puton:

						popallBack.eidtCallback(1,pici);
						break;
					case R.id.pop_putdown:
						popallBack.eidtCallback(2,pici);
						break;
					case R.id.pop_del:
						popallBack.eidtCallback(3,pici);
						break;
					case R.id.pop_share:
						popallBack.eidtCallback(4,pici);
						break;
				}
			}
		}

		public void popEditClick(View view) {
			btnSytle = 0;
		}
	}


	public PopCallBack popallBack = null;

	public interface PopCallBack{
		public  void eidtCallback(int type,Pici pici);
	}

	public void setPopCallBack(PopCallBack popallBack ){
		if (null != popallBack)
			this.popallBack = popallBack;
	}


	public PopChildCallBack popChilcallBack = null;

	public interface PopChildCallBack{
		public  void eidtchildCallback(int type,Pici pici);
	}

	public void setPopChildCallBack(PopChildCallBack popChilcallBack ){
		if (null != popallBack)
			this.popChilcallBack = popChilcallBack;
	}

}
