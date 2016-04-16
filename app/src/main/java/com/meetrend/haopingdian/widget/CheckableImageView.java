package com.meetrend.haopingdian.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.ImageView;

/***
 * @see android.widget.CheckedTextView
 *
 */
public class CheckableImageView extends ImageView implements Checkable {
	private boolean mChecked = false;
	private OnCheckedChangeListener mOnCheckedChangeListener;
    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

	 
	public CheckableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setChecked(mChecked);
	}

	public CheckableImageView(Context context) {
		this(context, null);
	}

	@Override
	public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
        }
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

    @Override
	public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
    
    public static interface OnCheckedChangeListener {
        public void onCheckedChanged(Checkable buttonView, boolean isChecked);
    }
    
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }
}