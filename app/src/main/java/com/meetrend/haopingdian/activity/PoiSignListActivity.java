package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.AddressListAdapter;
import com.meetrend.haopingdian.bean.AddressPoint;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.FinishPointListEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import de.greenrobot.event.EventBus;

/**
 * 选择Poi列表
 * 
 * @author 肖建斌
 *
 */
public class PoiSignListActivity extends BaseActivity
{
    
    private final static String TAG = PoiSignListActivity.class.getSimpleName()
            .toString();
    
    private BaiduMap mBaiduMap;
    
    private MapView mMapView = null;
    
    private ListView listView;
    
    private double weidu;
    
    private double jindu;
    
    private AddressListAdapter adapter;
    
    // 定位相关
    LocationClient mLocClient;
    
    public MyLocationListenner myListener = new MyLocationListenner();
    
    private LocationMode mCurrentMode;
    
    BitmapDescriptor mCurrentMarker;
    
    private List<AddressPoint> pointsList;
    
    private int oldSelect = 0;//点击ListView之前记录之前保存之前选择的item index
    
    private boolean dataHasLoad = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointsign);
        showDialog();
        
        pointsList = new ArrayList<AddressPoint>();
        
        Intent intent = getIntent();
        weidu = intent.getDoubleExtra("lat", 0.0);
        jindu = intent.getDoubleExtra("lon", 0.0);
        Log.i(TAG + "传递的经维度", weidu + "--" + jindu);
        
        initView();
        adapter = new AddressListAdapter(PoiSignListActivity.this, pointsList);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
            	
                pointsList.get(oldSelect).isSelect = false;
                oldSelect = position;
                
                SPUtil.getDefault(PoiSignListActivity.this).setSelectIndex(position);
                       
                AddressPoint addressInfo = (AddressPoint) listView.getItemAtPosition(position);
                Intent intent = new Intent(PoiSignListActivity.this,
                        CommitSignInfoActivity.class);
                intent.putExtra("name", addressInfo.name);
                intent.putExtra("lat", addressInfo.location.lat);
                intent.putExtra("lon", addressInfo.location.lng);
                startActivity(intent);
            }
        });
        
        requestSearchPoints();
        
    }
    
    private void initView()
    {
        TextView titleView = (TextView) this.findViewById(R.id.actionbar_title);
        titleView.setText("签到");
        findViewById(R.id.actionbar_home).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
        
        listView = (ListView) this.findViewById(R.id.signlistview);
        
        mMapView = (MapView) this.findViewById(R.id.mapview);
        mMapView.showZoomControls(false);// 隐藏放大缩小控件
        // 定位,百度地图参数设置
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mCurrentMode = LocationMode.NORMAL;
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.current_addres_point_icon);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        
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
    
    //刷新列表
    public void onEventMainThread(FinishPointListEvent event)
    {
        finish();
    }
    
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener
    {
        
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            
            double tweidu = location.getLatitude();
            double tjindu = location.getLongitude();
            Log.i(TAG + "维度，经度", tweidu + "--" + tjindu);
            // 此处设置开发者获取到的方向信息，顺时针0-360
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    .direction(0)
                    .latitude(tweidu)
                    .longitude(tjindu)
                    .build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }
        
        public void onReceivePoi(BDLocation poiLocation)
        {
            
        }
    }
    
    
    /**
     * 周边检索感兴趣的point
     * */
    private void requestSearchPoints()
    {
    	
    	Callback callback = new Callback(tag,PoiSignListActivity.this) {
    		
    		@Override
    		public void onSuccess(String t) {
    			super.onSuccess(t);
    			
    			
                    if (isSuccess)
                    {
                    	JsonArray josnArray = data.get("items").getAsJsonArray();
                        Gson gson = new Gson();
                        List<AddressPoint> tlist = gson.fromJson(josnArray,new TypeToken<List<AddressPoint>>() {}.getType());
                        
                        pointsList.addAll(tlist);
                        pointsList.get(0).isSelect = true;//默认第一项选中
                        SPUtil.getDefault(getApplicationContext()).setSelectIndex(0);
                        adapter.notifyDataSetChanged();
                        dataHasLoad = true;
                    }
                    
                    else {
                    	showToast("加载失败");
                    }
                    
                    dimissDialog();
    		}
    		
    		@Override
    		public void onFailure(Throwable t, int errorNo, String strMsg) {
    			super.onFailure(t, errorNo, strMsg);
    			
    		}
		};
        
        AjaxParams params = new AjaxParams();
        params.put("lat", weidu + "");
        params.put("lng", jindu + "");
        params.put("radius", "2000");
		params.put("token", SPUtil.getDefault(PoiSignListActivity.this).getToken());
		CommonFinalHttp commonFinalHttp = new CommonFinalHttp();
		commonFinalHttp.post(Server.BASE_URL + Server.NEAR_SIGN_LIST,params,callback);
    }
    
    public void finish(View view)
    {
        PoiSignListActivity.this.finish();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        mMapView.onResume();
        //刷新列表
        
        if (dataHasLoad) {
        	int currentPosition = SPUtil.getDefault(getApplicationContext())
                    .getSelectIndex();
            if (currentPosition != -1)
            {
                pointsList.get(currentPosition).isSelect = true;
                adapter.notifyDataSetChanged();
            }
		}
        
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        mMapView.onPause();
    }
    
    @Override
    public void onDestroy()
    {
    	
        EventBus.getDefault().unregister(this);
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        
        SPUtil.getDefault(getApplicationContext()).setSelectIndex(-1);
        super.onDestroy();
    }
    
}