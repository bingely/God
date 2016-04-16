package com.meetrend.haopingdian.widget;

import com.meetrend.haopingdian.util.TextWatcherChangeListener;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

public class GiveMoneyEditView extends EditText {

	private boolean disable;// 是否可编辑属性

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	
	
	public interface MoneyChangerListener{
		
		public void textChange(String money);
	}
	
	public interface  ToSQMFragmentLinstener{
		public void tofragment();
	}
	
	private MoneyChangerListener moneyChangerListener;
	private ToSQMFragmentLinstener  toSQMFragmentLinstener;
	
	public void setToSQMFragmentLinstener(ToSQMFragmentLinstener toSQMFragmentLinstener){
		if (toSQMFragmentLinstener != null) {
			this.toSQMFragmentLinstener = toSQMFragmentLinstener;
		}
	}
	
	public void setMoneyChangeListener(MoneyChangerListener moneyChangerListener){
		if (moneyChangerListener != null) {
			this.moneyChangerListener = moneyChangerListener;
		}
	}
	

	public GiveMoneyEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		
		this.addTextChangedListener(new TextWatcherChangeListener(){
			@Override
			public void afterTextChanged(Editable s) {
				
				if (isEnabled()) {
					
					if (moneyChangerListener != null) {
						moneyChangerListener.textChange(s.toString());
					}
					
				}
				
			}
		});
	}

	private long startTime;
	private long endTime;
	private long useTime;

	private int disX;
	private int disY;
	private int startX;
	private int startY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// 不可以按
			if (isEnabled()) {
				return super.onTouchEvent(event);
			}
			startTime = System.currentTimeMillis();
			startX = (int) event.getX();
			startY = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			disX = (int) (Math.abs(event.getX()) - Math.abs(startX));
			disY = (int) (Math.abs(event.getY()) - Math.abs(startY));

			if (Math.abs(disY) > Math.abs(disX)) {
				return super.onTouchEvent(event);
			}

			if (Math.abs(disX) > 20) {
				return super.onTouchEvent(event);
			}
			endTime = System.currentTimeMillis();
			useTime = endTime - startTime;
			if (useTime < 300) {
				if (toSQMFragmentLinstener != null) {
					toSQMFragmentLinstener.tofragment();
				}
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}





}
