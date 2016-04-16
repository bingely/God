package com.meetrend.haopingdian.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.CostBean;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.MapSendAddressEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.DateTimePickDialogUtil;
import com.meetrend.haopingdian.util.DateTimePickDialogUtil.TimeShow;
import com.meetrend.haopingdian.widget.MyDatePickerDialog;
import de.greenrobot.event.EventBus;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新建活动详情，编辑活动详情
 * 
 * @author 肖建斌
 *
 */
public class NewBuidEventActivity extends BaseActivity{
	
	private final static String TAG = NewBuidEventActivity.class.getName();

	private final static String Free = "free";
	private final static String NoFree = "noFree";
	
	@ViewInject(id = R.id.actionbar_home,click = "backClick")
	ImageView backImageView;
	@ViewInject(id = R.id.actionbar_title)
	public TextView titleView;
	
	@ViewInject(id = R.id.gridview)
	GridView gridView;
	@ViewInject(id = R.id.event_start_time_view,click = "startDataClick")
	TextView startTimeView;
	@ViewInject(id = R.id.event_end_time_view,click = "endDataClick")
	TextView endTimeView;
	@ViewInject(id = R.id.event_apply_stop_time_view,click ="deadlineClick")
	TextView deadTimeView;
	//新建活动、编辑重新发布
	@ViewInject(id = R.id.new_build_btn,click ="buildClick")
	TextView eventBuildView;
	
	//活动名字
	@ViewInject(id = R.id.event_name_view)
	TextView eventNameView;
	//活动详情
	@ViewInject(id = R.id.event_des_view)
	TextView eventDesView;

	//活动地点
	@ViewInject(id = R.id.event_address_name_view)
	EditText eventAddressView;
	//活动分类
	@ViewInject(id = R.id.event_name_type_view,click = "eventTypeClick")
	TextView eventTypeView;
	
	@ViewInject(id = R.id.mapviewbtn,click = "mapViewClick")
	LinearLayout mapViewLayout;
	//收费设置
	@ViewInject(id = R.id.paystatus_layout,click = "payStatusLayoutClick")
	LinearLayout payStatusLayout;
	@ViewInject(id = R.id.event_pay_status)
	TextView paystatusView;
	@ViewInject(id = R.id.right_arrow_img)
	ImageView rightArraowImg;
	@ViewInject(id = R.id.pay_type_bottom_line)
	View payTypeBottomLineView;


	//费用设置
	@ViewInject(id = R.id.paysetting_layout,click = "paysettingClick")
	LinearLayout paysettinglayout;
	@ViewInject(id = R.id.event_paysetting_text)
	TextView paysettingText;

	//限制人数
	@ViewInject(id = R.id.limit_people_layout)
	LinearLayout limitPeopleLayout;
	//活动限制人数
	@ViewInject(id = R.id.event_people_num_view)
	EditText eventPeopleNumView;

	private ArrayList<String> imgPaths = new ArrayList<String>();
	private ArrayList<String> pictrueids = new ArrayList<String>();
	
	private GridAdapter mGridAdapter;

	//时间戳
	Long mCurrentTime;
	Long mStartTime;
	Long mEndTime;
	Long mDeadLineTime;
	
	//活动id
	String id;
	
	//页面来源1新建2编辑3再发一个
	private  int fromType;
	
	//活动主题FValue
	private String eventValue;
	private String eventText;

	String costStrArray ="";//收费jsonarray

	private boolean ischarge = false;//0标识免费，1标识收费

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_newbuildevent);
		FinalActivity.initInjectedView(this);
		
		fromType = getIntent().getIntExtra("fromtype", -1);
		
		//calendar = Calendar.getInstance();
		
		mCurrentTime = new Date().getTime();//时间戳
		
		if (fromType == 1) {
			titleView.setText("新建活动");
			eventTypeView.setText("请选择活动分类主题");
		}else {
			id = getIntent().getStringExtra("eventId");
			titleView.setText("编辑活动");

			requestEventDetail(id);
			payTypeBottomLineView.setVisibility(View.VISIBLE);
		}

		eventBuildView.setText("完  成");
		
		mGridAdapter = new GridAdapter();
		gridView.setAdapter(mGridAdapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				String url = imgPaths.get(position);
				if (url.equals("add")) {

					if (imgPaths.size() > 5) {
						showToast("最多上传五张图片");
						return;
					}
					startActivityForResult(new Intent(NewBuidEventActivity.this, AddPictrueActivity.class), 0x912);
					NewBuidEventActivity.this.overridePendingTransition(R.anim.activity_popup, 0);
				} else {
					//删除
					imgPaths.remove(imgPaths.get(position));
					mGridAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	private String lnt;
	private String longt;
	
	public void onEventMainThread(MapSendAddressEvent event) {
		String address = event.address;
		lnt = event.latitude;
		longt = event.longitude;
		eventAddressView.setText(address);
		
	}

	private ArrayList<CostBean> codelist;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		
		if (resultCode == RESULT_OK && requestCode == 0x912) {
			
			try {
					String pictruePath = data.getStringExtra("path");
					Log.i(TAG+" pictrue path==",pictruePath);

					if (TextUtils.isEmpty(pictruePath)) {
						showToast("获取文件的路径失败");
						return;
					}
					showDialog();
					upLoadPictrue(pictruePath);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
				
		}else if (resultCode == 0X111 && requestCode == 0X222) {
			//选择活动主题返回结果
			eventValue = data.getStringExtra("fvalue");
			String eventText = data.getStringExtra("ftext");
			eventTypeView.setText(eventText);
		}else if (resultCode == 0X010 && requestCode == 0X009){
				String value = data.getStringExtra("fvalue");
				String ftext = data.getStringExtra("ftext");
			    mPayStatus = value;
				mPayStatusText = ftext;
				paystatusView.setText(mPayStatusText);

				payTypeBottomLineView.setVisibility(View.VISIBLE);
			    if (mPayStatus.equals(Free)){
						paysettinglayout.setVisibility(View.GONE);
					    limitPeopleLayout.setVisibility(View.VISIBLE);
				}else{
					paysettinglayout.setVisibility(View.VISIBLE);
					limitPeopleLayout.setVisibility(View.GONE);
				}
		}else if(resultCode == 0X011 && requestCode == 0X113){

						Bundle bundle = data.getBundleExtra("bundlelist");
						codelist = bundle.getParcelableArrayList("costlist");
						JsonArray costArray = new JsonArray();
						int size = codelist.size();
						if (size >0){
							paysettingText.setText("已设置");
						}else{
							paysettingText.setText("未设置");
						}
						for (int i=0;i<size;i++){
							CostBean bean = codelist.get(i);
							JsonObject jsonObject = new JsonObject();
							jsonObject.addProperty("name", bean.name);
							jsonObject.addProperty("money", bean.money);
							jsonObject.addProperty("limitAmount", bean.limitAmount);
							costArray.add(jsonObject);
							costStrArray = costArray.toString();
						}
						Log.i("==costStrArray======",costStrArray);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String mPayStatus = "";//是否免费
	private String mPayStatusText = "未设置";
	//选择活动是否收费
	public void payStatusLayoutClick(View view){
			Intent intent = new Intent();
		    intent.putExtra("fvalue", mPayStatus);
			intent.setClass(NewBuidEventActivity.this, EventPayTypeListActivity.class);
			startActivityForResult(intent, 0X009);//请求码
	}

	//收费设置
	public void paysettingClick(View view){
		Intent intent = new Intent(NewBuidEventActivity.this,SettingEventCostActivity.class);
		if (fromType == 1 || fromType == 3)
			intent.putExtra("viewtype",0);
		else
			intent.putExtra("viewtype", 1);

		Bundle bundle = new Bundle();
		if (null == codelist)
			codelist = new ArrayList<CostBean>();

		bundle.putParcelableArrayList("codelist",codelist);
		intent.putExtra("codelistbundle", bundle);

		startActivityForResult(intent, 0x113);
	}
	
	//新建、编辑重新发布
	public void buildClick(View view){
		commit();
	}
	
	
	//进入百度地图界面
	public void mapViewClick(View view){
		Intent intent = new Intent(NewBuidEventActivity.this, MapActivity.class);
		startActivity(intent);
		//NewBuidEventActivity.this.overridePendingTransition(R.anim.activity_left_in,R.anim.activity_left_out);
		NewBuidEventActivity.this.overridePendingTransition(R.anim.activity_popup, 0);
	}
	
	
	//选择活动主题
	public void eventTypeClick(View view){
		
		Intent intent = new Intent(NewBuidEventActivity.this, EventTypeListActivity.class);
		if (eventValue == null) {
			eventValue = "";
		}
		intent.putExtra("event_id", eventValue);//活动的id
		startActivityForResult(intent, 0X222);//请求码
		//startActivity(intent);
		NewBuidEventActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	/**
	 * 
	 * 活动开始时间 按钮 click事件
	 */
	public void startDataClick(View v) {
		
		/*MyDatePickerDialog dialog = new MyDatePickerDialog(this,
				new myStartDatelistener(), calendar.get(calendar.YEAR),
				calendar.get(calendar.MONTH),
				calendar.get(calendar.DAY_OF_MONTH));
		dialog.show();*/
		
		DateTimePickDialogUtil dateTimePickDialogUtil = new DateTimePickDialogUtil(NewBuidEventActivity.this, "");
		dateTimePickDialogUtil.dateTimePicKDialog(startTimeView);
		
		dateTimePickDialogUtil.setTimeShow(new TimeShow() {
			
			@Override
			public void show(String formatTime) {
				startTimeView.setText(formatTime);
				mStartTime = changeTime(formatTime);
			}
		});
		
	}

	/**
	 * 
	 * 活动结束时间 按钮 click事件
	 */
	public void endDataClick(View v) {
		
//		MyDatePickerDialog dialog = new MyDatePickerDialog(this,
//				new myEndDatelistener(), calendar.get(calendar.YEAR),
//				calendar.get(calendar.MONTH),
//				calendar.get(calendar.DAY_OF_MONTH));
//		dialog.show();
		
			
			DateTimePickDialogUtil dateTimePickDialogUtil = new DateTimePickDialogUtil(NewBuidEventActivity.this, "");
			dateTimePickDialogUtil.dateTimePicKDialog(endTimeView);
		
			dateTimePickDialogUtil.setTimeShow(new TimeShow() {
				
				@Override
				public void show(String formatTime) {
					endTimeView.setText(formatTime);
					mEndTime = changeTime(formatTime);
				}
			});
	}

	
	/**
	 * 
	 * 报名活动截止时间按钮 click 事件
	 */
	public void deadlineClick(View v) {
//		MyDatePickerDialog dialog = new MyDatePickerDialog(this,
//				new mDeadlinelistener(), calendar.get(calendar.YEAR),
//				calendar.get(calendar.MONTH),
//				calendar.get(calendar.DAY_OF_MONTH));
//		dialog.show();
			
			
			DateTimePickDialogUtil dateTimePickDialogUtil = new DateTimePickDialogUtil(NewBuidEventActivity.this,"");
					
			dateTimePickDialogUtil.dateTimePicKDialog(deadTimeView);
		
			dateTimePickDialogUtil.setTimeShow(new TimeShow() {
			
				@Override
				public void show(String formatTime) {
					deadTimeView.setText(formatTime);
					mDeadLineTime = changeTime(formatTime);
				}
			});
	}
	
	//将yyyy-MM-dd HH:mm的字符串转成 long时间戳
	public long changeTime(String mDate) {
		
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dDate = null;
		long longdate = 0;
		try {
			
			if (mDate == null) {
				longdate = 0;
				return longdate;
			}
			
			dDate = time.parse(mDate);
			longdate = dDate.getTime();
			
		} catch (ParseException e) {
			e.printStackTrace();
			longdate = 0;
		}
		return longdate;
	}

	    private String limitNum;
	
		//活动详情
		private void requestEventDetail(String eventId){
			showDialog();
		try {
			
			App.eventId = eventId;
			AjaxParams params = new AjaxParams();
			params.put("token", SPUtil.getDefault(NewBuidEventActivity.this).getToken());
			params.put("storeId", SPUtil.getDefault(NewBuidEventActivity.this).getStoreId());
			params.put("entityId",eventId);
			
			Callback callback = new Callback(tag,NewBuidEventActivity.this) {
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					mHandler.sendEmptyMessage(Code.FAILED);
				}

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					Log.i("数据", t);
					dimissDialog();
					
					if (isSuccess) {
						JsonObject object = data.getAsJsonObject("memberActivityDetails");
						String eventName = object.get("name").getAsString();//活动主题名字
						String startTime = object.get("activityStartDate").getAsString();//活动开始时间
						String endTime = object.get("activityEndDate").getAsString();//活动结束时间
						String deadlineTime = object.get("deadline").getAsString();//活动截止时间

						//费用设置
						ischarge = object.get("isCharge").getAsBoolean();
						JsonArray jsonArray = object.get("chargeArray").getAsJsonArray();
						costStrArray = jsonArray.toString();
						JsonObject obj = (JsonObject)jsonArray.get(0);
						limitNum = obj.get("limitAmount").getAsString();

						Gson gson = new Gson();
						codelist = gson.fromJson(costStrArray,new TypeToken<List<CostBean>>() {}.getType());

						if (ischarge){
							paystatusView.setText("收费");
							paysettinglayout.setVisibility(View.VISIBLE);
							limitPeopleLayout.setVisibility(View.GONE);
							if (null !=codelist && codelist.size() >0){
								paysettingText.setText("已设置");
							}else{
								paysettingText.setText("未设置");
							}
							if(fromType == 2){
								payStatusLayout.setEnabled(false);
								paystatusView.setEnabled(false);
								paysettingText.setEnabled(false);
							}

							mPayStatus = NoFree;
						}else {
							paystatusView.setText("免费");
							paysettinglayout.setVisibility(View.GONE);
							limitPeopleLayout.setVisibility(View.VISIBLE);

							if(fromType == 2){
								payStatusLayout.setEnabled(false);
								paystatusView.setEnabled(false);
								eventPeopleNumView.setEnabled(false);
							}
							mPayStatus = Free;
						}
						if (fromType == 2){
							rightArraowImg.setVisibility(View.GONE);
						}

						//转成相应的时间戳
						mStartTime = changeTime(startTime);
						mEndTime = changeTime(endTime);
						mDeadLineTime = changeTime(deadlineTime);
						
						String eventAddress = object.get("address").getAsString();//活动地址
						
						String eventInfo = object.get("detailExplain").getAsString();//活动详情
						eventValue = object.get("classify").getAsString();//活动类型id
						eventText = object.get("classifyText").getAsString();//活动类型名称
						
						if (eventText == null) {
							eventTypeView.setText("请选择活动分类主题");
						}
						eventTypeView.setText(eventText);
						eventNameView.setText(eventName);
						
						startTimeView.setText(startTime == null ?"未设置":startTime);
						endTimeView.setText(endTime == null ?"未设置":endTime);
						deadTimeView.setText(deadlineTime == null ?"未设置":deadlineTime);
						
						eventAddressView.setText(eventAddress);
						eventDesView.setText(eventInfo);
						eventPeopleNumView.setText(limitNum);
						if (fromType == 2){
							eventPeopleNumView.setHint("活动限制人数不可编辑");
						}
						
						JsonArray imagesArray = object.get("activityImages").getAsJsonArray();//活动详情图片
						for (int i = 0; i < imagesArray.size(); i++) {
							JsonObject jsonObject = (JsonObject)imagesArray.get(i);
							pictrueids.add(jsonObject.get("id").getAsString());
							imgPaths.add(jsonObject.get("pictureId").getAsString());
						}
						//刷新，显示图片
						mGridAdapter.notifyDataSetChanged();
					}
				}
			};
			
			FinalHttp request = new FinalHttp();
			request.get(Server.BASE_URL + Server.EVENT_DETAIL, params, callback);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	
	public void commit() {
		
		if (TextUtils.isEmpty(eventNameView.getText().toString())) {
			Toast.makeText(NewBuidEventActivity.this, "活动主题不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (TextUtils.isEmpty(startTimeView.getText().toString()) || startTimeView.getText().toString().equals("未设置")) {
			Toast.makeText(NewBuidEventActivity.this, "活动开始时间不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (TextUtils.isEmpty(endTimeView.getText().toString()) || endTimeView.getText().toString().equals("未设置")) {
			Toast.makeText(NewBuidEventActivity.this, "活动结束时间不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (TextUtils.isEmpty(deadTimeView.getText().toString()) || deadTimeView.getText().toString().equals("未设置")) {
			Toast.makeText(NewBuidEventActivity.this, "活动报名截止时间不能为空", Toast.LENGTH_SHORT).show();
			return;
		}


		if (mEndTime < mStartTime) {
			showToast("活动结束时间必须大于活动开始时间");
			return;
		}
		
		if (mDeadLineTime > mEndTime) {
			 showToast("活动截止时间必须小于活动结束时间");
			return;
		}
		
		if (TextUtils.isEmpty(eventAddressView.getText().toString())) {
			Toast.makeText(NewBuidEventActivity.this, "活动地址不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (TextUtils.isEmpty(eventDesView.getText().toString())) {
			Toast.makeText(NewBuidEventActivity.this, "活动详情不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		//至少上传一张图片
		if (pictrueids.size() < 1) {
			Toast.makeText(NewBuidEventActivity.this, "至少上传1张图片", Toast.LENGTH_SHORT).show();
			return;
		}

		if (eventTypeView.getText().toString().equals("请选择活动分类主题")){
			showToast("请选择活动分类主题");
			return;
		}

		if(paystatusView.getText().equals("未设置")){
			dimissDialog();
			showToast("请设置收费方式");
			return;
		}

		if (fromType == 1) {
			showDialog("正在新建活动...");
		}else {
			showDialog("正在提交...");
		}
		JsonArray jsonArray = new JsonArray();
		for (int i = 0; i < pictrueids.size(); i++) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("pictureId", pictrueids.get(i));
			jsonObject.addProperty("name", i+"上传图片.png");
			jsonArray.add(jsonObject);
		}
		
		String jsonArrayStr = jsonArray.toString();
		
		AjaxParams params = new AjaxParams();
		if (fromType == 2 || fromType == 3) {
			params.put("entityId", id);
		}

		params.put("token", SPUtil.getDefault(NewBuidEventActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(NewBuidEventActivity.this).getStoreId());
		params.put("name", eventNameView.getText().toString());//活动名字
		params.put("detailExplain", eventDesView.getText().toString());
		params.put("activityStartDate", mStartTime+"");
		params.put("activityEndDate", mEndTime+"");
		params.put("deadline", mDeadLineTime+"");
		params.put("address", eventAddressView.getText().toString());//活动地址
		params.put("latitude", lnt);//纬度
		params.put("longitude", longt);//经度
		params.put("activitySponsor", "");//主办方非必填字段
		params.put("classify", eventValue);//活动分类必填字段
		params.put("activityParticipation", "");//活动参与方式非必填字段
		params.put("pictures", jsonArrayStr);//图片id的json字符串

		int status;
		if (mPayStatus.equals(Free))
			status =0;
			else
			status =1;

		params.put("isCharge",status+"");//是否收费

		//免费方式
		if (mPayStatus.equals(Free)){

			if (eventPeopleNumView.getText().toString().equals("")){
				showToast("请输入活动限制人数");
				dimissDialog();
				return;
			}

//			JsonArray costArray = new JsonArray();
//			JsonObject jsonObject = new JsonObject();
//			jsonObject.addProperty("name", "");
//			jsonObject.addProperty("money", "");
//			if (eventPeopleNumView.isEnabled()){
//				jsonObject.addProperty("limitTheNumber", eventPeopleNumView.getText().toString());
//			}else{
//				jsonObject.addProperty("limitTheNumber", limitNum);
//			}
//
//			costArray.add(jsonObject);
//			costStrArray = costArray.toString();

			if (eventPeopleNumView.isEnabled()){
				params.put("limitTheNumber", eventPeopleNumView.getText().toString());//活动限制人数必填字段
			}else{
				params.put("limitTheNumber", limitNum);
			}

		}
		//收费
		else{

			if (TextUtils.isEmpty(costStrArray)){
				dimissDialog();
				showToast("费用未设置");
				return;
			}

			params.put("activeCharges",costStrArray);//收费信息
		}


		
		Callback callback = new Callback(tag, this) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					if (fromType == 1) {
						showToast("活动新建成功");
						id = data.get("entityId").getAsString();
						Intent intent = new Intent(NewBuidEventActivity.this,NewEventDetailActivity.class);
						intent.putExtra("id", id);
						startActivity(intent);
					}else {
						if (fromType == 2) {
							showToast("活动编辑成功");
						}else {
							showToast("新建活动成功");
						}
					}
					finish();
					
				} else {
					if (data.has("msg")) {
						String msgString = data.get("msg").getAsString();
						showToast(msgString);
					}
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		if (fromType == 2) {
			params.put("entityId", id);
			finalHttp.get(Server.BASE_URL + Server.UPDATE_ACTIVITY, params, callback);
		}else {
			finalHttp.get(Server.BASE_URL + Server.ADD_ACTIVITY, params, callback);
		}
		
	}
	
	/**
	 * 编辑时调用，获得当前活动数据
	 */
	public void getActivityInfo(String eventId) {
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(NewBuidEventActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(NewBuidEventActivity.this).getStoreId());
		params.put("entityId", eventId);

		Callback callback = new Callback(tag, this) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (isSuccess) {
					Gson gson = new Gson();
					JsonObject jsonObj = data.get("memberActivityDetails")
							.getAsJsonObject();
				}
			}
		};
		FinalHttp request = new FinalHttp();
		request.get(Server.BASE_URL + Server.EVENT_DETAIL, params, callback);
	}

	private int requestNum;

	/**
	 * 需要上传的本地图片的路径
	 * @param absPath  路径
	 */
	@SuppressWarnings("unused")
	private void upLoadPictrue(String absPath){
		
		AjaxParams params = new AjaxParams();
		params.put("storeId", SPUtil.getDefault(NewBuidEventActivity.this).getStoreId());

		try {
			
			File file = new File(absPath);
			if (null == file) {
				dimissDialog();
				Toast.makeText(NewBuidEventActivity.this, "没有找到图片路径", Toast.LENGTH_SHORT).show();
			}
			
			params.put("file", file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
			LogUtil.w(tag, e.getMessage());
		}

		Callback callback = new Callback(tag, NewBuidEventActivity.this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
						JSONObject jsonObject = new JSONObject(t);
						if (isSuccess) {
							
							String url = data.get("url").getAsString();
							String pictureId = data.get("pictureId").getAsString();
							
							if (!TextUtils.isEmpty(pictureId)) {
								pictrueids.add(pictureId);
							}
							
							if (!TextUtils.isEmpty(url)) {
								imgPaths.add(url);
								mGridAdapter.notifyDataSetChanged();
								showToast("图片上传成功");
							}else {
								showToast("图片上传失败");
							}
							
						}else {
							
							if (data.has("msg")) {
								showToast(msg);
							}else {
								showToast("图片上传失败");
							}
						}
						
						dimissDialog();
				} catch (Exception e) {
					dimissDialog();
					e.printStackTrace();
				}
					
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {

				dimissDialog();
				if (null != strMsg) {
					Toast.makeText(NewBuidEventActivity.this, strMsg, Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(NewBuidEventActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				}
				requestNum = 1;

				if (requestNum == 1){
						requestNum = 0;
					return;
				}

				super.onFailure(t, errorNo, strMsg);
			}
		};

		FinalHttp http = new FinalHttp();
		http.post(Server.BASE_URL + Server.UPLOAD_URL + 
				"?token="+ SPUtil.getDefault(NewBuidEventActivity.this).getToken(), params,callback);
	}
	
	public void backClick(View view){
		finish();
	}
	
	private class GridAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		private int datasize;

		public GridAdapter() {
			layoutInflater = LayoutInflater.from(NewBuidEventActivity.this);
			datasize = imgPaths.size();
			imgPaths.add(datasize, "add");
		}

		@Override
		public int getCount() {
			return imgPaths.size();
		}

		@Override
		public Object getItem(int position) {
			return imgPaths.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.edit_member_head_item, null);
				holder.head = (SimpleDraweeView) convertView
						.findViewById(R.id.headimg);
				holder.delIcon = (ImageView) convertView
						.findViewById(R.id.delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String imgId = imgPaths.get(position);
			if (imgId.equals("add")) {
				holder.head.setImageResource(R.drawable.add_pic);
				holder.delIcon.setVisibility(View.GONE);
			} else {
				holder.delIcon.setVisibility(View.VISIBLE);
				holder.head.setImageURI(Uri.parse(Server.BASE_URL + imgId));
			}
			return convertView;
		}
	}

	class ViewHolder {
		SimpleDraweeView head;
		ImageView delIcon;
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}