package com.meetrend.haopingdian.widget;

import com.meetrend.haopingdian.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class ProgressWebView extends WebView{

	private  ProgressBar progressbar;
	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
	        Resources resources = getContext().getResources();
	        Drawable drawbg = getResources().getDrawable(R.color.yellow);
	        progressbar.setProgressDrawable(drawbg);
	        progressbar.setVisibility(View.GONE);
	        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 12, 0, 0));
	        addView(progressbar);
	        setWebChromeClient(new WebChromeClient());
	}
	
	public class WebChromeClient extends android.webkit.WebChromeClient {
		
		@Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                    progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }
	

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

}