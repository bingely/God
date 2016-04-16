package com.meetrend.haopingdian.activity;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.JsonObject;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.StoreChangeListAdapter;
import com.meetrend.haopingdian.bean.StoreInfo;
import com.meetrend.haopingdian.env.Config;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.memberlistdb.StoreInfoOpetate;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.DaYiActivityManager;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.Utils;
import com.meetrend.haopingdian.widget.MyListView;
import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 门店切换
 * @author 肖建斌
 *
 */
public class StroeChangeActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	//
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	@ViewInject(id = R.id.tv_other_store, click = "onClickExit")
	TextView mOtherStore;
	//
	@ViewInject(id = R.id.lv_store_list)
	MyListView mStoreListView;
	
	private SPUtil mUtil;
	
	private StoreChangeListAdapter mAdapter ;
	
	private List<StoreInfo> storeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_store_change);
		FinalActivity.initInjectedView(this);
		
		mBarTitle.setText("门店切换");
		mUtil = SPUtil.getDefault(StroeChangeActivity.this);
		
		storeList = StoreInfoOpetate.getInstance().getStoreInfoList(StroeChangeActivity.this,App.storeInfo.shanghuNum);
		
		mStoreListView.setAdapter(mAdapter = new StoreChangeListAdapter(this,storeList,App.storeInfo.storeId+App.storeInfo.shanghuNum));
		
		mStoreListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				StoreInfo storeInfo = storeList.get(storeList.size()-1-position);
				mAdapter.setFlag(storeInfo.storeId + storeInfo.shanghuNum);
				mAdapter.notifyDataSetChanged();
				
				if (storeInfo.storeId.equals(App.storeInfo.storeId) 
						&& storeInfo.shanghuNum.equals(App.storeInfo.shanghuNum)) {
					showToast("已经是当前门店");
					return;
				}
				changeStore(storeInfo);
			}
		});
	}
	
	
	//退出登录
	public void onClickExit(View v){
		mUtil.clearLoginInfo();
		mUtil.clearToken();
		DaYiActivityManager.getWeCareActivityManager().popAllActivityExceptOne(null);
		StroeChangeActivity.this.startActivity(new Intent(StroeChangeActivity.this,AccountActivity.class));
	}
	
	
	/**
	 * 切换门店
	 *
	 */
	
	private void changeStore(final StoreInfo mstoreInfo){
		//showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("loginName", mstoreInfo.loginCount);
		params.put("password", mstoreInfo.loginPwd);
		params.put("deviceType", Config.DEVICE_TYPE_ANDROID);
		params.put("deviceDetail", android.os.Build.MODEL);
		params.put("resolution", Utils.getResolution(StroeChangeActivity.this));
		
		TelephonyManager tm = (TelephonyManager) StroeChangeActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		
		if (null == deviceId) { 
			LogUtil.w(tag, "设备Id为空"); 
		} else {
			params.put("deviceId", deviceId);
		}
		
		Callback callback = new Callback(tag,StroeChangeActivity.this) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				if (null != strMsg) {
					showToast(strMsg);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess && data.get("token") != null) {
					
					JsonObject user = data.get("user").getAsJsonObject();
					App.imgid = user.get("avatarId").getAsString();
					String storeID = user.get("storeId").getAsString();
					
					App.storeInfo =mstoreInfo;
					
					mUtil.removeLocalErWeiMaPath();
					
					mUtil.saveErweiMaPath("");//清空二维码本地的路径
					mUtil.saveLoginName(mstoreInfo.loginCount);
					mUtil.savePwd(mstoreInfo.loginPwd);
					mUtil.savePwdstatus("ok");
					String token = data.get("token").getAsString();
					mUtil.setId(data.get("id").getAsString());
					mUtil.saveToken(token);
					mUtil.saveSId(storeID);
					mUtil.saveShangWuNum(mstoreInfo.shanghuNum);//商户号
					
					App.imgid = user.get("avatarId").getAsString();
					mUtil.saveStoreName(mstoreInfo.storeName);
					
					if (user.has("number")) {
						App.mendianSQH = user.get("number").getAsString();//授权编码
					}
					if (user.has("phone")) {
						App.servicePhone = user.get("phone").getAsString();//客服电话
					}
					DaYiActivityManager.getWeCareActivityManager().popAllActivityExceptOne(null);
					Intent intent = new Intent(StroeChangeActivity.this, MainActivity.class);
					StroeChangeActivity.this.startActivity(intent);
					finish();
					
				} else {
					
					String mMsg = data.get("msg").getAsString();
					if(!TextUtils.isEmpty(mMsg)){
						showToast(mMsg);
						mUtil.clearLoginInfo();
						mUtil.clearToken();
						DaYiActivityManager.getWeCareActivityManager().popAllActivityExceptOne(null);
						StroeChangeActivity.this.startActivity(new Intent(StroeChangeActivity.this,AccountActivity.class));
					}
					
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		Server.BASE_URL = mstoreInfo.baseUrl;
		finalHttp.get(Server.BASE_URL + Server.LOGIN_URL, params, callback);
	}

	public void onClickHome(View v){
		StroeChangeActivity.this.finish();
	}

}