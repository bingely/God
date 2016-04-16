package com.meetrend.haopingdian.speechrecord;

import java.io.File;
import java.util.UUID;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ChatActivity;
import com.meetrend.haopingdian.fragment.FaceFragment;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * 录音管理类
 * @author 肖建斌
 *
 */
public class AudioManager {
	
	private MediaRecorder mediaRecorder;
	private static AudioManager audioManager = null;
	private String mDir;//语音文件的父目录
	public String mFilePath = null;//语音文件的位置
	
	private boolean isPrepare;//是否处于准备录音的状态
	
    public AudioStateLinstener audioStateLinstener;
	
	private AudioManager(String dir){
		mDir = dir;
	}
	
	//准备录音接口回调
	public interface AudioStateLinstener{
		 public void prepareComepelete();
	}
	
	
	public void setAudioSateLinstener(AudioStateLinstener audioStateLinstener){
		if (audioStateLinstener != null) {
			this.audioStateLinstener = audioStateLinstener;
		}
	}
	
	/**
	 * 语音管理类单例模式(懒汉式)
	 * 
	 * */
	public static AudioManager getInstance(String dir){
		if (audioManager == null) {
			synchronized (AudioManager.class) {
				if (audioManager == null) {
					audioManager = new AudioManager(dir);
				}
			}
		}
		return audioManager;
	}
	
	public String getCurrentFilePath(){
		return mFilePath;
	}
	
	
	//准备录音
	public void prepareAudio(){
		
		try {
				isPrepare = false;
				
				File mdir = new File(mDir);//语音文件父路径
				if (!mdir.exists()) {
					mdir.mkdirs();
				}
				String fileName = getFileName();
				File targetfile = new File(mdir,fileName);
				mFilePath = targetfile.getAbsolutePath();
				
				if (audioStateLinstener != null) {
					 //
				     audioStateLinstener.prepareComepelete();//在AudioButton.java中回调
				}
				
				mediaRecorder = new MediaRecorder();
				mediaRecorder.setOutputFile(targetfile.getAbsolutePath());
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mediaRecorder.prepare();
				mediaRecorder.start();
				
				isPrepare = true;
				
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String  getFileName(){
		
		return UUID.randomUUID().toString() + ".amr";
	}

	//mediaRecorder.getMaxAmplitude() 值 1- 32767
	public int  getlevel(int maxLevel){
		
		if (isPrepare) {
			try {
				
				  return maxLevel * mediaRecorder.getMaxAmplitude() / 32768;
				  
			} catch (Exception e) {
				
			}
		}
		
		return 1;
	}
	
	/**
	 * 释放资源
	 * 
	 * */
	public void  realse(){
		mediaRecorder.stop();
		mediaRecorder.release();
		mediaRecorder = null;
	}
	
	/**
	 * 
	 * 取消本次录音
	 * 
	 * */
	public void cancel(){
		realse();
		if (mFilePath != null) {
			File file = new File(mFilePath);
			file.delete();
			mFilePath = null;
		}
	}
}