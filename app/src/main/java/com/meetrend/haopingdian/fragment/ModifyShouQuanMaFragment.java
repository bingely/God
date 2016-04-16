package com.meetrend.haopingdian.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.meetrend.haopingdian.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author 肖建斌
 *
 * 修改授权码密码
 */
public class ModifyShouQuanMaFragment extends BaseFragment{
	
	
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
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		        View rootView = inflater.inflate(R.layout.fragment_modify_shouquanma, container, false);
		        FinalActivity.initInjectedView(this,rootView);
				
				titleView.setText("修改授权码密码");
				
		        
		return rootView;
	}
	
	public void back(View view){
		getActivity().getSupportFragmentManager().popBackStack();
	}
}