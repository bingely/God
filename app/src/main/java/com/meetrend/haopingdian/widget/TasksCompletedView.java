package com.meetrend.haopingdian.widget;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.meetrend.haopingdian.tool.Utils;
import com.meetrend.haopingdian.util.NumerUtil;

/**
 * 
 * @author 肖建斌
 *
 */
@SuppressLint("DrawAllocation")
public class TasksCompletedView extends View {
	
    // 画圆环的画笔
    private Paint mOnlineRingPaint;
    // 门店画圆环的画笔
    private Paint mOrderRingPaint;
    // 画字体的画笔
    private Paint mTextPaint;
    
    // 圆环门店收入颜色
    private int mOrderRingColor;
    //圆形线上收入颜色
    private int mOnlineRingColor;
    //没有值的收入颜色
    private int noDataRingColor;
    
    // 半径
    private float mRadius;
    // 圆环宽度
    private float mStrokeWidth;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    
    // 字的长度
    private float mTxtWidth;
    // 字的长度
    private float mTxtWidth1;
    // 字的高度
    private float mTxtHeight;
    // 字的颜色
    private int mTxtColor;
    
    
    // 当前进度
    private int mCurrentProgress;
    
    //private MyThread t = null;
    
    //圆环的范围
    private RectF oval = new RectF();
    
   public TasksCompletedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // 获取自定义的属性
        mRadius = Utils.adapterPx(context, 120);
        mStrokeWidth = Utils.adapterPx(context, 40);
        mOrderRingColor =  Color.rgb(5, 164, 168);
        mOnlineRingColor = Color.rgb(239, 70, 39);
        noDataRingColor = Color.rgb(110,106,106);
      
        mTxtColor = Color.BLACK;
        initVariable();
        
        //t = new MyThread();
    }

   private void initVariable() {
       
	   mOnlineRingPaint = new Paint();
	   mOnlineRingPaint.setAntiAlias(true);
	   mOnlineRingPaint.setColor(mOrderRingColor);
	   mOnlineRingPaint.setStyle(Paint.Style.STROKE);
	   mOnlineRingPaint.setStrokeWidth(mStrokeWidth);
       
       mOrderRingPaint = new Paint();
       mOrderRingPaint.setAntiAlias(true);
       mOrderRingPaint.setStyle(Paint.Style.STROKE);
       mOrderRingPaint.setStrokeWidth(mStrokeWidth);
        
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(mTxtColor);
        mTextPaint.setTextSize(mRadius / 5);
        
       FontMetrics fm = mTextPaint.getFontMetrics();
       mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
       
       
           
   }

   @Override
    protected void onDraw(Canvas canvas) {

	   mXCenter = getWidth() / 2;
       mYCenter = getHeight() / 2;
       
	   oval.left = (mXCenter - mRadius);
       oval.top = (mYCenter - mRadius);
       //重新设置一下矩阵的范围大小
       oval.right = mXCenter + mRadius;
       oval.bottom = mYCenter + mRadius;
       
   	    //线上视图
	   canvas.drawArc(oval, -90, onlinePress * 360, false, mOnlineRingPaint);
	   //门店收入视图
 	   canvas.drawArc(oval, -90 + onlinePress * 360, orderPress * 360, false, mOrderRingPaint);
 	  
       String txtTotal = "总收入";
       mTxtWidth = mTextPaint.measureText(txtTotal, 0, txtTotal.length());
       FontMetrics fm = mTextPaint.getFontMetrics();
       float mTxtHeight = (float)Math.ceil(fm.descent - fm.top)+ 2;
       String txt = "";
       try {
             txt = NumerUtil.getNum(sum + "") + "元";
             mTxtWidth1 = mTextPaint.measureText(txt, 0, txt.length());
	   } catch (Exception e) {
		  e.printStackTrace();
	   }
         
       canvas.drawText(txtTotal, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4-mTxtHeight/2, mTextPaint);
       if(sum == 0){
    	 canvas.drawText("0.00元", mXCenter - mTxtWidth1 / 2, mYCenter + mTxtHeight / 4+mTxtHeight/2, mTextPaint);
       }else{
    	 canvas.drawText(txt, mXCenter - mTxtWidth1 / 2, mYCenter + mTxtHeight / 4+mTxtHeight/2, mTextPaint);
       }
          
    }
   
    float onlinePress;
    float sum;
    float orderPress;
    public void setProgress(float online,float order,String total) {
    	
    	//online = 1000.0f;
    	//order = 1500.0f;
    	//total = "2500";
    	
    	sum = Float.parseFloat(total);
    	onlinePress = online / sum ;
		orderPress = order / sum ;
    	if(sum == 0){
    		orderPress = 1.0f;
    	}
         //t.start();
    	 handler.sendEmptyMessage(0);
    	 
    	 //ObjectAnimator onlineObjectAnimator = ObjectAnimator.ofFloat(this, "xjb", 0.0f,onlinePress);
    	// ObjectAnimator orderObjectAnimator = ObjectAnimator.ofFloat(this, "xjb", onlinePress,1- orderPress);
    }
    
    //实现动画效果线程
//    class MyThread extends Thread{
//
//		@Override
//		public void run() {
//			
//			while(true){
//				 if(mCurrentProgress<mTotalProgress){
//					 handler.sendEmptyMessage(0);
//					 mCurrentProgress += 1;
//				 }else{
//					 break;
//				 }
//				try { 
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//    	
//    }
    

	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(sum == 0){
				 mOrderRingPaint.setColor(noDataRingColor);
		       }else{
		    	   mOrderRingPaint.setColor(mOnlineRingColor);
		       }
			postInvalidate();
		}
		
	};

}
