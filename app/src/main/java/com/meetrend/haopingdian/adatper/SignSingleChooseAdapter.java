package com.meetrend.haopingdian.adatper;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.Server;

/**
 * 签到选择适配器
 *
 */
public class SignSingleChooseAdapter extends BasicAdapter<Member>{

	
	public SignSingleChooseAdapter(List<Member> list, Context context) {
		super(list, context);
	}
	
	public void setList(List<Member> list){
		this.mList = list;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return mList.get(position).isGroup == -1 ? false :true;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mList.get(position).isGroup == -1 ? -1 : 0;
	}


	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		ViewHolder holder = null;
		int type = this.getItemViewType(position);
		
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case -1:
				convertView = mLayoutInflater.inflate(R.layout.member_select_list_item_layout, null);
				holder.alphabet = (TextView) convertView.findViewById(R.id.tv_alphabet);
						
				break;
			case 0:
				convertView = mLayoutInflater.inflate(R.layout.item_layout_sign_singlechoose, null);
						
				holder.member_name = (TextView) convertView
						.findViewById(R.id.childname);
				holder.member_photo = (SimpleDraweeView) convertView
						.findViewById(R.id.child_img);
				holder.check = (ImageView) convertView.findViewById(R.id.child_check);
				holder.childRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.child_relative);
				holder.statusTv = (TextView) convertView
						.findViewById(R.id.tv_member_status);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Member member = mList.get(position);

		switch (type) {
		case -1:
			holder.alphabet.setText(member.pinyinName);
			break;
		case 0:
			
			if (member.checkstatus) {
				holder.check.setImageResource(R.drawable.check_on);
			}else {
				holder.check.setImageResource(R.drawable.check_off);
			}
			
			if (member.customerName.length() > 10) {
				String name = member.customerName.substring(0, 8);
				holder.member_name.setText(name+"...");
			}else {
				holder.member_name.setText(member.customerName);
			}
			
			holder.member_photo.setImageURI(Uri.parse(Server.BASE_URL+ member.pictureId));

			// 选中状态
			if (member.checkstatus) {
				holder.check.setImageResource(R.drawable.check_on);
			} else {
				holder.check.setImageResource(R.drawable.check_off);
			}

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

			break;
		}
		
		
		return convertView;
	}
	
	class ViewHolder {
		SimpleDraweeView member_photo;
		TextView member_name;
		ImageView check;
		TextView alphabet;
		TextView statusTv;
		RelativeLayout childRelativeLayout;
	}

}