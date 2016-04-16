package com.meetrend.haopingdian.widget;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.Server;

public class GroupMemberView extends RelativeLayout {
	private static final String TAG = GroupMemberView.class.getSimpleName();
	private LinearLayout memberLyt = null;
	
	float screenWidth = 0;
	
	final int imgWidth = 170;
	
	int imgTotalCount = 1;
	
	int currentImgCount = 0;
	
	Context context;
	
	FinalBitmap loader = null;
	
	OnClickListener onClickListener = null;

	public GroupMemberView(Context context) {
		this(context, null);
	}

	public GroupMemberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		loader = FinalBitmap.create(context);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
		
		LayoutInflater.from(context).inflate(R.layout.widget_group_member, this, true);
		screenWidth =((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		memberLyt = (LinearLayout) this.findViewById(R.id.member_lyt);
		imgTotalCount = (int)(screenWidth/imgWidth);
	}
	
	private ImageView createImageView(){
		ImageView img = new ImageView(context);
		LayoutParams params = new LayoutParams(160, 160);
		img.setLayoutParams(params);
		img.setScaleType(ImageView.ScaleType.CENTER_CROP);
		return img;
	}
	
	private View createPaddingView(){
		View v = new View(context);
		LayoutParams params = new LayoutParams(10, 1);
		v.setLayoutParams(params);
		return v;
	}
	
	public void initLayout(ArrayList<String> list){
		memberLyt.removeAllViews();
		int count = 0;
		if(list==null || list.size()<=0){
			count = 1;
		}else{
			count = (imgTotalCount>list.size())?list.size()+1:imgTotalCount;
		}
		for(int i = 0;i<count;i++){
			ImageView imgView = createImageView();
			
			if(i==(count-1)){
				imgView.setImageResource(R.drawable.member_add);
				memberLyt.addView(imgView);
			}else{
				loader.display(imgView, Server.BASE_URL +list.get(i), imgWidth, imgWidth);
				memberLyt.addView(imgView);
				memberLyt.addView(createPaddingView());
			}
			
			
			
		}
		
		setAddMemberOnClick(onClickListener);
	}
	
	public void setAddMemberOnClick(OnClickListener onClickListener){
		this.onClickListener = onClickListener;
		if(memberLyt!=null && memberLyt.getChildCount()>0){
			ImageView addImage = (ImageView)memberLyt.getChildAt(memberLyt.getChildCount()-1);
			addImage.setOnClickListener(onClickListener);
		}
			
	}
	
	public void addMemberLayout(Member member){
		
	}
	
	public void removeMemberLayout(Member member){
		
	}

}
