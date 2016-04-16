package com.meetrend.haopingdian.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.platform.comapi.map.t;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.MapSendAddressEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.Base64Util;
import com.tencent.weibo.sdk.android.component.sso.tools.Base64;
import com.umeng.socialize.utils.Log;

import de.greenrobot.event.EventBus;

import android.R.array;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 百度地图
 * @author 肖建斌
 *
 */
public class MapActivity extends BaseActivity implements OnGetGeoCoderResultListener{
	
	private final static String TAG = MapActivity.class.getName();
	
	@ViewInject(id = R.id.actionbar_home,click = "backClick")
	ImageView backImageView;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	@ViewInject(id = R.id.actionbar_action,click = "sureClick")
	TextView sureView;
	@ViewInject(id = R.id.addressview)
	TextView addressView;
	
	MapView mMapView = null;
	BaiduMap mBaiduMap;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	// 定位相关
	public MyLocationListenner myListener = new MyLocationListenner();
	LocationClient mLocClient;
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;// 是否首次定位
	boolean isFistStuatus = true;//状态
	
	private String curentAddress = null;
	private String moveAddress = null;
	
	//纬度，经度
	private double mlat;
	private double mlong;
	
	//坐标
	private double x;
	private double y;
	
//	mBaiduMap.setOnMarkerClickListener marker的点击事件
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapview);
		FinalActivity.initInjectedView(this);
		
		init();
		
	}
	
	private void init(){
		titleView.setText("百度地图");
		sureView.setText("确定");
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.showZoomControls(false);// 隐藏放大缩小控件
		mMapView.setEnabled(true);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		mCurrentMode = LocationMode.NORMAL;
		//mCurrentMarker = BitmapDescriptorFactory
			//	.fromResource(R.drawable.current_addres_point_icon);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode,
				true, null));
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
	}
	
		OnMapStatusChangeListener onMapStatusChangeListener = new OnMapStatusChangeListener() {
			
			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				
			}
			
			//状态结束
			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				
					//获取百度地图的中心点
					LatLng latLng = arg0.target;
					double longitude = latLng.longitude;//经度
					double latitude = latLng.latitude;//维度
					
					LatLng ptCenter = new LatLng(latitude, longitude);
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
				
			}
			
			@Override
			public void onMapStatusChange(MapStatus arg0) {
				
			}
		};
		
		

	//确定
	public void sureClick(View view){
		
		baidu2Gps(mlong+"", mlat+"");
	}
	
	//结束Activity
	public void backClick(View view){
		finish();
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}

	//编码转成实际地址
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MapActivity.this, "亲，没找到该地址", 200).show();
			return;
		}
		mBaiduMap.setMyLocationConfigeration(null);
		mBaiduMap.clear();
//		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//				.icon(BitmapDescriptorFactory
//						.fromResource(R.drawable.current_addres_point_icon)));
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
//				.getLocation()));
		
		LatLng latLng = result.getLocation();
		mlat = latLng.latitude;//纬度
		mlong = latLng.longitude;//经度
		
		if (!TextUtils.isEmpty(result.getAddress())) {
			//Toast.makeText(MapActivity.this, result.getAddress(),100).show();
			moveAddress = result.getAddress();
			addressView.setText("地址："+moveAddress);
		}else {
			addressView.setText("地址：没有找到该地方名称");
		}
				
	}
	Marker marker = null;
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(0).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
				
				//新加
				//BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.current_addres_point_icon);
				//准备 marker option 添加 marker 使用
				//MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(ll);
				//获取添加的 marker ,可以触发点击事件获取位置
				//marker = (Marker) mBaiduMap.addOverlay(markerOptions);
				
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			
		}
	}
	
	/**
	 * 步骤1：
	 * @param bx
	 * @param by
	 */
	public void baidu2Gps(final String bx,final String by){
		
		showDialog("请稍后...");
		
		//http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=116.397428&y=39.90923&callback=BMap.Convertor.cbk_7594 //参考传参数方法
		String url = "http://api.map.baidu.com/ag/coord/convert";//固定不变
		
		AjaxParams params = new AjaxParams();
		params.put("from", "0");
		params.put("to", "4");
		params.put("x", bx);
		params.put("y", by);
		
		Callback callback = new Callback(tag,this) {

			@Override
			public void onSuccess(String t) {
				
				try {
					JsonParser parser = new JsonParser();
			    	JsonObject root = parser.parse(t).getAsJsonObject();
			    	String base64x = root.get("x").getAsString();
			    	String base64y = root.get("y").getAsString();
			    	
			    	
			    	double x1 = Double.parseDouble(bx);
			    	double y1 = Double.parseDouble(by);
			    	
			    	//Log.i(TAG +"=======x1===============", x1+"");
			    	//Log.i(TAG +"=======y1===============", y1+"");
			    	
			    	String tempx = new String(Base64Util.decode(base64x));
			    	String tempy = new String(Base64Util.decode(base64y));
			    	
			    	//Log.i(TAG +"=======tempx==============", tempx+"");
			    	//Log.i(TAG +"=======tempy===============", tempy+"");
			    	
			    	double x2 = Double.parseDouble(tempx);
			    	double y2 = Double.parseDouble(tempy);
			    	
			    	//Log.i(TAG +"=======x2===============", x2+"");
			    	//Log.i(TAG +"=======y2===============", y2+"");
			    	
			    	x = 2 * x1 - x2;
			    	y = 2 * y1 - y2;
			    	
			    	//Log.i(TAG +"=======x===============", x+"");
			    	//Log.i(TAG +"=======y===============", y+"");
			    	
			    	getGps(x+"", y+"");
					
				} catch (Exception e) {
					e.printStackTrace();
					Log.i(TAG + "baidu2Gps 坐标转换异常", "baidu map");
					dimissDialog();
				}
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(url, params, callback);
	}
	
	/**
	 * 
	 * 获得百度坐标对应的gps坐标
	 * 
	 * 步骤2：
	 */
	public void getGps(String bx,String by){
		
		String url = "http://api.map.baidu.com/ag/coord/convert";
		
		AjaxParams params = new AjaxParams();
		params.put("from", "0");
		params.put("to", "4");
		params.put("x", bx);
		params.put("y", by);
		
		Callback callback = new Callback(tag,this) {

			@Override
			public void onSuccess(String t) {
				
				dimissDialog();
				
				try {
						JsonParser parser = new JsonParser();
				    	JsonObject root = parser.parse(t).getAsJsonObject();
				    	
				    	//获得真正的gps坐标
				    	String tempx  = root.get("x").getAsString();
				    	String tempy = root.get("y").getAsString();
				    	
				    	String resultx = new String(Base64Util.decode(tempx));
				    	String resulty = new String(Base64Util.decode(tempy));
				    	
				    	MapSendAddressEvent mapSendAddressEvent = new MapSendAddressEvent();
						mapSendAddressEvent.address = moveAddress;
						mapSendAddressEvent.latitude = resulty + "";
						mapSendAddressEvent.longitude = resultx + "";
						EventBus.getDefault().post(mapSendAddressEvent);
						finish();
					
				    	//Log.i(TAG+"============最后结果===========resultx", resultx+"");
				    	//Log.i(TAG +"===============最后结果========resulty", resulty+"");
				    	
				} catch (Exception e) {
					e.printStackTrace();
					Log.i(TAG + "getGps坐标转换异常", "baidu map");
				}
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(url, params, callback);
	}
	
	

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		mSearch.destroy();
	}

}