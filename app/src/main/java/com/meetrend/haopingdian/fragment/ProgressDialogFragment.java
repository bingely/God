package com.meetrend.haopingdian.fragment;


import com.meetrend.haopingdian.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 *进度框
 *
 */

public class ProgressDialogFragment extends DialogFragment {
//	private static final String RESID = "resid";
//	private static final String STR = "string"; 
//	private int resid;
//	private String str;
	
//	public static ProgressDialogFragment newInstance(int resid) {
//		ProgressDialogFragment fragment = new ProgressDialogFragment();
//		Bundle args = new Bundle();
//		args.putInt(RESID, resid);
//		fragment.setArguments(args);
//		return fragment;
//	}
	
	public static ProgressDialogFragment newInstance() {
		ProgressDialogFragment fragment = new ProgressDialogFragment();
//		Bundle args = new Bundle();
//		args.putString(STR, str);
//		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (this.getArguments() == null) {
//			throw new IllegalStateException("call newInstance first");
//		}
//		Bundle bundle = this.getArguments();
//		if (bundle.containsKey(RESID)) {
//			resid = bundle.getInt(RESID);
//		} else if (bundle.containsKey(STR)) {
//			str = bundle.getString(STR);
//		}		
		
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_progress_dialog, container, false);
		TextView tv = (TextView) rootView.findViewById(R.id.tv_fragment_progress_dialog);
		tv.setText("加载中...");
//		if (str == null) {
//			tv.setText(resid);
//		} else {
//			tv.setText(str);
//		}
		this.getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return rootView;
	}

}
