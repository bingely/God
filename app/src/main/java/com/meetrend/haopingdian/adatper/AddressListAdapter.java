package com.meetrend.haopingdian.adatper;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.AddressPoint;

public class AddressListAdapter extends BaseAdapter
{
    
    private final List<AddressPoint> list;
    
    private Context context;
    
    private LayoutInflater layoutInflater = null;
    
    public AddressListAdapter(Context context, List<AddressPoint> list)
    {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount()
    {
        return list.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @SuppressWarnings("unused")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (viewHolder == null)
        {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.signaddress_list_layout_item, null);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.addressname);
            viewHolder.detailView = (TextView) convertView.findViewById(R.id.addreesdetail);
            viewHolder.checkImg = (ImageView) convertView.findViewById(R.id.chekimg);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        AddressPoint point = list.get(position);
        viewHolder.nameView.setText(point.name);
        viewHolder.detailView.setText(point.address);
        
        if (point.isSelect)
        {
            viewHolder.nameView.setTextColor(Color.parseColor("#ff0000"));
            viewHolder.detailView.setTextColor(Color.parseColor("#ff0000"));
            viewHolder.checkImg.setVisibility(View.VISIBLE);
        }
        
        return convertView;
    }
    
    class ViewHolder
    {
        
        TextView nameView;
        
        TextView detailView;
        
        ImageView checkImg;
    }
    
}