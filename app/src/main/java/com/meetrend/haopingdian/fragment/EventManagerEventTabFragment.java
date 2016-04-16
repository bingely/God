package com.meetrend.haopingdian.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ErWeiMaActivity;
import com.meetrend.haopingdian.activity.MsgSelectMemberActivity;
import com.meetrend.haopingdian.activity.NewBuidEventActivity;
import com.meetrend.haopingdian.activity.NewEventDetailActivity;
import com.meetrend.haopingdian.activity.WebViewActiity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.HtmlUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * 详情
 * 活动管理
 * @author 肖建斌
 *
 */
public class EventManagerEventTabFragment extends BaseChildOrderListFragment{
	
	private String TAG = EventManagerEventTabFragment.class.getName();
	
	//top
	@ViewInject(id = R.id.applyNumView)
	TextView applyView;
	@ViewInject(id = R.id.read_person_numview)
	TextView readView;
	@ViewInject(id = R.id.share_person_numview)
	TextView shareView;
	@ViewInject(id = R.id.comment_person_numview)
	TextView commentView;
	@ViewInject(id = R.id.prise_person_numview)
	TextView priseView;
	
	@ViewInject(id = R.id.event_name)
	TextView eventNameView;
	
	//修改
	@ViewInject(id = R.id.modify_layout,click = "modifyclick")
	RelativeLayout modifyLayout;
	
	//以下操作
	
	//分享
	@ViewInject(id = R.id.sharelayout,click = "shareLayoutClick")
	LinearLayout shareLayout;
	//查看
	@ViewInject(id = R.id.looklayout,click = "lookClick")
	LinearLayout lookLayout;
	//微信群发
	@ViewInject(id = R.id.mass_send_layout,click = "wxMassClick")
	LinearLayout wxLayout;
	//再发一个活动
	@ViewInject(id = R.id.send_agin_layout,click = "sendEventClick")
	LinearLayout sendAginLayout;
	//开放报名
	@ViewInject(id = R.id.open_apply_layout,click = "openApplyEventClick")
	LinearLayout openEventLayout;
	//二维码
	@ViewInject(id = R.id.share_erm_layout,click = "ermCick")
	LinearLayout ewmLayout;
	
	//开放报名img
	@ViewInject(id = R.id.openbtn)
	ImageView openImg;
	@ViewInject(id = R.id.open_text)
	TextView openTextView;
	//总收入
	@ViewInject(id = R.id.money_amount_layout)
	LinearLayout moneyLayout;
	@ViewInject(id = R.id.amount_money_num)
	TextView amountNumView;
	@ViewInject(id = R.id.top_empty_view)
	TextView topEmptyView;

	private boolean isPrepare;//做好准备
	private boolean hasLoad = false;//是否加载过 
	
	
	View rootView = null;
	
	//详情
	private JsonObject object;
	
	//要传递的数据
	String eventId;
	String eventName;
	String startTime;
	String endTime;
	String deadlineTime;
	
	//是否开启报名
	private boolean isOpen;

	//是否免费
	private boolean  ischarge;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.from(getActivity()).inflate(R.layout.fragment_eventmanagertabfragment, container,false);
			FinalActivity.initInjectedView(this, rootView);
			
			isPrepare = true;
			eventId = ((NewEventDetailActivity)getActivity()).eventID;
			mCurrentTime = new Date().getTime();//当前时间的时间戳
			Bundle bundle = getArguments();
			ischarge = bundle.getBoolean("ischarge",false);
			if (ischarge){
				moneyLayout.setVisibility(View.VISIBLE);
				topEmptyView.setVisibility(View.GONE);
			}else{
				moneyLayout.setVisibility(View.GONE);
				topEmptyView.setVisibility(View.VISIBLE);
			}
			requestList();
		}
		
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent != null) {
			parent.removeView(rootView);
		}
		
		return rootView;
	}
	
	//分享
	//友盟成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case 0x149:
				 Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_SHORT).show();
				 updateShareNum();
				break;
				
			case 0x148:
				Toast.makeText(getActivity(), "分享失败",  Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		/**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	//修改活动
	public void modifyclick(View view){
		Intent intent = new Intent(getActivity(), NewBuidEventActivity.class);
		intent.putExtra("fromtype", 2);//标识从该页面跳过
		intent.putExtra("eventId", eventId);//活动id
		intent.putExtra("ischarge",ischarge);//是否免费
		startActivity(intent);
	}
	
	//分享
	public void shareLayoutClick(View view){
		updateShareNum();
		//人人网分享时跳转到此website的页面
		mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
		//分享弹框
		mController.openShare(getActivity(), false);
	}
	
	//查看
	public void lookClick(View view){
		Intent intent = new Intent(getActivity(),WebViewActiity.class);
		intent.putExtra("url", link);
		startActivity(intent);
	}
	
	//微信群发
	public void wxMassClick(View view){
		Intent intent = new Intent(getActivity(), MsgSelectMemberActivity.class);
		intent.putExtra("from", "event");//辨别是从群发界面进入还是从活动详情进入
		intent.putExtra("eventid", eventId);
		startActivity(intent);
	}
	
	//再发一个(类似新建)
	public void sendEventClick(View view){
		Intent intent = new Intent(getActivity(), NewBuidEventActivity.class);
		intent.putExtra("fromtype", 3);//标识从该页面跳过
		intent.putExtra("eventId", eventId);//活动id
		intent.putExtra("ischarge",ischarge);//是否免费
		startActivity(intent);
	}
	
	//开放报名
	public void openApplyEventClick(View view){
		updateEvent();
	}
	
	//二维码
	public void ermCick(View view){
		Intent intent = new Intent(getActivity(), ErWeiMaActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void processHandleMessage(Message msg) {
		super.processHandleMessage(msg);
		
		if (msg.what == 0x145) {
			String resultEventInfo;
			
			//取出html标签
			if (eventInfo.length() > 100) 
				resultEventInfo	= (HtmlUtil.delHTMLTag(eventInfo)).substring(0, 100);
			else
				resultEventInfo = HtmlUtil.delHTMLTag(eventInfo);
			
			String shareTAG = link.replace("jdp", "mtp");//分享的活动链接
			Log.i(TAG+"-format share url--", shareTAG);
			
			//分享到微信和朋友圈
			String appID = getActivity().getResources().getString(R.string.appID);
			String appSecret =  getActivity().getResources().getString(R.string.appSecret);
			
			//设置QQ分享内容
			QQShareContent qqShareContent = new QQShareContent();
			//设置分享文字
			qqShareContent.setShareContent(resultEventInfo);
			//设置分享title
			qqShareContent.setTitle(eventName);
			qqShareContent.setShareImage(new UMImage(getActivity(), sharePictrueUrl));
			qqShareContent.setTargetUrl(shareTAG);
			mController.setShareMedia(qqShareContent);
			//设置Qzone分享文字及内容
			QZoneShareContent qzone = new QZoneShareContent();
			//设置分享文字
			qzone.setShareContent(resultEventInfo);
			//设置点击消息的跳转URL
			qzone.setTargetUrl(shareTAG);
			//设置分享内容的标题
			qzone.setTitle(eventName);
			//设置分享图片
			qzone.setShareImage(new UMImage(getActivity(), sharePictrueUrl));
			mController.setShareMedia(qzone);
			
			// 添加微信平台
			UMWXHandler wxHandler = new UMWXHandler(getActivity(),appID,appSecret);
			wxHandler.addToSocialSDK();
			
			// 添加微信朋友圈
			UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(),appID,appSecret);
			wxCircleHandler.setToCircle(true);
			wxCircleHandler.addToSocialSDK();
			
			//设置微信好友分享内容
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			//设置分享文字
			weixinContent.setShareContent(resultEventInfo);
			//设置title
			weixinContent.setTitle(eventName);
			//设置分享内容跳转URL
			weixinContent.setTargetUrl(shareTAG);
			//设置分享图片
			weixinContent.setShareImage(new UMImage(getActivity(),sharePictrueUrl));
			mController.setShareMedia(weixinContent);
			
			//设置微信朋友圈分享内容
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent(resultEventInfo);
			//设置朋友圈title
			circleMedia.setTitle(eventName);
			circleMedia.setShareImage(new UMImage(getActivity(),sharePictrueUrl));
			circleMedia.setTargetUrl(shareTAG);
			mController.setShareMedia(circleMedia);
			
			//注册微信回调代码：
			SnsPostListener mSnsPostListener  = new SnsPostListener() {

		        @Override
		    public void onStart() {

		    }

		    @Override
		    public void onComplete(SHARE_MEDIA platform, int stCode,
		        SocializeEntity entity) {
		    	
			      if (stCode == 200) {
			    	  mHandler.sendEmptyMessage(0x149);
			      } else {
			    	 mHandler.sendEmptyMessage(0x148);
			      }
		    }
		  };
		mController.registerListener(mSnsPostListener);
			
		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), "1104683027","3oxalWUQqaELu5iK");
		qqSsoHandler.addToSocialSDK();  
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), "1104683027","3oxalWUQqaELu5iK");
		qZoneSsoHandler.addToSocialSDK();
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
			
		}
	}
	

	@Override
	public void requestList() {
		
		if (!isPrepare || !isVisible || hasLoad) {
			return;
		}
		requestEventDetail(eventId);
	}
	
	//活动详情
	
	//时间戳
	Long mCurrentTime;
	Long mStartTime;
	Long mEndTime;
	Long mDeadLineTime;
	
	String eventAddress;
	String eventInfo;
	String limitNum;
	String link;
	
	String latString;
	String longatString;
	
	String sharePictrueUrl;
	
	//分享人数
	int shareNumber;
	
	private ArrayList<String> imgPaths = new ArrayList<String>();
	private ArrayList<String> pictrueids = new ArrayList<String>();
	
	private void requestEventDetail(String eventId){
		showDialog();
	try {
		
		App.eventId = eventId;
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("entityId",eventId);
		
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				mHandler.sendEmptyMessage(Code.FAILED);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					
					try {
						
						hasLoad = true;
						
						object = data.getAsJsonObject("memberActivityDetails");
						eventName = object.get("name").getAsString();//活动主题
						String joinNum = object.get("joinNumber").getAsString();//参与人数
						String readNum;
						try {
							readNum = object.get("readNumber").getAsString();//阅读人数
						} catch (Exception e) {
							readNum ="0";
						}

						//总收入
						if(null == object.get("money")){
							double money = object.get("money").getAsDouble();
							amountNumView.setText(NumerUtil.setSaveTwoDecimals(money+""));
						}else{
							amountNumView.setText("0.00");
						}

						shareNumber = object.get("shareNumber").getAsInt();//分享人数
						String dianZanNum = object.get("thumbsUp").getAsString();//点赞人数
						String commentNum = object.get("numCommet").getAsString();//评论人数
						
						String eventName = object.get("name").getAsString();//活动主题名字
						String startTime = object.get("activityStartDate").getAsString();//活动开始时间
						String endTime = object.get("activityEndDate").getAsString();//活动结束时间
						String deadlineTime = object.get("deadline").getAsString();//活动截止时间
						
						eventNameView.setText(eventName);
						//((NewEventDetailActivity)getActivity()).titleView.setText(eventName);
						
						sharePictrueUrl = object.get("picturesurl").getAsString();//活动分享图片链接
					
						eventAddress = object.get("address").getAsString();//活动地址
						eventInfo = object.get("detailExplain").getAsString();//活动详情
						limitNum = object.get("limitTheNumber").getAsString();//活动限制人数
						
						link = object.get("link").getAsString();
						
						//Log.i(TAG+"---------活动名字-------", eventName);
						//Log.i(TAG+"---------活动链接-------", link);
						//Log.i(TAG+"---------活动地址-------", eventInfo);
						
						
						latString = object.get("latitude").getAsString();
						longatString  = object.get("longitude").getAsString();
						
						JsonArray imagesArray = object.get("activityImages").getAsJsonArray();//活动详情图片
						for (int i = 0; i < imagesArray.size(); i++) {
							JsonObject jsonObject = (JsonObject) imagesArray.get(i);
							pictrueids.add(jsonObject.get("id").getAsString());
							imgPaths.add(jsonObject.get("pictureId").getAsString());
						}
						
						//转成相应的时间戳
						mStartTime = changeTime(startTime);
						mEndTime = changeTime(endTime);
						mDeadLineTime = changeTime(deadlineTime);
						
						isOpen = object.get("enroll").getAsBoolean();//true为开启，false关闭
						int status = object.get("status").getAsInt();
						
						if (status == 3 && mDeadLineTime < mCurrentTime) {
							
							openTextView.setText("关闭报名");
							openImg.setImageResource(R.drawable.close_h);
							openEventLayout.setEnabled(false);
							
						}else {
							
							if (isOpen) {
								openTextView.setText("关闭报名");
								openImg.setImageResource(R.drawable.close);
							}else {
								openTextView.setText("开放报名");
								openImg.setImageResource(R.drawable.opening);
							}
							
						}
						
						applyView.setText(joinNum);
						readView.setText(readNum);
						shareView.setText(shareNumber+"");
						commentView.setText(commentNum);
						priseView.setText(dianZanNum);
						
						mHandler.sendEmptyMessage(0x145);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		CommonFinalHttp request = new CommonFinalHttp();
		request.get(Server.BASE_URL + Server.EVENT_DETAIL, params, callback);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//将yyyy-MM-dd的字符串转成 long时间戳
	public long changeTime(String mDate) {
		
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
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
	
	public void updateEvent() {
		
		if (isOpen) {
			showDialog("正在关闭...");
		}else {
			showDialog("正在打开...");
		}
		
		boolean temp = isOpen;
		if (temp) {
			temp = false;
		}else {
			temp = true;
		}
		
		AjaxParams params = new AjaxParams();
		params.put("entityId", eventId);
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("enroll", temp +"");
		
		
		Callback callback = new Callback(tag, getActivity()) {
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
					
					if (isOpen) {
						isOpen = false;
						openImg.setImageResource(R.drawable.opening);
						openTextView.setText("开放报名");
					}else {
						
						isOpen = true;
						openImg.setImageResource(R.drawable.close);
						openTextView.setText("关闭报名");
					}
					
				} else {
					if (data.has("msg")) {
						String msgString = data.get("msg").getAsString();
						Toast.makeText(getActivity(), msgString, Toast.LENGTH_SHORT).show();
					}
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.UPDATE_ACTIVITY, params, callback);
	}
	
	/**
	 * 更新分享的次数
	 */
	public void updateShareNum() {
		
		AjaxParams params = new AjaxParams();
		params.put("bonusId", eventId);
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		
		params.put("shareNumber", ++shareNumber +"");
		
		Callback callback = new Callback(tag, getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					shareView.setText(shareNumber+"");
				} else {
					shareView.setText(--shareNumber +"");
					Toast.makeText(getActivity(), "分享人数更新失败", Toast.LENGTH_SHORT).show();
				}
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.SHARENUMREQUEST, params, callback);
	}

}