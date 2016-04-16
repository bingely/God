package com.meetrend.haopingdian.util;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.AccountActivity;
import com.meetrend.haopingdian.adatper.UpdateContentAdapter;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.DaYiActivityManager;
import com.meetrend.haopingdian.tool.SPUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DialogUtil {

	private Context context;

	public DialogUtil(Context context) {
		this.context = context;
	}

	public void showPhoneDialog(final String phonestr, String cancel,
			String sure) {

		final Dialog dialog = new Dialog(context, R.style.dialog_theme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.phone_dialog_layout);
		TextView phoneView = (TextView) dialog.findViewById(R.id.phoneview);
		phoneView.setText(phonestr);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
		Button sureButton = (Button) dialog.findViewById(R.id.sure);
		cancelButton.setText(cancel);
		sureButton.setText(sure);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		sureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				finishListener.finishView();

				AjaxParams params = new AjaxParams();
				params.put("token", SPUtil.getDefault(context).getToken());
				params.put("storeId", SPUtil.getDefault(context).getStoreId());

				Callback callback = new Callback("", context) {
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						finishListener.finishView();
					}

					@Override
					public void onSuccess(String t) {
						super.onSuccess(t);

					}

				};
				FinalHttp request = new FinalHttp();
				request.get(Server.BASE_URL + Server.LOGIN_OUT, params,
						callback);
			}
		});

		dialog.show();
	}
	
	public void showUnloginDialog(final Context context,final String phonestr, String cancel,
			String sure) {

		final Dialog dialog = new Dialog(context, R.style.dialog_theme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.phone_dialog_layout);
		TextView phoneView = (TextView) dialog.findViewById(R.id.phoneview);
		phoneView.setText(phonestr);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
		Button sureButton = (Button) dialog.findViewById(R.id.sure);
		cancelButton.setText(cancel);
		sureButton.setText(sure);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		sureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SPUtil.getDefault(context).savePwd("");//清除密码
				DaYiActivityManager.getWeCareActivityManager().popAllActivityExceptOne(null);
				context.startActivity(new Intent(context,AccountActivity.class));
			}
		});

		dialog.show();
	}

	FinishListener finishListener;

	public void setListener(FinishListener finishListener) {
		this.finishListener = finishListener;
	}

	public interface FinishListener {

		public void finishView();

	}

	/**
	 * 
	 * @param path
	 *            更新的路径
	 * @param cancel
	 *            取消按钮 的标题
	 * @param sure
	 *            确定 按钮的 标题
	 * @param boolean isUpDate 为true强制更新 ，反之可选可不选
	 */
	public void showOverDialog(boolean isUpDate, final String path,
			String cancel, String sure,List<String> mList) {

		final Dialog dialog = new Dialog(context, R.style.dialog_theme);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_appversion_update_layout);
		
		Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
		Button sureButton = (Button) dialog.findViewById(R.id.sure);
		cancelButton.setText(cancel);
		sureButton.setText(sure);
		
		ListView listView = (ListView) dialog.findViewById(R.id.contentlistview);
		UpdateContentAdapter mAdapter = new UpdateContentAdapter(mList, context);
		listView.setAdapter(mAdapter);
		

		if (isUpDate) {
			cancelButton.setEnabled(false);
			cancelButton.setTextColor(Color.GRAY);
		}

		// 以后再说button
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (upLoadDialogCancelListener != null) {
					upLoadDialogCancelListener.cancel();
				}
				dialog.dismiss();
			}
		});

		// 马上更新
		sureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishListener.finishView();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void findpwdDialog(final String path, String cancel, String sure) {

		final Dialog dialog = new Dialog(context, R.style.dialog_theme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.findloginpwd_dialog_layout);
		TextView phoneView = (TextView) dialog.findViewById(R.id.phoneview);
		phoneView.setText(path);
		Button sureButton = (Button) dialog.findViewById(R.id.sure);
		sureButton.setText(sure);

		sureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishListener.finishView();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public interface UpLoadDialogCancelListener {
		public void cancel();
	}

	UpLoadDialogCancelListener upLoadDialogCancelListener;

	public void setUpLoadDialogCancelListener(
			UpLoadDialogCancelListener upLoadDialogCancelListener) {
		this.upLoadDialogCancelListener = upLoadDialogCancelListener;
	}

	/**
	 * 是否购买商品
	 * 
	 * @param phonestr
	 * @param cancel
	 * @param sure
	 */
	public void showPayDialog(final String phonestr, String cancel, String sure) {

		final Dialog dialog = new Dialog(context, R.style.dialog_theme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.phone_dialog_layout);
		TextView phoneView = (TextView) dialog.findViewById(R.id.phoneview);
		phoneView.setText(phonestr);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
		Button sureButton = (Button) dialog.findViewById(R.id.sure);
		cancelButton.setText(cancel);
		sureButton.setText(sure);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		sureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				finishListener.finishView();
			}
		});

		dialog.show();
	}
	
	//只显示单个确定按钮的对话框
	public  void showConfirmDialog(String customerNameStr){
		
		final Dialog dialog = new Dialog(context, R.style.dialog_theme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.comfirm_dialog);
		
		TextView t2View = (TextView) dialog.findViewById(R.id.text2);
		t2View.setText("发送至" + customerNameStr);
		Button sureButton = (Button) dialog.findViewById(R.id.sure);
		
		sureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (finishListener != null) {
					finishListener.finishView();
				}
				
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}

	/**
	 * 邀请和再次邀请对话框
	 */
	public void inviteMemberDialog(final String hint, String cancel, String sure) {

		final Dialog dialog = new Dialog(context, R.style.dialog_theme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.phone_dialog_layout);
		TextView phoneView = (TextView) dialog.findViewById(R.id.phoneview);
		phoneView.setText(hint);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
		Button sureButton = (Button) dialog.findViewById(R.id.sure);
		cancelButton.setText(cancel);
		sureButton.setText(sure);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		sureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				if (makeSureIn != null)
					makeSureIn.sure();
			}
		});

		dialog.show();
	}

	public interface  MakeSureIn{
		public abstract void sure();
	}

	MakeSureIn makeSureIn;

	public void setMakeSureIn(MakeSureIn makeSureIn){
		if(makeSureIn != null){
			this.makeSureIn = makeSureIn;
		}
	}

}
