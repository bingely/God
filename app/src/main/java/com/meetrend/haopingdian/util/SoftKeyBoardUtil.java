package com.meetrend.haopingdian.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyBoardUtil {
	
	 //通过定时器强制隐藏虚拟键盘
    public static void TimerHideKeyboard(final View v)
    {
        Timer timer = new Timer();
      timer.schedule(new TimerTask(){
      @Override
      public void run()
      {
          InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
          if ( imm.isActive( ) )
          {     
              imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
          }    
       }  
      }, 10);
     }

}
