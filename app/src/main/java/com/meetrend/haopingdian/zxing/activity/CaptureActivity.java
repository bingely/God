package com.meetrend.haopingdian.zxing.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.BaseActivity;
import com.meetrend.haopingdian.activity.PayResultActivity;
import com.meetrend.haopingdian.activity.WeiXinPayingActivity;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.PlaceOrderEntity;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.FinishOrderManagerEvent;
import com.meetrend.haopingdian.event.ScanEvent;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.zxing.camera.CameraManager;
import com.meetrend.haopingdian.zxing.decode.DecodeThread;
import com.meetrend.haopingdian.zxing.utils.BeepManager;
import com.meetrend.haopingdian.zxing.utils.CaptureActivityHandler;
import com.meetrend.haopingdian.zxing.utils.InactivityTimer;

import de.greenrobot.event.EventBus;

import java.io.IOException;
import java.lang.reflect.Field;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

/**
 * 客户，微信支支付   二维码扫描
 * 
 * @author 肖建斌
 * 
 */

public class CaptureActivity extends BaseActivity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	/**
	 * 返回
	 */
	@ViewInject(id = R.id.actionbar_home, click = "back")
	ImageView backImageView;
	/**
	 * 标题
	 */
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	
	/**
	 * 编码
	 */
	@ViewInject(id = R.id.editview)
	EditText editView;
	
	/**
	 * 确定
	 */
	@ViewInject(id = R.id.sureview,click = "toScanViewClick")
	TextView sureView;
	
	@ViewInject(id = R.id.layout_view)
	LinearLayout layoutView;
	
	/**
	 * 顶部提示语
	 */
	@ViewInject(id = R.id.tophintview)
	TextView topHintView;
	
	@ViewInject(id = R.id.tv_tip)
	TextView tvTipView;
	
	

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;

	private SurfaceView scanPreview = null;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	private boolean isHasSurface = false;
	
	/**1客户扫码2微信扫码3商品扫码*/
	private int type;
	
	/**
	 * 订单id
	 */
	private String orderId;
	
	/**
	 * 扫描结果
	 */
	private String scanText;
	
	
	/**
	 * 微信支付优惠金额
	 */
	private  double discountamount;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_qr_scan);
		FinalActivity.initInjectedView(this);
		
		titleView.setText("微信支付");
		
		Intent intent = getIntent();
		type = intent.getIntExtra("type", -1);
		
		//客户扫码信息
		if (type == 1) {
			titleView.setText("客户二维码");
			topHintView.setText("请客户打开微信 → 门店公众号 → 我  → 会员中心");
			layoutView.setVisibility(View.GONE);
		}
		
		//微信支付
		else if (type == 2){
			discountamount = intent.getDoubleExtra("discountamount", 0.0);
			topHintView.setText("请客户打开微信 → 我 → 钱包  → 刷卡");
			orderId = intent.getStringExtra("id");
			layoutView.setVisibility(View.VISIBLE);
		}
		
		//商品扫码
		else {
			titleView.setText("条形码");
			topHintView.setVisibility(View.GONE);
			tvTipView.setText("将条码放入框内，即可自动扫描");
			layoutView.setVisibility(View.GONE);
		}
		
		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);

		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);

		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.9f);
		animation.setDuration(4500);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		scanLine.startAnimation(animation);
	}
	
	
	/**
	 * 通过输入数字码代替扫描进行微信支付
	 *
	 */
	public void toScanViewClick(View view){
		
		if (!"".equals(editView.getText().toString()) && editView.getText().toString().length() == 18) {
				
			weiXinPay(editView.getText().toString());
			
		}else {
			
			Toast.makeText(CaptureActivity.this, "格式不正确", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		cameraManager = new CameraManager(getApplication());
		handler = null;

		if (isHasSurface) {
			initCamera(scanPreview.getHolder());
		} else {
			scanPreview.getHolder().addCallback(this);
		}

		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!isHasSurface) {
			scanPreview.getHolder().removeCallback(this);
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,"*** WARNING *** surfaceCreated() gave us a null surface!");
					
		}
		if (!isHasSurface) {
			isHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isHasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 接收扫描的结果
	 * 
	 */
	public void handleDecode(final Result rawResult, Bundle bundle) {
		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();

		 scanText = rawResult.getText().toString();
		//客户扫描
		if (type == 1) {
			ScanEvent scanEvent = new ScanEvent();
			scanEvent.scanContent = scanText;
			EventBus.getDefault().post(scanEvent);
			finish();
		}
		
		//微信支付扫描
		else if(type == 2){
			weiXinPay(scanText);
			finish();
		}else {
			//商品扫码
			getScanProductsResult(scanText);
		}
		
		
		
	}
	
	
	/**
	 * 微信支付
	 * 
	 */
	private void weiXinPay(String auth_code) {

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(CaptureActivity.this).getToken());
		params.put("orderid", orderId);
		params.put("auth_code", auth_code);
		params.put("discountamount", discountamount+"");

		Callback callback = new Callback(tag, CaptureActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				String code = data.get("error_code").getAsString();
				
				
				if (code.equals("success")) {
					//交易成功
					Toast.makeText(context, "交易成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(CaptureActivity.this, PayResultActivity.class);
					startActivity(intent);
				}else if (code.equals("userpaying")) {
					//支付中
					
					EventBus.getDefault().post(new FinishOrderManagerEvent());//结束本Activity
					
					Toast.makeText(context, "用户正在输入密码", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(CaptureActivity.this, WeiXinPayingActivity.class);
					intent.putExtra("orderid", orderId);
					intent.putExtra("auth_code", scanText);
					startActivity(intent);
					
					finish();
					
				
				}else if (code.equals("transfail")) {
					//交易失败
					
					String msg = data.get("msg").getAsString();
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
					finish();
				
				}else if (code.equals("systemerror")) {
					//系统错误
					String msg = data.get("msg").getAsString();
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
					finish();
				}
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.WEIXIN_PAY, params,callback);
	}
	
	
	//获取商品扫码结果
	private String productNum;
	private void getScanProductsResult(String query){
		
		productNum = query;
		//productNum = "2916102000001";
		
		showDialog("正在获取扫码结果...");
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(CaptureActivity.this).getToken());
		params.put("orderid", orderId);
		params.put("query", query);

		Callback callback = new Callback(tag, CaptureActivity.this) {

			@Override
			public void onSuccess(String t) {
				dimissDialog();
				
				JsonParser parser = new JsonParser();
		    	JsonObject root = parser.parse(t).getAsJsonObject();
		    	isSuccess = root.get("success").getAsBoolean();
		    	JsonObject parentJsonObject = root.get("data").getAsJsonObject();
		    	com.google.gson.JsonArray data = parentJsonObject.get("data").getAsJsonArray();
				
				if (productNum.startsWith("28")) {
					
					String weightNum = productNum.substring(productNum.length()-6, productNum.length()-1);//重量编码
					StringBuilder stringBuilder = new StringBuilder(weightNum);
				    weightNum = stringBuilder.insert(2, '.').toString();
				    
				    if (weightNum.startsWith("0")) {
						weightNum = weightNum.substring(1);
					}
				    
				    if (data.size() > 0) {
				    	
				    	JsonObject jObject = (JsonObject) data.get(0);
						PlaceOrderEntity placeOrderEntity = new PlaceOrderEntity();
						placeOrderEntity.isAmountBarCode = false;
						placeOrderEntity.isGift = false;
						placeOrderEntity.products = new Products();
						
						String fname = jObject.get("FName").getAsString();
						if (TextUtils.isEmpty(fname)) {
							placeOrderEntity.products.productName = "";
						}else {
							placeOrderEntity.products.productName = fname;
						}
						
						placeOrderEntity.count = Double.parseDouble(weightNum);//数量
						placeOrderEntity.pici = new Pici();
						placeOrderEntity.pici.productId = jObject.get("FId").getAsString();
						placeOrderEntity.pici.unitName = "";//先不管
						placeOrderEntity.pici.price = jObject.get("FOfferUnitPrice").getAsString();//单价
						placeOrderEntity.pici.fullName = fname+"("+jObject.get("FModel1Value")+" "+jObject.get("FModel2Value")+")";
						
						TeaAddEvent scanProductResultEvent = new TeaAddEvent();
						scanProductResultEvent.products = placeOrderEntity.products;
						scanProductResultEvent.pici = placeOrderEntity.pici;
						scanProductResultEvent.count = Double.parseDouble(NumerUtil.saveThreeDecimal(weightNum));
						scanProductResultEvent.isScanBarCode = true;
						EventBus.getDefault().post(scanProductResultEvent);
					}else {
						showToast("返回数据为空");
					}
				    
					
				}else if (productNum.startsWith("29")) {
					
					String allMoney = productNum.substring(productNum.length()-6, productNum.length()-1);//总金额编码
					StringBuilder stringBuilder = new StringBuilder(allMoney);
					allMoney = stringBuilder.insert(3, '.').toString();
					
				    if (allMoney.startsWith("00")) {
						
				    	allMoney = allMoney.substring(2);
				    	
					}else if (allMoney.startsWith("0")) {
						
						allMoney = allMoney.substring(1);
					}
				    
				    if (data.size() > 0) {
				    	
				    	JsonObject jObject = (JsonObject) data.get(0);
						PlaceOrderEntity placeOrderEntity = new PlaceOrderEntity();
						placeOrderEntity.isAmountBarCode = true;
						placeOrderEntity.isGift = false;
						placeOrderEntity.pici = new Pici();
						placeOrderEntity.pici.productId = jObject.get("FId").getAsString();
						placeOrderEntity.pici.unitName = "";//先不管
						placeOrderEntity.pici.price = jObject.get("FOfferUnitPrice").getAsString();//单价
						
						placeOrderEntity.products = new Products();
						
						String fname = jObject.get("FName").getAsString();
						if (TextUtils.isEmpty(fname)) {
							placeOrderEntity.products.productName = "";
						}else {
							placeOrderEntity.products.productName = fname;
						}
						placeOrderEntity.pici.fullName = fname+"("+jObject.get("FModel1Value")+" "+jObject.get("FModel2Value")+")";
						placeOrderEntity.count = Double.parseDouble(allMoney) / Double.parseDouble(placeOrderEntity.pici.price);//数量
					
						TeaAddEvent scanProductResultEvent = new TeaAddEvent();
						scanProductResultEvent.products = placeOrderEntity.products;
						scanProductResultEvent.pici = placeOrderEntity.pici;
						scanProductResultEvent.count = placeOrderEntity.count;
						scanProductResultEvent.isScanBarCode = true;
						EventBus.getDefault().post(scanProductResultEvent);
						
					}else {
						showToast("数据为空");
					}
				    
				}else {
					
					if (data.size() > 0) {
						
						JsonObject jObject = (JsonObject) data.get(0);
						PlaceOrderEntity placeOrderEntity = new PlaceOrderEntity();
						placeOrderEntity.pici = new Pici();
						placeOrderEntity.pici.productId = jObject.get("FId").getAsString();
						placeOrderEntity.pici.unitName = "";//先不管
						placeOrderEntity.pici.price = jObject.get("FOfferUnitPrice").getAsString();//单价
						
						placeOrderEntity.products = new Products();
						
						String fname = jObject.get("FName").getAsString();
						if (TextUtils.isEmpty(fname)) {
							placeOrderEntity.products.productName = "";
						}else {
							placeOrderEntity.products.productName = fname;
						}
						placeOrderEntity.pici.fullName = fname+"("+jObject.get("FModel1Value")+" "+jObject.get("FModel2Value")+")";
						placeOrderEntity.count = 1.000;//数量不管
						TeaAddEvent scanProductResultEvent = new TeaAddEvent();
						scanProductResultEvent.products = placeOrderEntity.products;
						scanProductResultEvent.pici = placeOrderEntity.pici;
						scanProductResultEvent.count = 1.000;
						scanProductResultEvent.isScanBarCode = true;
						EventBus.getDefault().post(scanProductResultEvent);
						
					}
					
				}
				
				finish();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (strMsg != null) {
					showToast(strMsg);
				}
				
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.SCAN_PRODUCT, params,callback);
	}
	
	

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager,
						DecodeThread.ALL_MODE);
			}

			initCrop();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		// camera error
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("相机打开出错，请稍后重试");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}

		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public Rect getCropRect() {
		return mCropRect;
	}

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = cameraManager.getCameraResolution().y;
		int cameraHeight = cameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	public void back(View view) {
		finish();
	}

}