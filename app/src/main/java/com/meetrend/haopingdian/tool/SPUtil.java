package com.meetrend.haopingdian.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.bean.LoginEntity;
import com.meetrend.haopingdian.bean.Store;
import com.meetrend.haopingdian.bean.StoreInfo;
import com.umeng.socialize.utils.Log;

public class SPUtil {
	private static final String TAG = SPUtil.class.getSimpleName();
	private static final String NAME = "setttings";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Context context;
	private static SPUtil util;
	
	private SPUtil(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public static SPUtil getDefault(Context context) {
		if (util == null) {
			util = new SPUtil(context);
		}
		return util;
	}
	
	public void setWidth(int width) {
		editor.putInt("width", width);
		editor.commit();
	}
	
	public int getWidth() {
		return sp.getInt("width", 0);
	}
	
	//存储通讯录是否加载过数据
	public void saveStu(int stu) {
		editor.putInt("stu", stu);
		editor.commit();
	}
	
	public int getStu() {
		return sp.getInt("stu", -1);
	}
	
	//保存门店名字
	public void saveStoreName(String storeName){
		editor.putString("storename", storeName);
		editor.commit();
	}
	
	//获取保存的门店名称
	public String getStoreName(){
		return sp.getString("storename", "没有信息");
	}
	
	public String getLastRequestTime(){
		return sp.getString("lastRequestTime", "0");
	}
	
	public void saveLastRequestTime(String time){
		editor.putString("lastRequestTime", time);
		editor.commit();
	}
	
	/**
	 * 保存tag
	 * @param tag
	 */
	public void setTag(String tag) {
		editor.putString("tag", tag);
		editor.commit();
	}
	
	/**
	 * 获取tag
	 * @return
	 */
	public String getTag() {
		return sp.getString("tag", "");
	}
	
	
	public String getLoginName() {
		return sp.getString("login_name", "");
	}
	
	public void saveLoginName(String name) {
		editor.putString("login_name", name);
		editor.commit();
	}
	
	//获得token
	public String getToken() {
		return sp.getString("token", "");
	}
	
	public void saveToken(String sid) {
		editor.putString("token", sid);
		editor.commit();
	}
	
	public void saveSId(String sid) {
		editor.putString("sid", sid);
		editor.commit();
	}
	
	//获得appstoreId
	public String getStoreId() {
		return sp.getString("sid", "");
	}
	
	public String getPwdstatus() {
		return sp.getString("login_pwdstatus", "");
	}
	
	public void savePwdstatus(String pwdstatus) {
		editor.putString("login_pwdstatus", pwdstatus);
		editor.commit();
	}
	
	public String getPwd() {
		return sp.getString("loginpwd", "");
	}
	
	public void savePwd(String pwd) {
		editor.putString("loginpwd", pwd);
		Log.i("TAG","保存登录密码");
		editor.commit();
	}
	
	public String getId() {
		return sp.getString("id", "");
	}
	
	public void setId(String id) {
		editor.putString("id", id);
		editor.commit();		
	}
	
	public int getPotion() {
		return sp.getInt("position", 0);
	}
	
	public void savePotion(int position) {
		editor.putInt("position", position);
		editor.commit();		
	}
	
	/**
	 * 保存店内下单的实收金额
	 */
	public void saveShiShouMoney(String money) {
		editor.putString("smoney", money);
		editor.commit();
	}
	
	/**
	 * 获取保存的实收金额
	 */
	public String getShiShouMoney() {
		return sp.getString("smoney", "");
	}
	
	
	/***
	 * 
	 * 保存门店信息
	 * 
	 * */
	public void saveStore(StoreInfo store) {
		String jsonStore = sp.getString("store", "");
		
		Gson gson = new Gson();
		ArrayList<StoreInfo> storeList = gson.fromJson(jsonStore, new TypeToken<List<StoreInfo>>() {}.getType());
		
		int index = 0;
		if(storeList!=null){
			for(StoreInfo storeinfo: storeList){
				if(storeinfo.storeId.equals(store.storeId)){
					storeList.remove(index);
					storeList.add(store);
					break;
				}
				index++;
			}
		}else{
			storeList = new ArrayList<StoreInfo>();
			storeList.add(store);
		}
		
		if(!storeList.contains(store)){
			storeList.add(store);
		}
		
		String jsonArr = gson.toJson(storeList);
		
		editor.putString("store", jsonArr);
		editor.commit();
	}
	
	/***
	 * 
	 * 获取门店信息列表
	 * 
	 * @author bob
	 * */
	public ArrayList<StoreInfo> getStoreList() {
		String jsonStore = sp.getString("store", "");
		Gson gson = new Gson();
		ArrayList<StoreInfo> storeList = gson.fromJson(jsonStore, new TypeToken<List<StoreInfo>>() {}.getType());
		return storeList;
	}
	
	/**
	 * 清除门店信息列表
	 */
	public void removeStorelt(){
		editor.remove("store");
		editor.commit();
	}
	
//	public void saveLoginEntity(String username, String token, Store store) {
//		LoginEntity entity = new LoginEntity(username, token, store);
//		
//		File file = new File(context.getFilesDir(), "logininfo"); 
//		if (file.exists()) {
//			LogUtil.d(TAG, file.getName() + " exists");
//		} else {
//			LogUtil.d(TAG, file.getName() + " is not exists");
//		}
//		FileOutputStream output = null;  
//		ObjectOutputStream out = null; 
//		try {
//			output = new FileOutputStream(file);
//			out = new ObjectOutputStream(output); 
//			out.writeObject(entity);		
//		} catch (IOException e) {
//			e.printStackTrace();
//			LogUtil.e(TAG, "saveLoginEntity save failed");
//		} finally {
//			try {
//				if (out != null) {
//					out.close();
//				}
//				if (output != null) {
//					output.close();
//				}
//			} catch (IOException e) {
//				LogUtil.e(TAG, "saveLoginEntity close failed");
//			}
//		}
//	}
	
	public void clearToken() {
		File file = new File(context.getFilesDir(), "logininfo");
		if (file.exists()) {
			file.delete();
		}
	}
	
	public LoginEntity getLoginToken() {
		File file = new File(context.getFilesDir(), "logininfo"); 
		if (!file.exists())  {
			LogUtil.d(TAG, file.getName() + " is not exists");
			return null;
		}
		
		FileInputStream input = null;
		ObjectInputStream in = null;
		LoginEntity entity = null;
		try {
			input = new FileInputStream(file);
			in = new ObjectInputStream(input) ;
			entity = (LoginEntity)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, "getLoginEntity save failed");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				LogUtil.e(TAG, "getLoginEntity close failed");
			}
		}
		
		return entity;
	}	
	
	
	public void setProductId(String productId) {
		editor.putString("productId", productId);
		editor.commit();
	}
	
	public String getProductId() {
		return sp.getString("productId", "");
	}
	
	public void setOrderId(String orderId) {
		editor.putString("orderid", orderId);
		editor.commit();
	}
	
	public String getOrderId() {
		return sp.getString("orderid", "");
	}
	
	/**
	 * 存储商大益务号
	 */
	public void setBusinessNum(String num){
		editor.putString("busynum", num);
		editor.commit();
	}
	
	/**
	 * 获得大益商务号
	 */
	public String getBusiNessNum() {
		return sp.getString("busynum", "");
	}
	
	
	public void clearLoginInfo(){
		editor.remove("login_pwd");
		editor.remove("id");
		editor.commit();	
	}
	
	/**
	 * 存储商务号
	 */
	public void saveShangWuNum(String num){
		editor.putString("swnum", num);
		editor.commit();
	}
	
	/**
	 * 取出商务号
	 */
	public String getShangWuNum(){
		return sp.getString("swnum", "");
	}
	
	
	//存储小票打印的门店二维码
	public void saveErweiMaPath(String path){
		editor.putString("epath", path);
		editor.commit();
	}
	
	//取出
	public String getErWeiMaPath(){
		return sp.getString("epath", "");
	}
	
	
	public void removeLocalErWeiMaPath(){
		editor.remove("epath");
		editor.commit();
	}
	
	
	 //保存维度
    public void saveMapWeidu(String weidu)
    {
        editor.putString("weidu", weidu);
        editor.commit();
    }
    
    //获取经度
    public String getMapJindu()
    {
        
        return sp.getString("jindu", "");
    }
    
    //保存经度
    public void saveMapJindu(String jindu)
    {
        editor.putString("jindu", jindu);
        editor.commit();
    }
    
    //获取经度
    public String getMapWeidu()
    {
        
        return sp.getString("weidu", "");
    }
    
    //保存公司名字
    public void saveCName(String cname)
    {
        editor.putString("cname", cname);
        editor.commit();
    }
    
    //获取公司名字
    public String getCName()
    {
        
        return sp.getString("cname", "");
    }
    
    
    public int getSelectIndex()
    {
        return sp.getInt("sindex", -1);
    }
    
    public void setSelectIndex(int index)
    {
        editor.putInt("sindex", index);
        editor.commit();
    }
    
    /**
     * 签到上班时间提醒
     * 
     * @param status 1标识打开，-1标识未打开
     */
    public void saveStartTimeStatus(int status){
    	  editor.putInt("start", status);
          editor.commit();
    }
    
    public int getStartTimeStatus(){
    	return sp.getInt("start", -1);
    }
    
    /**
     * 签到下班时间提醒
     * 
     * @param status 1标识打开，-1标识未打开
     */
    public void saveEndTimeStatus(int status){
    	  editor.putInt("end", status);
          editor.commit();
    }

    public int getEndTimeStatus(){
    	return sp.getInt("end", -1);
    }
    
    //-----------------------
    public void saveStartTime(String time){
	   editor.putString("starttime", time);
       editor.commit();
    }
    
    public String getStartTime(){
    	return sp.getString("starttime", "09:00");
    }
    
    public void saveEndTime(String time){
 	   editor.putString("endtime", time);
        editor.commit();
     }
     
     public String getEndTime(){
     	return sp.getString("endtime", "18:00");
     }
     
     //设置每周重复响铃的数据
     public void setWeekData(String weekDatas){
    	 
    	 editor.putString("week", weekDatas);
         editor.commit();
     }
	
     
     public String getWeekData(){
    	 return sp.getString("week", "");
     }


	public void saveCommonUrl(String headurl){
		editor.putString("commonurl", headurl);
		editor.commit();
	}

	public String getCommonUrl(){
		return sp.getString("commonurl", " ");
	}

}
