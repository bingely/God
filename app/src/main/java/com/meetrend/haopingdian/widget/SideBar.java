package com.meetrend.haopingdian.widget;


import com.meetrend.haopingdian.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Type;

public class SideBar extends View {

	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

	public static String[] ALPHATS1 = { "↑","★","A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
		"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
	public static String[] ALPHATS2 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
		"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"}; 
	
	public String[] b;
	
	private int choose = -1;
	private Paint paint = new Paint();

	private TextView hintTextView;

	private int defaultSize = 0;

	public void setTextView(TextView hintTextView) {
		this.hintTextView = hintTextView;
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Sidebar);
		boolean isShowMore = typedArray.getBoolean(0, true);
		if (!isShowMore) {
			b = ALPHATS2;
		}else {
			b = ALPHATS1;
		}

		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		defaultSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,11,dm);
	}

	public SideBar(Context context) {
		super(context);
	}

	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / b.length;

		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.parseColor("#111111"));
			paint.setAntiAlias(true);
			paint.setTextSize(defaultSize);
			if (i == choose) {
				paint.setColor(Color.parseColor("#000000"));
				paint.setFakeBoldText(true);
			}

			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);
		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
			if (hintTextView != null) {
				hintTextView.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			setBackgroundResource(R.drawable.alphabet_tableview_bg);
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(b[c]);
					}
					if (hintTextView != null) {
						hintTextView.setText(b[c]);
						hintTextView.setVisibility(View.VISIBLE);
					}

					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}