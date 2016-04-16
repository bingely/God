
package com.meetrend.haopingdian.widget;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.faceicon.FaceiconHandler;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

public class FaceiconEditText extends EditText {
    private int mEmojiconSize;

    public FaceiconEditText(Context context) {
        super(context);
        mEmojiconSize = (int) getTextSize();

    }

    public FaceiconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FaceiconEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Faceicon);
        mEmojiconSize = (int) a.getDimension(R.styleable.Faceicon_faceiconSize, getTextSize());
        a.recycle();
        setText(getText());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    	FaceiconHandler.addEmojis(getContext(), getText(), mEmojiconSize);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}