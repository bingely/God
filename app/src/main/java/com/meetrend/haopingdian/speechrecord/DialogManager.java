package com.meetrend.haopingdian.speechrecord;

import com.meetrend.haopingdian.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 语音对话框管理器
 * @author 肖建斌
 *
 */
public class DialogManager {
	
	private Dialog dialog;
	
	private ImageView img1;
	private ImageView img2;
	
	private TextView lableTextView;
	
	Context context;
	
	public DialogManager(Context context){
		this.context = context;
	}
	
	public void showDialog(){
		dialog = new Dialog(context, R.style.record_dialog_theme);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.record_dialog, null);
		dialog.setContentView(view);
		img1 = (ImageView) dialog.findViewById(R.id.img1);
		img2 = (ImageView) dialog.findViewById(R.id.img2);
		lableTextView = (TextView) dialog.findViewById(R.id.label_text);
		lableTextView.setText(R.string.label_recording_txt);
		dialog.show();
	}
	
	public void wantCancel(){
		if (dialog != null && dialog.isShowing()) {
			img1.setVisibility(View.VISIBLE);
			img1.setImageResource(R.drawable.cancel);
			img2.setVisibility(View.GONE);
			lableTextView.setVisibility(View.VISIBLE);
			lableTextView.setText("松开手指，取消发送");
		}
	}
	
	//录音时间太短
	public void tooShort(){
		if (dialog != null && dialog.isShowing()) {
			img1.setVisibility(View.VISIBLE);
			img1.setImageResource(R.drawable.voice_to_short); 
			img2.setVisibility(View.GONE);
			lableTextView.setVisibility(View.VISIBLE);
			lableTextView.setText("录音时间太短");
		}
	}
	
	public void recording(){
		if (dialog != null && dialog.isShowing()) {
			img1.setVisibility(View.VISIBLE);
			img2.setVisibility(View.VISIBLE);
			img1.setImageResource(R.drawable.recorder);
			lableTextView.setVisibility(View.VISIBLE);
			lableTextView.setText("手指上滑，取消发送");
		}
	}
	
	public void dismissDialog(){
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	//level 1 - 7
	public void updateVoiceLevel(int level){
		if (dialog != null && dialog.isShowing()) {
			
			if (level == 0) {
				level = 1;
			}
			
			int resId = context.getResources().getIdentifier("v"+level, "drawable", context.getPackageName());
			//img2.setVisibility(View.VISIBLE);
			img2.setImageResource(resId);
		}
	}

}