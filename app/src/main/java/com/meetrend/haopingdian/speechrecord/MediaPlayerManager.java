package com.meetrend.haopingdian.speechrecord;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

public class MediaPlayerManager {
	
	private static MediaPlayer mediaPlayer = null;
	private static boolean isPause;
	
	public static void playSound(String filePath, OnCompletionListener listener){
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			//播放遇到错误监听
			mediaPlayer.setOnErrorListener(new OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					Log.i("哈哈", "播放过程遇到错误");
					mediaPlayer.release();
					return false;
				}
			});
			
		}else {
			mediaPlayer.reset();
		}
		
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		if (listener != null) {
			mediaPlayer.setOnCompletionListener(listener);
			
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//遇到类似来电
	public static void pause(){
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = false;
		}
	}
	
	public static void resume(){
		if (mediaPlayer != null && !isPause) {
			mediaPlayer.start();
		}
	}
	
	public static void relase(){
		if (mediaPlayer != null) {
		   mediaPlayer.release();	
		   mediaPlayer = null;
		}
	}

}
