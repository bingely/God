package com.meetrend.haopingdian.activity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.event.SendTimeListEvent;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.wheel.widget.OnWheelChangedListener;
import com.meetrend.haopingdian.wheel.widget.OnWheelScrollListener;
import com.meetrend.haopingdian.wheel.widget.WheelView;
import com.meetrend.haopingdian.wheel.widget.adapters.NumericWheelAdapter;
import de.greenrobot.event.EventBus;
import android.R.integer;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 上班下班时间设置
 * 
 * @author 肖建斌
 * 
 * 
 */
public class TimeRemindActivity extends BaseActivity implements
		OnCheckedChangeListener {

	@ViewInject(id = R.id.actionbar_home, click = "finishActivity")
	ImageView backView;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	@ViewInject(id = R.id.actionbar_action, click = "sureClick")
	TextView sureView;

	@ViewInject(id = R.id.start_work_btn, click = "startClick")
	CheckBox startCheckBox;
	@ViewInject(id = R.id.start_work_time)
	TextView startTimeView;

	@ViewInject(id = R.id.finish_work_btn, click = "finishClick")
	CheckBox endCheckBox;
	@ViewInject(id = R.id.finish_work_time)
	TextView endTimeView;

	@ViewInject(id = R.id.choostime_layout, click = "chooseTimeClick")
	LinearLayout chooseLayout;
	@ViewInject(id = R.id.choose_content_view)
	TextView chooseContentView;

	@ViewInject(id = R.id.sign_des_layout, click = "desClick")
	RelativeLayout desLayout;

	private List<String> timeList;

	private TimeChooseDialog timeDialog;
	private boolean isStart;// 是否是第一个控件触发dialog操作

	SPUtil spUtil;

	private final static int NO_START_REAPEAT = 0X111;
	private final static int NO_END_REAPEAT = 0X222;

	private final static int ONE = 0X333;
	private final static int TWO = 0X444;
	private final static int THREE = 0X555;
	private final static int FOUR = 0X666;
	private final static int FIVE = 0X777;
	private final static int SIX = 0X888;
	private final static int SEVEN = 0X999;

	// 结束
	private final static int EONE = 0X033;
	private final static int ETWO = 0X044;
	private final static int ETHREE = 0X055;
	private final static int EFOUR = 0X066;
	private final static int EFIVE = 0X077;
	private final static int ESIX = 0X088;
	private final static int ESEVEN = 0X099;

	private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
	
	private  int[] codes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timesetting);
		EventBus.getDefault().register(this);
		FinalActivity.initInjectedView(this);

		init();
	}

	private void init() {
		titleView.setText("设置");
		sureView.setText("保存");
		timeList = new ArrayList<String>();

		spUtil = SPUtil.getDefault(this);
		startCheckBox
				.setBackgroundResource(spUtil.getStartTimeStatus() == 1 ? R.drawable.butn_open
						: R.drawable.butn_close);
		endCheckBox
				.setBackgroundResource(spUtil.getEndTimeStatus() == 1 ? R.drawable.butn_open
						: R.drawable.butn_close);

		// 时间显示
		startTimeView.setText(spUtil.getStartTime());
		endTimeView.setText(spUtil.getEndTime());
		
		//展示每周重复的天数
		chooseContentView.setText(spUtil.getWeekData());

		startCheckBox.setOnCheckedChangeListener(this);
		endCheckBox.setOnCheckedChangeListener(this);
		
		String weekDataStr = spUtil.getWeekData();
		if (!weekDataStr.equals("")) {
			if (!weekDataStr.contains("、")) {
				timeList.add(weekDataStr);
			}else {
				String[] arryString = weekDataStr.split("、");
				timeList = Arrays.asList(arryString);
			}
		}
		
		codes = new int[]{0X111,0X222,0X333,0X444,0X555,0X666,0X777,0X888,0X999,0X033,0X044,0X055,0X066,0X077,0X088,0X099};
	}

	// 选择星期日期
	public void onEventMainThread(SendTimeListEvent event) {

		timeList = event.timeList;
		StringBuilder builder = new StringBuilder();
		for (String item : timeList) {
			builder.append(item + "、");
		}
		
		String result = builder.deleteCharAt(builder.toString().length() - 1).toString();
		chooseContentView.setText(result);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (timeDialog == null) {
			timeDialog = new TimeChooseDialog(TimeRemindActivity.this);
			timeDialog.setCancelable(true);
			timeDialog.setCanceledOnTouchOutside(true);
		}

		switch (buttonView.getId()) {
		case R.id.start_work_btn:
			isStart = true;

			if (spUtil.getStartTimeStatus() == -1) {

				startCheckBox.setBackgroundResource(R.drawable.butn_open);

				if (!timeDialog.isShowing()) {
					timeDialog.show();
				}

				// 保存打开状态
				spUtil.saveStartTimeStatus(1);

			} else {
				startCheckBox.setBackgroundResource(R.drawable.butn_close);

				// 保存未打开状态
				spUtil.saveStartTimeStatus(-1);
			}

			break;
		case R.id.finish_work_btn:

			isStart = false;
			if (spUtil.getEndTimeStatus() == -1) {

				endCheckBox.setBackgroundResource(R.drawable.butn_open);

				if (!timeDialog.isShowing()) {
					timeDialog.show();
				}
				spUtil.saveEndTimeStatus(1);

			} else {
				endCheckBox.setBackgroundResource(R.drawable.butn_close);
				spUtil.saveEndTimeStatus(-1);
			}

			break;

		default:
			break;
		}
	}
	
	
	private long calculateTimeOffset(Date setDate,Date date){
		
		long millSendsOffset = 0;
		
		//当前时间在设定时间的后面
		if (date.after(setDate)) {
			
			//求得时间差
			long timeOffset = date.getTime() - setDate.getTime();
			millSendsOffset = INTERVAL + timeOffset;
		}else {
			
			long timeOffset = setDate.getTime() - date.getTime();
			millSendsOffset = timeOffset;
		}
		
		return millSendsOffset;
		
	}
	
	private String timeStr = "";
	private int someDayNum;
	private int requestCode;
	private int currentWeekIndex;
	//时间列表
	private int size;
	private String currrentSetTimeStr;
	private String[] strs;

	// 保存
	public void sureClick(View view) {
		
		//保存每周重复的数据
		spUtil.setWeekData(chooseContentView.getText().toString());
		if (spUtil.getStartTimeStatus() == -1 && spUtil.getEndTimeStatus() == -1) {
			spUtil.setWeekData("");
		}
		
		//重置提醒
		for (int i = 0; i < codes.length; i++) {
			
			Intent noRepeatStartIntent = new Intent(
					TimeRemindActivity.this, NotificationService.class);
			PendingIntent sender = PendingIntent.getService(
					TimeRemindActivity.this, codes[i],
					noRepeatStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) TimeRemindActivity.this
					.getSystemService(Context.ALARM_SERVICE);
			am.cancel(sender);
		}

		Calendar startSettingCalendar = Calendar.getInstance();
		Date setDate = startSettingCalendar.getTime();
		Date date = new Date();
		
		// 当天在每星期的index
		currentWeekIndex = startSettingCalendar.get(Calendar.DAY_OF_WEEK);
		size = timeList.size();

		//上班时间设置
		if (spUtil.getStartTimeStatus() == 1) {
			
			currrentSetTimeStr = startTimeView.getText().toString();// 当前设置时间
			strs = currrentSetTimeStr.split(":");
			
			if (size == 0) {
				
				Intent noRepeatStartIntent = new Intent(
						TimeRemindActivity.this, NotificationService.class);
				noRepeatStartIntent.putExtra("hint", "亲，该上班了");
				noRepeatStartIntent.putExtra("start", 1);
				
				PendingIntent sender = PendingIntent.getService(
						TimeRemindActivity.this, NO_START_REAPEAT,
						noRepeatStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager am = (AlarmManager) TimeRemindActivity.this
						.getSystemService(Context.ALARM_SERVICE);

				// 响铃时间设置
				startSettingCalendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(strs[0]));
				startSettingCalendar.set(Calendar.MINUTE,Integer.parseInt(strs[1]));
				
				//当前时间在设定时间的后面
				if (date.after(setDate)) {
					
					//求得时间差
					long timeOffset = (date.getTime() - setDate.getTime());
					long nextNotificationOffset = INTERVAL + timeOffset;
					am.set(AlarmManager.RTC_WAKEUP,startSettingCalendar.getTimeInMillis() + nextNotificationOffset, sender);
					
				}else {
					
					long timeOffset = setDate.getTime() - date.getTime();
					long nextNotificationOffset = timeOffset;
					am.set(AlarmManager.RTC_WAKEUP,startSettingCalendar.getTimeInMillis() + nextNotificationOffset, sender);
				}
				
				showToast("保存成功");
			}
			
			long millSendsOffset = 0;

			for (int i = 0; i < size; i++) {

				timeStr = timeList.get(i);

				// 代表1
				if (timeStr.equals("周日")) {

					int mIndex = 1;

					if (currentWeekIndex < mIndex){
						
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
						
					}else if (currentWeekIndex == mIndex) {
						
						 millSendsOffset = calculateTimeOffset(setDate, date);
						 someDayNum = 0;
						
					}else{
						// 计算设置闹铃的时间和该天相差几天
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					requestCode = ONE;
				}

				// 代表2
				else if (timeStr.equals("周一")) {
					int mIndex = 2;
					
					if (currentWeekIndex < mIndex){
						
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
						
					}else if (currentWeekIndex == mIndex) {
						
						 millSendsOffset = calculateTimeOffset(setDate, date);
						 someDayNum = 0;
					 }else{
							
							someDayNum = (7 - currentWeekIndex) + mIndex;
							millSendsOffset = 0;
					}
					
					requestCode = TWO;

				} else if (timeStr.equals("周二")) {
					int mIndex = 3;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
						
					}else if (currentWeekIndex == mIndex) {
						
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
						
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					
					requestCode = THREE;

				} else if (timeStr.equals("周三")) {
					int mIndex = 4;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
						
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
						
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
						
					}
					
					requestCode = FOUR;

				} else if (timeStr.equals("周四")) {
					int mIndex = 5;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
						
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					requestCode = FIVE;

				} else if (timeStr.equals("周五")) {
					
					int mIndex = 6;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					
					requestCode = SIX;

				} else if (timeStr.equals("周六")) {
					
					int mIndex = 7;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					
					requestCode = SEVEN;
				}

				Intent intent = new Intent(TimeRemindActivity.this,
						NotificationService.class);
				intent.putExtra("hint", "亲，该上班了");
				PendingIntent sender = PendingIntent.getService(
						TimeRemindActivity.this, requestCode, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager am = (AlarmManager) TimeRemindActivity.this
						.getSystemService(Context.ALARM_SERVICE);

				startSettingCalendar.set(Calendar.HOUR_OF_DAY,
						Integer.parseInt(strs[0]));
				startSettingCalendar.set(Calendar.MINUTE,
						Integer.parseInt(strs[1]));

				am.setRepeating(AlarmManager.RTC_WAKEUP,startSettingCalendar.getTimeInMillis(), INTERVAL* someDayNum + millSendsOffset, sender);
			}
		}
		
		
		
		//下班时间设置
		if (spUtil.getEndTimeStatus() == 1){
			
			currrentSetTimeStr = endTimeView.getText().toString();// 当前设置时间
			strs = currrentSetTimeStr.split(":");
			long millSendsOffset = 0;
			
			if (size == 0) {
				Intent noRepeatStartIntent = new Intent(TimeRemindActivity.this, NotificationService.class);
				noRepeatStartIntent.putExtra("hint", "亲，该下班了");
				noRepeatStartIntent.putExtra("end", 1);
				
				PendingIntent sender = PendingIntent.getService(TimeRemindActivity.this, NO_END_REAPEAT,noRepeatStartIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager am = (AlarmManager) TimeRemindActivity.this.getSystemService(Context.ALARM_SERVICE);

				// 响铃时间设置
				startSettingCalendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(strs[0]));
				startSettingCalendar.set(Calendar.MINUTE,Integer.parseInt(strs[1]));

				//当前时间在设定时间的后面
				if (date.after(setDate)) {
					
					//求得时间差
					long timeOffset = date.getTime() - setDate.getTime();
					long nextNotificationOffset = INTERVAL + timeOffset;
					am.set(AlarmManager.RTC_WAKEUP,startSettingCalendar.getTimeInMillis() + nextNotificationOffset, sender);
					
				}else {
					
					long timeOffset = setDate.getTime() - date.getTime();
					long nextNotificationOffset = timeOffset;
					am.set(AlarmManager.RTC_WAKEUP,startSettingCalendar.getTimeInMillis() + nextNotificationOffset, sender);
				}
				
				finish();
			}


			for (int i = 0; i < size; i++) {

				timeStr = timeList.get(i);

				// 代表1
				if (timeStr.equals("周日")) {

					int mIndex = 1;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0; 
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						// 计算设置闹铃的时间和该天相差几天
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0; 
					}
					
					requestCode = EONE;
				}

				// 代表2
				else if (timeStr.equals("周一")) {
					int mIndex = 2;
					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					requestCode = ETWO;

				} else if (timeStr.equals("周二")) {
					
					int mIndex = 3;
					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					requestCode = ETHREE;

				} else if (timeStr.equals("周三")) {
					int mIndex = 4;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					
					
					requestCode = EFOUR;

				} else if (timeStr.equals("周四")) {
					
					int mIndex = 5;
					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					requestCode = EFIVE;
					

				} else if (timeStr.equals("周五")) {
					int mIndex = 6;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
						
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					requestCode = ESIX;

				} else if (timeStr.equals("周六")) {
					
					int mIndex = 7;

					if (currentWeekIndex < mIndex){
						someDayNum = mIndex - currentWeekIndex;
						millSendsOffset = 0;
					}else if (currentWeekIndex == mIndex) {
						millSendsOffset = calculateTimeOffset(setDate, date);
						someDayNum = 0;
					}else{
						someDayNum = (7 - currentWeekIndex) + mIndex;
						millSendsOffset = 0;
					}
					
					requestCode = ESEVEN;
				}

				Intent intent = new Intent(TimeRemindActivity.this,
						NotificationService.class);
				intent.putExtra("hint", "亲，该下班了");
				PendingIntent sender = PendingIntent.getService(
						TimeRemindActivity.this, requestCode, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager am = (AlarmManager) TimeRemindActivity.this
						.getSystemService(Context.ALARM_SERVICE);

				startSettingCalendar.set(Calendar.HOUR_OF_DAY,
						Integer.parseInt(strs[0]));
				startSettingCalendar.set(Calendar.MINUTE,
						Integer.parseInt(strs[1]));

				am.setRepeating(AlarmManager.RTC_WAKEUP,
						startSettingCalendar.getTimeInMillis(), INTERVAL
								* someDayNum + millSendsOffset, sender);
			}
			
			saveSettingTime();
			finish();
		}else {
			finish();
			saveSettingTime();
		}
	}

	// 上班时间选择
	public void startClick(View view) {

	}

	// 下班时间选择
	public void finishClick(View view) {

	}

	// 选择周期
	public void chooseTimeClick(View view) {
		Intent intent = new Intent(TimeRemindActivity.this,ChooseTimeListActivity.class);
		startActivity(intent);
	}

	// 功能说明
	public void desClick(View view) {
		Intent intent = new Intent(TimeRemindActivity.this,
				FuncationActivity.class);
		startActivity(intent);
	}

	private void saveSettingTime() {
		// 保存设置时间
		if (spUtil.getStartTimeStatus() == 1) {
			spUtil.saveStartTime(startTimeView.getText().toString());
		}

		if (spUtil.getEndTimeStatus() == 1) {
			spUtil.saveEndTime(endTimeView.getText().toString());
		}
	}

	// 时间选择对话框
	private class TimeChooseDialog extends Dialog {

		public TimeChooseDialog(Context context, int theme) {
			super(context, theme);
			View dialogView = getLayoutInflater().inflate(
					R.layout.time_choose_dialog, null);
			final WheelView hoursWheel = (WheelView) dialogView
					.findViewById(R.id.hour);
			hoursWheel.setViewAdapter(new NumericWheelAdapter(context, 0, 23,
					"%02d"));
			hoursWheel.setCyclic(true);
			final WheelView minuteWheel = (WheelView) dialogView
					.findViewById(R.id.mins);
			minuteWheel.setViewAdapter(new NumericWheelAdapter(context, 0, 59,
					"%02d"));
			minuteWheel.setCyclic(true);
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			// 滚轮控件变化监听
			OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					String dymh = "";
					String dymminute = "";
					String realh = hoursWheel.getCurrentItem() + "";
					String realMunite = minuteWheel.getCurrentItem() + "";
					if (realh.length() == 1) {
						dymh = "0" + realh;
					} else {
						dymh = realh;
					}

					if (realMunite.length() == 1) {
						dymminute = "0" + realMunite;
					} else {
						dymminute = realMunite;
					}

					if (isStart) {
						startTimeView.setText(dymh + ":" + dymminute);
					} else {
						endTimeView.setText(dymh + ":" + dymminute);
					}
				}
			};
			hoursWheel.addChangingListener(wheelListener);
			minuteWheel.addChangingListener(wheelListener);

			// 滚轮开始和结束控件
			OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
				@Override
				public void onScrollingStarted(WheelView wheel) {

				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					String dymh = "";
					String dymminute = "";
					String realh = hoursWheel.getCurrentItem() + "";
					String realMunite = minuteWheel.getCurrentItem() + "";
					if (realh.length() == 1) {
						dymh = "0" + realh;
					} else {
						dymh = realh;
					}

					if (realMunite.length() == 1) {
						dymminute = "0" + realMunite;
					} else {
						dymminute = realMunite;
					}

					if (isStart) {
						startTimeView.setText(dymh + ":" + dymminute);
					} else {
						endTimeView.setText(dymh + ":" + dymminute);
					}
				}
			};
			hoursWheel.addScrollingListener(scrollListener);
			minuteWheel.addScrollingListener(scrollListener);
			super.setContentView(dialogView);
		}

		public TimeChooseDialog(Context context) {
			this(context, R.style.discount_dialog_theme);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			getWindow().setGravity(Gravity.BOTTOM);
			// 设置对话框的宽度
			WindowManager m = getWindow().getWindowManager();
			Display d = m.getDefaultDisplay();
			WindowManager.LayoutParams p = getWindow().getAttributes();
			p.width = d.getWidth();
			p.height = d.getHeight() / 3;
			getWindow().setAttributes(p);
		}
	}

	public void finishActivity(View view) {
		finish();
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}