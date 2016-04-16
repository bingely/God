package com.meetrend.haopingdian.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.MemberChangeEvent;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.StringUtils;

import de.greenrobot.event.EventBus;

/**
 * 手动添加会员
 *
 */
public class MemberAddActivity extends BaseActivity {
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.actionbar_action, click = "actionClick")
	TextView mAction;
	//body
	@ViewInject(id = R.id.et_member_name)
	EditText mName;
	@ViewInject(id = R.id.et_member_phonenumber)
	EditText mPhone;
	TextView mSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_member_add);
		FinalActivity.initInjectedView(this);
		mTitle.setText("手动添加");
		mAction.setText("添加");
	}


	public void homeClick(View v) {
		this.finish();
	}
	
	public void actionClick(View v) {
		String name = mName.getText().toString();
		String phoneNumber = mPhone.getText().toString();
		
		if (TextUtils.isEmpty(name)) {
			showToast("姓名不能为空");
			return;
		}

		if (TextUtils.isEmpty(phoneNumber)) {
			showToast(R.string.str_not_allow_empty_phone_number);
			return;
		}
		
		if (!StringUtils.judageMobile(phoneNumber)) {
			showToast("手机号码格式不正确");
			return;
		}

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(MemberAddActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(MemberAddActivity.this).getStoreId());
		params.put("name", name);
		params.put("ageGroup", "1");
		params.put("gender","1");
		params.put("mobile", phoneNumber);
		
		Callback callback = new Callback(tag,this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (!isSuccess) {
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
					}else {
						showToast("添加失败");
					}
					
					return;
				}
				
				EventBus.getDefault().post(new RefreshEvent());
				showToast("添加会员成功");
				finish();
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
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.IMPORT_MEMBER_URL_NO2, params, callback);
	}
}