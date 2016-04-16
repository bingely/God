package com.meetrend.haopingdian.tool;

import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.AccountActivity;
import com.meetrend.haopingdian.fragment.LoginFragment;


public abstract class Callback extends AjaxCallBack<String>{
	
	private final static String TAG = Callback.class.getName();
	
	private String tag;
	public JsonObject data;
	public boolean isSuccess;
	public Context context;
	public String msg;
	
	public Callback(String tag) {
		this.tag = tag;
	}
	
	public Callback(String tag,Context context) {
		this.tag = tag;
		this.context = context;
	}
	
	@Override
	public void onSuccess(String t) {
		
		Log.i(TAG + "REPONSE_DATA", t);
    	
		JsonParser parser = new JsonParser();
    	JsonObject root = parser.parse(t).getAsJsonObject();
    	data = root.get("data").getAsJsonObject();
    	isSuccess = root.get("success").getAsBoolean();
    	try {
    		if(isSuccess == false){
    			
    			if (data.has("msg")) {
    				msg = data.get("msg").getAsString();
				}else {
					msg = "请求失败";
				}
    			
    			if(data.get("ecode")!=null){
    				String ecode = data.get("ecode").getAsString();
    				if(ecode.equals("401")){
						if (data.has("msg")){
							Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
						}
                		DaYiActivityManager.getWeCareActivityManager().popAllActivityExceptOne(null);
                		context.startActivity(new Intent("com.meetrend.account"));
                	}
    			}
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
	}

}
