package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.faceicon.FaceiconHandler;

public class FaceiconTextView extends TextView {
	private static final String TAG = FaceiconTextView.class.getSimpleName();
	public FaceiconTextView(Context context) {
		super(context);
		init(null);
	}

	public FaceiconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public FaceiconTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	private int mFaceiconSize;
	private void init(AttributeSet attrs) {
	      if (attrs == null) {
	            mFaceiconSize = (int) getTextSize();
	        } else {
	            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Faceicon);
	            mFaceiconSize = (int) a.getDimension(R.styleable.Faceicon_faceiconSize, getTextSize());
	            a.recycle();
	        }
	        setText(getText());
	}
	
    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        FaceiconHandler.addEmojis(getContext(), builder, mFaceiconSize);
        super.setText(builder, type);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
    	mFaceiconSize = pixels;
    }
}