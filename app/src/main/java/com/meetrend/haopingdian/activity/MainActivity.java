package com.meetrend.haopingdian.activity;


import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.ExecutorEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.StoreChangeEvent;
import com.meetrend.haopingdian.fragment.BaseFragment;
import com.meetrend.haopingdian.fragment.MemberListFragment;
import com.meetrend.haopingdian.fragment.MessageListFragment;
import com.meetrend.haopingdian.fragment.OrderManagerFragment;
import com.meetrend.haopingdian.fragment.SettingFragment;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.Utils;
import com.meetrend.haopingdian.util.DialogUtil;
import com.meetrend.haopingdian.util.DialogUtil.FinishListener;
import com.meetrend.haopingdian.widget.ScrollLayout;
import com.meetrend.haopingdian.widget.UIToggleButton;
import com.meetrend.tea.db.DbOperator;


public class MainActivity extends BaseActivity {
	
	private final static String TAG = MainActivity.class.getName();

	private static final int SIZE = 4;
	private int curIndex = 0;
	private BaseFragment[] fragments = new BaseFragment[SIZE];
	private UIToggleButton[] btns = new UIToggleButton[SIZE];

	@ViewInject(id = R.id.uitb_msg,click = "btnClick")
	UIToggleButton msgBtn;
	@ViewInject(id = R.id.uitb_member,click = "btnClick")
	UIToggleButton memberBtn;
	@ViewInject(id = R.id.uitb_order,click = "btnClick")
	UIToggleButton orderBtn;
	@ViewInject(id = R.id.uitb_inventory,click = "btnClick")
	UIToggleButton inventoryBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		FinalActivity.initInjectedView(this);

		fragments[0] = new MessageListFragment();
		fragments[1] = new MemberListFragment();
		fragments[2] = new OrderManagerFragment();
		fragments[3] = new SettingFragment();
		btns[0] = msgBtn;
		btns[1] = memberBtn;
		btns[2] = orderBtn;
		btns[3] = inventoryBtn;

		btns[curIndex].setChecked(true);
		this.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.container_main, fragments[curIndex])
				.commitAllowingStateLoss();

		getExecutor();
	}

	//选项卡点击事件
	public void btnClick(View v) {
		FragmentTransaction ft = MainActivity.this.getSupportFragmentManager().beginTransaction();
		ft.hide(fragments[curIndex]);
		btns[curIndex].setChecked(false);

		switch (v.getId()) {
			case R.id.uitb_msg:
				curIndex = 0;
				break;
			case R.id.uitb_member:
				curIndex = 1;
				break;
			case R.id.uitb_order:
				curIndex = 2;
				break;
			case R.id.uitb_inventory:
				curIndex = 3;
				break;
		}

		btns[curIndex].setChecked(true);
		BaseFragment fragment = fragments[curIndex];
		if (!fragment.isAdded()) {
			ft.add(R.id.container_main, fragment);
		}
		ft.show(fragment).commitAllowingStateLoss();
	}
	
	public void getExecutor() {
		App.executorList = new ArrayList<ExecutorEntity>();
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(MainActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(MainActivity.this).getStoreId());

		AjaxCallBack<String> callback = new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				
				Log.i(TAG +"执行人信息", t);
				
				JsonParser parser = new JsonParser();
				Gson gson = new Gson();
				JsonObject root = parser.parse(t).getAsJsonObject();
				boolean isSuccess = Boolean.parseBoolean(root.get("success").getAsString());

				if (isSuccess) {
					JsonObject data = root.get("data").getAsJsonObject();
					String jsonArrayStr = data.get("jsonArray").toString();
					List<ExecutorEntity> list = gson.fromJson(jsonArrayStr, new TypeToken<List<ExecutorEntity>>() {
					}.getType());
					if("[{}]".equals(jsonArrayStr)){
						list = new ArrayList<ExecutorEntity>();
					}
					App.executorList.addAll(list);
				}
				//将执行人数据保存值数据库
				DbOperator dbOperator = DbOperator.getInstance();
				//执行删除操作
				dbOperator.clearTable(MainActivity.this);
				dbOperator.saveExecutors(MainActivity.this, App.executorList);

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.EXECUTOR_LIST_URL, params, callback);
	}


	@Override
	public void onBackPressed() {
		
		DialogUtil dialog = new DialogUtil(MainActivity.this);
		dialog.showPhoneDialog("确定退出程序?", "取消", "确定");
		dialog.setListener(new FinishListener() {
			
			@Override
			public void finishView() {
				MainActivity.this.finish();
			}
		});
		
	}
}