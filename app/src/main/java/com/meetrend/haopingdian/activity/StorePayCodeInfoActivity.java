package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshPayCodeInfoEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.UMshareUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import de.greenrobot.event.EventBus;

public class StorePayCodeInfoActivity extends  BaseActivity{

    private final static String TAG = StorePayCodeInfoActivity.class.getName();

    @ViewInject(id = R.id.actionbar_home,click = "finishActivity")
    ImageView backView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleTextView;
    @ViewInject(id = R.id.actionbar_action,click = "actionClick")
    TextView editTextView;

    @ViewInject(id = R.id.storeerweima_img)
    SimpleDraweeView storeImageView;

    @ViewInject(id = R.id.storename_textview)
    TextView storeNameView;

    //分享
    @ViewInject(id = R.id.share_btn,click = "sharecick")
    TextView shareBtn;

    private String payCodeId;
    private String payCodeName;
    private String payCodeAmount;
    private int fromCode;//0标识从门店收款码进入，1标识从收款码管理列表进来

    private UMSocialService mController;//um分享类


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storetimascan);
        EventBus.getDefault().register(this);
        FinalActivity.initInjectedView(this);

        titleTextView.setText(getResources().getText(R.string.store_title));
        editTextView.setText(getResources().getString(R.string.editview_text));
        shareBtn.setVisibility(View.VISIBLE);

        if (getIntent().getStringExtra("paycodeid") != null){
            Intent intent = getIntent();
            payCodeId = intent.getStringExtra("paycodeid");
            payCodeName = intent.getStringExtra("paycodename");
            payCodeAmount = intent.getStringExtra("paycodeamount");

            fromCode= 1;
            getStoreEwMInfo();
            shareBtn.setVisibility(View.VISIBLE);
        }else{
            fromCode= 0;
            getStoreEwMInfo();
        }
    }

    public void onEventMainThread(RefreshPayCodeInfoEvent refreshEvent){

        getStoreEwMInfo();
    }


    //分享
    public void sharecick(View view){

        mController = new UMshareUtil().initShare(this,"门店收款码","好评店",shareImgUrl,"http://");

        //人人网分享时跳转到此website的页面
        mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
        //分享弹框
        mController.openShare(this, false);
    }

    /**使用SSO授权必须添加如下代码 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    //编辑
    public void actionClick(View view){
        Intent intent = new Intent(this,StorePayCodeInfoEditActivity.class);
        intent.putExtra("name",payCodeName);
        intent.putExtra("paycodeid",mPayCodeId);
        intent.putExtra("logo",logo);
        intent.putExtra("paycodeamount",payCodeAmount);
        intent.putExtra("desStr",desStr);
        startActivity(intent);
    }

    /**
     * 获取门店收款码信息
     */

    private String mPayCodeId;
    private String mStoreName;
    private String mQrCode;//二维码
    private String logo;//图标
    private String mOpenName;
    private String desStr;//描述
    private String shareImgUrl;//分享图片的地址

    private void getStoreEwMInfo(){

        showDialog();
        Callback callback = new Callback(tag,this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dimissDialog();

                if (isSuccess) {
                    mPayCodeId = data.get("payCodeId").getAsString();
                    mStoreName = data.get("storeName").getAsString();
                    mQrCode = data.get("qrCode").getAsString();

                    if (data.has("logo"))
                     logo = data.get("logo").getAsString();

                    if (data.has("openName"))
                    mOpenName = data.get("openName").getAsString();

                    if (data.has("systemName"))
                        mOpenName = data.get("systemName").getAsString();

                    if (data.has("description"))
                        desStr = data.get("description").getAsString();

                    if (data.has("downPictureURL"))
                        shareImgUrl = data.get("downPictureURL").getAsString();

                    storeNameView.setText(mOpenName + "(" + mStoreName + ")");
                    if(fromCode == 0){
                        storeImageView.setImageURI(Uri.parse(Server.BASE_URL +mQrCode));
                    }else{
                        storeImageView.setImageURI(Uri.parse(Server.BASE_URL + "Ecp.Picture.view.img?pictureId="+mQrCode));
                    }


                }else{

                    if (data.has("msg"))
                        showToast(data.get("msg").getAsString());
                    else
                        showToast(R.string.load_fail_str);
                }


            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (strMsg != null) {
                    showToast(strMsg);
                }else {
                    showToast(R.string.load_fail_str);
                }
            }
        };
        AjaxParams params = new AjaxParams();
        CommonFinalHttp commonFinalHttp = new CommonFinalHttp();

        if (fromCode == 1){
            params.put("payCodeId", payCodeId);
            params.put("token", SPUtil.getDefault(this).getToken());
            commonFinalHttp.get(Server.BASE_URL + Server.LIST_PAYCODE_INFO, params, callback);
        } else{
            params.put("storeId", SPUtil.getDefault(this).getStoreId());
            params.put("token", SPUtil.getDefault(this).getToken());
            commonFinalHttp.get(Server.BASE_URL + Server.STORE_EWM_INFO, params, callback);
        }
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