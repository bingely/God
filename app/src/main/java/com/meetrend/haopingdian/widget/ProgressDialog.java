package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.meetrend.haopingdian.R;

public class ProgressDialog extends RelativeLayout{
	private ProgressBar mProgress;
//	private Button mReload;
	
	public ProgressDialog(Context context) {
		this(context, null);
	}

	public ProgressDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.widget_progress_dialog, this, true);
		mProgress = (ProgressBar)this.findViewById(R.id.pb_widget);
//		mReload = (Button)this.findViewById(R.id.btn_reload);	
	}
	
	public void setReLoadVisibility(boolean flag) {
		mProgress.setVisibility(flag ? View.GONE : View.VISIBLE);
//		mReload.setVisibility(flag ? View.VISIBLE : View.GONE);
	}	

//	public void setReloadClickListener(OnClickListener listener) {
//		mReload.setOnClickListener(listener);
//	}	
	
}
