
package com.meetrend.haopingdian.widget;

import java.util.ArrayList;
import java.util.List;
import com.meetrend.haopingdian.bean.InCommeBean;
import com.meetrend.haopingdian.bean.Point;
import com.meetrend.haopingdian.util.NumerUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


public class LineChartView extends View{

	private final static int HORIENT_LINE_NUM = 5;

	private  float Y_KEDU_WIDTH = 0;

	private final static float X_NUM = 7;

	private Paint horientLinePaint;
	private Paint linePaint;
	private Paint textPaint;//x轴坐标画笔
	private Paint ytextPaint;//y轴坐标画笔
	private Paint measurePaint;
	private Paint circlePaint;

	//水平间隔线间距
	private double horient_line_distance;
	//横坐标高度
	private float bottom_x_height;
	private DisplayMetrics dm;

	private float mTextSize;


	private boolean isInit = true;

	private List<Point> points;

	private float width;
	private float height;

	private List<String> xTimeValues;
	private List<Double> yValues;

	long minValue = 0;
	long maxValue = 0;
	long ykeduDivide;//y轴刻度值间距

	public boolean isDrawScrollLine = false;

	public Paint pathPaint;

	int paddingLeft ;
	int paddingRight ;
	int paddingTop ;
	int paddingBottom;
	//y刻度值与间隔线的间距
	private float y_textWithLine_distance = 0;
	private float saveYkeduWidth = 0.0f;

	public void setIsDrawScrollLine(boolean isDrawScrollLine){
		this.isDrawScrollLine = isDrawScrollLine;
	}

	private List<InCommeBean> inCommeBeans ;
	private InCommeBean selectComeBean;

	private boolean isReallyPoint;

	public void setIncommeBeans(List<InCommeBean> inCommeBeans){
		this.inCommeBeans = inCommeBeans;
	}

	public List<InCommeBean> getInCommeBeans(){
		  return  inCommeBeans;
	}

	public void setChatLineIsInit(){
		isInit = true;
	}


	public LineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		dm = getResources().getDisplayMetrics();
		horientLinePaint = new Paint();
		horientLinePaint.setColor(Color.parseColor("#cbf1cb"));
		horientLinePaint.setAntiAlias(true);
		horientLinePaint.setStrokeWidth(2);

		textPaint = new TextPaint();
		textPaint.setColor(Color.parseColor("#797979"));
		textPaint.setAntiAlias(true);
		mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm);
		textPaint.setTextSize(mTextSize);

		ytextPaint = new TextPaint();
		ytextPaint.setColor(Color.parseColor("#797979"));
		ytextPaint.setAntiAlias(true);
		ytextPaint.setTextSize(mTextSize);
		ytextPaint.setTextAlign(Paint.Align.RIGHT);

		linePaint = new Paint();
		linePaint.setColor(Color.parseColor("#33ff33"));
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(7);

		measurePaint = new Paint();
		measurePaint.setColor(Color.RED);
		measurePaint.setAntiAlias(true);
		measurePaint.setStrokeWidth(5);

		circlePaint = new Paint();
		circlePaint.setColor(Color.RED);
		circlePaint.setAntiAlias(true);
		circlePaint.setStyle(Style.STROKE.FILL);
		circlePaint.setStrokeWidth(10);

		pathPaint = new Paint();


		bottom_x_height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, dm);
		y_textWithLine_distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		Log.i("TAG","width== "+width);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		Log.i("TAG", "height== " + height);

		horient_line_distance = (height-bottom_x_height - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm))/4.0f;

		//确定大小
		setMeasuredDimension((int) width, (int) height);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		 paddingLeft = getPaddingLeft();
		 paddingRight = getPaddingRight();
		 paddingTop = getPaddingTop();
		 paddingBottom = getPaddingBottom();

		canvas.save();
		canvas.clipRect(paddingLeft, 0 + paddingTop, width - paddingRight, height - paddingBottom);
		canvas.drawColor(Color.WHITE);

		if (null == yValues || null == xTimeValues) {
			return;
		}

		ykeduDivide = maxValue/4;
		int dataSize = yValues.size();

		//拼装y轴刻度值
		long ykeduStr = 0;
		List<String> ykeduList = new ArrayList<String>();
		for (int i = 0; i < HORIENT_LINE_NUM; i++) {
			if (ykeduDivide == 0)
				ykeduStr = i;
			else{
				ykeduStr = i*ykeduDivide;
			}
			ykeduList.add(ykeduStr+"");
		}

		float textHeight=0.0f;

		//计算刻度长度最长的长度值
		for (int i = 0; i < HORIENT_LINE_NUM; i++) {
			if (i == 0){
				textHeight = ytextPaint.ascent()+ ytextPaint.descent();
			}
			float length = ytextPaint.measureText(ykeduList.get(i)) + y_textWithLine_distance;
			if (length > Y_KEDU_WIDTH){
				Y_KEDU_WIDTH = length;
				saveYkeduWidth = length;
			}
		}

		//水平间隔线绘制
		for (int i = 1; i < HORIENT_LINE_NUM + 1; i++) {
			double lineY = (height-bottom_x_height) - (i - 1) * horient_line_distance;
			canvas.drawLine(0 + Y_KEDU_WIDTH + paddingLeft, (float)lineY, width-paddingRight, (float)lineY, horientLinePaint);
		}

		float xpx = 0;
		String ystr;
		//绘制纵坐标刻度
		for (int i = 0; i < HORIENT_LINE_NUM; i++) {
			ystr = ykeduList.get(i);
			double ypx = height - i * horient_line_distance - bottom_x_height - textHeight/2;
			canvas.drawText(ystr, xpx + paddingLeft + Y_KEDU_WIDTH - y_textWithLine_distance, (float)ypx, ytextPaint);
		}

		points = new ArrayList<Point>();
		InCommeBean inCommeBean = null;
		double pointY =0.0f;

		double pxValueScale = (HORIENT_LINE_NUM -1)*horient_line_distance/(double)maxValue;///纵坐标1代表屏幕多少个像素

		for (int i = 0; i < dataSize; i++) {
			if (ykeduDivide == 0) {
				pointY = height-bottom_x_height;
			}else {
				 pointY = height - bottom_x_height - yValues.get(i) * pxValueScale ;//数据对应坐标点的y坐标
			}
			float pointX = paddingLeft + Y_KEDU_WIDTH + i * (width - Y_KEDU_WIDTH-paddingLeft - paddingRight)/dataSize;

			if (null != inCommeBeans && inCommeBeans.size() > 0) {
				inCommeBean = inCommeBeans.get(i);
				inCommeBean.x = pointX;
				inCommeBean.y = (float)pointY;
			}
			Point point = new Point(pointX, pointY,yValues.get(i));
			points.add(point);
		}

		//平均每个坐标占的位置
		float avgWdith = (width - Y_KEDU_WIDTH - paddingLeft - paddingRight)/ X_NUM;
		//绘制x坐标
		for (int i = 0; i < X_NUM; i++) {
			float pointX = Y_KEDU_WIDTH + i * avgWdith + paddingLeft;
			canvas.drawText(xTimeValues.get(i).substring(5, 10), pointX, height - bottom_x_height / 3, textPaint);
		}

		Path path = new Path();
		path.moveTo((float)points.get(0).xValue, (float)points.get(0).yValue);

		for (int i = 1; i < dataSize ; i++) {
			path.lineTo((float)points.get(i).xValue, (float)points.get(i).yValue);
		}
		canvas.drawPath(path, linePaint);

		Y_KEDU_WIDTH = 0;

		if (isInit && null !=inCommeBeans && inCommeBeans.size() > 0) {
			selectComeBean = inCommeBeans.get(inCommeBeans.size() - 1);
			canvas.drawLine(selectComeBean.x, height-bottom_x_height, selectComeBean.x,
					height - (float)horient_line_distance * (HORIENT_LINE_NUM-1)-bottom_x_height, measurePaint);
			canvas.drawCircle(selectComeBean.x, selectComeBean.y, 10, circlePaint);
			isInit = false;
			return;
		}

		if (isDrawScrollLine && null !=inCommeBeans && inCommeBeans.size() > 0) {
			canvas.drawLine(selectComeBean.x, height-bottom_x_height, selectComeBean.x,
					height - (float)horient_line_distance * (HORIENT_LINE_NUM-1)-bottom_x_height, measurePaint);
			if (isReallyPoint) {
				canvas.drawCircle(selectComeBean.x, selectComeBean.y, 10, circlePaint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				if (!isDrawScrollLine) {
					return false;
				}

				invalidate(event);

				getParent().requestDisallowInterceptTouchEvent(true);

				break;

			case MotionEvent.ACTION_MOVE:

				invalidate(event);

				getParent().requestDisallowInterceptTouchEvent(true);

				break;

			case MotionEvent.ACTION_UP:
				getParent().requestDisallowInterceptTouchEvent(false);

				break;

			default:
				break;
		}

		return true;
	}


	public void setYData(List<Double> datas){
		yValues = datas;
	}

	public void setYMaxValue(double maxValue){
		this.maxValue = (int)maxValue;
	}

	public void setYMinValue(double minValue){
		this.minValue = (int)minValue;
	}


	public void setTime(List<String> values){

		xTimeValues = values;
		invalidate();
	}

	private void invalidate(MotionEvent event){

		float curentX = event.getX();
		float curentY = event.getY();

		if (curentX < saveYkeduWidth + y_textWithLine_distance + paddingLeft || curentX > width - paddingRight) {
			return;
		}

		if (curentY > height + bottom_x_height || curentY < 0) {
			return;
		}

		selectComeBean = new InCommeBean();
		selectComeBean.x = curentX;
		selectComeBean.y = curentY;

		int size = inCommeBeans.size();

		for(int i = 0 ; i < size ;i++){

			InCommeBean point = inCommeBeans.get(i);

			if (Math.abs(selectComeBean.x - point.x) <= 5) {
				selectComeBean = point;
				if (showLinstener != null) {
					showLinstener.show(point);
					isReallyPoint = true;
					invalidate();
					return;
				}
			}
		}
		isReallyPoint = false;
		invalidate();
	}

	public ShowLinstener showLinstener;

	public interface ShowLinstener{

		void show(InCommeBean point);
	}

	public void setLinstener(ShowLinstener showLinstener){
		if (showLinstener != null) {
			this.showLinstener = showLinstener;
		}
	}


}
