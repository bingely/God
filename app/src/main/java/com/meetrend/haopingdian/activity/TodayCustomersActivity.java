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
import com.meetrend.haopingdian.adatper.TodayCustomersAdapter;
import com.meetrend.haopingdian.bean.OrderBean;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.DateUtil;
import com.meetrend.haopingdian.util.LineChartViewUtil;
import com.meetrend.haopingdian.widget.LineChartView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 今日访客
 * @author 肖建斌
 *
 */
public class TodayCustomersActivity extends BaseActivity{
	
	
	@ViewInject(id = R.id.actionbar_home,click ="finishActivity")
	ImageView back;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	
	@ViewInject(id = R.id.currentTime_textview)
	TextView curentTimeView;
	
	@ViewInject(id = R.id.mplineview)
	LineChartView mChart;

	@ViewInject(id = R.id.group_radio_btns)
	RadioGroup radioGroup;
	
	@ViewInject(id = R.id.mylistview)
	ListView listview;
	@ViewInject(id = R.id.currentTime_textview)
	TextView currentTimeTv;
	
	private List<OrderBean> mList;
	private TodayCustomersAdapter customersAdapter;
	
	//数据的长度
	int length;
	private int lengthPosition = 1;
	
	private HashMap<String, OrderBean> hashMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todayorderlist);
		FinalActivity.initInjectedView(this);
		
		showDialog();
		hashMap = new HashMap<String, OrderBean>();
		
		titleView.setText("今日访客");
		//当前日期
		curentTimeView.setText("当前时间：" + DateUtil.getCurrentDate());
		
		mList = new ArrayList<OrderBean>();
		customersAdapter = new TodayCustomersAdapter(mList,TodayCustomersActivity.this);
		listview.setAdapter(customersAdapter);
		
		requestData("90", 1);
		
		//radioGroup的监听，天数的确定
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				length = 7;//初始化
				
				switch (checkedId) {
				case R.id.button1:
					
					lengthPosition = 1;
					dealData();
					
					break;
				case R.id.button2:
					
					lengthPosition = 2;
					dealData();
					
					break;
				case R.id.button3:
					
					lengthPosition = 3;
					dealData();
					
					break;

				default:
					break;
				}
				
			}
		});
	}
	
    
    private List<OrderBean> sevenList;
    private List<OrderBean> thirtyList;
    private List<OrderBean> nityList;
    
    List<OrderBean> resultList;
	
    private void dealData(){

    	if (lengthPosition == 1) {
   		 
    		if (null == sevenList) {
				sevenList = getNumIncommeBeanList(findRecentLyDate(7), hashMap);
			}
    		
    		resultList = sevenList;
    		
    		//设置坐标点的y值
        	List<Double> yFloats = new ArrayList<Double>();
        	//设置坐标点的x值
        	List<String> xStrs = new ArrayList<String>();
        	for (OrderBean orderBean : resultList) {
        		yFloats.add(orderBean.Num);
        		xStrs.add(orderBean.Date);
    		}
        	
        	double maxValue = Collections.max(yFloats);
			double minValue = Collections.min(yFloats);
        	
        	mChart.setYData(yFloats);
			maxValue =  LineChartViewUtil.makeYKEDUMaxValue((long) maxValue,5);//最大值
        	mChart.setYMaxValue(maxValue);
        	mChart.setYMinValue(minValue);
        	mChart.setTime(xStrs);//时间中包含刷新mChat代码
        	
    		
		}else if (lengthPosition == 2) {
			
			if (null == thirtyList) {
				thirtyList = getNumIncommeBeanList(findRecentLyDate(30), hashMap);
			}
    		
    		resultList = thirtyList;
    		
    		//设置坐标点的y值
        	List<Double> yFloats = new ArrayList<Double>();
        	//设置坐标点的x值
        	List<String> xStrs = null;
        	for (OrderBean orderBean : resultList) {
        		yFloats.add(orderBean.Num);
    		}
        	
        	xStrs = getNumList(resultList);

			double maxValue = Collections.max(yFloats);
			double minValue = Collections.min(yFloats);
        	
        	mChart.setYData(yFloats);
			maxValue =  LineChartViewUtil.makeYKEDUMaxValue((long) maxValue,5);//最大值
        	mChart.setYMaxValue(maxValue);
        	mChart.setYMinValue(minValue);
        	mChart.setTime(xStrs);//时间中包含刷新mChat代码
			
			
		}else {
			if (null == nityList) {
				nityList = getNumIncommeBeanList(findRecentLyDate(90), hashMap);
			}
    		
    		resultList = nityList;
    		
    		//设置坐标点的y值
        	List<Double> yFloats = new ArrayList<Double>();
        	//设置坐标点的x值
        	List<String> xStrs = null;
        	for (OrderBean orderBean : resultList) {
        		yFloats.add(orderBean.Num);
    		}
        	
        	xStrs = getNumList(resultList);
        	
        	double maxValue = Collections.max(yFloats);
        	double minValue = Collections.min(yFloats);
        	
        	mChart.setYData(yFloats);
			maxValue =  LineChartViewUtil.makeYKEDUMaxValue((long) maxValue,5);//最大值
        	mChart.setYMaxValue(maxValue);
        	mChart.setYMinValue(minValue);
        	mChart.setTime(xStrs);//时间中包含刷新mChat代码
		}
	}
    
	public  List<String> getNumList(List<OrderBean> timeList){
		
		List<String> list = new ArrayList<String>();
		
		int size = timeList.size();
		for (int i = 0; i < 7; i++) {
			
			if (i == 0) {
				list.add(timeList.get(0).Date);
				continue;
			}
			
			if (size == 30) {
				list.add(timeList.get(i*5-1).Date);
				
			}else if (size == 90) {
				list.add(timeList.get(i*15-1).Date);
			}
			
		}
		
		return list;
	}
	
	
	
	private void requestData(String pageSize,int pageIndex){
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(TodayCustomersActivity.this).getToken());
		params.put("pageInedx", pageIndex + "");
		params.put("pageSize",pageSize);
		
		Callback callback = new Callback(tag, TodayCustomersActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				
				if (isSuccess) {
					
					String jsonArrayStr = data.get("objArray").toString();
					Gson gson = new Gson();
					List<OrderBean> tempList = gson.fromJson(jsonArrayStr,new TypeToken<List<OrderBean>>() {}.getType());
					mList.addAll(tempList);
					
					for (OrderBean orderBean : mList) {
						hashMap.put(orderBean.Date, orderBean);
					}
					lengthPosition = 2;
					dealData();
					
					customersAdapter.notifyDataSetChanged();
					ListViewUtil.setListViewHeightBasedOnChildren(listview);
					
				} else {
					
					dimissDialog();
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						Toast.makeText(TodayCustomersActivity.this, msg, Toast.LENGTH_SHORT).show();
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
		finalHttp.get(Server.BASE_URL + Server.TODAY_CUSTOMERS, params,callback);
	}
	
	 /**
	   * 
	   * @param dymList 前 多少天
	   * @param mHashMap 原始数据转成的HashMap
	   * @return
	   */
	  private List<OrderBean> getNumIncommeBeanList(List<OrderBean> dymList,HashMap<String, OrderBean> mHashMap){
		  
		  int size = dymList.size();
		  
		  for (int j = 0; j < size; j++) {
			  
			  OrderBean orderBean = dymList.get(j);
			  if (null == mHashMap.get(orderBean.Date)) {
				  orderBean.Num = 0.0f;
			  }else {
				  OrderBean orderBean2 = mHashMap.get(orderBean.Date);
				  orderBean.Date = orderBean2.Date;
				  orderBean.Num = orderBean2.Num;
			  }
		 }
		  
		  return dymList;
	  }
	  
	  /**
	   * 
	   * @param num 最近的天数
	   */
	  private List<OrderBean> findRecentLyDate(int num){
		  	
		    List<OrderBean> mList = new ArrayList<OrderBean>();
		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        
	        for (int i = num ; i > 0; i--) {
	        	Calendar c = Calendar.getInstance();  
	        	c.add(Calendar.DATE, 1- i);
	 	        Date date = c.getTime();
	 	        String dateStr = sdf.format(date);
	 	        OrderBean inCommeBean = new OrderBean();
	 	        inCommeBean.Date = dateStr;
	 	        mList.add(inCommeBean);
			}
	        
	        return mList;
	  }
	
	public void finishActivity(View view){
		finish();
	}


}