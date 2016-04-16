package com.meetrend.haopingdian.tool;

import java.io.IOException;

import android.media.MediaRecorder;

/**
 * 录音操作封装 修改 tigereye
 * 
 * 来自oschina AudioRecordUtils 类
 */
public class RecordHelper {
	private static final String TAG = RecordHelper.class.getSimpleName();
	private MediaRecorder mMediaRecorder = null;

	/**
	 * 开启录音
	 * 
	 */
	public void start(String parentDir, String fileName) {
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
			// 设置音频录入源（麦克风）
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频输出格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// 设置音频编码格式
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// 设置输出文件路径
			mMediaRecorder.setOutputFile(parentDir + fileName);
			try {
				mMediaRecorder.prepare();
				mMediaRecorder.start();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	public void stop() {
		if (mMediaRecorder != null) {
			try {
				mMediaRecorder.stop();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mMediaRecorder.reset();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		}
	}

	public double getAmplitude() {
		if (mMediaRecorder != null) {
			// 获取在前一次调用此方法之后录音中出现的最大振幅
			return (mMediaRecorder.getMaxAmplitude() / 2700.0);
		}
		return 0;
	}

}