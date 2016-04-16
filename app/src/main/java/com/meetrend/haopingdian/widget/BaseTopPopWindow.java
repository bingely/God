package com.meetrend.haopingdian.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.UISpinnerAdapter;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 自定义顶部popwindow
 */
public class BaseTopPopWindow extends FrameLayout {

    private TranslateAnimation yInAnimation;
    private TranslateAnimation yOutAnimation;

    public TextView titleView;//标题栏

    public LinearLayout parentContainer;
    public LinearLayout popContainer;
    public ListView mListView;
    private View maskView;
    public BaseAdapter mAdapter;


    public void setTitleView(TextView titleView) {
        this.titleView = titleView;
    }

    /**
     * maskview的click回调
     */
    public ToggleValueCallback toggleback;

    public void setCallback(ToggleValueCallback toggleback){
        this.toggleback = toggleback;
    }

    public interface ToggleValueCallback{
        void callback(boolean value);
    }

    public BaseTopPopWindow(Context context){
        super(context);
        initPop(context);
    }

    public BaseTopPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPop(context);
    }

    public BaseTopPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPop(context);
    }


    public void initPop(Context context){

        View containerView = LayoutInflater.from(context).inflate(R.layout.topwindow_layout,null);
        parentContainer = (LinearLayout) containerView.findViewById(R.id.container);
        popContainer = (LinearLayout) containerView.findViewById(R.id.container_linear);

        mListView = (ListView) containerView.findViewById(R.id.contentlisview);
        maskView = (View)containerView.findViewById(R.id.maskview);

        maskView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                StartOutAnimation();
                //回调通知MainActivity的toggle值
                if (null != toggleback) {
                    toggleback.callback(false);
                }
            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        addView(containerView, params);
    }

    public int getPopContentViewVisibility(){

        if (parentContainer.getVisibility() == View.GONE)
            return  View.GONE;
        else
            return  View.VISIBLE;
    }

    /**
     * 下拉动画
     */
    public void startInAnimation(){
        //刷新数据
        if (null != mAdapter)
            mAdapter.notifyDataSetChanged();

        if (yInAnimation != null) {
            yInAnimation.reset();
        }

        yInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        yInAnimation.setDuration(300);
        yInAnimation.setFillAfter(true);
        //yInAnimation.setInterpolator(new AccelerateInterpolator());
        parentContainer.setVisibility(View.VISIBLE);//设置可显示

        parentContainer.startAnimation(yInAnimation);
        yInAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                maskView.setVisibility(View.VISIBLE);
            }
        });
    }


    public void StartOutAnimation(){

        if (yOutAnimation != null) {
            yOutAnimation.reset();
        }

        yOutAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        yOutAnimation.setDuration(300);
        yOutAnimation.setFillAfter(true);
        // yOutAnimation.setInterpolator(new AccelerateInterpolator());
        parentContainer.startAnimation(yOutAnimation);

        yOutAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                maskView.setVisibility(View.GONE);

                //补间动画
//                AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f,0.0f);
//                alphaAnimation.setDuration(200);
//                alphaAnimation.setFillAfter(true);
//                maskView.startAnimation(alphaAnimation);
//                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        maskView.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });


                //属性动画
//                ObjectAnimator aphatanim = ObjectAnimator.ofFloat(maskView,"alpha",0.5f,0.0f);
//                aphatanim.setDuration(150);
//                aphatanim.start();
//                aphatanim.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animator) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animator) {
//                        maskView.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animator) {
//                    }
//                    @Override
//                    public void onAnimationRepeat(Animator animator) {
//                    }
//                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                parentContainer.setVisibility(View.GONE);
            }
        });
    }


}
