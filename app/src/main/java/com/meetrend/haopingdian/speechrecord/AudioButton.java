package com.meetrend.haopingdian.speechrecord;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.speechrecord.AudioManager.AudioStateLinstener;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * 录音button
 * @author 肖建斌
 *
 */
public class AudioButton extends Button implements AudioStateLinstener {

	private final static int DISTANCE_WANTTO_CANCEL = 50;

	private final static int STATE_NORMAL = 1;//正常状态
	private final static int STATE_RECORDING = 2;//正在录音
	private final static int STATE_WANT_CANCEL = 3;//想要取消录音

	private final static int PREPARE = 0x111;
	private final static int VOICECHANGE = 0x222;
	private final static int DIALOGDISS = 0X333;

	public int currentState = STATE_NORMAL;
	public boolean isRecording;

	public DialogManager dialogManager;
	public AudioManager audioManager;
	
	private float mTime;
	private boolean mReady;
	
	//开始录音的接口回调
	public interface StartHintRecordListener{
		
		public void  startHintRecord();
	}
	
	public StartHintRecordListener startHintRecordListener;
	public void setStartHintRecordListener(StartHintRecordListener startHintRecordListener){
		
		this.startHintRecordListener = startHintRecordListener;
	}
	
	private RecorderFinishListener recorderFinishListener;
	
	public interface RecorderFinishListener{
		public void recorderFinish(float time,String filePath);
	}
	
	public void setRecorderFinishListener(RecorderFinishListener recorderFinishListener){
		if (recorderFinishListener != null) {
			this.recorderFinishListener = recorderFinishListener;
		}
	}
	

	public AudioButton(Context context) {
		this(context, null);
	}

	public AudioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		dialogManager = new DialogManager(context);
		String dir = Environment.getExternalStorageDirectory() + "/meetrend_audios";
		audioManager = AudioManager.getInstance(dir);
		audioManager.setAudioSateLinstener(this);

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mReady = true;
				audioManager.prepareAudio();
				return false;
			}
		});
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PREPARE:
				
				if (startHintRecordListener != null) {
					startHintRecordListener.startHintRecord();
				}
				dialogManager.showDialog();
				isRecording = true;
				new Thread(getLevelRunnable).start();
				break;
			case VOICECHANGE:
				int level =  audioManager.getlevel(7);
				Log.i("AudioButton level value", level + "");
				dialogManager.updateVoiceLevel(level);//根据声音的大小显示对应的图片
				break;
				
			case DIALOGDISS:
				dialogManager.dismissDialog();
				break;

			default:
				break;
			}
		};
	};
	
	Runnable getLevelRunnable = new Runnable() {

		@Override
		public void run() {
			
			while (isRecording) {
				try {
					Thread.sleep(100);
					mTime = mTime + 0.1f;
					handler.sendEmptyMessage(VOICECHANGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	};

	@Override
	public void prepareComepelete() {
		handler.sendEmptyMessage(PREPARE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setBackgroundResource(R.drawable.record_btn_press2_bg);
			changeState(STATE_RECORDING);
			break;
		case MotionEvent.ACTION_MOVE:

			if (isRecording) {
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}

			}

			break;
		case MotionEvent.ACTION_UP:
			setBackgroundResource(R.drawable.record_btn_normal_bg);
			if (!mReady) {
				Log.i("还没准备好录音", "hh");
				reset();
				return super.onTouchEvent(event);
			}
			
			if (!isRecording) {
				Log.i("准备好了，但是没有开始录音", "hh");
				dialogManager.tooShort();
				reset();
				audioManager.realse();
				handler.sendEmptyMessageDelayed(DIALOGDISS, 1300);
				return  super.onTouchEvent(event);
			}else if (isRecording && mTime < 1f) {
				//录音时间小于一秒
				Log.i("启动了但是录音时间太短", "hh");
				dialogManager.tooShort();
				reset();
				audioManager.cancel();
				handler.sendEmptyMessageDelayed(DIALOGDISS, 1300);
				return  super.onTouchEvent(event);
			}
			
			if (currentState == STATE_RECORDING) {
				dialogManager.dismissDialog();
				//正常录音结束回调
				if (recorderFinishListener != null) {
					recorderFinishListener.recorderFinish(mTime,audioManager.getCurrentFilePath());
				}
				reset();
				audioManager.realse();
			} else if (currentState == STATE_WANT_CANCEL) {
				dialogManager.dismissDialog();
				reset();
				audioManager.cancel();
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 
	 *	重置
	 *
	 *
	 * */
	private void reset() {
		isRecording = false;
		mReady = false;
		mTime = 0f;
		changeState(STATE_NORMAL);
	}

	/**
	 * 
	 * 超出某个范围则取消录音
	 * 
	 * */
	private boolean wantToCancel(int x, int y) {
		if ((x < 0 || x > getWidth()) || (y < -DISTANCE_WANTTO_CANCEL || y > getHeight() + DISTANCE_WANTTO_CANCEL)) {
			return true;
		} else {
			return false;
		}
	}

	public void changeState(int state) {
		if (currentState != state) {
			currentState = state;
			switch (state) {
			case STATE_NORMAL:
				setText(R.string.btn_normal_txt);
				break;
			case STATE_WANT_CANCEL:
				setText("松开手指，取消发送");
				dialogManager.wantCancel();
				break;
			case STATE_RECORDING:
				setText("松开结束");
				if (isRecording) {
					dialogManager.recording();
				}
				break;

			default:
				break;
			}
		}

	}

}