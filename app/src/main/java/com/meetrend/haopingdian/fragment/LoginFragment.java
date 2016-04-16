package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.FindPwdActivity;
import com.meetrend.haopingdian.activity.MainActivity;
import com.meetrend.haopingdian.bean.StoreInfo;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Config;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.memberlistdb.StoreInfoOpetate;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.CheckNet;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.SystemUtil;
import com.meetrend.haopingdian.tool.Utils;
import com.meetrend.haopingdian.util.DialogUtil;
import com.meetrend.haopingdian.util.DialogUtil.FinishListener;
import com.meetrend.haopingdian.util.DialogUtil.UpLoadDialogCancelListener;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;

public class LoginFragment extends BaseFragment {

	private final static String TAG = LoginFragment.class.getName();

	public final static String SIGNUP = "signup";//退出登录传递参数

	@ViewInject(id = R.id.et_login_name)
	EditText mName;
	@ViewInject(id = R.id.iv_clear, click = "clearClick")
	ImageButton mClear;
	
	@ViewInject(id = R.id.et_login_pwd)
	EditText mPwd;
	@ViewInject(id = R.id.iv_clear1, click = "clearClick1")
	ImageButton mClear1;
	
	@ViewInject(id = R.id.btn_login, click = "loginClick")
	Button mLoginBtn;
	
	@ViewInject(id = R.id.tv_forgot_pwd, click = "forgotClick")
	TextView mForgot;
	
	@ViewInject(id = R.id.busy_layout)
	RelativeLayout busyLayout;
	
	@ViewInject(id = R.id.et_shanghu_pwd)
	EditText shanghuEdit;
	@ViewInject(id = R.id.iv_clear2, click = "clearClick2")
	ImageButton shhClear;

	//三条线
	@ViewInject(id = R.id.busy_line)
	View busyLineView;
	@ViewInject(id = R.id.user_line)
	View userLineView;
	@ViewInject(id = R.id.pass_line)
	View passLineView;

	/**
	 * apK 下载更新地址
	 */
	public String url;

	/**
	 * 是否需要强制更新
	 */
	public Boolean isNeedUpdate;

	/**
	 * 服务器apK的版本名
	 */
	public String versionName;
	
	/**
	 * 是否可以登录的标识
	 */
	boolean isCanLoad;
	
	/**
	 * 通过商务号获取的服务器地址
	 */
	private String busynessUrl;
	
	/**
	 * 先通过商务号获取登录地址再发消息开始登录
	 */
	private final static int START_LOGIN = 0X752;
	
	private final static int START_CHECK_VERSION = 0X587;
	
	//商务号栏已经显示
	private boolean isbusyLayoutShow;
	//版本更新对话框显示
	private boolean ischeckVersionDialog;
	
	/**
	 * 登录的流程：
	 * 
	 * 商务号显示    1、则通过商务号获得登录路径  2、检查版本  3登录
	 * 
	 * 商务号不显示    1、检查版本   2、登录
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_login, container,false);
		FinalActivity.initInjectedView(this, rootView);

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//商务号编辑框监听
		shanghuEdit.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(shanghuEdit.getText().toString())) {
					shhClear.setVisibility(View.GONE);
				} else {
					shhClear.setVisibility(View.VISIBLE);
				}
			}
		});
		
		//如果商务账号为空，则隐藏输入商务号一栏,商务栏显示的时候则需要取出保存的商务号
		if (TextUtils.isEmpty(SPUtil.getDefault(mActivity).getBusiNessNum())) {
			busyLayout.setVisibility(View.VISIBLE);
			busyLineView.setVisibility(View.VISIBLE);
			String busyNum = SPUtil.getDefault(mActivity).getShangWuNum();
			shanghuEdit.setText(busyNum);
			shanghuEdit.setSelection(busyNum.length());
			
		}else {
			busyLayout.setVisibility(View.GONE);
			busyLineView.setVisibility(View.GONE);
		}
		
		//登录名
		String name = SPUtil.getDefault(mActivity).getLoginName();
		if (!TextUtils.isEmpty(name)) {
			mName.setText(name);
			mName.setSelection(name.length());
		}

		//登录密码
		if (!TextUtils.isEmpty(SPUtil.getDefault(mActivity).getPwd())){
			mPwd.setText(SPUtil.getDefault(mActivity).getPwd());
			mPwd.setSelection(SPUtil.getDefault(mActivity).getPwd().length());
		}else{
			mPwd.requestFocus();
		}

		if (mName.getText().length() > 0) {
			mClear.setVisibility(View.VISIBLE);
		}
		if (mPwd.getText().length() > 0) {
			mClear1.setVisibility(View.VISIBLE);
		}
		
		
		mName.addTextChangedListener(new TextWatcherChangeListener() {

			@Override
			public void afterTextChanged(Editable arg0) {
				if (TextUtils.isEmpty(mName.getText().toString())) {
					mPwd.getText().clear();
					mClear.setVisibility(View.GONE);
				} else {
					mClear.setVisibility(View.VISIBLE);
				}
			}
		});

		mPwd.addTextChangedListener(new TextWatcherChangeListener() {

			@Override
			public void afterTextChanged(Editable arg0) {
				if (TextUtils.isEmpty(mPwd.getText().toString())) {
					mClear1.setVisibility(View.GONE);
				} else {
					mClear1.setVisibility(View.VISIBLE);
				}
			}
		});

		final int greenColorId = Color.parseColor("#02BC00");
		final int backColorId = Color.parseColor("#e0e0e0");
		mName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (view.hasFocus())
					userLineView.setBackgroundColor(greenColorId);
				else
					userLineView.setBackgroundColor(backColorId);
			}
		});

		mPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (view.hasFocus())
					passLineView.setBackgroundColor(greenColorId);
				else
					passLineView.setBackgroundColor(backColorId);
			}
		});

		shanghuEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (view.hasFocus())
					busyLineView.setBackgroundColor(greenColorId);
				else
					busyLineView.setBackgroundColor(backColorId);
			}
		});


		
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.FAILED:
				dimissDialog();
				if (msg.obj == null) {
					showToast(R.string.login_failed);
				} else {
					showToast((String) msg.obj);
				}
				break;
			case START_CHECK_VERSION:
				  getSystemVersion();
				break;
			}
		}
	};
	
	/**
	 * 登录按钮的点击事件
	 * @param v
	 */
	public void loginClick(View v) {

		if (!CheckNet.checkNetState(getActivity())) {
			showToast("请检查您的网络");
			return;
		}

		if (isCanLoad) {
			login();
		}else {
			chooseLoginType();
		}
	}

	public void clearClick(View v) {
		mName.getText().clear();
	}

	public void clearClick1(View v) {
		mPwd.getText().clear();
	}
	
	public void clearClick2(View v) {
		shanghuEdit.getText().clear();
	}
	
	

	public void forgotClick(View v) {
		Intent intent = new Intent(mActivity, FindPwdActivity.class);
		startActivity(intent);
	}

	/**
	 * 获取系统版本
	 */
	public void getSystemVersion() {
		
		if (!isbusyLayoutShow) {
			showDialog();
		}
		
		AjaxParams params = new AjaxParams();
		params.put("deviceType", "5");
		params.put("name", "O2OAPP");

		Callback callback = new Callback(tag, getActivity()) {
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
				LogUtil.d(tag, "  my info : " + t);
				JsonParser parser = new JsonParser();
				JsonObject root = parser.parse(t).getAsJsonObject();
				data = root.get("data").getAsJsonObject();
				isSuccess = Boolean.parseBoolean(root.get("success")
						.getAsString());
				if (isSuccess) {

					versionName = data.get("version").getAsString();
					String appVersion = SystemUtil.getAppVersionName(mActivity);
					Log.i(TAG, appVersion);
					
					// 判断服务器是否是新版本
					String serverNumArray[] = new String[3];
					String tempServerNumArray[] = versionName.split("\\.");
					
					if (tempServerNumArray.length == 3) {
						serverNumArray[0] = tempServerNumArray[0];
						serverNumArray[1] = tempServerNumArray[1];
						serverNumArray[2] = tempServerNumArray[2];
					}else if (tempServerNumArray.length == 2) {
						serverNumArray[0] = tempServerNumArray[0];
						serverNumArray[1] = tempServerNumArray[1];
						serverNumArray[2] = "0";
					}

					String localNumArray[] = new String[3];
					String tempLocalNumArray[] = appVersion.split("\\.");
					
					if (tempLocalNumArray.length == 3) {
						localNumArray[0] = tempLocalNumArray[0];
						localNumArray[1] = tempLocalNumArray[1];
						localNumArray[2] = tempLocalNumArray[2];
					}else if (tempLocalNumArray.length == 2) {
						localNumArray[0] = tempLocalNumArray[0];
						localNumArray[1] = tempLocalNumArray[1];
						localNumArray[2] = "0";
					}

					// 服务器有新版本
					if (judgeVersion(localNumArray, serverNumArray)) {
						ischeckVersionDialog = true;
						dimissDialog();

						isNeedUpdate = data.get("isNeedUpdate").getAsBoolean();
						url = data.get("url").getAsString();
						
						List<String> list = new ArrayList<String>();
						list.add("1.天机打印机功能");
						list.add("2.阅读性能优化");
						list.add("3.修复点赞bug");

						DialogUtil dialogUtil = new DialogUtil(mActivity);
						dialogUtil.showOverDialog(isNeedUpdate, "要更新吗？",
								"下次再说", "马上更新",list);
						dialogUtil.setListener(new FinishListener() {

							@Override
							public void finishView() {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
								Uri content_url = Uri.parse(url);
								intent.setData(content_url);
								startActivity(intent);
							}
						});
						
						dialogUtil.setUpLoadDialogCancelListener(new UpLoadDialogCancelListener() {
							
							@Override
							public void cancel() {
								isCanLoad = true;
								login();
							}
						});
					} else {
						login();
					}
				}

			}
		};

		CommonFinalHttp finalHttp = new CommonFinalHttp();
		//版本更新接口切换
		finalHttp.get(Server.SYSTEM_UPDATE2, params, callback);
	}

	/**
	 * 本地版本和服务器apk版本大小进行判断
	 * 
	 * @param la
	 * @param sa
	 * @return
	 */


	private boolean judgeVersion(String[] la, String[] sa) {

		if (Integer.parseInt(la[0]) == Integer.parseInt(sa[0])) {

			if (la[1].equals(sa[1])) {

				if (Integer.parseInt(la[2]) == Integer.parseInt(sa[2])) {
					dimissDialog();
					return false;
				} 
				
				if (Integer.parseInt(la[2]) > Integer.parseInt(sa[2])) {
					dimissDialog();
					return false;
				} 
				
				if (Integer.parseInt(la[2]) < Integer.parseInt(sa[2])) {
					dimissDialog();
					return true;
				}

			}  
			
			if (Integer.parseInt(la[1]) > Integer.parseInt(sa[1])) {
				dimissDialog();
				return false;
			} 
			
			if (Integer.parseInt(la[1]) < Integer.parseInt(sa[1])) {
				dimissDialog();
				return true;
			}

		} 
		
		if (Integer.parseInt(la[0]) > Integer.parseInt(sa[0])) {
			dimissDialog();
			return false;

		} 
		
		if (Integer.parseInt(la[0]) < Integer.parseInt(sa[0])) {
			dimissDialog();
			return true;
		}

		dimissDialog();
		return false;
	}



	/**
	 * 选择登录方式
	 */
	private void chooseLoginType() {
		
		final String name = mName.getText().toString();
		final String pwd = mPwd.getText().toString();
		
		if (TextUtils.isEmpty(name)) {
			showToast(R.string.str_empty_name);
			return;
		}
		
		if (TextUtils.isEmpty(pwd)) {
			showToast(R.string.str_empty_pwd);
			return;
		}
		
		if (busyLayout.getVisibility() == View.VISIBLE) {
			if (TextUtils.isEmpty(shanghuEdit.getText().toString())) {
				showToast(getResources().getString(R.string.business_num_not_empty));
				return;
			}
			isbusyLayoutShow = true;
			getServerPath(shanghuEdit.getText().toString());
		}else {
			Server.BASE_URL = Server.DAYI;
			Log.i(TAG+": result base_url url", Server.BASE_URL);
			getSystemVersion();
		}
		
	}
	
	/**
	 * 登录
	 */
	private void login(){
		
		if (ischeckVersionDialog) {
			showDialog();
		}
		// String cId = PushManager.getInstance().getClientid(getActivity());
		TelephonyManager tm = (TelephonyManager) mActivity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		String resolution = Utils.getResolution(mActivity);

		AjaxParams params = new AjaxParams();
		params.put("loginName", mName.getText().toString());
		params.put("password", mPwd.getText().toString());
		params.put("deviceType", Config.DEVICE_TYPE_ANDROID);
		params.put("deviceDetail", android.os.Build.MODEL);
		params.put("resolution", resolution);
		// params.put("deviceToken", cId);

		if (deviceId == null) {
			LogUtil.w(tag, "设备Id为空");
		} else {
			params.put("deviceId", deviceId);
		}

		Callback callback = new Callback(tag, getActivity()) {

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
				LogUtil.v(tag, "login info : " + t);
				
				if (isSuccess && data.get("token") != null) {
					//商务号栏可见需要进行存储
					if (shanghuEdit.getVisibility() == View.VISIBLE) {
						SPUtil.getDefault(mActivity).saveShangWuNum(shanghuEdit.getText().toString());
					}
					
					SPUtil.getDefault(mActivity).removeLocalErWeiMaPath();

					SPUtil.getDefault(mActivity).saveLoginName(mName.getText().toString());
					SPUtil.getDefault(mActivity).savePwd(mPwd.getText().toString());
					SPUtil.getDefault(mActivity).savePwdstatus("ok");
					String token = data.get("token").getAsString();
					SPUtil.getDefault(mActivity).setId(data.get("id").getAsString());
					SPUtil.getDefault(mActivity).saveToken(token);

					JsonObject user = data.get("user").getAsJsonObject();
					String storeID = user.get("storeId").getAsString();
					String storeName = user.get("storeName").getAsString();
					App.imgid = user.get("avatarId").getAsString();

					SPUtil.getDefault(mActivity).saveStoreName(storeName);
					
					if (user.has("number")) {
						App.mendianSQH = user.get("number").getAsString();//授权编码
					}
					if (user.has("phone")) {
						App.servicePhone = user.get("phone").getAsString();//客服电话
					}
					
					StoreInfo store = new StoreInfo();
					//storeName, storeID, mName.getText().toString(),mPwd.getText().toString()
					store.storeName = storeName;
					store.storeId = storeID;
					store.loginCount = mName.getText().toString();
					store.loginPwd = mPwd.getText().toString();
					store.baseUrl = Server.BASE_URL;
					store.shanghuNum = shanghuEdit.getText().toString();
					
					App.storeInfo = store;
					SPUtil.getDefault(mActivity).saveSId(storeID);

					StoreInfoOpetate storeInfoOpetate = StoreInfoOpetate.getInstance();
					//获取门店数据表中所有数据
					List<StoreInfo> storeList = storeInfoOpetate.getStoreInfoList(context,store.shanghuNum);
					
					if (null != storeList && storeList.size() > 0) {
						
						//如果数据中没有该门店信息，则添加
						if (!storeInfoOpetate.findOneStoreInfo(getActivity(), storeID,shanghuEdit.getText().toString())){
							storeInfoOpetate.saveStoreInfos(getActivity(), store);
						}
					}else {
						if (null != storeList && storeList.size() == 0) {
							storeInfoOpetate.saveStoreInfos(getActivity(), store);
							Log.i(TAG+"登录 存储门店信息成功", "success");
						}
					}
					
					Intent intent = new Intent(mActivity, MainActivity.class);
					mActivity.startActivity(intent);
					dimissDialog();
					mActivity.finish();

				} else {
					Message msg = new Message();
					msg.what = Code.FAILED;
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString()) ;
						dimissDialog();
					}else {
						showToast("登录失败");
					}
				}
			}
			
		};
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.LOGIN_URL, params, callback);
	}

	/**
	 * @param businessNum 商户号
	 */
	 
	private void getServerPath(String businessNum){
		
		showDialog("正在登录...");
		
		AjaxParams params = new AjaxParams();
		params.put("number", businessNum);
		
		Callback callback = new Callback(tag,getActivity()) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					busynessUrl = data.get("host").getAsString();
					Log.i(TAG+": busyness temp url", busynessUrl);
					
					if (busynessUrl.lastIndexOf("/") != busynessUrl.length()-1) {
						busynessUrl = busynessUrl +"/";
					}
					Server.BASE_URL = busynessUrl;
//					Server.BASE_URL = "http://10.1.15.108:8084/lili/";
					//Log.i(TAG+": result base_url url", Server.BASE_URL);
					mHandler.sendEmptyMessage(START_CHECK_VERSION);
				}else {
					if (data.has("msg")) 
						Toast.makeText(getActivity(), data.get("msg").getAsString(), Toast.LENGTH_SHORT).show();
					else 
					showDialog("获取商户号失败");	
					
					dimissDialog();
				}
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BUSINESS_URL, params, callback);
	}
}