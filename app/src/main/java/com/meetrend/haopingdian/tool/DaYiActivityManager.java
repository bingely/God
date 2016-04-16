package com.meetrend.haopingdian.tool;

import java.util.Stack;

import android.app.Activity;
import android.util.Log;

public class DaYiActivityManager { 
    private static Stack<Activity> activityStack; 
    private static DaYiActivityManager instance; 
    private DaYiActivityManager() { 
    }
    public static DaYiActivityManager getWeCareActivityManager() { 
        if (instance == null) { 
            instance = new DaYiActivityManager(); 
        }
        return instance; 
    } 
    //退出栈顶Activity 
    public void popActivity(Activity activity) { 
        if (activity != null) { 
           //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作 
            activity.finish();
            activityStack.remove(activity); 
            activity = null; 
        } 
    }
    //从栈中移除Activity不finish
    public void removeStackActivity(Activity activity) { 
        if (activity != null) { 
           //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作 
            activityStack.remove(activity); 
            activity = null; 
        } 
    } 
    //获得当前栈顶Activity 
    public Activity currentActivity() { 
        Activity activity = null; 
       if(!activityStack.empty()) 
         activity= activityStack.lastElement(); 
        return activity; 
    } 
    //将当前Activity推入栈中 
    public void pushActivity(Activity activity) {
    	Log.i("tag",activity+" activity.Name" + activity.getClass().getName());
    	
        if (activityStack == null) { 
            activityStack = new Stack<Activity>(); 
        } 
        activityStack.add(activity); 
    } 
    //退出栈中所有Activity 
    @SuppressWarnings("rawtypes")
	public void popAllActivityExceptOne(Class cls) {
        while (true) { 
            Activity activity = currentActivity(); 
            if (activity == null) { 
                break; 
            } 
            if (activity.getClass().equals(cls)) { 
                break; 
            } 
            popActivity(activity);
        } 
    } 
} 
