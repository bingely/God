package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.CostBean;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a on 2016/2/22.
 *
 * 设置活动费用
 */
public class SettingEventCostActivity extends  BaseActivity{

    @ViewInject(id = R.id.actionbar_home, click = "backClick")
    ImageView backImageView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;
    @ViewInject(id = R.id.actionbar_action,click = "sureClick")
    TextView sureView;

    @ViewInject(id = R.id.listview)
    ListView mListView;

    public CostAdapter setCostAdapter = null;
    public ArrayList<CostBean> mList;

    /**
     * 0新建 1编辑
     */
    private int viewType ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingeventcost);
        FinalActivity.initInjectedView(this);
        sureView.setText("确定");
        titleView.setText("设置活动费用");

       viewType =  getIntent().getIntExtra("viewtype",0);
        Bundle bundle = getIntent().getBundleExtra("codelistbundle");

        mList = bundle.getParcelableArrayList("codelist");
        //新建状态
        if (viewType ==0){
                if (mList.size() == 0)
                mList.add(new CostBean());
        }

        setCostAdapter = new CostAdapter();
        mListView.setAdapter(setCostAdapter);

        View footerView = LayoutInflater.from(SettingEventCostActivity.this).inflate(R.layout.footer_setcost_layout,null);
        Button addBtn = (Button)footerView.findViewById(R.id.cost_add_btn);
        //添加
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.add(new CostBean());
                ListViewUtil.setListViewHeightBasedOnChildren(mListView);
            }
        });

        if (viewType ==0){
            mListView.addFooterView(footerView);
            sureView.setVisibility(View.VISIBLE);
        }else
            sureView.setVisibility(View.GONE);

        ListViewUtil.setListViewHeightBasedOnChildren(mListView);
    }

    //确定
    public void sureClick(View view){

        if (mList == null || mList.size() == 0){
            showToast("请添加费用项");
            return;
        }

        int size = mList.size();
        int j;
        CostBean bean;
        for (int i =0;i<mList.size();i++) {
            bean = mList.get(i);
            j = i+1;
            if (TextUtils.isEmpty(bean.name)){
                showToast("第"+ j +"个费用项 名称 不能为空");
                return;
            }

            if (TextUtils.isEmpty(bean.money)){
                showToast("第"+ j +"个费用项 金额 不能为空");
                return;
            }

            if (TextUtils.isEmpty(bean.limitAmount)){
                showToast("第"+ j +"个费用项 名额限制 不能为空");
                return;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("costlist", mList);
        Intent intent = new Intent();
        intent.putExtra("bundlelist", bundle);
        setResult(0X011, intent);
        finish();
    }

     private class  CostAdapter extends BaseAdapter{

         LayoutInflater mLayoutInflater;

         public CostAdapter(){
             mLayoutInflater = LayoutInflater.from(SettingEventCostActivity.this);
         }

         @Override
         public CostBean getItem(int i) {
             return mList.get(i);
         }

         @Override
         public int getCount() {
             return mList.size();
         }

         @Override
         public long getItemId(int i) {
             return i;
         }

         @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (null == convertView){
                viewHolder = new ViewHolder();
                convertView  = mLayoutInflater.inflate(R.layout.adapter_setcost_layout,null);
                viewHolder.clearImg = (ImageView)convertView.findViewById(R.id.cost_item_clear);
                viewHolder.nameEdit = (EditText)convertView.findViewById(R.id.cost_name_edit);
                viewHolder.valueEdit = (EditText)convertView.findViewById(R.id.cost_value_edit);
                viewHolder.limitEdit = (EditText)convertView.findViewById(R.id.cost_peoplelimit_edit);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

             if (viewType == 0)//新建
                 if (position == 0){
                     viewHolder.clearImg.setVisibility(View.GONE);
                 }else{
                     viewHolder.clearImg.setVisibility(View.VISIBLE);
                 }
             else
                 viewHolder.clearImg.setVisibility(View.GONE);//编辑

            final CostBean bean = mList.get(position);
             viewHolder.nameEdit.setText(bean.name);
             viewHolder.valueEdit.setText(bean.money);
             viewHolder.limitEdit.setText(bean.limitAmount);

            //删除操作
            viewHolder.clearImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mList.remove(position);
                    ListViewUtil.setListViewHeightBasedOnChildren(mListView);
                }
            });

            viewHolder.nameEdit.addTextChangedListener(new TextWatcherChangeListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    bean.name = s.toString();
                }
            });

            viewHolder.valueEdit.addTextChangedListener(new TextWatcherChangeListener() {
                @Override
                public void afterTextChanged(Editable s) {

                    if (!NumerUtil.isFloat(s.toString()) || !NumerUtil.isInteger(s.toString()))
                        bean.money = "0.00";
                    else
                        bean.money = s.toString();
                }
            });

            viewHolder.limitEdit.addTextChangedListener(new TextWatcherChangeListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (NumerUtil.isInteger(s.toString()))
                        bean.limitAmount = s.toString();
                    else
                        bean.limitAmount = "0";
                }
            });
            return convertView;
        }

        final class ViewHolder{
            ImageView clearImg;
            EditText nameEdit;
            EditText valueEdit;
            EditText limitEdit;
        }
    }

    //结束
    public void backClick(View view){
            finish();
    }

}
