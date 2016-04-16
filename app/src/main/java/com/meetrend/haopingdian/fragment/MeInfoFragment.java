package com.meetrend.haopingdian.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.AddPictrueActivity;
import com.meetrend.haopingdian.activity.LoginUserInfoActivity;
import com.meetrend.haopingdian.bean.MeDetail;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
/**
 * 个人信息
 * 
 * @author 肖建斌
 *
 */
public class MeInfoFragment extends BaseFragment {
	
	private static final Pattern PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
	private final int WHAT_PIC_SUCCESS = 100;

	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	@ViewInject(id = R.id.my_photo)
	SimpleDraweeView mMyPhoto;
	@ViewInject(id = R.id.my_name)
	EditText mMyName;
	@ViewInject(id = R.id.my_phone)
	EditText mMyPhone;
	@ViewInject(id = R.id.my_sex)
	TextView mMySex;
	@ViewInject(id = R.id.my_login_name)
	TextView mMyLoginName;
	@ViewInject(id = R.id.loginname_lyt)
	LinearLayout mMyLoginNameLyt;
	@ViewInject(id = R.id.divider_1_)
	View mDivider1;
	@ViewInject(id = R.id.login_layout)
	LinearLayout loginLayout;
	
	private boolean isModify = false;
	private Resources resources = null;
	private MeDetail mMe = null;;
	private String userid;
	
	//来自聊天列表
	private boolean isFromChat;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_myinfo, container, false);
		FinalActivity.initInjectedView(this, rootView);
		resources = mActivity.getResources();
		
		if (getArguments() != null) {
			
			Bundle bundle = getArguments();
			userid = bundle.getString("id");
			//请求
			getInfos(userid);
			
			if (!SPUtil.getDefault(getActivity()).getId().equals(userid)) {
				loginLayout.setVisibility(View.GONE);	
			}
			
			isFromChat = true;
			
		} else {
			mMe = App.meDetail;
			initView();
			initData();
		}
		
		
		return rootView;
	}
	
	public void initView(){
	
		if (isFromChat) {
			if (SPUtil.getDefault(getActivity()).getId().equals(userid)) {
				mBarAction.setVisibility(View.VISIBLE);
			}else {
				mBarAction.setVisibility(View.GONE);
			}
		}

		mBarTitle.setText(R.string.myinfo);
		mBarAction.setText("编辑");


		mMyName.setEnabled(false);
		mMyPhone.setEnabled(false);


		if(mMyName.getText()!=null){
			mMyName.setSelection(mMyName.getText().toString().length());
		}
		//mMyLoginNameLyt.setVisibility(View.GONE);
		//mDivider1.setVisibility(View.GONE);
	}
	
    
	public void initData(){

		if(mMe!=null){
			mMyName.setText(mMe.userName);
			if (null != mMe.userName && mMe.userName.length() >0){
				mMyName.setSelection(mMe.userName.length());
			}
			mMyLoginName.setText(mMe.loginName);
			mMyPhone.setText(mMe.mobile);

			if(mMe.gender.equals("1")){
				mMySex.setText("男");
			}else if(mMe.gender.equals("2")){
				mMySex.setText("女");
			}else{
				mMySex.setText("保密");
			}
			mMyPhoto.setImageURI(Uri.parse( Server.BASE_URL + mMe.avatarId));
		}

	}

	public void onClickHome(View v){
		mActivity.finish();
	}
	
	/**
	 * 编辑 或者 完成
	 * */
	public void onClickAction(View v){

		Intent intent = new Intent(getActivity(), LoginUserInfoActivity.class);
		intent.putExtra("imgurl",mMe.avatarId);
		intent.putExtra("name",mMe.userName);
		intent.putExtra("loginname", mMe.loginName);
		intent.putExtra("mobile",mMe.mobile);
		intent.putExtra("malestr", mMySex.getText().toString());
		//startActivityForResult(intent, 0X333);
		startActivity(intent);
		getActivity().finish();
	}

	public void getInfos(String userid) {
		
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("userId", userid);
		
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				if (null != strMsg) {
					showToast(strMsg);
				}
				dimissDialog();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
            	
				dimissDialog();
				
				if (isSuccess) {
					
			    	Gson gson = new Gson();
					mMe = gson.fromJson(data.toString(), MeDetail.class);
					initView();
					initData();
					
				} else {
					if (data.has("msg")) {
						Toast.makeText(getActivity(), data.get("msg").toString(), Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(getActivity(), "个人信息加载失败", Toast.LENGTH_SHORT).show();
					}
				}			
				
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_MY_INFOS, params, callback);
	}

}