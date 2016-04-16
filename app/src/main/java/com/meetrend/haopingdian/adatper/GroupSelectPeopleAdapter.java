package com.meetrend.haopingdian.adatper;

import java.util.List;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Code;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupSelectPeopleAdapter extends BaseAdapter{

private static final String TAG = GroupSelectPeopleAdapter.class.getSimpleName();
	
	private LayoutInflater mLayoutInflater;
	private List<Member> list;
	
	private int groupEditMode;//此种情况：是店小二群组处于编辑状态，并且该属性是isDefualt为true
	
	
	public GroupSelectPeopleAdapter(Context context, List<Member> list,int isGroupEditMode) {
		
		mLayoutInflater = LayoutInflater.from(context);
		this.list = list;
		this.groupEditMode = isGroupEditMode;
	}
	
	public void setList(List<Member> list){
		this.list = list;
	}

	public interface OnCheckSelectListener{
		public void check(String position);
		public void uncheck(String position);
	}
	
	
	
	public void setListData(List<Member> list){
		this.list = list;
	}
	
	// 混合布局
	enum ITEM_TYPE {
		ALPHABET, MEMBER;
	}
	private static final int ITEM_TYPE_COUNAT = ITEM_TYPE.values().length;
		
	@Override
	public boolean isEnabled(int position) {
		return list.get(position).isGroup == -1 ? false :true;
	}
	
	@Override
	public int getItemViewType(int position) {
		return list.get(position).isGroup == -1 ? -1 : 0;
	}
	
//	private ITEM_TYPE getType(int position) {
//		Member member = list.get(position);
//		boolean flag = (member.pinyinName.length() == 1);
//		return flag ? ITEM_TYPE.ALPHABET : ITEM_TYPE.MEMBER;
//	}
	
	@Override
	public int getViewTypeCount() {
		return ITEM_TYPE_COUNAT;
	}

	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
		
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Member member = list.get(position);
		int type = this.getItemViewType(position);
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case -1:
				convertView = mLayoutInflater.inflate(R.layout.member_select_list_item_layout, null);
				holder.alphabet = (TextView) convertView.findViewById(R.id.tv_alphabet);
				break;
			case 0:
				convertView = mLayoutInflater.inflate(R.layout.multichecklistadapter, null);
				holder.member_group_name = (TextView) convertView.findViewById(R.id.group_name); //组名
				holder.member_name = (TextView) convertView.findViewById(R.id.tv_member_name); // 会员名称
				holder.member_status = (TextView) convertView.findViewById(R.id.tv_member_status);//状态
				holder.member_photo = (SimpleDraweeView) convertView.findViewById(R.id.iv_member_photo);//头像
				holder.member_checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);//checkbox
				holder.multiLayout = (RelativeLayout) convertView.findViewById(R.id.multicheck_layout);
				break;
			}
			 convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		switch (type) {
		case -1:
			holder.alphabet.setText(member.pinyinName);
			break;
		case 0:
			holder.member_name.setText(member.customerName);//会员名字
			holder.member_checkbox.setVisibility(View.VISIBLE);
			
			//此种情况：是店小二群组处于编辑状态，并且该属性是isDefualt为true
			if (member.isDefault && groupEditMode == Code.ASSITANT) {
				
				holder.member_checkbox.setEnabled(false);
				holder.multiLayout.setOnClickListener(null);
				
				if (member.isCheckstatus()) {
					holder.member_checkbox.setChecked(true);
				}else {
					holder.member_checkbox.setChecked(false);
				}
			}else {
				holder.member_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							member.checkstatus = isChecked;
							if (listener != null) {
								listener.checkItem(member);
							}
					}
				});
				
				if (member.isCheckstatus()) {
					holder.member_checkbox.setChecked(true);
					
				}else {
					holder.member_checkbox.setChecked(false);
				}
				
				holder.multiLayout.setOnClickListener(new LayoutItemClick(holder.member_checkbox,member));
			}
			
			holder.member_photo.setImageURI(Uri.parse(Server.BASE_URL + member.pictureId));
			
			holder.member_status.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(member.status)) {
				int status = Integer.parseInt(member.status);
				switch (status) {
				case 0:
					holder.member_status.setTextColor(Color.GRAY);
					holder.member_status.setBackgroundResource(0);
					holder.member_status.setText("未邀请");
					break;
				case 1:
					holder.member_status.setTextColor(Color.GREEN);
					holder.member_status.setBackgroundResource(0);
					holder.member_status.setText("已邀请");
					break;
				case 2:
					holder.member_status.setTextColor(Color.GRAY);
					holder.member_status.setBackgroundResource(0);
					holder.member_status.setText("已绑定");
					break;
				}
			}else{
				holder.member_status.setTextColor(Color.GRAY);
				holder.member_status.setBackgroundResource(0);
				holder.member_status.setText("未邀请");
			}
			
			break;
		}
		
		return convertView;
	}
	
	
	
	public class LayoutItemClick implements OnClickListener{
		
		private CheckBox checkBox;
		private Member member;
		
		public LayoutItemClick(CheckBox checkBox, Member member){
			this.checkBox = checkBox;
			this.member = member;
		}

		@Override
		public void onClick(View v) {
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				member.checkstatus = false;
				if (listener != null) {
					listener.checkItem(member);
				}
			}else {
				checkBox.setChecked(true);
				member.checkstatus = true;
				if (listener != null) {
					listener.checkItem(member);
				}
			}
		}
		
	}
	
	
	public CheckListItemListener listener;
	
	public void setCheckListItemListener(CheckListItemListener listener){
		this.listener = listener;
	}
	
	public interface CheckListItemListener{
        void checkItem(Member member);
	}
 
	class ViewHolder {
		
		public RelativeLayout multiLayout;
		public SimpleDraweeView member_photo;
		public TextView member_name;
		public TextView member_status;
		public CheckBox member_checkbox;
		public TextView member_group_name;

		public ImageView group_photo;
		public TextView group_name;
		public CheckBox group_checkbox;
		
		public TextView alphabet;
		
	}
}
