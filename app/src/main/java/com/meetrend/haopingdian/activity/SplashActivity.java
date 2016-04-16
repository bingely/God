package com.meetrend.haopingdian.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import com.meetrend.haopingdian.R;

public class SplashActivity extends Activity {
	
	private static final int SPLASH_DISPLAY_LENGHT = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		 new Handler().postDelayed(new Runnable(){    
		     
	         @Override    
	         public void run() {    
	             Intent mainIntent = new Intent(SplashActivity.this,AccountActivity.class);    
	             SplashActivity.this.startActivity(mainIntent);    
	             SplashActivity.this.finish();    
	         }    
	        }, SPLASH_DISPLAY_LENGHT);    
	    }    
	
}