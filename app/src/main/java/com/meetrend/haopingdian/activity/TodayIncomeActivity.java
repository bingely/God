package com.meetrend.haopingdian.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.InCommeAdapter;
import com.meetrend.haopingdian.adatper.PayNumAdapter;
import com.meetrend.haopingdian.adatper.PriceAdapter;
import com.meetrend.haopingdian.bean.InCommeBean;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.event.RefreshIncommeEvent;
import com.meetrend.haopingdian.fragment.BaseFragment;
import com.meetrend.haopingdian.fragment.TodayBaseFragment;
import com.meetrend.haopingdian.memberlistdb.MemberListDbOperator;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.DateUtil;
import com.meetrend.haopingdian.util.LineChartViewUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.widget.CommonTabView;
import com.meetrend.haopingdian.widget.CommonTabView.CheckLinstener;
import com.meetrend.haopingdian.widget.LineChartView;
import com.meetrend.haopingdian.widget.LineChartView.ShowLinstener;
import com.umeng.socialize.utils.Log;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

/**
 * 今日收入
 * @author 肖建斌
 *
 */
public class TodayIncomeActivity extends BaseActivity implements CheckLinstener{
	
	private final static int DEALDATA = 1;
	
	@ViewInject(id = R.id.actionbar_home,click ="finishActivity")
	ImageView back;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	@ViewInject(id = R.id.comtabview)
	CommonTabView tabView;

//	@ViewInject(id = R.id.currentTime_textview)
//	TextView curentTimeView;
//
//	@ViewInject(id = R.id.mplineview)
//	LineChartView mChart;
//
//	@ViewInject(id = R.id.group_radio_btns)
//	RadioGroup radioGroup;
//
//	@ViewInject(id = R.id.mylistview)
//	ListView listview;
//	@ViewInject(id = R.id.currentTime_textview)
//	TextView currentTimeTv;
	
	
	private String[][] titleDdata = new String[][]{{"收入",""},{"支付笔数",""},{"客单价",""}};
	
	public static List<InCommeBean> mList;
	
	private InCommeAdapter mInCommeAdapter;
	private PayNumAdapter payNumAdapter;
	private PriceAdapter priceAdapter;
	
	private int typePosition = 1;
	private int lengthPosition = 1;
	
	private String requestPath;
	
	//原始数据转成的Map,数据保存不变
	public static HashMap<String, InCommeBean> hashMap;

	private int currentIndex = 0;
	private BaseFragment[] fragmentArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todayincome);
		EventBus.getDefault().register(this);
		FinalActivity.initInjectedView(this);

		titleView.setText("今日收入");
		showDialog("加载中...");
		requestPath = Server.BASE_URL + Server.TODAY_INCOMME;

		hashMap = new HashMap<String, InCommeBean>();
		mList = new ArrayList<InCommeBean>();

		//fragmentArray = new BaseFragment[3];
		//BaseFragment incommeFragment = new TodayBaseFragment();
		//BaseFragment paynumFragment = new TodayBaseFragment();
		//BaseFragment priceFragment = new TodayBaseFragment();

		//fragmentArray[0] = incommeFragment;
		//fragmentArray[1] = paynumFragment;
		//fragmentArray[2] = priceFragment;

		tabView.setCheckLintener(this);

		requestData("90", 1);
	}

	public void onEventMainThread(RefreshIncommeEvent event) {
		tabView.setFirstBottomViewValue(event.pay);
		tabView.setSecBottomViewValue(event.paynum);
		tabView.setThirdBottomViewValue(event.price);
	}

	//种类的确定
	@Override
	public void checkPosition(int position) {

		android.support.v4.app.FragmentTransaction ft = TodayIncomeActivity.this.getSupportFragmentManager().beginTransaction();
		//ft.hide(fragmentArray[currentIndex]);

		BaseFragment fragment = null;
		switch (position) {
			case 1:
				currentIndex = 0;
				fragment = new TodayBaseFragment();

				break;

			case 2:
				currentIndex = 1;
				fragment = new TodayBaseFragment();

				break;

			case 3:
				currentIndex = 2;
				fragment = new TodayBaseFragment();
				break;
		}

		//BaseFragment fragment = fragmentArray[currentIndex];
		//if (!fragment.isAdded()) {
			Bundle bundle = new Bundle();
			bundle.putInt(TodayBaseFragment.TYPE,currentIndex);
		    fragment.setArguments(bundle);
			ft.replace(R.id.incomme_container, fragment).commitAllowingStateLoss();
		//}

		//ft.show(fragment).commitAllowingStateLoss();
	}



	private void requestData(String pageSize,int pageIndex){

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(TodayIncomeActivity.this).getToken());
		params.put("pageInedx", pageIndex + "");
		params.put("pageSize",pageSize);

		Callback callback = new Callback(tag, TodayIncomeActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				Bundle bundle = new Bundle();
				bundle.putInt(TodayBaseFragment.TYPE,currentIndex);
				BaseFragment fragment = new TodayBaseFragment();
				fragment.setArguments(bundle);

				if (isSuccess) {

					float todayIncommeNum = data.get("todayNum").getAsFloat();
					int todayPayedNum = data.get("todayPayed").getAsInt();
					String todayAvgNum = data.get("AVG").getAsString();

					titleDdata[0][1] = todayIncommeNum + "";// NumerUtil.getNum(todayIncommeNum)
					titleDdata[1][1] = todayPayedNum + "";
					titleDdata[2][1] = todayAvgNum ;
					//标题显示
					tabView.setTopDadas(titleDdata);

					String jsonArrayStr = data.get("objArray").toString();
					Gson gson = new Gson();
					List<InCommeBean> tempList = gson.fromJson(jsonArrayStr,new TypeToken<List<InCommeBean>>() {}.getType());
					mList.addAll(tempList);

					//将数据保存至HashMap
					for (InCommeBean inCommeBean : mList) {
						hashMap.put(inCommeBean.Date, inCommeBean);
					}



					getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.incomme_container,fragment)
							.commitAllowingStateLoss();
				} else {

					titleDdata[0][1] = "0.00";
					titleDdata[1][1] = "0";
					titleDdata[2][1] = "0.00" ;
					//标题显示
					tabView.setTopDadas(titleDdata);

					getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.incomme_container,fragment)
							.commitAllowingStateLoss();

					dimissDialog();
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
					}
				}
				dimissDialog();
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
		finalHttp.get(requestPath, params,callback);
	}

	public void finishActivity(View view){
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}