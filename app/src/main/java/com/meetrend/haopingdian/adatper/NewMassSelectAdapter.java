package com.meetrend.haopingdian.adatper;

import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.Server;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 群发适配器
 *
 */
public class NewMassSelectAdapter extends BaseAdapter {
	
	private int flag = 1;//1发送活动，2群发

	private List<Member> memberList;


	public interface ChildCheckCallBack {
		public void isCheck(boolean ischeck, Member member);
	}

	private int checkableVisibility = View.VISIBLE;
	public ChildCheckCallBack callBack;

	public void setChildCheckCallBack(ChildCheckCallBack callBack) {
		this.callBack = callBack;
	}

	private LayoutInflater mLayoutInflater;

	private Drawable drawable;
	private int drawpadding;

	public NewMassSelectAdapter(Context context, List<Member> memberList,int flag) {

		mLayoutInflater = LayoutInflater.from(context);
		this.memberList = memberList;
		this.flag = flag;
		Resources resources = context.getResources();
		drawable = resources.getDrawable(R.drawable.forbid_send);
		DisplayMetrics metrics = resources.getDisplayMetrics();
		drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
		drawpadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,metrics);
	}
	
	public void setList(List<Member> memberlList){
		this.memberList = memberlList;
	}

	@Override
	public boolean isEnabled(int position) {
		return memberList.get(position).isGroup == -1 ? false :true;
	}
	
	@Override
	public int getItemViewType(int position) {
		return memberList.get(position).isGroup == -1 ? -1 : 0;
	}


	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return memberList.size();
	}

	@Override
	public Object getItem(int position) {

		return memberList.get(position);

	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Member member = memberList.get(position);
		member.setPosition(position);
		int type = this.getItemViewType(position);
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case -1:
				convertView = mLayoutInflater.inflate(
						R.layout.member_select_list_item_layout, null);
				holder.alphabet = (TextView) convertView
						.findViewById(R.id.tv_alphabet);
				break;
			case 0:
				convertView = mLayoutInflater.inflate(
						R.layout.mass_select_memberlist_item, null);
				holder.member_name = (TextView) convertView
						.findViewById(R.id.childname);
				holder.member_photo = (SimpleDraweeView) convertView
						.findViewById(R.id.child_img);
				holder.check = (CheckBox) convertView
						.findViewById(R.id.child_check);
				holder.childRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.child_relative);
				holder.statusTv = (TextView) convertView
						.findViewById(R.id.tv_member_status);
				holder.undistributeImg = (ImageView) convertView
						.findViewById(R.id.undistributeimg);

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

			if(!TextUtils.isEmpty(member.customerName)){
				if (member.customerName.length() > 10) {
					String name = member.customerName.substring(0, 8);
					holder.member_name.setText(name+"...");
				}else {
					holder.member_name.setText(member.customerName);
				}
			}else{
				holder.member_name.setText(member.mobile);
			}

			if (member.canTalk){
				holder.member_name.setCompoundDrawables(null,null,null, null);
			} else {
				holder.member_name.setCompoundDrawables(null,null,drawable, null);
				holder.member_name.setCompoundDrawablePadding(drawpadding);
			}

			if (member.managerId.equals("")) {
				holder.undistributeImg.setVisibility(View.VISIBLE);
			} else {
				holder.undistributeImg.setVisibility(View.GONE);
			}

			holder.member_photo.setImageURI(Uri.parse(Server.BASE_URL+ member.pictureId));
					
			holder.check.setVisibility(checkableVisibility);

			holder.check
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
							member.checkstatus = isChecked;
							if (callBack != null) {
								callBack.isCheck(isChecked, member);
							}
						}
					});

			// 选中状态
			if (member.checkstatus) {
				holder.check.setChecked(true);
			} else {
				holder.check.setChecked(false);
			}

			holder.childRelativeLayout.setOnClickListener(new LayoutItemClick(holder.check, member));

			if (flag == 2) {
				holder.statusTv.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(member.status)) {
					int status = Integer.parseInt(member.status);
					switch (status) {
					case 0:
						holder.statusTv.setTextColor(Color.GRAY);
						holder.statusTv.setBackgroundResource(0);
						holder.statusTv.setText("未邀请");
						break;
					case 1:
						holder.statusTv.setTextColor(Color.GREEN);
						holder.statusTv.setBackgroundResource(0);
						holder.statusTv.setText("已邀请");
						break;
					case 2:
						holder.statusTv.setTextColor(Color.GRAY);
						holder.statusTv.setBackgroundResource(0);
						holder.statusTv.setText("已绑定");
						break;
					}
				} else {
					holder.statusTv.setTextColor(Color.GRAY);
					holder.statusTv.setBackgroundResource(0);
					holder.statusTv.setText("未邀请");
				}
			}

			break;
		}

		return convertView;
	}
	
	
	//item onclick
	public class LayoutItemClick implements OnClickListener{
		
		private CheckBox checkBox;
		private Member member;
		
		public LayoutItemClick(CheckBox checkBox,Member member){
			this.checkBox = checkBox;
			this.member = member;
		}

		@Override
		public void onClick(View v) {
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				member.checkstatus = false;
				if (callBack != null) {
					callBack.isCheck(false,member);
				}
			}else {
				checkBox.setChecked(true);
				member.checkstatus = true;
				if (callBack != null) {
					callBack.isCheck(true,member);
				}
			}
		}
	}
	class ViewHolder {
		SimpleDraweeView member_photo;
		TextView member_name;
		CheckBox check;
		TextView alphabet;
		TextView statusTv;
		ImageView undistributeImg;
		RelativeLayout childRelativeLayout;
	}
}