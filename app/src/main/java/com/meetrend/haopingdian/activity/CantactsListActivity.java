package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.MemberListFragment;
import com.meetrend.haopingdian.tool.Code;


public class CantactsListActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.setContentView(R.layout.activity_fragment);
		
		int mode;
		int groupMode;
	     List<String> memberIdList = new ArrayList<String>();
		//int fromactivity = -1;
		
		try {
			 mode = getIntent().getIntExtra(Code.MODE, -1);//显示模式
		} catch (Exception e) {
			e.printStackTrace();
			mode = -1;
		}
		
		try {
			groupMode = getIntent().getIntExtra(Code.GROUP_MODE, -1);//是否是店小二
		} catch (Exception e) {
			e.printStackTrace();
			groupMode = -1;
		}
		
		try {
			//fromactivity = getIntent().getIntExtra(Code.FROM_TYPE, -1);//标识从添加成员界面跳转过来
			memberIdList = getIntent().getStringArrayListExtra("memberIdList");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Fragment fragment = new MemberListFragment();
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putInt(Code.MODE, mode);
		bundle.putInt(Code.GROUP_MODE, groupMode);
		bundle.putStringArrayList("memberIdList", (ArrayList<String>) memberIdList);
		//bundle.putInt(Code.FROM_TYPE, fromactivity);
		fragment.setArguments(bundle);
		ft.add(R.id.frame_container, fragment);
		ft.commit();
	}

}