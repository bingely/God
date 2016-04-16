package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.AccountActivity;
import com.meetrend.haopingdian.activity.MyInfoActivity;
import com.meetrend.haopingdian.activity.ShowErWeiMaActivity;
import com.meetrend.haopingdian.activity.StorePayCodeInfoActivity;
import com.meetrend.haopingdian.activity.StroeChangeActivity;
import com.meetrend.haopingdian.bean.MeDetail;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.MeChangeEvent;
import com.meetrend.haopingdian.event.ReFreshLoginUserInfoEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.DaYiActivityManager;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.DialogUtil;
import com.meetrend.haopingdian.widget.ComItemLayoutView;

import de.greenrobot.event.EventBus;

/**
 * 应用主界面四大模块之一 ： 我
 *
 */
public class SettingFragment extends BaseFragment {
	
	private final static String TAG = SettingFragment.class.getName();
	
	/**
	 * 标题
	 */
	@ViewInject(id = R.id.m_title)
	TextView mTitle;
	/**
	 * 登录用户信息
	 */
	@ViewInject(id = R.id.my_photo_lyt, click = "onClick")
	RelativeLayout mMyPhotoLyt;
	
	/**
	 * 门店二维码
	 */
	@ViewInject(id = R.id.store_erweima, click = "onClick")
	ComItemLayoutView storeErwema;
	
	/**
	 *我的二维码
	 */
	@ViewInject(id = R.id.my_erweima, click = "onClick")
	ComItemLayoutView my_erweima;

	/**
	 * 门店收款码
	 */
	@ViewInject(id = R.id.store_get_tiaoma, click = "onClick")
	ComItemLayoutView storeTiaoMaView;

	
	/**
	 * 切换店铺
	 */
	@ViewInject(id = R.id.store_code, click = "onClick")
	ComItemLayoutView mStoreCode;
	
	/**
	 * 修改密码
	 */
	@ViewInject(id = R.id.pwd_modify, click = "onClick")
	ComItemLayoutView mPwdModify;
	
	/**
	 * 关于我们
	 */
	@ViewInject(id = R.id.about_diyi, click = "onClick")
	ComItemLayoutView mAboutDayi;
	
	/**
	 * 退出登录
	 */
	@ViewInject(id = R.id.unlogin, click = "onClick")
	ComItemLayoutView mUnlogin;
	
	@ViewInject(id = R.id.my_photo)
	SimpleDraweeView mMyPhoto;
	
	@ViewInject(id = R.id.my_name)
	TextView mMyName;
	
	@ViewInject(id = R.id.my_phone)
	TextView mMyPhone;
	
	private SPUtil sp = null;
	
	private String mUserEWMurl;
	private String mStoreEWMurl;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		EventBus.getDefault().register(this);
		View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		sp = SPUtil.getDefault(mActivity);
		mTitle.setText(App.storeInfo.storeName);
		


		getInfos();
		return rootView;
	}
	

	/**
	 * 获取个人信息
	 */
	public void getInfos() {

		showDialog();

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());

		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
				
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				dimissDialog();
				JsonParser parser = new JsonParser();
            	JsonObject root = parser.parse(t).getAsJsonObject();
            	data = root.get("data").getAsJsonObject();
            	LogUtil.d(tag, "success " + root.get("success").getAsString());
            	isSuccess = Boolean.parseBoolean(root.get("success").getAsString());
				if (isSuccess) {
					App.meDetail = new Gson().fromJson(data, MeDetail.class);
					
					mMyName.setText(App.meDetail.userName);
					mMyPhone.setText("电话: "+App.meDetail.mobile);
					
					mUserEWMurl = data.get("userQrCodr").getAsString();
					mStoreEWMurl = data.get("storeQrCodr").getAsString();
					
					initPic();
				} else {
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}
				}			
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_MY_INFOS, params, callback);
	}

	
	public void onEventMainThread(MeChangeEvent event) {
		initPic();
	}
	public void onEventMainThread(ReFreshLoginUserInfoEvent event) {
		getInfos();
	}

	
	/**
	 * 
	 * 单击事件
	 * @param view
	 * @author bob
	 * 
	 * */
	
	public void onClick(View view){
		Intent intent = null;
		switch(view.getId()){
			case R.id.my_photo_lyt:{
				intent = new Intent(mActivity,MyInfoActivity.class);
				intent.putExtra(MyInfoActivity.type, MyInfoActivity.TYPE_MY_INFO);
				startActivity(intent);
				break;
			}
			case R.id.store_code:{
				Intent storeChangeIntent = new Intent(mActivity,StroeChangeActivity.class);
				startActivity(storeChangeIntent);
				break;
			}

			case R.id.store_get_tiaoma:{
				Intent tiaoMaIntent = new Intent(mActivity,StorePayCodeInfoActivity.class);
				startActivity(tiaoMaIntent);
				break;
			}
			case R.id.pwd_modify:{
				intent = new Intent(mActivity,MyInfoActivity.class);
				intent.putExtra(MyInfoActivity.type, MyInfoActivity.TYPE_PWD_MODIFY);
				startActivity(intent);
				break;
			}
			case R.id.about_diyi:{
				intent = new Intent(mActivity,MyInfoActivity.class);
				intent.putExtra(MyInfoActivity.type, MyInfoActivity.TYPE_ABOUT_DAYI);
				startActivity(intent);
				break;
			}
			case R.id.unlogin:{
				
				DialogUtil unloginDialog = new DialogUtil(getActivity());
				unloginDialog.showUnloginDialog(getActivity(),"确定退出当前账号?", "取消", "确定");
				
				break;
			}
			case R.id.store_erweima:{
				intent = new Intent(mActivity, ShowErWeiMaActivity.class);
				intent.putExtra("ey", "3");
				intent.putExtra("mQRC", mStoreEWMurl);
				mActivity.startActivity(intent);
				break;
			}
			
			case R.id.my_erweima:
				intent = new Intent(mActivity, ShowErWeiMaActivity.class);
				intent.putExtra("ey", "4");
				intent.putExtra("mMy", mUserEWMurl);
				mActivity.startActivity(intent);
				break;
		}
	}
	
	private void initPic(){
		if(App.meDetail!=null){
			mMyPhoto.setImageURI(Uri.parse(Server.BASE_URL + App.meDetail.avatarId));
		}
	}

	//退出登录Dialog
//	private class FinishDialog extends Dialog {
//
//		public FinishDialog(Context context,int theme) {
//			super(context,theme);
//			View dialogView = getLayoutInflater().inflate(R.layout.cancel_dialog_layout, null);
//			requestWindowFeature(Window.FEATURE_NO_TITLE);
//			Button sureBtn = (Button) dialogView.findViewById(R.id.sure_btn);
//			Button cancelBtn = (Button) dialogView.findViewById(R.id.cancel_btn);
//
//
//			sureBtn.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					SPUtil.getDefault(getActivity()).savePwd("");//清除密码
//					DaYiActivityManager.getWeCareActivityManager().popAllActivityExceptOne(null);
//
//					Intent intent = new Intent(getActivity(),AccountActivity.class);
//					getActivity().startActivity(intent);
//				}
//			});
//
//			cancelBtn.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					dismiss();
//				}
//			});
//
//			this.setCancelable(true);
//			this.setCanceledOnTouchOutside(true);
//			super.setContentView(dialogView);
//		}
//
//		public FinishDialog(Context context){
//			this(context,R.style.discount_dialog_theme);
//		}
//
//
//		@Override
//		protected void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//
//			getWindow().setGravity(Gravity.BOTTOM);
//			//设置对话框的宽度
//			WindowManager m = getWindow().getWindowManager();
//	        Display d = m.getDefaultDisplay();
//	        WindowManager.LayoutParams p = getWindow().getAttributes();
//	        p.width = d.getWidth();
//	        p.height = LayoutParams.WRAP_CONTENT;
//	        getWindow().setAttributes(p);
//		}
//	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}