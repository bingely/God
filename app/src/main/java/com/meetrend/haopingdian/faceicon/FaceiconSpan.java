package com.meetrend.haopingdian.faceicon;

import com.meetrend.haopingdian.tool.LogUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;

public class FaceiconSpan extends DynamicDrawableSpan {
    private final Context mContext;
    private final int mResourceId;
    private final int mSize;
    private Drawable mDrawable;

    public FaceiconSpan(Context context, int resourceId, int size) {
        super();
        mContext = context;
        mResourceId = resourceId;
        mSize = size;
    }

    public Drawable getDrawable() {
        if (mDrawable == null) {
            try {
                mDrawable = mContext.getResources().getDrawable(mResourceId);
                int size = mSize;
                mDrawable.setBounds(0, 0, size, size);
            } catch (Exception e) {
                LogUtil.e("FaceiconSpan", e.getMessage());
            }
        }
        return mDrawable;
    }
}
