package com.meetrend.haopingdian.adatper;


import java.util.List;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.BaseActivity;
import com.meetrend.haopingdian.activity.CommonWebViewActivity;
import com.meetrend.haopingdian.activity.TeaTypeListActivity;
import com.meetrend.haopingdian.activity.WebViewActiity;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;

import de.greenrobot.event.EventBus;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProductOrgRightChildAdpater extends BaseAdapter {

    public LayoutInflater mLayoutInflater = null;
    public List<Pici> mPiciList;
    public Products products;
    public Context mContext;

    public ProductOrgRightChildAdpater(Context context,List<Pici> mPiciList,Products products){

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
            convertView = mLayoutInflater.inflate(R.layout.product_right_org_list_layout, null);
            viewHelper.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.child_relative_layout);
            viewHelper.imgeView = (SimpleDraweeView) convertView.findViewById(R.id.child_product_img);
            viewHelper.productNameView = (TextView) convertView.findViewById(R.id.child_product_name_view);
            viewHelper.kucunNameView = (TextView) convertView.findViewById(R.id.child_product_kucun_name_view);
            viewHelper.beizhuView = (TextView) convertView.findViewById(R.id.child_product_mark_view);
            viewHelper.priceView = (TextView) convertView.findViewById(R.id.child_product_price_view);
            viewHelper.moreBtn = (ImageButton) convertView.findViewById(R.id.more_btn);
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
        viewHelper.priceView.setText("零售价:" + NumerUtil.getNum(pici.price));

        //备注
        if (TextUtils.isEmpty(pici.description)){
            viewHelper.beizhuView.setText("备注：无");
        }else{
            viewHelper.beizhuView.setText(pici.description);
        }

        //更多按钮pop
        viewHelper.moreBtn.setOnClickListener(new MoreBtnClick(viewHelper,pici));

        viewHelper.relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommonWebViewActivity.class);
                String url = SPUtil.getDefault(mContext).getCommonUrl()+"/"+"Crm.Product.Show.jdp?productId="
                        + pici.productId+"&storeId="+SPUtil.getDefault(mContext).getStoreId();
                intent.putExtra("url",url);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }


    class ViewHelper{
        RelativeLayout relativeLayout;
        SimpleDraweeView imgeView;
        TextView productNameView;
        TextView kucunNameView;
        TextView beizhuView;
        TextView priceView;
        ImageButton moreBtn;
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
            Log.i("----width---",width+ "");
            Log.i("----height---",height + "");

            menuPopView.showAtLocation(view, Gravity.NO_GRAVITY,location[0]-915,location[1]-45);
        }
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
}

