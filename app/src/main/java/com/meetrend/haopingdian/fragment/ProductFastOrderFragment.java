package com.meetrend.haopingdian.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.BalancePayActivity;
import com.meetrend.haopingdian.activity.CurrencyPayActivity;
import com.meetrend.haopingdian.activity.NewSelectExcutorActivity;
import com.meetrend.haopingdian.activity.StoreOrderChooseTypeListActivity;
import com.meetrend.haopingdian.activity.SwipingNumActivity;
import com.meetrend.haopingdian.adatper.DiscountAdapter;
import com.meetrend.haopingdian.bean.OnlineOrderDetail;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.PlaceOrderEntity;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.bean.Voucher;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.FinishOrderManagerEvent;
import com.meetrend.haopingdian.event.PayTypeEvent;
import com.meetrend.haopingdian.event.PeiSongTypeEvent;
import com.meetrend.haopingdian.event.ScanEvent;
import com.meetrend.haopingdian.event.SendEnableMsgEvent;
import com.meetrend.haopingdian.event.SendExcutorNameEvent;
import com.meetrend.haopingdian.event.SendTeaListEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.DialogUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.util.PhoneUtil;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;
import com.meetrend.haopingdian.util.DialogUtil.FinishListener;
import com.meetrend.haopingdian.widget.GiveMoneyEditView;
import com.meetrend.haopingdian.widget.GiveMoneyEditView.MoneyChangerListener;
import com.meetrend.haopingdian.widget.GiveMoneyEditView.ToSQMFragmentLinstener;
import com.meetrend.haopingdian.zxing.activity.CaptureActivity;
import de.greenrobot.event.EventBus;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 订单详情
 * 
 * @author 肖建斌
 * 
 *         记录：本功能开发遇倒的问题： 在布局文件设置 oneLinear 和 moreRelative 的visibility
 *         时,重复切换时moreRelative的点击事件有问题
 * 
 */
public class ProductFastOrderFragment extends BaseFragment {

	private final static String TAG = ProductFastOrderFragment.class.getName();
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;

	/**
	 * 单个产品视图
	 * 
	 */
	@ViewInject(id = R.id.one_product_layout)
	LinearLayout oneLinear;
	@ViewInject(id = R.id.tea_imgview)
	ImageView oneTeaImageView;

	@ViewInject(id = R.id.tea_name_view)
	TextView oneTeaNameView;

	@ViewInject(id = R.id.tea_price)
	TextView oneTeaPrice;

	@ViewInject(id = R.id.tea_num_view)
	TextView oneTeaNum;

	@ViewInject(id = R.id.tea_total_price_view)
	TextView oneTeaNumAllMomeyView;

	/**
	 * 多个产品视图
	 */
	@ViewInject(id = R.id.more_product_container)
	RelativeLayout moreRelative;
	@ViewInject(id = R.id.more_product_img_layout)
	LinearLayout imgsContainer;

	/**
	 * 显示执行人view
	 */
	@ViewInject(id = R.id.dianxiaoer_name_edit)
	TextView excutorPeopleView;

	/**
	 * 选择执行人view
	 */
	@ViewInject(id = R.id.choose_dianxiaoer_view, click = "chooseExcutorClick")
	TextView chooseExcutorView;

	/**
	 * 显示执行人
	 */
	@ViewInject(id = R.id.dianxiaoer_name_edit)
	TextView showExcutorView;

	/**
	 * 客户名字
	 */
	@ViewInject(id = R.id.customer_name_edit)
	EditText customerNameView;

	/**
	 * 客户电话号码
	 */
	@ViewInject(id = R.id.customer_phone_edit)
	EditText phoneEditView;

	/**
	 * 二维码扫描按钮
	 */
	@ViewInject(id = R.id.scan_view, click = "scanClick")
	ImageView scanImg;

	/**
	 * 积分
	 */
	@ViewInject(id = R.id.jifen_edit)
	EditText jifenEditText;
	
	//用户可用积分
	@ViewInject(id = R.id.can_use_points)
	TextView canUsePointView;
	
	/**
	 * 积分视图container
	 */
	@ViewInject(id = R.id.jifen_layout)
	LinearLayout jifenLayout;

	/**
	 * 优惠券
	 */
	@ViewInject(id = R.id.choose_jifen_quan_view)
	TextView jifenQuanView;

	/**
	 * 备注
	 */
	@ViewInject(id = R.id.beizhu_edit)
	EditText beizhuEidt;
	
	/**
	 * 送货人
	 */
	@ViewInject(id = R.id.kuaidi_container)
	LinearLayout kuaiDiDetailLayout;
	
	/**
	 * 送货地址
	 */
	@ViewInject(id = R.id.address_edit)
	EditText sendAddressEdit;

	/**
	 * 合计商品金额
	 */
	@ViewInject(id = R.id.all_products_money_view)
	TextView heJiView;

	/**
	 * 应收金额
	 */
	@ViewInject(id = R.id.detail_all_products_give_money_view)
	GiveMoneyEditView yingShouEdit;

	/**
	 * 底部栏实收金额
	 */
	@ViewInject(id = R.id.detail_bottom_should_givew_money_view)
	TextView bottomShouldGiveMoneyView;

	/**
	 * 结账
	 */
	@ViewInject(id = R.id.confirm_order, click = "payClick")
	TextView bottomSureBtn;
	
	/**
	 * 产品数量
	 */
	@ViewInject(id = R.id.product_count_view)
	TextView teaCountView;
	
	/**
	 * 选择优惠券
	 */
	@ViewInject(id = R.id.choose_point_discount_layout,click = "chooseDiscountClick")
	LinearLayout chooseDiscountLayout;
	
	
	/**
	 * 添加商品
	 */
	@ViewInject(id = R.id.add_tea_layout, click = "addProductsClick")
	RelativeLayout chooseTeaLayout;
	
	//配送方式
	@ViewInject(id = R.id.peisong_type_view, click = "peisongTypeChooseClick")
	TextView peisongView;
	//支付方式
	@ViewInject(id = R.id.pay_type_view, click = "payTyeChooseClick")
	TextView payTypeView;
	
	@ViewInject(id = R.id.right_arrow_icon)
	ImageView rightArrowImg;
	
	private String userPoints;
	
	/**
	 * 购买的茶品数据
	 */
	private ArrayList<PlaceOrderEntity> teaList = new ArrayList<PlaceOrderEntity>();

	/**
	 * Afinal 图片加载类
	 */
	private FinalBitmap finalBp;

	/**
	 * 是否已经扫描用户信息成功
	 * 
	 * 若为true,则监听phoneEditView，反之不监听
	 */
	private boolean isScanSuccess;

	/**
	 * 商品明细总金额
	 */
	private String totalMoney;

	/**
	 * 商品实收金额
	 */
	private String shiShouMoney;
	

	/**
	 * 支付方式
	 * 
	 * 默认现金支付
	 */
	private String payTye = "";
	
	/**
	 * 送货方式
	 */
	private String sendType = "";
	
	/**
	 * 标识从哪个界面进行跳转过来进行结账
	 * 1：从订单管理待付款详情界面进入结账
	 * 0：直接从店内下单依次果料结账
	 */
	private int from_pay = 0;
	
	/**
	 * 标识1从订单详情进入2其他
	 */
	private int fromDetail = -1;
	
	private OnlineOrderDetail onlineOrderDetail;
	
	/**
	 * 优惠金额
	 */
	private double discountamount;
	
	//应收金额
	private String avaliableAmount;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_order_tea_detail,
				container, false);
		mActivity = this.getActivity();
		EventBus.getDefault().register(this);
		FinalActivity.initInjectedView(this, rootView);
		mBarTitle.setText("店内收银");
		yingShouEdit.setEnabled(false);
		rightArrowImg.setVisibility(View.VISIBLE);
		kuaiDiDetailLayout.setVisibility(View.GONE);
		
		return rootView;
	}
	
	//配送方式选择
	public void peisongTypeChooseClick(View view){
		
		Intent intent = new Intent(getActivity(), StoreOrderChooseTypeListActivity.class);
		intent.putExtra("chooseValue", peisongView.getText().toString());//配送方式
		intent.putExtra("chooseType", 1);//标识是配送方式
		startActivity(intent);
	}
	
	//支付方式选择
	public void payTyeChooseClick(View view){
		
		Intent intent = new Intent(getActivity(), StoreOrderChooseTypeListActivity.class);
		intent.putExtra("chooseValue", payTypeView.getText().toString());//配送方式
		intent.putExtra("chooseType", 2);//标识是配送方式
		startActivity(intent);
	}
	
	//dialog选择优惠券
	DiscountChooseDialog discountDiaog = null;
	
	public void chooseDiscountClick(View view){
		
		if (fromDetail == -1) {
			
			if (null == mVoucherList) {
				
				getVouerListRequest();
			}else {
				
				if (null != mVoucherList && mVoucherList.size() > 0) {
					
					if (null != discountDiaog) {
						discountDiaog.show();
					}
				}
			}
		}
		
	}
	
	//配送方式选择返回值
	public void onEventMainThread(PeiSongTypeEvent event) {
		sendType = event.typeVlaue;
		peisongView.setText(sendType);
		
		if (sendType.equals("快递") && kuaiDiDetailLayout.getVisibility() == View.GONE) {
			kuaiDiDetailLayout.setVisibility(View.VISIBLE);
		}else {
			kuaiDiDetailLayout.setVisibility(View.GONE);
		}
	}
	
	//支付方式选择返回值
	public void onEventMainThread(PayTypeEvent event) {
		
		payTye = event.typeVlaue;//发送给后台的支付标识
		payTypeView.setText(event.typeText);
	}
	
	//是否需要判断积分可用（从订单详情界面进入，标识积分编辑框不能监听）
	private boolean isNeedJudgePointsCanUse = true;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		finalBp = FinalBitmap.create(getActivity());
		finalBp.configLoadingImage(R.drawable.loading_default);
		finalBp.configLoadfailImage(R.drawable.loading_failed);
		
		chooseTeaLayout.setVisibility(View.VISIBLE);
		
		Bundle teaBundle = getArguments();
		//从订单详情进入
		if (teaBundle != null) {
			
			from_pay = teaBundle.getInt("from_pay");
			fromDetail = teaBundle.getInt("fromdetail");
			
			if (from_pay == 1) {
				
				isNeedJudgePointsCanUse = false;
				scanImg.setEnabled(false);
				
				onlineOrderDetail = App.onlineOrderDetail;//全局标量（以后要改进）
				
				if (fromDetail == 1) {
					
					jifenLayout.setVisibility(View.VISIBLE);
					jifenQuanView.setText(onlineOrderDetail.voucherName);//积分券名称
					jifenEditText.setText(onlineOrderDetail.inputPoint);//积分
					canUsePointView.setText("可用积分"+ pointBalance +"   " + "兑换金额"+pointAmount+"元");
					
					jifenLayout.setOnClickListener(null);
					jifenEditText.setEnabled(false);
					jifenQuanView.setEnabled(false);
					phoneEditView.setEnabled(false);
					
					userPoints = onlineOrderDetail.inputPoint;
					currentVoucherRecordId = onlineOrderDetail.voucherRecordId;
					pointAmount =Double.parseDouble(onlineOrderDetail.integralAmount);
					try {
						currentVoucherValue = Double.parseDouble(onlineOrderDetail.voucherValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				
				if (onlineOrderDetail.jsonArray.size() > 0) {
					chooseTeaLayout.setVisibility(View.GONE);
				}
				
				if (onlineOrderDetail.jsonArray.size() == 1) {
					oneLinear.setVisibility(View.VISIBLE);
					moreRelative.setVisibility(View.GONE);

					finalBp.configBitmapMaxHeight(oneTeaImageView.getLayoutParams().height);
					finalBp.configBitmapMaxWidth(oneTeaImageView.getLayoutParams().width);
					//第一条数据
					final com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray item = onlineOrderDetail.jsonArray.get(0);

					finalBp.display(oneTeaImageView, Server.BASE_URL
							+ item.avatarId);// 茶图片
					oneTeaNameView.setText(item.name + " "+ item.productPici);// 茶品名
					oneTeaNum.setText("数量："+ NumerUtil.saveThreeDecimal(item.quantity));//件数
					oneTeaPrice.setText("单价：¥" + NumerUtil.setSaveTwoDecimals(item.piecePrice));// 单价
					oneTeaNumAllMomeyView.setText("总价：¥" + NumerUtil.getNum(onlineOrderDetail.detailAmount));// 总价
					
					PlaceOrderEntity placeOrderEntity = new PlaceOrderEntity();
					placeOrderEntity.products = new Products();
					placeOrderEntity.products.productName = item.fullName;
					placeOrderEntity.count = 1;
					placeOrderEntity.pici = new Pici();
					placeOrderEntity.pici.unitName = item.unitName;
					placeOrderEntity.pici.productId = item.productId;
					
					placeOrderEntity.pici.price = item.piecePrice;
					teaList.add(placeOrderEntity);
					
					oneLinear.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							Bundle bundle = new Bundle();
							bundle.putParcelableArrayList("list", teaList);
							bundle.putInt("from_pay", 1);
							
							StorePlaceOrderFragment storePlaceOrderFragment = new StorePlaceOrderFragment();
							storePlaceOrderFragment.setArguments(bundle);
							
							getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.setCustomAnimations(R.anim.push_left_in, 
									R.anim.push_left_out,
									R.anim.push_right_in, R.anim.push_right_out)
							.addToBackStack(null)
							.add(R.id.quikpay_container, storePlaceOrderFragment)
							.commit();
						}
					});

				} else {
					oneLinear.setVisibility(View.GONE);
					moreRelative.setVisibility(View.VISIBLE);
					int count = onlineOrderDetail.jsonArray.size();
					teaCountView.setText(NumerUtil.saveOneDecimal(count+"")+"件");
					
					if (count > 3) {
						count = 3;
					}
					
					for (int i = 0; i < count; i++) {
						com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray entity = onlineOrderDetail.jsonArray.get(i);
						ImageView childImg = (ImageView) imgsContainer
								.getChildAt(i);
						childImg.setVisibility(View.VISIBLE);
						finalBp.configBitmapMaxHeight(childImg.getLayoutParams().height);
						finalBp.configBitmapMaxWidth(childImg.getLayoutParams().width);
						finalBp.display(childImg, Server.BASE_URL
								+ entity.avatarId);// 茶图片
					}
					
					for (com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray item : onlineOrderDetail.jsonArray) {
						
						PlaceOrderEntity placeOrderEntity = new PlaceOrderEntity();
						placeOrderEntity.products = new Products();
						placeOrderEntity.products.productName = item.fullName;
						placeOrderEntity.count = (int) Float.parseFloat(item.quantity);//件数
						placeOrderEntity.pici = new Pici();
						placeOrderEntity.pici.productId = item.productId;
						placeOrderEntity.pici.unitName = item.unitName;
						placeOrderEntity.pici.price = item.piecePrice;
						teaList.add(placeOrderEntity);
					}

					//切换到更多列表界面
					moreRelative.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
									
							Bundle bundle = new Bundle();
							bundle.putParcelableArrayList("list", teaList);
							bundle.putInt("from_pay", 1);
							StorePlaceOrderFragment storePlaceOrderFragment = new StorePlaceOrderFragment();
							storePlaceOrderFragment.setArguments(bundle);
							getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.setCustomAnimations(R.anim.push_left_in, 
									R.anim.push_left_out,
									R.anim.push_right_in, R.anim.push_right_out)
							.addToBackStack(null)
							.add(R.id.quikpay_container, storePlaceOrderFragment)
							.commit();
							
						}
					});
					
				}
				
				userId = onlineOrderDetail.userId;//客户id
				excutorUserId = onlineOrderDetail.executeUserId;//执行人id
				 
				//执行人
				//excutorPeopleView.setText(onlineOrderDetail.executeUserName);
				//客户名字
				if (null != onlineOrderDetail.userName) {
					if (onlineOrderDetail.userName.length() >= 1) {
						String familyName = onlineOrderDetail.userName.substring(0, 1);
						familyName = familyName +"**";
						customerNameView.setText(familyName);
					}
				}
				
				//客户电话
				phoneEditView.setText(onlineOrderDetail.shipPhone);
				
				//商品明细金额
				totalMoney = onlineOrderDetail.detailAmount;
				//实收金额（改价后的金额）
				avaliableAmount = shiShouMoney = (Double.parseDouble(onlineOrderDetail.receivableAmount) - pointAmount - currentVoucherValue) +"";
				//应收
				yingShouEdit.setText(NumerUtil.getNum(onlineOrderDetail.receivableAmount));
				//底部 应收金额显示
				bottomShouldGiveMoneyView.setText("应收 ：¥"+ NumerUtil.getNum(onlineOrderDetail.receivableAmount));
				//调用接口获得优惠金额
				priferenceRequest();
			}
		}
		

		// 在非扫描客户信息的情况下，
		// 监听用户输入的电话号码是否是电话号码，如果是校验正确，则发起网络请求获取客户的信息，否则不发起网络请求
		phoneEditView.addTextChangedListener(new TextWatcherChangeListener() {
			
			@Override
			public void afterTextChanged(Editable s) {

				if (!isScanSuccess) {
					if (PhoneUtil.isMobileNO(s.toString())) {

						requestCustomerNameFromPhone(s.toString());
					}
				}
			}
		});

		// 应收金额监听
		yingShouEdit.setMoneyChangeListener(new MoneyChangerListener() {
			
			@SuppressLint("ResourceAsColor") 
			@Override
			public void textChange(String money) {
				
				if (from_pay != 1) {
					
						 //检测积分是否可用
						checkPointsIsCanUse(money);
						
						if (yingShouEdit.isEnabled()) {
							
							//合法数字
						    if (NumerUtil.isFloat(money) || NumerUtil.isInteger(money)) {
						    	
						    	 bottomSureBtn.setEnabled(true);
								 totalMoney = money;//商品总金额
								 shiShouMoney = money;//应收金额
								 bottomSureBtn.setBackgroundResource(R.drawable.pay_btn_bg);
								 bottomShouldGiveMoneyView.setText("应收： ¥ " + NumerUtil.getNum(money));
								 yingShouEdit.setTextColor(Color.parseColor("#02bc00"));
						    }else {
						    	
						    	bottomSureBtn.setEnabled(false);
						    	bottomSureBtn.setBackgroundColor(Color.parseColor("#f05b72"));
						    	yingShouEdit.setTextColor(Color.RED);
							}
						}
						
				}
				
			}
		});
		
		//跳转界面的回调接口
		yingShouEdit.setToSQMFragmentLinstener(new ToSQMFragmentLinstener() {
			
			@Override
			public void tofragment() {

				if (from_pay == 1) {
					if (onlineOrderDetail.jsonArray.size() == 0) {
						showToast("请先选择商品");
						return;
					}

				}else {

					if (teaList.size() == 0) {
						showToast("请先选择商品");
						return;
					}
				}
				
				customerNameView.clearFocus();
				phoneEditView.clearFocus();
				jifenEditText.clearFocus();
				beizhuEidt.clearFocus();
				yingShouEdit.clearFocus();
				
				if (from_pay == 1) {
					getActivity().getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.push_left_in,
							R.anim.push_left_out,
							R.anim.push_right_in, R.anim.push_right_out)
					.addToBackStack(null)
					.add(R.id.quikpay_container, new InputSQMFragment()).commit();
				}else{
					getActivity().getSupportFragmentManager()
					.beginTransaction()
//					.setCustomAnimations(R.anim.push_left_in,
//												R.anim.push_left_out,
//												R.anim.push_right_in, R.anim.push_right_out)
					.addToBackStack(null)
					.add(R.id.frame_container, new InputSQMFragment()).commit();
				}
				
			}
		});
		
		//输入积分EditText监听
		jifenEditText.addTextChangedListener(new TextWatcherChangeListener(){
			
			@Override
			public void afterTextChanged(Editable s) {
				
					userPoints = s.toString();
					
					//from_pay是从订单详情的“马上付款”按钮点击进入
					if (from_pay != 1) {
						
						if (s.toString().equals("")) {
							
							chooseDiscountLayout.setEnabled(true);
					    }else {
							chooseDiscountLayout.setEnabled(false);
							//计算积分金额
							if (NumerUtil.isInteger(s.toString())) {
								
								if (Double.parseDouble(s.toString()) <= pointBalance) {
									calculatePointAmountRequest(s.toString(), yingShouEdit.getText().toString(), userId);
								}else {
									canUsePointView.setText("输入积分不能大于可用积分");
								}
								
							}
					   }
						
					}
				}
		});
	}
	
	
	//从选产品页面传递的数据
	public void onEventMainThread(SendTeaListEvent event) {
		
		Bundle bundle = event.getBundle();
		
		if (null != bundle) {
			
					from_pay = bundle.getInt("from_pay");
				
					teaList = bundle.getParcelableArrayList("tealist");
					
					if (teaList.size() > 0) {
						chooseTeaLayout.setVisibility(View.GONE);
					}else {
						chooseTeaLayout.setVisibility(View.VISIBLE);
						oneLinear.setVisibility(View.GONE);
						moreRelative.setVisibility(View.GONE);
						return;
					}
					
					teaCountView.setText(teaList.size() + "件");
					if (teaList.size() == 1) {
						oneLinear.setVisibility(View.VISIBLE);
						moreRelative.setVisibility(View.GONE);

						finalBp.configBitmapMaxHeight(oneTeaImageView.getLayoutParams().height);
						finalBp.configBitmapMaxWidth(oneTeaImageView.getLayoutParams().width);
						PlaceOrderEntity entity = teaList.get(0);

						finalBp.display(oneTeaImageView, Server.BASE_URL
								+ entity.pici.productImage);// 茶图片
						oneTeaNameView.setText(entity.pici.fullName);// 茶品名
						
						oneTeaNum.setText("数量："+ NumerUtil.saveThreeDecimal(entity.count+""));//购买数量
						
						oneTeaPrice.setText("单价：¥ " + entity.pici.price );// 单价
						double money = Double.parseDouble(entity.pici.price) * entity.count;
								
						
						oneTeaNumAllMomeyView.setText("总价：¥ " + NumerUtil.getNum(money+""));// 总价
						
						oneLinear.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Bundle bundle = new Bundle();
								bundle.putParcelableArrayList("list", teaList);
								bundle.putInt("from_pay", from_pay);
								StorePlaceOrderFragment storePlaceOrderFragment = new StorePlaceOrderFragment();
								storePlaceOrderFragment.setArguments(bundle);
								
								if (from_pay == 1) {
									getActivity()
									.getSupportFragmentManager()
									.beginTransaction()
									.setCustomAnimations(R.anim.push_left_in, 
											R.anim.push_left_out,
											R.anim.push_right_in, R.anim.push_right_out)
									.addToBackStack(null)
									.add(R.id.quikpay_container, storePlaceOrderFragment)
									.commit();
								}else{
									getActivity()
									.getSupportFragmentManager()
									.beginTransaction()
									.setCustomAnimations(R.anim.push_left_in, 
											R.anim.push_left_out,
											R.anim.push_right_in, R.anim.push_right_out)
									.addToBackStack(null)
									.add(R.id.frame_container, storePlaceOrderFragment)
									.commit();
								}
							}
						});

					} else {
						
						oneLinear.setVisibility(View.GONE);
						moreRelative.setVisibility(View.VISIBLE);
						int size = teaList.size();
						if (size > 3) {
							size = 3;
						}
						for (int i = 0; i < size; i++) {
							PlaceOrderEntity entity = teaList.get(i);
							ImageView childImg = (ImageView) imgsContainer
									.getChildAt(i);
							childImg.setVisibility(View.VISIBLE);
							finalBp.configBitmapMaxHeight(childImg.getLayoutParams().height);
							finalBp.configBitmapMaxWidth(childImg.getLayoutParams().width);
							finalBp.display(childImg, Server.BASE_URL
									+ entity.pici.productImage);// 茶图片
						}

						moreRelative.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Bundle bundle = new Bundle();
								bundle.putParcelableArrayList("list", teaList);
								bundle.putInt("from_pay", from_pay);
								
								StorePlaceOrderFragment storePlaceOrderFragment = new StorePlaceOrderFragment();
								storePlaceOrderFragment.setArguments(bundle);
								
								if (from_pay == 1) {
									getActivity()
									.getSupportFragmentManager()
									.beginTransaction()
									.setCustomAnimations(R.anim.push_left_in, 
											R.anim.push_left_out,
											R.anim.push_right_in, R.anim.push_right_out)
									.addToBackStack(null)
									.add(R.id.quikpay_container, storePlaceOrderFragment)
									.commit();
									
								}else{
									
									getActivity()
									.getSupportFragmentManager()
									.beginTransaction()
									.setCustomAnimations(R.anim.push_left_in, 
											R.anim.push_left_out,
											R.anim.push_right_in, R.anim.push_right_out)
									.addToBackStack(null)
									.add(R.id.frame_container, storePlaceOrderFragment)
									.commit();
								}
									
								
							}
						});

					}
					
					double allMoney = 0.0;
					
					for (PlaceOrderEntity entity : teaList) {
						double price = Double.parseDouble(entity.pici.price);
						double allprice = entity.count * price;
						allMoney = allMoney + allprice;
					}
					
					String money = NumerUtil.getNum(allMoney + "");
					
					totalMoney = allMoney + "";
					shiShouMoney = (allMoney - pointAmount - currentVoucherValue)+ "";
					// 合计金额
					heJiView.setText(shiShouMoney);
					// 商品应收金额
					yingShouEdit.setText(shiShouMoney);
					// 底部应收金额
					bottomShouldGiveMoneyView.setText("应收： ¥ " + shiShouMoney + "");
					
					//获得优惠金额
					priferenceRequest();
			}
	}
	
	/**
	 *  选择产品
	 *  @param view
	 */
	public void addProductsClick(View view){
		
		StorePlaceOrderFragment storePlaceOrderFragment = new StorePlaceOrderFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("from_pay", from_pay);
		bundle.putInt("isEmpty", 1);//1标识当前的数据为空
		storePlaceOrderFragment.setArguments(bundle);
		
		if (from_pay == 1) {
			getActivity()
			.getSupportFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.anim.push_left_in, 
					R.anim.push_left_out,
					R.anim.push_right_in, R.anim.push_right_out)//fragment过度动画效果
			.addToBackStack(null)
			.add(R.id.quikpay_container, storePlaceOrderFragment).commit();
		}else {
			getActivity()
			.getSupportFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.anim.push_left_in, 
					R.anim.push_left_out,
					R.anim.push_right_in, R.anim.push_right_out)
			.addToBackStack(null)
			.add(R.id.frame_container, storePlaceOrderFragment).commit();
		}
		
	}

	
	/**
	 * 
	 * 选择执行人
	 */

	public void chooseExcutorClick(View view) {

		Intent intent = new Intent(mActivity, NewSelectExcutorActivity.class);
		String dataString = excutorPeopleView.getText().toString();
		if (dataString.equals("请选择")) {
			intent.putExtra("exename", "");
		}else {
			if (excutorUserId == null) {
				excutorUserId = "";
			}
			//传送执行人id
			intent.putExtra("exename", excutorUserId);
		}
		intent.putExtra("from", 1);
		startActivity(intent);
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void onClickHome(View view) {
		getActivity().finish();
	}


	/**
	 * 店小二执行选择后回调
	 * */
	private String excutorName = "";
	private String excutorUserId = "";

	public void onEventMainThread(SendExcutorNameEvent event) {

		excutorName = event.getName();
		excutorUserId = event.getId();
		
		Log.i(TAG+"执行人id", excutorUserId);
		Log.i(TAG+"执行人名字", excutorName);
		
		showExcutorView.setText(excutorName);
	}
	
	
	/**
	 * 结束本Activity
	 */
	public void onEventMainThread(FinishOrderManagerEvent event) {

		getActivity().finish();
	}

	/**
	 * 客户信息扫描成功后回调结果
	 * */
	public void onEventMainThread(ScanEvent event) {

		Log.i(TAG + "  scan conntent", event.scanContent);

		Message message = mHandler.obtainMessage(Code.SCAN_CUSTOMER_SUCCESS,
				event.scanContent);

		mHandler.sendMessage(message);

	}
	
	/**
	 * 实收金额可以按
	 * */
	public void onEventMainThread(SendEnableMsgEvent event) {

		mHandler.sendEmptyMessage(Code.EDIT_ENABLE_SUCCESS);
	}

	/**
	 * 
	 */
	@Override
	public void processHandleMessage(Message msg) {
		super.processHandleMessage(msg);
		switch (msg.what) {
		case Code.SCAN_CUSTOMER_SUCCESS:
			jifenLayout.setVisibility(View.VISIBLE);
			requestCustomerInfo((String) msg.obj);
			break;
		case Code.EDIT_ENABLE_SUCCESS:
			rightArrowImg.setVisibility(View.GONE);
			yingShouEdit.setEnabled(true);
			yingShouEdit.requestFocus();
			break;

		default:
			break;
		}
	}

	

	/**
	 * 扫描客户信息
	 * 
	 * @param view
	 */
	public void scanClick(View view) {
		
		if (from_pay == 1) {
			if (onlineOrderDetail.jsonArray.size() == 0) {
				showToast("请先选择商品");
				return;
			}
			
		}else {
			
			if (teaList.size() == 0) {
				showToast("请先选择商品");
				return;
			}
		}
		
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘 

		Intent intent = new Intent(getActivity(), CaptureActivity.class);
		intent.putExtra("type", 1);
		startActivity(intent);
	}

	/**
	 * 结账 
	 * 
	 * */
	public void payClick(View v) {
		
	    DialogUtil dialog = new DialogUtil(getActivity());
		dialog.showPayDialog("确认结账？", "否", "是");
		dialog.setListener(new FinishListener() {
			
			@Override
			public void finishView() {
				
				if (peisongView.getText().toString().equals("自提") 
						|| peisongView.getText().toString().equals("快递")) {
					
					if (phoneEditView.getText().equals("") || customerNameView.getText().toString().equals("")) {
						showToast("客户姓名或者手机号码不能为空");
						return;
					}else if (!PhoneUtil.isMobileNO(phoneEditView.getText().toString())) {
						showToast("客户电话号码不合法");
						return;
					}
					
				}
				
				payProgress();
			}
		});
	}
	
	/**
	 * 结账流程
	 * 
	 * 如果是现金支付  跳转到现金支付找零界面
	 */
	public void payProgress(){
		
		this.showDialog();
		
		double total = 0;
		JsonArray jsonArr = null;
		if (teaList.size() <= 0) {
			this.dimissDialog();
			Toast.makeText(getActivity(), "您还没有选择茶品", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//订单管理待付款详情界面进入结账
		if (from_pay == 1) {
			 
			jsonArr = new JsonArray();
			// 产品
			for (int i = 0; i < onlineOrderDetail.jsonArray.size(); i++) {
				com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray pici = onlineOrderDetail.jsonArray.get(i);
				double count = Double.parseDouble(pici.quantity);
				if (count != 0) {
					String price = pici.piecePrice;//单价
					total += Double.parseDouble(price) * count;
					JsonObject jsonObj = new JsonObject();
					jsonObj.addProperty("productId", pici.productId);// 目前没有返回产品id,暂时不传
					jsonObj.addProperty("productPrice", pici.piecePrice);//单价
					jsonObj.addProperty("productUnitId", pici.unitId);//小单位(件)id
					jsonObj.addProperty("productNumber", pici.quantity);// 数量(件)
					
					jsonObj.addProperty("remark", "订单详情产品");
					jsonObj.addProperty("isGift", "FALSE");
					jsonObj.addProperty("subTotal", total);//商品合计
					jsonObj.addProperty("isAmountBarCode", "FALSE");
					
					jsonArr.add(jsonObj);
				}
			}
			
		}else {
			
			if (payTypeView.getText().toString().equals("余额")) {
				
				if (phoneEditView.getText().toString().equals("")) {
					showToast("客户电话号码不能为空");
					dimissDialog();
					return;
				}
				
				if (!PhoneUtil.isMobileNO(phoneEditView.getText().toString())) {
					showToast("客户电话号码不合法");
					dimissDialog();
					return;
				}
			}
			
			//直接店内下单
			for (int i = 0; i < teaList.size(); i++) {
				if (teaList.get(i).products == null) {
					showToast("请选择茶品");
					this.dimissDialog();
					return;
				} else {
					if (teaList.get(i).count == 0) {
						showToast("请选择当前茶品购买数量");
						this.dimissDialog();
						return;
					}
				}
			}

			
			jsonArr = new JsonArray();
			// 产品
			
			Pici pici;
			PlaceOrderEntity placeOrderEntity;
			for (int i = 0; i < teaList.size(); i++) {
				double count = teaList.get(i).count;
				if (count != 0) {
					placeOrderEntity = teaList.get(i);//产品
					pici = placeOrderEntity.pici;// 批次
					String price = pici.price;
					total += Double.parseDouble(price) * count;
					JsonObject jsonObj = new JsonObject();
					jsonObj.addProperty("productId", pici.productId);// 现在只传批次的名称
					jsonObj.addProperty("productPrice", pici.price);
					jsonObj.addProperty("productUnitId", pici.unitId);
					jsonObj.addProperty("productNumber", teaList.get(i).count);// 产品数量
					
					//2015/12/8新增字段
					String remarkStr = placeOrderEntity.isGift ? "赠送商品" : "非赠送商品";
					jsonObj.addProperty("remark", remarkStr);
					jsonObj.addProperty("isGift", placeOrderEntity.isGift);
					jsonObj.addProperty("subTotal", total);//商品合计
					jsonObj.addProperty("isAmountBarCode", placeOrderEntity.isAmountBarCode);//是否计价条码，如果条码是29开头，为true否则为false
					
					jsonArr.add(jsonObj);
					
				}
			}
			
		}
		
		//保存应收金额，供PayResultActivity显示使用
		SPUtil.getDefault(getActivity()).saveShiShouMoney(yingShouEdit.getText().toString());
		
		confimOrder(total, jsonArr);
	}

	/**
	 * 结账
	 * paytype: cash:现金 swipecard:刷卡 weixin:微信支付
	 */
	public void confimOrder(double totalMoney, JsonArray jsonArr) {

		AjaxParams params = new AjaxParams();
		if (sendType.equals("快递")) {
			if (excutorPeopleView.getText().toString().equals("请选择")) {
				this.dimissDialog();
					showToast("执行人不能为空");
				return;
			}
			
			if (TextUtils.isEmpty(sendAddressEdit.getText().toString())) {
				this.dimissDialog();
					showToast("收货地址不能为空");
				return;
			}
			
			params.put("transactor", excutorUserId);// 执行人的id
			params.put("obtainAddress", sendAddressEdit.getText().toString());
		}
		
		params.put("description", beizhuEidt.getText().toString());// 备注，可选字段
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("products", jsonArr.toString());// 产品JsonArray
		params.put("totalAmount",  yingShouEdit.getText().toString() + "");// "totalAmount" 对应的是是应收字段
		params.put("detailAmount", totalMoney + "");// 商品明细总金额
		params.put("userId", userId);// 客户userId
		params.put("obtainMan", customerNameView.getText().toString());// 客户姓名，可选字段
		params.put("mobile", phoneEditView.getText().toString());// 客户电话，可选字段
		
		if (TextUtils.isEmpty(userPoints)) {
			userPoints = "0";
		}
		
		//输入积分
		params.put("inputPoint", userPoints);
		//积分金额
		params.put("integralAmount", pointAmount + "");
		//优惠券记录id
		params.put("voucherRecordId", currentVoucherRecordId);
		//优惠金额
		params.put("discountAmount", (discountamount + currentVoucherValue)+"");
		
		Log.i(TAG+"输入积分", userPoints + "");
		Log.i(TAG+"积分金额", pointAmount + "");
		Log.i(TAG+"优惠金额", (discountamount + currentVoucherValue)+"");
		
		
		if (TextUtils.isEmpty(sendType)) {
			this.dimissDialog();
			Toast.makeText(getActivity(), "请选择配送方式", Toast.LENGTH_SHORT).show();
			return;
		}
		params.put("shipMethod", sendType);//送货方式
		
		if (TextUtils.isEmpty(payTye)) {
			this.dimissDialog();
			Toast.makeText(getActivity(), "请选择支付方式", Toast.LENGTH_SHORT).show();
			return;
		}
		params.put("payType", payTye);
		
		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {

					LogUtil.v(tag, "login info : " + t);
					if (isSuccess) {
						final String orderId = data.get("orderId").getAsString();// 订单id
						
						SPUtil.getDefault(getActivity()).setOrderId(orderId);//保存订单id
						
						//微信扫码支付
						if (payTye.equals("weixinswipecard")) {
							Intent intent = new Intent(getActivity(), CaptureActivity.class);
							intent.putExtra("type", 2);
							intent.putExtra("id", orderId);
							intent.putExtra("discountamount", discountamount);//优惠金额
							startActivity(intent);
						}
						
						//现金支付
						else if (payTye.equals("cash")) {
							
							Intent intent = new Intent(getActivity(), CurrencyPayActivity.class);
							intent.putExtra("orderid", orderId);
							intent.putExtra("paytype", payTye);
							intent.putExtra("remark", beizhuEidt.getText().toString());//备注
							intent.putExtra("ys", yingShouEdit.getText().toString());//改价后的金额(应收)
							intent.putExtra("shipMethod", sendType);//送货方式
							intent.putExtra("total", ProductFastOrderFragment.this.totalMoney);//商品金额
							intent.putExtra("points", jifenEditText.getText().toString());//积分
							intent.putExtra("yishou", yingShouEdit.getText().toString());//应收金额
							intent.putExtra("incomeAmount", heJiView.getText().toString());//合计金额
							
							intent.putExtra("discountAmount", currentVoucherValue);//优惠金额
							intent.putExtra("integralamount", pointAmount);//积分金额
							startActivity(intent);
						}
						
						//记账和刷卡支付
						else if (payTye.equals("keepaccounts") || payTye.equals("swipecard")) {
							
							Intent intent = new Intent(getActivity(), SwipingNumActivity.class);
							intent.putExtra("orderid", orderId);
							intent.putExtra("paytype", payTye);
							intent.putExtra("ys", yingShouEdit.getText().toString());//应收
							
							intent.putExtra("shipMethod", sendType);//送货方式
							intent.putExtra("total", ProductFastOrderFragment.this.totalMoney);//商品金额
							intent.putExtra("points", jifenEditText.getText().toString());//积分
							intent.putExtra("incomeAmount", Double.parseDouble(heJiView.getText().toString())+"");//如果有优惠券需要减去优惠金额
							
							intent.putExtra("discountAmount", currentVoucherValue);//优惠券金额
							intent.putExtra("integralamount", pointAmount);//积分金额
							startActivity(intent);
						}
						
						else if (payTye.equals("yue")) {
							
							final String ye = data.get("accountBalance").getAsString();
							dimissDialog();
							
							DialogUtil dialog = new DialogUtil(getActivity());
							dialog.showConfirmDialog("发送至" + phoneEditView.getText().toString());
							dialog.setListener(new FinishListener() {
								
								@Override
								public void finishView() {
									
									Intent intent = new Intent(getActivity(), BalancePayActivity.class);
									intent.putExtra("orderid", orderId);
									intent.putExtra("discountAmount", currentVoucherValue+"");//优惠金额
									intent.putExtra("integralamount", pointAmount);//积分金额
									intent.putExtra("total", ProductFastOrderFragment.this.totalMoney);//商品金额
									intent.putExtra("yishou", yingShouEdit.getText().toString());//应收金额
									intent.putExtra("incomeAmount", heJiView.getText().toString());//合计金额
									intent.putExtra("ye", ye);
									startActivity(intent);
								}
							});
						}
						
					}else {
						
						if (data.has("msg")) {
							String msg = data.get("msg").getAsString();
							Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						}else {
							showToast("下单失败");
						}
						
					}

					dimissDialog();

				} catch (Exception e) {
					e.printStackTrace();

					Toast.makeText(getActivity(), "程序异常", Toast.LENGTH_SHORT).show();
					dimissDialog();
				}

			}
		};

		Log.i(TAG, "## 路径"+Server.BASE_URL + Server.FAST_TAKE_ORDER);
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.FAST_TAKE_ORDER, params,
				callback);
	}

	/**
	 * 获取客户的电话号码和信息
	 * 
	 * @param String
	 * 
	 *            scanContent 扫描客户二维码的结果
	 */

	private String userId;
	private String name;
	private String mobile;
	//用户总积分
	private float  pointBalance;

	private void requestCustomerInfo(String scanContent) {

		showDialog();

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("params", scanContent);
		

		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {

					isScanSuccess = true;

					userId = data.get("userId").getAsString();
					
					//传入userId获得优惠金额
					priferenceRequest();
					
					
					name = data.get("name").getAsString();
					mobile = data.get("mobile").getAsString();
					pointBalance = data.get("pointBalance").getAsFloat();//用户积分
					
					customerNameView.setText(name);
					phoneEditView.setText(mobile);
					canUsePointView.setText("可用积分"+ pointBalance);
					
					// 设置电话框不可编辑
					phoneEditView.setEnabled(false);
					customerNameView.setEnabled(false);
					
					//获取优惠
					priferenceRequest();
					
					//检测积分是否可用
					mCheckHandler.sendEmptyMessage(CHECK_POINTS_IS_USE);
					
				} else {
					String msg = data.get("msg").getAsString();
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.SCAN_CUS_RQUEST, params,
				callback);

	}

	/**
	 * 根据电话号码获取客户名字
	 * 
	 * @param phone
	 */
	private void requestCustomerNameFromPhone(String phone) {

		showDialog();

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("mobile", phone);

		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (isSuccess) {
					
					name = data.get("name").getAsString();
					userId = data.get("userId").getAsString();
					if (TextUtils.isEmpty(name)) {
						customerNameView.setEnabled(true);
						dimissDialog();
						return;
					}
					//String familyName = name.substring(0, 1);
					//familyName = familyName +"**";
					customerNameView.setText(name);
					dimissDialog();
					
					//获取优惠
					priferenceRequest();
				} else {
					
					String msg = data.get("msg").getAsString();
					showDialog();
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.FROM_PHONE_FIND_NAME, params,callback);
	}
	
	
	private final static int CHECK_POINTS_IS_USE = 0X351;
	
	Handler mCheckHandler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case CHECK_POINTS_IS_USE:
					checkPointsIsCanUse(userId);
				break;

			default:
				break;
			}
			
		}
	};
	
	
	/**
	 * 优惠金额
	 */
	private void priferenceRequest(){
		
//		showDialog("获得优惠金额中...");
		
		JsonObject parentObject = new JsonObject();
		JsonArray productArray = new JsonArray();
		for (PlaceOrderEntity placeOrderEntity : teaList) {
			JsonObject object = new JsonObject();
			
			if (null != placeOrderEntity.products) {
				object.addProperty("id", "");
				object.addProperty("productId", placeOrderEntity.pici.productId);
				object.addProperty("quantity", placeOrderEntity.count);
				object.addProperty("isGift", placeOrderEntity.isGift);
				object.addProperty("price", placeOrderEntity.pici.price);
				object.addProperty("subTotal", Double.parseDouble(placeOrderEntity.pici.price) * placeOrderEntity.count);//商品合计
				object.addProperty("isAmountBarCode", placeOrderEntity.isAmountBarCode);
				object.addProperty("remark", placeOrderEntity.isGift ? "赠送商品" : "非赠送商品");
				
				productArray.add(object);
			}
			
		}
		
		parentObject.add("orderDetailItems", productArray);
		//parentObject.addProperty("token", SPUtil.getDefault(getActivity()).getToken());
		parentObject.addProperty("uid", userId);
		//parentObject.addProperty("storeId", SPUtil.getDefault(getActivity()).getStoreId());

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		//params.put("uid", userId);//客户id
		
		params.put("args", parentObject.toString());

		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String result) {
				super.onSuccess(result);
				Log.i(TAG+"-------优惠金额----------", result);
				
				//dimissDialog();
				
				if (data.has("ret")) {
					if (data.get("ret").getAsBoolean()) {
						
						if (!data.has("discountamount")) {
							discountamount = 0.0;
						}else {
							discountamount = data.get("discountamount").getAsDouble();
						}
						
						discountamount = NumerUtil.bigDecimalRoundHalfUp(discountamount);//四舍五入
						
						if (from_pay == 1) {
							//合计
							avaliableAmount = NumerUtil.saveOneDecimal((Double.parseDouble(onlineOrderDetail.receivableAmount)
									- discountamount - pointAmount - currentVoucherValue)+"");
									
							heJiView.setText(avaliableAmount);
							//应收
							yingShouEdit.setText(avaliableAmount);
							//底部金额显示
							bottomShouldGiveMoneyView.setText("应收 ：¥"+ avaliableAmount);
							
						}else{
							
							// 商品金额
							avaliableAmount =  (Double.parseDouble(totalMoney) - discountamount - pointAmount - currentVoucherValue)+"";
							heJiView.setText(NumerUtil.saveOneDecimal(avaliableAmount));
							//应收金额
							yingShouEdit.setText(NumerUtil.saveOneDecimal(avaliableAmount));
							// 底部金额
							bottomShouldGiveMoneyView.setText("应收： ¥ " + NumerUtil.saveOneDecimal(avaliableAmount));
						}
						
						//从订单详情进入
						if (fromDetail != 1) {
							//检测积分是否可用
							if (null != userId) {
								mCheckHandler.sendEmptyMessage(CHECK_POINTS_IS_USE);
							}
						}
						
						
					} else {
						
						if (data.get("msg") != null) {
							showToast(data.get("msg").getAsString());
						}
						
					}
				}else {
					if (data.get("msg") != null) {
						showToast(data.get("msg").getAsString());
					}
				}
				
				
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};

		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.post(Server.BASE_URL + Server.PREFERENTIAL_REQUEST, params,callback);
	}
	
	/**
	 * 
	 * @param inputPoint 输入积分
	 * @param productAllMoney 应收总金额
	 * @author userId 用户id
	 */
	
	private double pointAmount;//积分总金额
	
	private void calculatePointAmountRequest(String inputPoint,String productAllMoney,String userId){
		//Log.i("------inputPoint-------productAllMoney---userId---", inputPoint+"-------"+ productAllMoney +"----------"+userId);
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("inputPoint", inputPoint);
		params.put("total", productAllMoney);
		params.put("userId", userId);
		
		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (isSuccess) {
					pointAmount = data.get("pointAmount").getAsDouble();
					
					canUsePointView.setText("可用积分"+ pointBalance+"   " + "兑换金额"+pointAmount+"元");
					
					if (from_pay == 1) {
						
						 double afterMoney = Double.parseDouble(onlineOrderDetail.receivableAmount) - discountamount - pointAmount - currentVoucherValue;
						 BigDecimal decimal = new BigDecimal(afterMoney);
						 double result = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
						 
						//合计
						heJiView.setText(result < 0 ? "0.0" : result+"");
						//应收
						yingShouEdit.setText(result < 0 ? "0.0" : result+"");
						//底部金额显示
						bottomShouldGiveMoneyView.setText("应收 ：¥"+ (result < 0 ? "0.0" : result));
						
					}else{
						
						 //String afterMoney = NumerUtil.saveOneDecimal((Double.parseDouble(totalMoney) - discountamount - pointAmount)+"");
						 double afterMoney = Double.parseDouble(totalMoney) - discountamount - pointAmount - currentVoucherValue;
						 BigDecimal decimal = new BigDecimal(afterMoney);
						 double result = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						// 商品金额
						heJiView.setText(result < 0 ? "0.0" : result+"");
						//应收金额
						yingShouEdit.setText(result < 0 ? "0.0" : result+"");
						// 底部金额
						bottomShouldGiveMoneyView.setText("应收： ¥ " + (result < 0 ? "0.0" : result+""));
						
					}
					
				} else {
					
					//积分不可用--------------------
					
					jifenEditText.setText("");
					jifenEditText.setHint("积分不可用");
					
					//重置应收金额
					String valueStr = (Double.parseDouble(avaliableAmount) - currentVoucherValue)+"";
					yingShouEdit.setText(valueStr);
					heJiView.setText(valueStr);
					bottomShouldGiveMoneyView.setText("应收 ：¥"+ valueStr);
					
					//积分不可用，应收金额必须充值到之前的状态
					pointAmount = 0;
					userPoints = "";
					
					if (jifenLayout.getVisibility() == View.VISIBLE) {
						jifenEditText.setEnabled(false);
					}
					//String msg = data.get("msg").getAsString();
					//Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.CALCULATE_POINT_AMOUNT, params,callback);
	}
	
	private List<Voucher> mVoucherList;
	
	/**
	 * 获取优惠券列表
	 */
	private void getVouerListRequest(){
		
		showDialog();
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("userId", userId);
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("orderId", "");
		params.put("amount", yingShouEdit.getText().toString());//应收总金额
		
		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				dimissDialog();
				
				if (isSuccess) {
					
					String jsonArray = data.get("voucherList").toString();
					Gson gson = new Gson();
					mVoucherList = gson.fromJson(jsonArray,new TypeToken<List<Voucher>>() {}.getType());
					
					jifenQuanView.setText("共" + mVoucherList.size() +"张");
					
					if (mVoucherList != null && mVoucherList.size() >0) {
						if (null == discountDiaog) {
							 discountDiaog = new DiscountChooseDialog(getActivity());
					    }
						discountDiaog.show();
					}
					
					
				} else {
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					}else {
						showToast("获取失败");
					}
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.GET_DISCOUNT_LIST, params,callback);
	}
	
	/**
	 * 积分是否可用
	 */
	private boolean isPointCanUse;
	
	//检查积分是否可用
	private void checkPointsIsCanUse(String userId){
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("userId", userId);
		params.put("total", yingShouEdit.getText().toString());//应收总金额
		
		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					
					isPointCanUse = data.get("isEnable").getAsBoolean();

					if (isPointCanUse) {
						jifenEditText.setText("");
						jifenEditText.setHint("请输入积分");
						if (jifenLayout.getVisibility() == View.VISIBLE) {
							jifenEditText.setEnabled(true);
						}
						
					}else {
						jifenEditText.setText("");
						jifenEditText.setHint("积分不可用");
						
						//重置应收金额
						String valueStr = (Double.parseDouble(yingShouEdit.getText().toString()) - currentVoucherValue - pointAmount)+"";
						yingShouEdit.setText(valueStr);
						heJiView.setText(valueStr);
						bottomShouldGiveMoneyView.setText("应收 ：¥"+ valueStr);
						
						//积分不可用，应收金额必须充值到之前的状态
						pointAmount = 0;
						userPoints = "";
						
						if (jifenLayout.getVisibility() == View.VISIBLE) {
							jifenEditText.setEnabled(false);
						}
						
					}
					
				} else {
					
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					}else {
						showToast("获取失败");
					}
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.CHECK_POINTS_IS_ENABLE, params,callback);
	}
	
	private double currentVoucherValue;
	private String currentVoucherId;
	private String currentVoucherRecordId;
	
	//优惠券dialog选择
	private class DiscountChooseDialog extends Dialog {
		
		private ListView mListView = null;
		private ImageView clearImg = null;
		private TextView clearDisTextView = null;
		
		public DiscountChooseDialog(Context context,int theme) {
			super(context,theme);
			View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout_discount, null);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			mListView = (ListView) dialogView.findViewById(R.id.discount_listview);
			clearImg = (ImageView) dialogView.findViewById(R.id.clearbtn);
			clearDisTextView = (TextView) dialogView.findViewById(R.id.clear_discount_btn);
			
			final DiscountAdapter discountAdapter = new DiscountAdapter("",mVoucherList, context);
			mListView.setAdapter(discountAdapter);
			
			clearImg.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			
			//清除优惠券
			clearDisTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					jifenQuanView.setText("共" + mVoucherList.size() +"张");
					
					currentVoucherValue = 0.0;
					currentVoucherId = "";
					currentVoucherRecordId = "";
					
					if (from_pay == 1) {
						//合计
						avaliableAmount = NumerUtil.saveOneDecimal((Double.parseDouble(onlineOrderDetail.receivableAmount)
								- discountamount - pointAmount - currentVoucherValue)+"");
								
						heJiView.setText(avaliableAmount);
						//应收
						yingShouEdit.setText(avaliableAmount);
						//底部金额显示
						bottomShouldGiveMoneyView.setText("应收 ：¥"+ avaliableAmount);
						
					}else{
						
						// 商品金额
						avaliableAmount =  (Double.parseDouble(totalMoney) - discountamount - pointAmount - currentVoucherValue)+"";
						heJiView.setText(NumerUtil.saveOneDecimal(avaliableAmount));
						//应收金额
						yingShouEdit.setText(NumerUtil.saveOneDecimal(avaliableAmount));
						// 底部金额
						bottomShouldGiveMoneyView.setText("应收： ¥ " + NumerUtil.saveOneDecimal(avaliableAmount));
					}
					
					dismiss();
					
				}
			});
			
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					
					checkPointsIsCanUse(userId);
					
					Voucher voucher = mVoucherList.get(position);
					discountAdapter.setVourId(voucher.volumeid);
					discountAdapter.notifyDataSetChanged();
					
					jifenQuanView.setText(voucher.value + voucher.name);//显示优惠券信息
					
					currentVoucherValue = voucher.value;
					currentVoucherId = voucher.volumeid;
					currentVoucherRecordId = voucher.fid;
					
					//记录应收金额
					avaliableAmount = yingShouEdit.getText().toString();
					
					if (from_pay == 1) {
						
						 double afterMoney = Double.parseDouble(onlineOrderDetail.receivableAmount) - discountamount - pointAmount - currentVoucherValue;
						 BigDecimal decimal = new BigDecimal(afterMoney);
						 double result = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
						 
						//合计
						heJiView.setText(result+"");
						//应收
						yingShouEdit.setText(result+"");
						//底部金额显示
						bottomShouldGiveMoneyView.setText("应收 ：¥"+ result);
						
					}else{
						
						String afterMoney = NumerUtil.saveOneDecimal((Double.parseDouble(totalMoney) - discountamount - pointAmount - currentVoucherValue)+"");
						// 商品金额
						heJiView.setText(afterMoney);
						//应收金额
						yingShouEdit.setText(afterMoney);
						// 底部金额
						bottomShouldGiveMoneyView.setText("应收： ¥ " + afterMoney);
					}
					
					dismiss();
				}
			});
			
			this.setCancelable(true);
			this.setCanceledOnTouchOutside(true);
			
			super.setContentView(dialogView);
		}
		
		public DiscountChooseDialog(Context context){
			this(context,R.style.discount_dialog_theme);
		}
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		
			getWindow().setGravity(Gravity.BOTTOM);
			//设置对话框的宽度
			WindowManager m = getWindow().getWindowManager();
	        Display d = m.getDefaultDisplay();
	        WindowManager.LayoutParams p = getWindow().getAttributes();
	        p.width = d.getWidth();
	        p.height = LayoutParams.WRAP_CONTENT;
	        getWindow().setAttributes(p);
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
	

}