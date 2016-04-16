package com.meetrend.haopingdian.adatper;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.CostBean;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;

import java.util.List;

/**
 *    Created by a on 2016/2/22.
 */
public class SetCostAdapter extends  BasicAdapter<CostBean> {

    public SetCostAdapter(List<CostBean> list, Context context) {
        super(list, context);
    }

    //删除回调接口
    public interface AfterClearNotify{
            public void del(int position);
    }

    public AfterClearNotify afterClearNotify = null;
    public void setAfterClearNotify(AfterClearNotify afterClearNotify){
        this.afterClearNotify = afterClearNotify;
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

        final CostBean bean = mList.get(position);

        //删除操作
        viewHolder.clearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        if (afterClearNotify != null){
                              afterClearNotify.del(position);
                        }
            }
        });

        viewHolder.nameEdit.addTextChangedListener(new TextWatcherChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                        bean.name = s.toString();
            }
        });

        viewHolder.valueEdit.addTextChangedListener(new TextWatcherChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                bean.money = s.toString();
            }
        });

        viewHolder.limitEdit.addTextChangedListener(new TextWatcherChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                    bean.limitAmount = s.toString();
            }
        });
        return convertView;
    }

    final static class ViewHolder{
        ImageView clearImg;
        EditText nameEdit;
        EditText valueEdit;
        EditText limitEdit;
    }
}
