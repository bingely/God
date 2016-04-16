package com.meetrend.haopingdian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.tsz.afinal.http.AjaxParams;
import android.app.Application;
import android.support.v4.util.LruCache;
import com.meetrend.haopingdian.bean.ExecutorEntity;
import com.meetrend.haopingdian.bean.InventoryDetailEntity;
import com.meetrend.haopingdian.bean.InventoryItem;
import com.meetrend.haopingdian.bean.MeDetail;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.bean.MemberDetail;
import com.meetrend.haopingdian.bean.NetInfoBean;
import com.meetrend.haopingdian.bean.OnlineOrderDetail;
import com.meetrend.haopingdian.bean.StoreInfo;
import com.meetrend.haopingdian.bean.StoreOrderDetail;
import com.meetrend.haopingdian.bean.YearPair;
import com.meetrend.haopingdian.tool.SPUtil;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
//import com.igexin.sdk.PushManager;


public class App extends Application {
	
	public static ArrayList<Member> members;
	
	/**
	 * 客服电话
	 */
	
	public static String servicePhone;
	
	/**
	 * 门店授权号
	 */
	public static String mendianSQH;
	
	
	
	/**
	 *  登录用户 avarid
	 * 
	 * */
	public static String imgid;
	
	
	/**
	 *  时间记录
	 */
	public static String mDate;
	
	/**
	 * 门店列表
	 */
	public static StoreInfo storeInfo;
	/**
	 *  订单执行人
	 */
	public static List<ExecutorEntity> executorList;
	/**
	 *  联系人号码列表
	 */
	public static List<String> memberPhoneList;
	
	// 库存
	public static Map<String, List<YearPair>> group;
	public static Map<String, List<InventoryItem>> child;
	public static InventoryItem inventoryItem;
	public static InventoryDetailEntity inventoryDetail;
	// 订单
	public static StoreOrderDetail storeOrderDetail;
	public static OnlineOrderDetail onlineOrderDetail;
	public static List<StoreOrderDetail.Detail> detailList;
	
	//活动
	public static String eventId;
	
	//个人信息
	public static MeDetail meDetail;
	//会员
	public static MemberDetail memberDetail;
	
	/**
	 * 搜索关键字 SearchActivity
	 */
	public static String keyword;
	
	public static int ScreenWidth;
	public static int ScreenHeight;
	
	private static String msg;
	
	private static App app;
	
	public static AjaxParams ajaxParams;
	
	public static LruCache<String, HashMap<String, Object>> mLruCache = null;

	public static List<Member> tmemberList;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		app = this;

		// 百度地图初始化
		SDKInitializer.initialize(getApplicationContext());

		//fresco初始化
		Fresco.initialize(getApplicationContext());
		

		//个推初始化
		//PushManager.getInstance().initialize(this.getApplicationContext());
		
		SPUtil.getDefault(getApplicationContext()).setBusinessNum("");//为空字符串，则登录显示商务号，为1000标识大益，登录正常显示
		//SPUtil.getDefault(getApplicationContext()).setBusinessNum("");
	}
	
	public static App getInstance(){
		
		return app;
	}
	
	/**
	 * 内存缓存实例
	 * 
	 * 单例模式
	 * 
	 * 缓存1000 个 NetInfoBean对象的网络请求信息
	 */
	public static LruCache<String, HashMap<String, Object>> getLruCacheInstance(){
		
		if (null == mLruCache) {
		
			mLruCache = new LruCache<String, HashMap<String, Object>>(1000){
				@Override
				protected int sizeOf(String key, HashMap<String, Object> hashMap) {
					
					return 1000;
				}
				
			};
			
		}
		return mLruCache;
	}
	
}
