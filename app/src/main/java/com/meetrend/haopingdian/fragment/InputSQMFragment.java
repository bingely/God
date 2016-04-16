package com.meetrend.haopingdian.fragment;


import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.SendEnableMsgEvent;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.UnableEditTextCopyUtil;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 授权码
 * @author 肖建斌
 *
 */
public class InputSQMFragment extends BaseFragment{
	
	/**
	 * 返回
	 */
	@ViewInject(id = R.id.actionbar_home, click = "back")
	ImageView backImageView;
	
	/**
	 * 标题
	 */
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	
	private static final int LENGTH = 4;
	private TextView[] password = new TextView[LENGTH];
	private EditText dymPassEdit;
	
	private int fromType = -1;//标识从哪个界面跳转的 1 标识从GiveProductSqlActivity进来
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inputsqm, container, false);
		FinalActivity.initInjectedView(this, view);
		
		titleView.setText("授权码");
		
		if (getArguments() != null) {
			fromType = 1;
		}else {
			//强制显示软键盘
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput( InputMethodManager.SHOW_IMPLICIT, 0 );
		}
		
		dymPassEdit = (EditText) view.findViewById(R.id.editpass);
		UnableEditTextCopyUtil.setEditHide_Copy_Paste_attr(dymPassEdit);
		password[0] = (TextView) view.findViewById(R.id.one);
		password[1] = (TextView) view.findViewById(R.id.two);
		password[2] = (TextView) view.findViewById(R.id.three);
		password[3] = (TextView) view.findViewById(R.id.four);
		
		dymPassEdit.requestFocus();
		
	
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		OnClickListener listner = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) dymPassEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				dymPassEdit.requestFocus();
				
			}
		};

		for (int i = 0; i < password.length; i++) {
			
			password[i].setOnClickListener(listner);
		}
		
		dymPassEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				
				if (s.toString().length() > 4) {
					return;
				}

				if (dymPassEdit.getText().length() == 4) {
					
					processToShowShouQuanMa(dymPassEdit.getText().toString());
				} 

				String text = dymPassEdit.getText().toString();

				for (int i = 0; i < 4; i++) {
					password[i].setText("");
				}

				for (int i = 0; i < text.length(); i++) {
					password[i].setText(".");
				}
				dymPassEdit.requestFocus();
			}
		});		
	}
	
	public void back(View view){
		
		//强制隐藏软键盘
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput( InputMethodManager.HIDE_IMPLICIT_ONLY, 0 );
		
		if (fromType == 1) {
			getActivity().finish();
		}else {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}
	
	/**
	 * 校验授权码是否正确
	 */
	private void processToShowShouQuanMa(String sqm){
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("code", sqm);
		
		
		Callback callback = new Callback(tag,getActivity()) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					if (data.get("ret").getAsBoolean()) {
						if (fromType != 1) {
							EventBus.getDefault().post(new SendEnableMsgEvent());
							getActivity().getSupportFragmentManager().popBackStack();
						}else {
							//发送消息，更新列表
							//EventBus.getDefault().post(new GiftRefreshEvent() );
							EventBus.getDefault().post(new TeaAddEvent(true) );
							getActivity().finish();
						}
						
					}else {
						showToast("授权码错误");
						dymPassEdit.setText("");//清零
					}
				}else {
					dymPassEdit.setText("");//清零
				}
				
				dimissDialog();
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.JADGE_SHOUQUANMA, params, callback);
	}
}