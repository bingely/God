package com.meetrend.haopingdian.adatper;

import java.util.List;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProductSectionedAdapter extends SectionedBaseAdapter {
	
	private Context mContext;
	private List<LeftItemBean> leftStr;
	private List<TeaProductEntity> rightStr;
	private LayoutInflater inflator;
	
	public ProductSectionedAdapter(Context context, List<LeftItemBean> leftStr, List<TeaProductEntity> rightStr){
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
        	convertView = (LinearLayout) inflator.inflate(R.layout.kucunlist_item, null);
        	viewHelper.parentLayout = (RelativeLayout) convertView.findViewById(R.id.parent_layout);
        	viewHelper.imgeView = (SimpleDraweeView)  convertView.findViewById(R.id.parent_product_img);
        	viewHelper.productNameView = (TextView) convertView.findViewById(R.id.parent_product_name_view);
        	viewHelper.kucunNameView = (TextView) convertView.findViewById(R.id.parent_product_kucun_name_view);
        	viewHelper.beizhuView = (TextView) convertView.findViewById(R.id.parent_product_mark_view);
        	viewHelper.icon = (ImageView)  convertView.findViewById(R.id.parent_product_hint_icon);
        	viewHelper.listview = (ListView) convertView.findViewById(R.id.item_listview);
        	viewHelper.productpriceview = (TextView) convertView.findViewById(R.id.parent_product_price_view);
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
			
			 //价格
			 viewHelper.productpriceview.setVisibility(View.VISIBLE);
			 viewHelper.productpriceview.setText("¥" + picilist.get(0).price);
			 
			 //备注
			 viewHelper.beizhuView.setVisibility(View.VISIBLE);
			 viewHelper.beizhuView.setText(picilist.get(0).description);
		}else {
			viewHelper.icon.setVisibility(View.VISIBLE);//显示
			
			 //价格
			 viewHelper.productpriceview.setVisibility(View.GONE);
			
			//备注
			viewHelper.beizhuView.setVisibility(View.GONE);
			viewHelper.productNameView.setText(products.productName);
		}
		
		//一级菜单显示的图片
		viewHelper.imgeView.setImageURI(Uri.parse(Server.BASE_URL +picilist.get(0).productImage));
		
		//库存
		viewHelper.kucunNameView.setText("库存：" + NumerUtil.saveThreeDecimal(all+""));//暂时用零去代替，可能需要将子项的累加起来
		
        
		//三级菜单及指示器的显示状态
        if (products.isShowChildList) {
        	viewHelper.listview.setVisibility(View.VISIBLE);
        	viewHelper.icon.setImageResource(R.drawable.icon_collapse);
		}else{
			viewHelper.listview.setVisibility(View.GONE);
			viewHelper.icon.setImageResource(R.drawable.icon_unfold);
		}
        
        viewHelper.listview.setAdapter(new RightChildListAdapter(mContext, products.productList,products));
        ListViewUtil.setListViewHeightBasedOnChildren(viewHelper.listview);
       
        //click
        viewHelper.parentLayout.setOnClickListener(new ParentClick(mContext,viewHelper.listview, products,viewHelper.icon,picilist));
        
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
    
    //item的点击事件
    private class ParentClick implements OnClickListener{
    	
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
//				try {
//				    EventBus.getDefault().post(new TeaAddEvent(products,piciList.get(0),1.000));
//				   ((BaseActivity) mContext).finish();
//
//			    } catch (Exception e) {
//				   Toast.makeText(mContext, "数据异常", 100).show();
//			    }

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

//			if (piciList.size() == 0) {
//				if (products.isShowChildList) {
//					products.isShowChildList = false;
//					this.listView.setVisibility(View.GONE);
//					this.iconImg.setImageResource(R.drawable.icon_unfold);
//				}else {
//					products.isShowChildList = true;
//					this.listView.setVisibility(View.VISIBLE);
//					this.iconImg.setImageResource(R.drawable.icon_collapse);
//				}
//			}
			
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
    }
    
}
