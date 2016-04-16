package com.meetrend.haopingdian.activity;

import java.lang.ref.WeakReference;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Config;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.DaYiActivityManager;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.Utils;
//import com.umeng.analytics.MobclickAgent;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class BaseActivity extends FragmentActivity {
	public String tag;
	protected BaseHandler mHandler = new BaseHandler(this);
	public SystemBarTintManager tintManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DaYiActivityManager.getWeCareActivityManager().pushActivity(this);
		
		
		//19,状态栏颜色
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}
		tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.happyred);

	}

	@TargetApi(19)
	public  void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}


	@Override
	public void onDestroy() {
		DaYiActivityManager.getWeCareActivityManager().removeStackActivity(this);
		super.onDestroy();
	}

	public void showToast(int resId) {
		View toastLayout = LayoutInflater.from(this).inflate(R.layout.toast_layout,null);
		TextView textView = (TextView)toastLayout.findViewById(R.id.toast_textview);
		textView.setText(getString(resId));
		Toast toast=new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setView(toastLayout);
		toast.show();
	}

	public void showToast(String msg) {
		View toastLayout = LayoutInflater.from(this).inflate(R.layout.toast_layout,null);
		TextView textView = (TextView)toastLayout.findViewById(R.id.toast_textview);
		textView.setText(msg);

		Toast toast=new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setView(toastLayout);
		toast.show();
	}


	static class BaseHandler extends Handler {
		private WeakReference<BaseActivity> activity;

		public BaseHandler(BaseActivity activity) {
			this.activity = new WeakReference<BaseActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseActivity activity = this.activity.get();
			if (activity != null) {
				activity.handleMessage(msg);
			}
		}
	}
	
	public void checkPwdChange(){
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		String resolution = Utils.getResolution(this);

		AjaxParams params = new AjaxParams();
		params.put("loginName",SPUtil.getDefault(this).getLoginName());
		params.put("password", SPUtil.getDefault(this).getPwd());
		params.put("deviceType", Config.DEVICE_TYPE_ANDROID);
		params.put("deviceDetail", android.os.Build.MODEL);
		params.put("resolution", resolution);

		if (deviceId == null) {
			LogUtil.w(tag, "设备Id为空");
		} else {
			params.put("deviceId", deviceId);
		}

		Callback callback = new Callback(tag,this) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mHandler.sendEmptyMessage(222);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "login info : " + t);
				if (isSuccess && data.get("token") != null) {
					return;
				} else {
					Message msg = new Message();
					msg.what = 222;
					msg.obj = data.get("msg").getAsString();
					mHandler.sendMessage(msg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.LOGIN_URL, params, callback);
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case 111:
			 showToast("请求失败，请重试！");
			break;
		default:
			break;
		}
	}
	
	/*public void showDialog() {
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ProgressDialogFragment fragment = ProgressDialogFragment.newInstance();
		fragment.setCancelable(true);
		ft.add(fragment, "BASE_ACTIVITY").commitAllowingStateLoss();
	}*/
	
	/*public void dimissDialog() {
		DialogFragment fragment = (DialogFragment) this.getSupportFragmentManager().findFragmentByTag("BASE_ACTIVITY");
		if (fragment != null) {
			fragment.dismissAllowingStateLoss();
		}
	}
	
	public boolean dialogIsShow(){
		
		if (this.getSupportFragmentManager().findFragmentByTag("BASE_ACTIVITY") != null) {
			return true;
		}
		
		return false;
	}*/
	
	public Dialog dialog = null;
	
	public void showDialog() {
	    dialog = new Dialog(BaseActivity.this, R.style.base_dialog_theme);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setContentView(R.layout.fragment_progress_dialog);
	    TextView tv = (TextView) dialog.findViewById(R.id.tv_fragment_progress_dialog);
		tv.setText("加载中...");
	    dialog.show();
	}
	
	public void showDialog(String hint) {
	    dialog = new Dialog(BaseActivity.this, R.style.base_dialog_theme);
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
		dialog = null;
	}
	
	public boolean hasDialog(){
		if (null !=  dialog) {
			return true;
		}
		return false;
	}

	//@Override
	//public void startActivity(Intent intent) {
	//	super.startActivity(intent);
		                                 //即将进入视野的Actvitiy的近来动画
		//this.overridePendingTransition(R.anim.push_left_in,
				//即将退出视野的Actvitiy的退出动画
			//	R.anim.push_left_out);
	//}
	
	//自定义退出
	//public void personalStartActivity(Intent intent){
	//	super.startActivity(intent);
		
		//this.overridePendingTransition(0,R.anim.push_left_out);
	//}
	
	//@Override
	//public void finish() {
	//	super.finish();
		//this.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
	//}
	
}