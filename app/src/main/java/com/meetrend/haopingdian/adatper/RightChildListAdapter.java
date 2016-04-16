package com.meetrend.haopingdian.adatper;

import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.BaseActivity;
import com.meetrend.haopingdian.activity.TeaTypeListActivity;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.util.NumerUtil;

import de.greenrobot.event.EventBus;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RightChildListAdapter extends BaseAdapter{
	
	public LayoutInflater mLayoutInflater = null;
	public List<Pici> mPiciList;
	public Products products;
	public Context mContext;
	
	public RightChildListAdapter(Context context,List<Pici> mPiciList,Products products){
		
		mLayoutInflater = LayoutInflater.from(context);
		this.mPiciList = mPiciList;
		this.products = products;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mPiciList.size();
	}

	@Override
	public Pici getItem(int position) {
		return mPiciList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHelper viewHelper = null;
    	
        if (convertView == null) {
            viewHelper = new ViewHelper();
        	convertView = mLayoutInflater.inflate(R.layout.right_listview_child_item_layout, null);
        	viewHelper.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.child_relative_layout);
        	viewHelper.imgeView = (SimpleDraweeView) convertView.findViewById(R.id.child_product_img);
        	viewHelper.productNameView = (TextView) convertView.findViewById(R.id.child_product_name_view);
        	viewHelper.kucunNameView = (TextView) convertView.findViewById(R.id.child_product_kucun_name_view);
        	viewHelper.beizhuView = (TextView) convertView.findViewById(R.id.child_product_mark_view);
        	viewHelper.priceView = (TextView) convertView.findViewById(R.id.child_product_price_view);
            convertView.setTag(viewHelper);
            
        } else {
            viewHelper = (ViewHelper) convertView.getTag();
        }
        
        final Pici pici = mPiciList.get(position);
        
         //图片显示
         viewHelper.imgeView.setImageURI(Uri.parse(Server.BASE_URL + pici.productImage));
         
		 //pici的名字显示
		 if (TextUtils.isEmpty(pici.model2Value)) {
			 viewHelper.productNameView .setText(pici.model1Value);
		 }else {
			 viewHelper.productNameView .setText(pici.model1Value + " "+ pici.model2Value);
		 }
        
		 //库存
		 viewHelper.kucunNameView.setText("库存：" + pici.number);
		 
		 //价钱
		 viewHelper.priceView.setText("¥" + NumerUtil.getNum(pici.price));
		 
		 //备注
		 viewHelper.beizhuView.setText(pici.description == null ? "暂无" : pici.description);
		 
//		 viewHelper.relativeLayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				try {
//				    EventBus.getDefault().post(new TeaAddEvent(products,pici,1.000));
//				    ((BaseActivity) mContext).finish();
//				} catch (Exception e) {
//				Toast.makeText(mContext, "数据异常", Toast.LENGTH_SHORT).show();
//				}
//				
//			}
//		});
		 
        return convertView;
    }

    
    class ViewHelper{
    	RelativeLayout relativeLayout;
    	SimpleDraweeView imgeView;
    	TextView productNameView;
    	TextView kucunNameView;
    	TextView beizhuView;
    	TextView priceView;
    }
}
