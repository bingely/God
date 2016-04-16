package com.meetrend.haopingdian.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.SignListAdapter;
import com.meetrend.haopingdian.bean.Sign;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshSignListEvent;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NetUtil;

import de.greenrobot.event.EventBus;

/**
 * 签到记录列表,签到
 * 
 * @author 肖建斌
 *
 */

public class SignRecordsActivity extends BaseActivity   implements OnGetGeoCoderResultListener{

	private final static String TAG = SignRecordsActivity.class.getSimpleName();
	public static final int LOCATION_SUCCESS = 0x128;
    public static final int LOCATION_FAIL = 0x129;

	private ImageButton signBtn;
	private TextView signNumView;
	private ImageView backView;
	private PullToRefreshListView listView;
	private ListView androidListView;
	private TextView monthView;
	private TextView dayView;
	//private TextView emptyView;

	private LocationClient locationClient = null;
	private double currentJindu = 0;
	private double currentWeidu = 0;

	private double companySignRadius;
	private String companySignJindu;
	private String companySignWeidu;
	private String companyName;// 公司名称

	private SignListAdapter signListAdapter;
	private ArrayList<Sign> signList;

	private boolean hasSuccess;
	private Calendar calendar;

	private int num;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signrecords);

		initLocation();
		initView();
		signList = new ArrayList<Sign>();
		signListAdapter = new SignListAdapter(SignRecordsActivity.this, signList);
		listView.setAdapter(signListAdapter);
		
		if (NetUtil.hasConnected(this)) {
			requestSignList();
		}else {
			showToast("网络连接异常，请检查网络");
		}
	}

	public void initView() {
		backView = (ImageView) this.findViewById(R.id.actionbar_home);
		TextView titleView = (TextView) this.findViewById(R.id.actionbar_title);
		titleView.setText("签到");
		TextView sureView = (TextView) this.findViewById(R.id.actionbar_action);
		sureView.setText("设置");
		signBtn = (ImageButton) this.findViewById(R.id.btn_sign_in);
		signNumView = (TextView) this.findViewById(R.id.sign_num_view);
		listView = (PullToRefreshListView) this.findViewById(R.id.id_sign_in_listview);
		androidListView = listView.getRefreshableView();//
		androidListView.setSelector(R.color.translate);
		listView.setMode(Mode.DISABLED);// 不可下拉刷新和上拉加载
		monthView = (TextView) this.findViewById(R.id.id_date_month);
		dayView = (TextView) this.findViewById(R.id.id_date_day);
		//emptyView = (TextView) this.findViewById(R.id.sign_empty_view);

		calendar = Calendar.getInstance();
		monthView.setText((calendar.get(Calendar.MONTH) + 1) + "月");
		dayView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
		sureView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignRecordsActivity.this, TimeRemindActivity.class);
				startActivity(intent);
			}
		});
		
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	private void initLocation() {

		locationClient = new LocationClient(this);
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 是否打开GPS
		option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
		option.setProdName("MeetrendCRM"); // 设置产品线名称。
		locationClient.setLocOption(option);

		// 注册位置监听器
		locationClient.registerLocationListener(new BDLocationListener() {

			//该方法会被多次调用
			@Override
			public void onReceiveLocation(BDLocation location) {

				if (location == null) {
					return;
				}

				try {
					if (!hasSuccess) {
						currentWeidu = location.getLatitude();
						currentJindu = location.getLongitude();
						mHandler.sendEmptyMessage(LOCATION_SUCCESS);
						Log.e("百度服务" + "-----", "正常");
						hasSuccess = true;
						locationClient.stop();
					}
				} catch (Exception e) {
					Toast.makeText(SignRecordsActivity.this, "百度服务异常", Toast.LENGTH_SHORT).show();

					e.printStackTrace();
					hasSuccess = true;
					mHandler.sendEmptyMessage(LOCATION_FAIL);
					Log.e("百度服务" + "-----", "异常");
				}
			}

		});
	}
	
	
	//刷新列表
	public void onEventMainThread(RefreshSignListEvent event) {
		if (signList.size() > 0) {
			signList.clear();
		}
		
		requestSignList();
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case LOCATION_SUCCESS:

				if (null == companySignWeidu || null == companySignJindu
						|| companySignWeidu.equals("0.00") || companySignJindu.equals("0.00")) {

					showToast("经纬度无效");
					dimissDialog();
					return;
				}

				double distance = 0.0;
				LatLng companyPoint = new LatLng(Double.parseDouble(companySignWeidu), Double.parseDouble(companySignJindu));
				LatLng currentPoint = new LatLng(currentWeidu, currentJindu);
				
				try {
					
				  distance = DistanceUtil.getDistance(companyPoint, currentPoint);
				
				} catch (Exception e) {
					e.printStackTrace();
					showToast("计算距离失败");
					dimissDialog();
				}

				if (distance > 0 && distance < companySignRadius) {
					
					//提交签到信息
					sendCurrentInfo();
					
				} else {
					
					//跳到下一个界面选择 位置地点
					Intent intent = new Intent(SignRecordsActivity.this, PoiSignListActivity.class);
					intent.putExtra("lat", currentWeidu);
					intent.putExtra("lon", currentJindu);
					startActivity(intent);
					dimissDialog();
				}
				
			
			

			break;
		case LOCATION_FAIL:
			SignRecordsActivity.this.showToast("获取经纬度失败");
			//signBtn.setEnabled(true);
			dimissDialog();

			break;

		default:
			break;
		}
	}


	// 签到功能
	public void sign(View view) {
		
		if (!NetUtil.hasConnected(this)) {
			showToast("网络连接异常，请检查网络");
			return;
		}
		
		if (locationClient == null) {
			showToast("百度地图出现异常");
			return;
		}

		if (hasSuccess == true) {
			hasSuccess = false;
		}
		
		showDialog("正在签到...");
		
		if (companySignJindu == null || companySignWeidu == null || companyName == null) {
			
			companySignJindu = SPUtil.getDefault(SignRecordsActivity.this).getMapJindu();
			companySignWeidu = SPUtil.getDefault(SignRecordsActivity.this).getMapWeidu();
			companyName = SPUtil.getDefault(SignRecordsActivity.this).getCName();
		}
		
			
		 locationClient.start();
	}

	/**
	 * 只显示今日签到数据
	 * */
	public void showTodaySignNum() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String today = simpleDateFormat.format(new Date());
		if (signList.size() == 0 || signList == null) {
			return;
		}

		for (Sign sign : signList) {
			String serverTime = sign.FCheckinTime.substring(0, 10);
			if (serverTime.equals(today)) {
				++num;
			}
		}
		signNumView.setText("今日共" + num + "条签到记录");
	}
	
	//获得签到列表的数据
	private void requestSignList() {
		
		showDialog();
		
		Callback callback = new Callback(tag,this) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
					if (isSuccess) {

						signBtn.setEnabled(true);

						companySignRadius = data.get("companyCheckRadius").getAsDouble();
						companyName = data.get("companyName").getAsString();//公司名称

						if(data.has("companyPoi")){
							String location = data.get("companyPoi").getAsString();// 公司经纬度
							String locations[] = location.split(",");
							companySignJindu = locations[0];
							companySignWeidu = locations[1];
						}

						SPUtil.getDefault(SignRecordsActivity.this).saveMapWeidu(companySignWeidu == null ?"0.00":companySignWeidu);
						SPUtil.getDefault(SignRecordsActivity.this).saveMapJindu(companySignJindu == null ? "0.00" : companySignJindu);
						SPUtil.getDefault(SignRecordsActivity.this).saveCName(companyName);

						JsonArray signArray = data.get("items").getAsJsonArray();
						if (null != signArray && signArray.size() != 0) {
							
							int size = signArray.size();
							for (int i = 0; i < size; i++) {
								JsonObject jsonObject = (JsonObject) signArray.get(i);
								String signId = jsonObject.get("FId").getAsString();
								String address = jsonObject.get("FLocation").getAsString();
								String signTime = jsonObject.get("FCheckinTime").getAsString();
								String des = jsonObject.get("FDesc").getAsString();
								Sign sign = new Sign(signId, address, des, signTime);
								signList.add(sign);
							}
						}
						
						signNumView.setText("今日共" + signList.size() + "条签到记录");
						if (null == signList || signList.size() == 0) {
							
							//if (emptyView.getVisibility() == View.GONE) {
							//	emptyView.setVisibility(View.VISIBLE);
							//}
							
						}else {
							
							//if (emptyView.getVisibility() == View.VISIBLE) {
							//	emptyView.setVisibility(View.GONE);
							//}
							desOrderList(signList);
							signListAdapter.notifyDataSetChanged();
						}
						
					} 
					dimissDialog();
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				signBtn.setEnabled(false);
			}
		};
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(SignRecordsActivity.this).getToken());
		CommonFinalHttp commonFinalHttp = new CommonFinalHttp();
		commonFinalHttp.post(Server.BASE_URL + Server.SIGN_LIST,params,callback);
	}
	
	//逆序排列签到信息
	private void desOrderList(List<Sign> list){
		class StrLenComp implements Comparator<Sign>  
		{  
		    public int compare(Sign sign1, Sign sign2)  
		    {   
		        return sign1.FCheckinTime.compareToIgnoreCase(sign2.FCheckinTime);  
		    }  
		}
		Collections.sort(list, Collections.reverseOrder(new StrLenComp()));
	}
	
	// 将当前位置信息发送至服务器
	public void sendCurrentInfo() {
		
		Callback callback = new Callback(tag,this) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					
					if (signList.size() > 0) {
						signList.clear();
					}
					
					dimissDialog();
					
					requestSignList();
					
				}else {
					dimissDialog();
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
					}
				}
					
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(SignRecordsActivity.this).getToken());
		params.put("location", companyName);
		params.put("lat", companySignWeidu);
		params.put("lng", companySignJindu);

		CommonFinalHttp commonFinalHttp = new CommonFinalHttp();
		commonFinalHttp.post(Server.BASE_URL + Server.SIGN_REQUEST, params, callback);
		
	}

	public void sureOperate(View view) {

	}

	public void finishActivity(View view) {
		SignRecordsActivity.this.finish();
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
		if (locationClient != null && locationClient.isStarted()) {
			locationClient.stop();
			locationClient = null;
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			dimissDialog();
			showToast("获取当前位置名称失败");
			return;
		}
		
		String address = result.getAddress();
		if (TextUtils.isEmpty(address)) {
			showToast("无法找到经纬度对应的地名");
		}
		dimissDialog();
		
	}

}