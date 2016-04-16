package com.meetrend.haopingdian.activity;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.UMshareUtil;
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
/**
 * 分享二维码
 *
 */
public class ShowErWeiMaActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home,click ="finishActivity")
	ImageView back;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	@ViewInject(id = R.id.er_img)
	SimpleDraweeView qrcImg;
	@ViewInject(id = R.id.right_actionbar_action,click = "shareEWM")
	TextView sendView;
	
	//扫描提示
	@ViewInject(id = R.id.bottom_hint_layout)
	LinearLayout bottomHintLayout;
	
	//门店图片显示在二维码上面
	@ViewInject(id = R.id.store_img)
	SimpleDraweeView storeImageView;
	
	//门店的名字
	@ViewInject(id = R.id.storenameview)
	TextView storeNameView;
	
	@ViewInject(id = R.id.hinttext)
	TextView hintTextView;

	
	private String eType;//二维码类型,1签到二维码 2推广二维码
	
	//图片路径
//	String signQrc;
//	String shareQrc;
//	String mQRC;
//	String myQrc;

	private String shareImgUrl;
	
	private FinalBitmap loader = null;
	public Bitmap mBitmap;

	private UMSocialService mController;//um分享类
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_erweima);
		FinalActivity.initInjectedView(this);
		loader = FinalBitmap.create(this);
		sendView.setText("转发");

		initView();
	}


	public void initView(){
		//分享到微信和朋友圈
		String appID = getResources().getString(R.string.appID);
		String appSecret =  getResources().getString(R.string.appSecret);

		Intent intent = getIntent();
		try {

			eType = intent.getStringExtra("ey");

			if (eType.equals("1")) {

				titleView.setText("签到二维码");
				shareImgUrl = intent.getStringExtra("signQrc");


			}else if(eType.equals("2")) {

				titleView.setText("推广二维码");
				shareImgUrl = intent.getStringExtra("shareQrc");


			}else if(eType.equals("3")){

				titleView.setText("门店二维码");
				shareImgUrl = intent.getStringExtra("mQRC");
				bottomHintLayout.setVisibility(View.VISIBLE);
				storeImageView.setVisibility(View.VISIBLE);
				storeImageView.setImageURI(Uri.parse(""));
				storeNameView.setVisibility(View.VISIBLE);
				storeNameView.setText(SPUtil.getDefault(ShowErWeiMaActivity.this).getStoreName());
				hintTextView.setText("扫一扫二维码，关注门店公众号");

			}else {

				titleView.setText("我的二维码");
				storeNameView.setVisibility(View.VISIBLE);
				storeNameView.setText(App.meDetail.userName);
				bottomHintLayout.setVisibility(View.VISIBLE);
				hintTextView.setText("扫一扫，我是您的专属客服");
				shareImgUrl = intent.getStringExtra("mMy");
			}

			qrcImg.setImageURI(Uri.parse(shareImgUrl));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**使用SSO授权必须添加如下代码 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
		if(ssoHandler != null){
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	//转发二维码
	public void shareEWM(View view){
		mController = new UMshareUtil().initShare(this, "二维码", "好评店", shareImgUrl, "http://");
		//人人网分享时跳转到此website的页面
		mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
		//分享弹框
		mController.openShare(this, false);
	}

	//结束activity
	public void finishActivity(View view){
		this.finish();
	}
}