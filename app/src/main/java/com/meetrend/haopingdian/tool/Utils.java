package com.meetrend.haopingdian.tool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.EditText;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.fragment.BaseFragment;

public class Utils {
    public static final String TAG = "PushDemoActivity";
    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    protected static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";

    public static String logStringCache = "";

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }

    public static String getLogText(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString("log_text", "");
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }
   //分辨率
    public static String getResolution(Activity mActivity) {
    	Display display = mActivity.getWindowManager().getDefaultDisplay();
    	String resolution = String.format("%s*%s", display.getWidth(), display.getHeight());
    	return resolution;
    }
    
    /**
     * 
     * 校验字段是否为空
     * @author bob
     * 
     * */
    public static boolean checkArgsIsEmpty(BaseFragment fragment,EditText edView,int resId){
    	if(edView==null || edView.getText()==null){
    		fragment.showToast(R.string.error_control_is_null);
    		return false;
    	}else{
    		if(StringUtil.isEmpty(edView.getText().toString())){
    			fragment.showToast(resId);
        		return false;
    		}
    	}
    	return true;
    }
    
    
    /**
     * 
     * 校验2控件值是否一样
     * @author bob
     * 
     * */
    public static boolean checkArgsIsEquals(BaseFragment fragment,EditText edView1,EditText edView2,int resId){
    	if(edView1==null || edView1.getText()==null || edView2==null || edView2.getText()==null){
    		fragment.showToast(R.string.error_control_is_null);
    		return false;
    	}else{
    		if(!edView1.getText().toString().equals(edView2.getText().toString())){
    			fragment.showToast(resId);
        		return false;
    		}
    	}
    	return true;
    }
    
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels; // 屏幕宽（像素，
        App.ScreenWidth = screenWidth;
        return screenWidth;
    }
    
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels; // 屏幕高（像素，
        App.ScreenHeight = screenHeight;
        return screenHeight;
    }
    
    public static int adapterPx(Context context, int px) {
 		int adapterPx = (int) (px * (((float) App.ScreenWidth) / 480.0f));
 		return adapterPx;
 	}

 	public static int adapterPy(Context context, int py) {
 		int adapterPy = (int) (py * (((float) App.ScreenHeight) / 800.0f));
 		return adapterPy;
 	}

 	public static float adapterPx(Context context, float px) {
 		float adapterPx = (px * (((float)App.ScreenHeight) / 480.0f));
 		return adapterPx;
 	}
 	
 	 public static int adapter720x1280Px(Context context, int px) {
 			int adapterPx = (int) (px * (((float) App.ScreenWidth) / 720.0f));
 			return adapterPx;
 	}
    
}
