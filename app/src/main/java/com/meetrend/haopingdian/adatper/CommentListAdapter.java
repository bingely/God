package com.meetrend.haopingdian.adatper;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.EventGridAdapter.ViewHolder;
import com.meetrend.haopingdian.bean.CommentUser;
import com.meetrend.haopingdian.bean.CommentUser.Pictrue;
import com.meetrend.haopingdian.bean.EventPictrue;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.widget.FaceiconTextView;
import com.meetrend.haopingdian.widget.GridView;
import com.umeng.socialize.net.u;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentListAdapter extends BaseAdapter{
	
	private List<CommentUser> list;
	private Context context;
	private LayoutInflater layoutInflater;
	private FinalBitmap loader;
	
	
	
	public CommentListAdapter(Context context,List<CommentUser> list){
		this.context = context;
		this.list = list;
		layoutInflater = LayoutInflater.from(context);
		loader = FinalBitmap.create(context);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
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
		
		ViewHelper viewHelper = null;
		if (convertView == null) {
			viewHelper = new ViewHelper();
			convertView = layoutInflater.inflate(R.layout.comment_listview_item_layout, null);
			viewHelper.head = (ImageView) convertView.findViewById(R.id.comment_user_head);
			viewHelper.userNameTv = (TextView) convertView.findViewById(R.id.comment_user_name);
			viewHelper.commentTimeTv = (TextView) convertView.findViewById(R.id.comment_time);
			viewHelper.answerTv = (TextView) convertView.findViewById(R.id.comment_answer);
			viewHelper.gridView = (GridView) convertView.findViewById(R.id.comment_gridview);
			viewHelper.commentContentTv = (FaceiconTextView) convertView.findViewById(R.id.comment_content);
			convertView.setTag(viewHelper);
		}else {
			viewHelper = (ViewHelper) convertView.getTag();
		}
		final CommentUser user = list.get(position);
		loader.display(viewHelper.head, Server.BASE_URL + user.head);
		viewHelper.commentTimeTv.setText(user.createTime);
		
		//门店的发表
		if (user.type == 1) {
			viewHelper.answerTv.setVisibility(View.GONE);
			viewHelper.userNameTv.setText(user.commentUser);
			String content = "回复@"+ user.replyUser + ":"+user.commentContent;
			int start = 2;
			int end = start + user.replyUser.length()+1; 
			
			SpannableStringBuilder style = new SpannableStringBuilder(content);
			style.setSpan(new ForegroundColorSpan(Color.parseColor("#4a77a8")), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色    
			viewHelper.commentContentTv.setText(style);
			viewHelper.gridView.setVisibility(View.GONE);
			
		}
		//会员的评论
		else if (user.type == 0) {
			viewHelper.answerTv.setVisibility(View.VISIBLE);
			viewHelper.answerTv.setTextColor(Color.parseColor("#0bba0a"));
			viewHelper.userNameTv.setText(user.commentUser);
			viewHelper.commentContentTv.setText(user.commentContent);
			if (user.picList.size() == 0) {
				viewHelper.gridView.setVisibility(View.GONE);
			}else {
				viewHelper.gridView.setVisibility(View.VISIBLE);
				viewHelper.gridView.setAdapter(new EventGridAdapter(context, user.picList));//显示gridview数据
			}
			
			//可以回复
			if (user.clickType == 0) {
				viewHelper.answerTv.setText("回复");
				viewHelper.answerTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).clickType == 1) {
								list.get(i).clickType = 0;
							}
						}
						
						user.clickType = 1;
						answerClick.click(user.id,user.commentUser);
						((TextView)v).setText("取消");
						

						
						notifyDataSetChanged();
						//Log.i("-----------点击了回复--------------", "回复");
					}
				});
				
			}
			//取消回复
			else if (user.clickType == 1) {
				viewHelper.answerTv.setTextColor(Color.parseColor("#ff0000"));
				viewHelper.answerTv.setText("取消回复");
				viewHelper.answerTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						user.clickType = 0;
						//answerClick.click(user.id,user.commentUser);
						((TextView)v).setText("回复");
						cancelClick.cancel();
						notifyDataSetChanged();
						//Log.i("-----------点击了取消--------------", "取消");
					}
				});
			}
		}
		return convertView;
	}
	
	//回复接口回调
	public interface AnswerClick{
		public void click(String commentID,String name);
	}
	
	public AnswerClick answerClick;
	
	public void setAnswerClick(AnswerClick answerClick){
		this.answerClick = answerClick;
	}
	
	//取消接口回调
	public interface CancelClick{
		public void cancel();
	}
	
	public CancelClick cancelClick;
	
	public void setCancelClick(CancelClick cancelClick){
		this.cancelClick = cancelClick;	
	}
	
	class ViewHelper{
		ImageView head;
		TextView userNameTv;
		TextView answerTv;
		TextView commentTimeTv;
		FaceiconTextView commentContentTv;
		GridView gridView;
	}
	
	/**
	 * gridview显示部分
	 * */
	public class EventGridAdapter extends BaseAdapter{
		private Context context;
		private LayoutInflater layoutInflater;
		private List<Pictrue> list;
		private FinalBitmap loader2;
		
		public EventGridAdapter(Context context,List<Pictrue> list){
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
			this.list = list;
			loader2 = FinalBitmap.create(context);
			loader2.configLoadingImage(R.drawable.loading_default);
			loader2.configLoadfailImage(R.drawable.loading_failed);
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
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.event_grid_adapter_layout, null);
				viewHolder.gridImg = (ImageView) convertView.findViewById(R.id.grid_img);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			loader2.display(viewHolder.gridImg, Server.BASE_URL + list.get(position));
			return convertView;
		}
		
		class ViewHolder{
			ImageView gridImg;
		}

	}

}
