package com.meetrend.haopingdian.fragment;

import java.lang.ref.WeakReference;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.LogUtil;

public class BaseFragment extends Fragment {
	
	public FragmentActivity mActivity;
	public String tag;
	public BaseHandler mHandler = new BaseHandler(this);
	public ProgressDialogFragment fragment = null;
	
	public boolean isFirstLoad = true;
	
	@Override
	public void onAttach(Activity activity) {
		tag = this.getClass().getSimpleName();
		super.onAttach(activity);
	}

	public void showToast(int resId) {
		View toastLayout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout,null);
		TextView textView = (TextView)toastLayout.findViewById(R.id.toast_textview);
		textView.setText(getString(resId));
		Toast toast=new Toast(getActivity());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setView(toastLayout);
		toast.show();
	}

	public void showToast(String msg) {
		View toastLayout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout,null);
		TextView textView = (TextView)toastLayout.findViewById(R.id.toast_textview);
		textView.setText(msg);
		Toast toast=new Toast(getActivity());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setView(toastLayout);
		toast.show();
	}
	
	public  class BaseHandler extends Handler {
		private WeakReference<BaseFragment> fragment;

		public BaseHandler(BaseFragment fragment) {
			this.fragment = new WeakReference<BaseFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseFragment fragment = this.fragment.get();
			if (fragment != null) {
				fragment.processHandleMessage(msg);
			}
		}
	}
	
	public void processHandleMessage(Message msg) {
			switch (msg.what) {
			case 111:
				String rmsg = (String)msg.obj;
				showToast(rmsg);
				break;

			default:
				break;
			}
	}
	
/*	public void showDialog() {
		FragmentTransaction ft = this.getChildFragmentManager().beginTransaction();
		fragment = ProgressDialogFragment.newInstance();
		fragment.setCancelable(true);
		ft.add(fragment, "BASE_FRAGMENT").commit();
	}
	
	public void dimissDialog() {
		//DialogFragment fragment = (DialogFragment) this.getChildFragmentManager().findFragmentByTag("BASE_FRAGMENT");
		if (fragment != null) {
			fragment.dismissAllowingStateLoss();
			fragment = null;
		}
	}
	
	public boolean dialogIsShow(){
		
		if ((DialogFragment) this.getChildFragmentManager().findFragmentByTag("BASE_FRAGMENT") != null) {
			return true;
		}
		return false;
	}*/
	
	public Dialog dialog = null;
	
	public void showDialog() {
	    dialog = new Dialog(getActivity(), R.style.base_dialog_theme);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setContentView(R.layout.fragment_progress_dialog);
	    TextView tv = (TextView) dialog.findViewById(R.id.tv_fragment_progress_dialog);
		tv.setText("加载中...");
	    dialog.show();
	}
	
	public void showDialog(String hint) {
	    dialog = new Dialog(getActivity(), R.style.base_dialog_theme);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setContentView(R.layout.fragment_progress_dialog);
	    TextView tv = (TextView) dialog.findViewById(R.id.tv_fragment_progress_dialog);
		tv.setText(hint);
	    dialog.show();
	}
	
	public void dimissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
}