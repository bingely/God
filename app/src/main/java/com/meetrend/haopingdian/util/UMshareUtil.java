package com.meetrend.haopingdian.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.meetrend.haopingdian.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class UMshareUtil {

    /**
     * 分享封装，因多处需要使用
     * @param shareTitle
     * @param shareContent
     * @param shareImgUrl
     * @param shareImgTargetUrl
     * @return
     */
    public UMSocialService initShare(Activity activity,String shareTitle,String shareContent,
                                     String shareImgUrl,String shareImgTargetUrl){
        Log.i("shareTile",shareTitle);
        Log.i("shareContent",shareContent);
        Log.i("shareImgUrl",shareImgUrl);
        Log.i("shareImgTargetUrl",shareImgTargetUrl);

        String appID = activity.getString(R.string.appID);
        String appSecret =  activity.getString(R.string.appSecret);

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");//UM分享类

        //QQ
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(shareContent);
        qqShareContent.setTitle(shareTitle);
        qqShareContent.setShareImage(new UMImage(activity, shareImgUrl));
        qqShareContent.setTargetUrl(shareImgTargetUrl);
        mController.setShareMedia(qqShareContent);
        //QQZONE
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(shareContent);
        qzone.setTargetUrl(shareImgTargetUrl);
        qzone.setTitle(shareTitle);
        qzone.setShareImage(new UMImage(activity, shareImgUrl));
        mController.setShareMedia(qzone);
        // 微信
        UMWXHandler wxHandler = new UMWXHandler(activity,appID,appSecret);
        wxHandler.addToSocialSDK();
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(shareTitle);
        weixinContent.setShareContent(shareContent);
        weixinContent.setTargetUrl(shareImgTargetUrl);
        if (!TextUtils.isEmpty(shareImgUrl)){
            weixinContent.setShareImage(new UMImage(activity,shareImgUrl));
        }
        mController.setShareMedia(weixinContent);

        // 微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity,appID,appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareContent);
        circleMedia.setTitle(shareTitle);
        if (!TextUtils.isEmpty(shareImgUrl)){
            circleMedia.setShareImage(new UMImage(activity,shareImgUrl));
        }
        circleMedia.setTargetUrl(shareImgTargetUrl);
        mController.setShareMedia(circleMedia);

        //注册微信回调代码：
        SocializeListeners.SnsPostListener mSnsPostListener  = new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(SHARE_MEDIA platform, int stCode,
                                   SocializeEntity entity) {

                if (stCode == 200) {

                } else {

                }
            }
        };
        mController.registerListener(mSnsPostListener);

        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1104683027","3oxalWUQqaELu5iK");
        qqSsoHandler.addToSocialSDK();
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1104683027","3oxalWUQqaELu5iK");
        qZoneSsoHandler.addToSocialSDK();
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        return  mController;
    }

    public UMSocialService initProductManagerShare(Activity activity,String shareTitle,String shareContent,
                                     String shareImgUrl,String shareImgTargetUrl){
        String appID = activity.getString(R.string.appID);
        String appSecret =  activity.getString(R.string.appSecret);

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");//UM分享类

        //QQ
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(shareContent);
        qqShareContent.setTitle(shareTitle);
        qqShareContent.setShareImage(new UMImage(activity, shareImgUrl));
        qqShareContent.setTargetUrl(shareImgTargetUrl);
        mController.setShareMedia(qqShareContent);
        //QQZONE
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(shareContent);
        qzone.setTargetUrl(shareImgTargetUrl);
        qzone.setTitle(shareTitle);
        qzone.setShareImage(new UMImage(activity, shareImgUrl));
        mController.setShareMedia(qzone);

        String targeturl = shareImgTargetUrl.substring(0, shareImgTargetUrl.length() - 9);
        Log.i("TAG","目标路径"+targeturl);
        // 微信
        UMWXHandler wxHandler = new UMWXHandler(activity,appID,appSecret);
        wxHandler.addToSocialSDK();
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(shareTitle);
        weixinContent.setShareContent(shareContent);
        weixinContent.setTargetUrl(targeturl);
        if (!TextUtils.isEmpty(shareImgUrl)){
            weixinContent.setShareImage(new UMImage(activity,shareImgUrl));
        }
        mController.setShareMedia(weixinContent);

        // 微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity,appID,appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareContent);
        circleMedia.setTitle(shareTitle);
        if (!TextUtils.isEmpty(shareImgUrl)){
            circleMedia.setShareImage(new UMImage(activity,shareImgUrl));
        }
        circleMedia.setTargetUrl(targeturl);
        mController.setShareMedia(circleMedia);

        //注册微信回调代码：
        SocializeListeners.SnsPostListener mSnsPostListener  = new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(SHARE_MEDIA platform, int stCode,
                                   SocializeEntity entity) {

                if (stCode == 200) {

                } else {

                }
            }
        };
        mController.registerListener(mSnsPostListener);

        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1104683027","3oxalWUQqaELu5iK");
        qqSsoHandler.addToSocialSDK();
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1104683027","3oxalWUQqaELu5iK");
        qZoneSsoHandler.addToSocialSDK();
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        return  mController;
    }
}
