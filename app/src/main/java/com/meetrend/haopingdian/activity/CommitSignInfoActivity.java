package com.meetrend.haopingdian.activity;


import java.io.File;
import java.io.FileNotFoundException;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.FinishPointListEvent;
import com.meetrend.haopingdian.event.RefreshSignListEvent;
import com.meetrend.haopingdian.event.SendMemberEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

import de.greenrobot.event.EventBus;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 提交客户签到的详情信息
 * @author 肖建斌
 *
 */
public class CommitSignInfoActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home,click = "finishActivity")
	ImageView backView;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	@ViewInject(id = R.id.actionbar_action,click = "sureClick")
	TextView sureView;
	
	@ViewInject(id = R.id.choose_member_layout,click = "chooseClick")
	LinearLayout chooseLayout;
	@ViewInject(id = R.id.address_name_view)
	TextView addressNameView;
	@ViewInject(id = R.id.sign_customer_name)
	TextView signCustomerView;
	@ViewInject(id = R.id.sign_remark_txt)
	TextView desView;
	@ViewInject(id = R.id.sign_imageview,click = "signImageViewClick")
	SimpleDraweeView signImageView;
	@ViewInject(id = R.id.upload_hint_view )
	TextView hintView;
	
	private String location;//当前位置
	private String lat;
	private String lon;
	
	private String customerId = "";
	private String imageId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commitsigninfo);
		EventBus.getDefault().register(this);
		FinalActivity.initInjectedView(this);
		
		titleView.setText("签到");
		sureView.setText("提交");
		
		Intent intent = getIntent();
		location = intent.getStringExtra("name");
		lat = intent.getStringExtra("lat");
		lon = intent.getStringExtra("lon");
		
		addressNameView.setText(location);

		hintView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!hintView.getText().toString().equals("修改上传图片"))
					return;
				startActivityForResult(new Intent(CommitSignInfoActivity.this, AddPictrueActivity.class), 0x912);
				CommitSignInfoActivity.this.overridePendingTransition(R.anim.activity_popup, R.anim.dialog_out_anim);
			}
		});
	}
	
	private Member member;
	public void onEventMainThread(SendMemberEvent event){
		
		member = event.member;
		signCustomerView.setText(member.customerName);
	} 
	
	//确定
	public void sureClick(View view){
		commitInfos(member);
	}
	
	//选择联系人
	public void chooseClick(View view){
		Intent intent = new Intent(CommitSignInfoActivity.this, ChooseSingleMemberActivity.class);
		startActivity(intent);
	}
	
	public void signImageViewClick(View view){
		if (uploadSuccess){
			Intent intent = new Intent(CommitSignInfoActivity.this, PictrueBrowserActivity.class);
			intent.putExtra("img_url", Server.BASE_URL + url);
			startActivity(intent);
		}else{
			startActivityForResult(new Intent(this, AddPictrueActivity.class),0x912);
			this.overridePendingTransition(R.anim.activity_popup, R.anim.dialog_out_anim);
		}
	}
	
	// 将当前位置信息发送至服务器
	public void commitInfos(Member member) {
		
		showDialog("正在提交...");
		
		Callback callback = new Callback(tag,this) {
			
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				if (isSuccess) {
					
					EventBus.getDefault().post(new FinishPointListEvent());
					EventBus.getDefault().post(new RefreshSignListEvent());
					finish();
					
					dimissDialog();
					
				}else {
					dimissDialog();
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
					}
				}
				
				dimissDialog();
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
			}
		};
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(CommitSignInfoActivity.this).getToken());
		params.put("location", location);
		params.put("lat", lat);
		params.put("lng", lon);
		params.put("desc", desView.getText().toString());
		params.put("imageId", imageId);//图片id
		
		if (null != member) {
			params.put("customerId", member.userId);//客户id
		}
		
		CommonFinalHttp commonFinalHttp = new CommonFinalHttp();
		commonFinalHttp.post(Server.BASE_URL + Server.SIGN_REQUEST, params, callback);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == this.RESULT_OK) {
			switch (requestCode) {
			case 0x912:
				String localPath = data.getStringExtra("path");
				showDialog("上传中...");
				upLoadPictrue(localPath);
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 上传图片
	private boolean uploadSuccess;
	private String url;
	private void upLoadPictrue(String localPath){
		
		AjaxParams params = new AjaxParams();
		params.put("storeId", SPUtil.getDefault(this).getStoreId());

		try {
			params.put("file", new File(localPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogUtil.w(tag, e.getMessage());
		}

		Callback callback = new Callback(tag, this) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();

				try {
					if (isSuccess) {

						uploadSuccess = true;
						
						imageId = data.get("pictureId").getAsString();
						 url = data.get("url").getAsString();
						showToast("上传成功");
						signImageView.setImageURI(Uri.parse(Server.BASE_URL + url));

						Resources resource=(Resources)getBaseContext().getResources();
						ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.happyred);
						hintView.setTextColor(csl);
						hintView.setText("修改上传图片");

					} else {
						uploadSuccess = false;
						if (data.has("msg")) {
							showToast(data.get("msg").toString());
						} else {
							showToast("上传失败");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					uploadSuccess = false;
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
				
				if (null != strMsg) {
					showToast(strMsg);
				}
			}

		};

		CommonFinalHttp http = new CommonFinalHttp();
		http.post(Server.BASE_URL + Server.UPLOAD_URL + "?token="+ SPUtil.getDefault(this).getToken(), params,callback);
				
	}
	
	public void finishActivity(View view){
		finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
}