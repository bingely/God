package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.ReFreshLoginUserInfoEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.StringUtils;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 *
 * 登录用户信息编辑显示
 */
public class LoginUserInfoActivity extends  BaseActivity{

    private static final Pattern PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    //titlebar
    @ViewInject(id = R.id.actionbar_home, click = "onClickHome")
    ImageView mBarHome;
    @ViewInject(id = R.id.actionbar_title)
    TextView mBarTitle;
    @ViewInject(id = R.id.actionbar_action, click = "onClickAction")
    TextView mBarAction;
    //头像
    @ViewInject(id = R.id.my_photo_lyt, click = "onClickPhoto")
    RelativeLayout mMyPhotoLyt;
    @ViewInject(id = R.id.my_photo)
    SimpleDraweeView mMyPhoto;
    //内容
    @ViewInject(id = R.id.my_name)
    EditText mMyName;
    @ViewInject(id = R.id.my_phone)
    EditText mMyPhone;

    @ViewInject(id = R.id.login_layout)
    LinearLayout loginLayout;
    @ViewInject(id = R.id.my_login_name)
    TextView mMyLoginName;
    @ViewInject(id = R.id.divider_1_)
    View mDivider1;

    @ViewInject(id = R.id.my_sex,click = "chooseSexClick")
    TextView mMySex;
    @ViewInject(id = R.id.right_arrow)
    ImageView rightArrowImg;


    private String modifyPicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_myinfo);
        FinalActivity.initInjectedView(this);

        mBarAction.setText("保存");
        mBarTitle.setText("个人信息");
        rightArrowImg.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);
        mDivider1.setVisibility(View.GONE);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        mMyName.setText(name);
        if (null != name && name.length() >0){
            mMyName.setSelection(name.length());
        }
        mMyLoginName.setText(intent.getStringExtra("loginname"));
        mMyPhone.setText(intent.getStringExtra("mobile"));
        mMySex.setText(intent.getStringExtra("malestr"));
        Drawable drawable = getResources().getDrawable(R.drawable.right_arrow);
        drawable.setBounds(0,0,drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mMySex.setCompoundDrawables(null,null,drawable,null);
        mMyPhoto.setImageURI(Uri.parse(Server.BASE_URL + intent.getStringExtra("imgurl")));
    }

    private static final int REQUESTCODE_PHOTO = 0x912;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUESTCODE_PHOTO
                    && resultCode == LoginUserInfoActivity.this.RESULT_OK) {

                String imgFilePath = data.getStringExtra("path");
                upLoadPictrue(imgFilePath);

            }else if (requestCode == 0X456 && resultCode == 0X456){
                mMySex.setText(sexStr=data.getStringExtra("sex"));
            }
    }


    public void onClickPhoto(View view){
        startActivityForResult(new Intent(this, AddPictrueActivity.class), REQUESTCODE_PHOTO);
        this.overridePendingTransition(R.anim.activity_popup, 0);
    }

    //更新
    public void onClickAction(View view){
            updateInfos();
    }

    String sexStr = "";//性别

    //选择性别
    public void chooseSexClick(View view){
        Intent intent = new Intent(LoginUserInfoActivity.this,SexChooseActivity.class);
        if (TextUtils.isEmpty(mMySex.getText().toString())){
            intent.putExtra("sex","保密");
        }else{
            intent.putExtra("sex", sexStr);
        }
        startActivityForResult(intent,0X456);
    }

    /**
     * 修改个人信息
     */
    public void updateInfos() {



        String username = mMyName.getText().toString().trim();
        String userphone = mMyPhone.getText().toString();

        if(TextUtils.isEmpty(username)){
            showToast("用户名不能为空");
            return;
        }
        if(TextUtils.isEmpty(userphone)){
            showToast("手机号码不能为空");
            return;
        }

        if (!StringUtils.judageMobile(userphone)) {
            showToast("手机号码格式不正确");
            return;
        }

        showDialog("正在更新...");
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(LoginUserInfoActivity.this).getToken());
        params.put("avatarId", modifyPicId);
        params.put("userName", username);
        params.put("mobile", userphone);

        String gender;
        if("男".equals(mMySex.getText().toString())){
            gender = "1";
        }else if("女".equals(mMySex.getText().toString())){
            gender = "2";
        }else{
            gender = "0";
        }
        params.put("gender", gender);

        Callback callback = new Callback(tag,this) {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    showToast(strMsg);
                }

            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dimissDialog();

                if (isSuccess) {
                    EventBus.getDefault().post(new ReFreshLoginUserInfoEvent());
                    finish();
                } else {
                    if (data.has("msg")) {
                        showDialog(data.get("msg").toString());
                    }else {
                        showDialog("更新失败");
                    }
                }
            }
        };

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.get(Server.BASE_URL + Server.UPDATE_MY_INFOS, params, callback);
    }

    public void upLoadPictrue(String absPath){

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(LoginUserInfoActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(LoginUserInfoActivity.this).getStoreId());

        try {
            params.put("file", new File(absPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.w(tag, e.getMessage());
        }

        Callback callback = new Callback(tag,LoginUserInfoActivity.this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                try{
                        JSONObject jsonObject = new JSONObject(t);
                        if(isSuccess){
                            modifyPicId = data.get("pictureId").getAsString();
                            String url = data.get("url").getAsString();
                            mMyPhoto.setImageURI(Uri.parse(Server.BASE_URL + url));
                        }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

        };

        FinalHttp http = new FinalHttp();
        http.post(Server.BASE_URL + Server.UPLOAD_URL+"?token="+SPUtil.getDefault(LoginUserInfoActivity.this).getToken(), params, callback);
    }

    public void onClickHome(View view){
        finish();
    }
}
