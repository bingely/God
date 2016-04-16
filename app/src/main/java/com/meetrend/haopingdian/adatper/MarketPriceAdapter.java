package com.meetrend.haopingdian.adatper;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.MarketPriceInfo;
import com.meetrend.haopingdian.env.Server;

public class MarketPriceAdapter extends BasicAdapter<MarketPriceInfo> {

    int green;
    int red;

    public MarketPriceAdapter(Context context, List<MarketPriceInfo> list) {
        super(list, context);
        green = Color.parseColor("#02bc00");
        red = Color.parseColor("#ff0000");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.market_price_item_layout, null);
            viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.market_img);
            viewHolder.productName_view = (TextView) convertView.findViewById(R.id.product_name_view);
            viewHolder.referencePrice_view = (TextView) convertView.findViewById(R.id.price_view);
            viewHolder.increaseAmount_view = (TextView) convertView.findViewById(R.id.increase_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MarketPriceInfo marketPriceInfo = mList.get(position);
        viewHolder.imageView.setImageURI(Uri.parse(Server.BASE_URL + marketPriceInfo.picture));
        viewHolder.productName_view.setText(marketPriceInfo.name);
        viewHolder.referencePrice_view.setText("¥" +marketPriceInfo.price);

        if (Double.parseDouble(marketPriceInfo.changeRate) > 0) {
            viewHolder.increaseAmount_view.setTextColor(red);
            viewHolder.increaseAmount_view.setText("¥" + marketPriceInfo.change
                    + "  ↑" + marketPriceInfo.changeRate);
        } else {
            viewHolder.increaseAmount_view.setTextColor(green);
            String changeRate = marketPriceInfo.changeRate.substring(1,marketPriceInfo.changeRate.length());
            String change = marketPriceInfo.change.substring(1,marketPriceInfo.change.length());
            viewHolder.increaseAmount_view.setText("降¥" + change + "  ↓" + changeRate);
        }
        return convertView;
    }

    final class ViewHolder {
        SimpleDraweeView imageView;
        TextView productName_view;
        TextView referencePrice_view;
        TextView increaseAmount_view;
    }


}
