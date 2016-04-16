package com.meetrend.haopingdian.activity;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.SPUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

/**
 * 提醒服务
 *
 */
public class NotificationService extends Service{
	
    private static final int NOTIFICATION_FLAG = 1;  
    
    private MediaPlayer mPlayer = null;
    private String hint = "温馨提示：到点了";
    private int mode;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	

	@Override
	public void onCreate() {
		super.onCreate();
		
		mPlayer = MediaPlayer.create(NotificationService.this, R.raw.in_call_alarm);
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mPlayer.release();
			}
		});
		mPlayer.start();
		
		
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,  
                new Intent(this, MainActivity.class), 0);  
        
        Notification notify = new Notification.Builder(this)  
                .setSmallIcon(R.drawable.about_white_logo) 
                .setTicker("好评店温馨提示")
                .setContentTitle("好评店温馨提示：")
                .setContentText(hint)
                .setContentIntent(pendingIntent)
                .setNumber(1)
                .getNotification(); 
        
        notify.flags |= Notification.FLAG_AUTO_CANCEL;  
        manager.notify(NOTIFICATION_FLAG, notify);
        
    }  
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		hint = intent.getStringExtra("hint");
		
		SPUtil spUtil = SPUtil.getDefault(NotificationService.this);
		
		if (intent.getIntExtra("start", -1) == 1) {
			spUtil.saveStartTimeStatus(-1);
		}
		
		if (intent.getIntExtra("end", -1) == 1) {
			spUtil.saveEndTimeStatus(-1);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
}