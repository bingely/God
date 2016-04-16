package com.meetrend.haopingdian.adatper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.PayCodeBean;
import java.util.List;

public class PayCodeListAdapter extends BasicAdapter{

    public PayCodeListAdapter(Context context, List<PayCodeBean> list) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;
        if (convertView == null){

            holder = new Holder();
            convertView = mLayoutInflater.inflate(R.layout.paycode_listtiem_layout,null);
            holder.codeNameView = (TextView) convertView.findViewById(R.id.paycodenameview);
            holder.codeMoneyView = (TextView) convertView.findViewById(R.id.paycode_money);
            convertView.setTag(holder);

        }else{
            holder = (Holder)convertView.getTag();
        }
        PayCodeBean payCodeBean = (PayCodeBean)mList.get(position);
        holder.codeNameView.setText(payCodeBean.payCodeName);
        holder.codeMoneyView.setText("¥"+payCodeBean.payCodeAmount);

        return convertView;
    }

    final class Holder{

        TextView codeNameView;
        TextView codeMoneyView;

    }
}
