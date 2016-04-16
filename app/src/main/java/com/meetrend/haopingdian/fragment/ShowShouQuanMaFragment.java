package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ShouQuanMaActivity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author joy
 * 
 * 获取授权码界面
 *
 */
public class ShowShouQuanMaFragment extends BaseFragment{
	
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
	/**
	 * 修改密码
	 */
	@ViewInject(id = R.id.actionbar_action)
	TextView modifyPassView;
	
	/**
	 * 重新生成按钮
	 */
	@ViewInject(id = R.id.get_code_btn)
	Button reCreateBtn;
	
	/**
	 * 显示授权码控件
	 */
	@ViewInject(id = R.id.show_code_view)
	TextView showCodeView;
	
	/**
	 * 获取授权码密码
	 */
	private String SQPass;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		
		        View rootView = inflater.inflate(R.layout.fagment_show_shouquanma, container, false);
		        FinalActivity.initInjectedView(this,rootView);
				
				titleView.setText("授权码");
				modifyPassView.setText("修改密码");
				
				if (null != getArguments()) {
					String code = getArguments().getString("sqcode");
					showCodeView.setText(code);
					SQPass = getArguments().getString("pass");
				}
				
		        
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//重新获取授权码
		reCreateBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					reCreateSQMa(SQPass);
			}
		});
		
		modifyPassView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getActivity().getSupportFragmentManager()
				.beginTransaction()
				.addToBackStack(null)
				.add(R.id.shoquanma_container, new ModifySqmPassFragment())
				.commit();
			}
		});
	}
	
	
	/**
	 * 修改密码
	 * @param view
	 */
	public void modifyPassWard(View view){
		
		getActivity().getSupportFragmentManager()
		.beginTransaction()
		.addToBackStack(null)
		.add(R.id.shoquanma_container, new ModifyShouQuanMaFragment())
		.commit();
		
	}
	
	
	
	/**
	 * 重新生成授权码
	 */
	private void reCreateSQMa(String passWard){
		showDialog();
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("password", passWard);
		params.put("getStatus", 1+"");//0默认，1重新获取
		
		  
		
		Callback callback = new Callback(tag,getActivity()) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				dimissDialog();

				if (isSuccess) {
					String shouQunCode = data.get("code").getAsString();//授权码
					showCodeView.setText(shouQunCode);
				}else{
					if (data.has("msg")){
						showToast(data.get("msg").getAsString());
					}
				}
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_SHOUQUANMA, params, callback);
	}
	
	public void back(View view){
		getActivity().finish();
	}
	
}