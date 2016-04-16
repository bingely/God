package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  修改授权密码 之   修改授权密码
 *  
 * @author 肖建斌
 *
 */
public class ModifySqmPassFragment extends BaseFragment{
	/**
	 * back键
	 */
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	/**
	 * 标题
	 */
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	/**
	 * 老密码
	 */
	@ViewInject(id = R.id.old_sqm_pass_edit)
	EditText mOldPswEditText;
	
	/**
	 * 新密码
	 */
	@ViewInject(id = R.id.new_sqm_pass)
	EditText mNewPswEditText;
	
	/**
	 * 确认密码
	 */
	@ViewInject(id = R.id.confim_sqm_pwd)
	EditText mConfirmPswEditText;
	
	/**
	 * 确认修改
	 */
	@ViewInject(id = R.id.btn_sqm_pwd_ok,click ="sureModifyPwd")
	TextView sureView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mActivity = this.getActivity(); 
		View rootView = inflater.inflate(R.layout.fragment_modify_sqm_pass, container, false);
		FinalActivity.initInjectedView(this, rootView);
		mTitle.setText("修改授权密码");
		
		return rootView;
	}
	
	/**
	 * 确认修改密码
	 */
	public void sureModifyPwd(View view){
		
		if (TextUtils.isEmpty(mOldPswEditText.getText().toString())) {
			showToast("旧密码不能空");
			return;
		}else{
			
			if (TextUtils.isEmpty(mNewPswEditText.getText().toString())) {
				showToast("请输入新密码");
				return;
			}
			
			if (TextUtils.isEmpty(mConfirmPswEditText.getText().toString())) {
				showToast("请输入确认密码");
				return;
			}
			
			if (!mNewPswEditText.getText().toString().equals(mConfirmPswEditText.getText().toString())) {
				showToast("新密码与确认密码不一致");
				return;
			}
			
			
			showDialog();
			modifySQMPass();
		}
		
	
	}
	
	
	/**
	 * 修改授权密码
	 */
	private void modifySQMPass(){
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		
		params.put("oldpwd", mOldPswEditText.getText().toString());
		params.put("newpwd", mNewPswEditText.getText().toString());
		
		Callback callback = new Callback(tag,getActivity()) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				String msgStr = data.get("msg").getAsString();
				Toast.makeText(getActivity(), msgStr, Toast.LENGTH_SHORT).show();
				dimissDialog();
				
				if (data.get("ret").getAsBoolean()) {
					getActivity().finish();
				}
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.MODIFY_SHOUQUANMA_PWD, params, callback);
		
	}
	
	/**
	 * 返回
	 * @param view
	 */
	public void homeClick(View view){
		
		getActivity().getSupportFragmentManager().popBackStack();
		
	}

}