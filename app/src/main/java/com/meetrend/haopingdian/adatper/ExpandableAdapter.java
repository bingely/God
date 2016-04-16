package com.meetrend.haopingdian.adatper;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.InventoryItem;
import com.meetrend.haopingdian.env.Server;

public class ExpandableAdapter extends BaseExpandableListAdapter {
	private FinalBitmap mLoader;
	private String mTitle;
	private LayoutInflater mLayoutInflater;

	public ExpandableAdapter(Context context, String title) {
		this.mTitle = title;
		mLayoutInflater = LayoutInflater.from(context);
		mLoader = FinalBitmap.create(context);
		mLoader.configLoadingImage(R.drawable.loading_default);
		mLoader.configLoadfailImage(R.drawable.loading_failed);
	}

	@Override
	public int getGroupCount() {
		return App.group.get(mTitle).size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		String yearId = App.group.get(mTitle).get(groupPosition).yearId;
		if (App.child.get(yearId) == null) {
			return 0;
		} else {
			return App.child.get(yearId).size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return App.group.get(mTitle).get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		String yearId = App.group.get(mTitle).get(groupPosition).yearId;
		return App.child.get(yearId).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String yearName = App.group.get(mTitle).get(groupPosition).yearName;
		ParentViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_inventory_year, null);
			holder = new ParentViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.parent_inventory_year);
			holder.iv = (ImageView) convertView.findViewById(R.id.parent_inventory_indicator);
			convertView.setTag(holder);
		} else {
			holder = (ParentViewHolder) convertView.getTag();
		}
		if (getChildrenCount(groupPosition) == 0) {
			holder.iv.setVisibility(View.GONE);
		} else {
			holder.iv.setVisibility(View.VISIBLE);
			if (isExpanded) {
				holder.iv.setImageResource(R.drawable.group_indicator_default);
			} else {
				holder.iv.setImageResource(R.drawable.group_indicator_selected);
			}
		}

		holder.tv.setText(yearName);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		String yearId = App.group.get(mTitle).get(groupPosition).yearId;
		InventoryItem item = App.child.get(yearId).get(childPosition);

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_inventory, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.child_inventory_image);
			holder.tip = (TextView) convertView.findViewById(R.id.child_product_name_and_pici);
			holder.amount = (TextView) convertView.findViewById(R.id.child_product_amount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tip.setText(item.productName + " " + item.productPici);
		mLoader.display(holder.iv, Server.BASE_URL + item.avatarId);
		holder.amount.setText(item.picecInventory + "件 " + item.unitInventory + item.unitName);
		return convertView;
	}

	public final static class ParentViewHolder {
		public TextView tv;
		public ImageView iv;
	}

	public final static class ViewHolder {
		public ImageView iv;
		public TextView tip;
		public TextView amount;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}