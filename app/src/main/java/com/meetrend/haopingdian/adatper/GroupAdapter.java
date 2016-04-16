package com.meetrend.haopingdian.adatper;

import java.util.List;
import net.tsz.afinal.FinalBitmap;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.MemberGroup;import com.meetrend.haopingdian.env.Server;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter{
	private int checkableVisibility = View.VISIBLE;//checkview 状态
	private List<MemberGroup> list;
	private Context context;
	private LayoutInflater mLayoutInflater;
	public GroupAdapter(Context context,List<MemberGroup> list){
		this.list = list;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public void setList(List<MemberGroup> list){
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public MemberGroup getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.group_list_item, null);
			viewHolder.grounName = (TextView)convertView.findViewById(R.id.groupname);
			viewHolder.groupImg = (SimpleDraweeView) convertView.findViewById(R.id.groupimg);
			viewHolder.checkableImageView = (CheckBox) convertView.findViewById(R.id.group_check);
			viewHolder.group_realtive = (RelativeLayout) convertView.findViewById(R.id.group_relative);
		    convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		final MemberGroup memberGroup = list.get(position);
		viewHolder.groupImg.setImageURI(Uri.parse(Server.BASE_URL + memberGroup.image));
		viewHolder.grounName.setText(memberGroup.tagName);//显示群组名称
		viewHolder.checkableImageView.setVisibility(checkableVisibility);
		
		if (memberGroup.checkstatus) {
			viewHolder.checkableImageView.setChecked(true);
		}else {
			viewHolder.checkableImageView.setChecked(false);
		}
		
		viewHolder.checkableImageView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				memberGroup.checkstatus = isChecked;
				if (callBack != null) {
					callBack.isCheck(memberGroup,isChecked);
				}
			}
		});
		
		viewHolder.group_realtive.setOnClickListener(new LayoutItemClick(viewHolder.checkableImageView, memberGroup));
		
		
		return convertView;
	}
	
	//item onclick
	public class LayoutItemClick implements OnClickListener{
		
		private CheckBox checkBox;
		private MemberGroup memberGroup;
		
		public LayoutItemClick(CheckBox checkBox,MemberGroup memberGroup){
			this.checkBox = checkBox;
			this.memberGroup = memberGroup;
		}

		@Override
		public void onClick(View v) {
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				memberGroup.checkstatus = false;
				if (callBack != null) {
					callBack.isCheck(memberGroup,false);
				}
			}else {
				checkBox.setChecked(true);
				memberGroup.checkstatus = true;
				if (callBack != null) {
					callBack.isCheck(memberGroup,true);
				}
			}
		}
		
	}
	
	public void setCheckableViewVisibility(int visibility) {
		checkableVisibility = visibility;
		this.notifyDataSetChanged();
	}
	
	class ViewHolder{
		SimpleDraweeView groupImg;
		TextView grounName;
		CheckBox checkableImageView;
		RelativeLayout group_realtive;
	}
	
	public GroupCheckCallBack callBack;
	
	public void setGroupCheckCallBack(GroupCheckCallBack callBack){
		this.callBack = callBack;
	}
	
	public interface GroupCheckCallBack{
		public void isCheck(MemberGroup memberGroup,boolean flag);
	}
	
	

}
