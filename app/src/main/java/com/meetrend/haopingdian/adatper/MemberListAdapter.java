package com.meetrend.haopingdian.adatper;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.widget.GroupNameLayout;

public class MemberListAdapter extends BaseAdapter {

    private static final String TAG = MemberListAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;
    private List<Member> list;

    private int showMode;

    public NotifySelectionLinstener selectionLinstener;


    public MemberListAdapter(Context context, List<Member> list, int showMode) {

        mLayoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.showMode = showMode;
    }

    public void setList(List<Member> list) {
        this.list = list;
    }

    public interface OnCheckSelectListener {
        public void check(String position);

        public void uncheck(String position);
    }

    public void setListData(List<Member> list) {
        this.list = list;
    }

    @Override
    public boolean isEnabled(int position) {
        return list.get(position).isGroup == -1 ? false : true;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isGroup == -1 ? -1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Member member = list.get(position);
        int type = this.getItemViewType(position);

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case -1:

                    convertView = mLayoutInflater.inflate(R.layout.member_select_list_item_layout, null);
                    holder.alphabet = (TextView) convertView.findViewById(R.id.tv_alphabet);
                    holder.alphabet.setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });

                    break;
                case 0:
                    convertView = mLayoutInflater.inflate(R.layout.item_memberlist, null);
                    holder.member_group_name = (TextView) convertView.findViewById(R.id.group_name); //组名
                    holder.member_name = (TextView) convertView.findViewById(R.id.tv_member_name); // 会员名称
                    holder.member_status = (TextView) convertView.findViewById(R.id.tv_member_status);//状态
                    holder.member_photo = (SimpleDraweeView) convertView.findViewById(R.id.iv_member_photo);//头像
                    holder.unDitributeImg = (ImageView) convertView.findViewById(R.id.undistributeimg);//未分配
                    holder.cannotTalkImg = (ImageView) convertView.findViewById(R.id.cannottal_icon);//
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (type) {
            case -1:
                holder.alphabet.setText(member.pinyinName.toUpperCase());
                break;
            case 0:
                if (!TextUtils.isEmpty(member.customerName))
                    holder.member_name.setText(member.customerName);
                else {
                    if (!TextUtils.isEmpty(member.mobile))
                        holder.member_name.setText(member.mobile);
                }

                if (member.managerId.equals("")) {
                    holder.unDitributeImg.setVisibility(View.VISIBLE);
                } else {
                    holder.unDitributeImg.setVisibility(View.GONE);
                }

                holder.member_photo.setImageURI(Uri.parse(Server.BASE_URL + member.pictureId));

                if (member.canTalk) {
                    holder.cannotTalkImg.setVisibility(View.GONE);//可以聊天不显示
                } else {
                    holder.cannotTalkImg.setVisibility(View.VISIBLE);
                }

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
                } else {
                    holder.member_status.setTextColor(Color.GRAY);
                    holder.member_status.setBackgroundResource(0);
                    holder.member_status.setText("未邀请");
                }
                break;
        }


        return convertView;
    }

    public interface NotifySelectionLinstener {
        public void selection(int positon);
    }

    public void setNotifySelectionLinstener(NotifySelectionLinstener selectionLinstener) {
        this.selectionLinstener = selectionLinstener;
    }

    class ViewHolder {

        public SimpleDraweeView member_photo;
        public TextView member_name;
        public TextView member_status;
        public TextView member_group_name;

        public ImageView group_photo;
        public TextView group_name;
        public CheckBox group_checkbox;

        public TextView alphabet;
        public GroupNameLayout groupNameLayout;

        public ImageView unDitributeImg;

        public ImageView cannotTalkImg;
    }

}