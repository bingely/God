package com.meetrend.haopingdian.activity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.MemberDetail;
import com.meetrend.haopingdian.bean.OrderBean;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.event.RfreshMemberInfoEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.InputTools;
import com.meetrend.haopingdian.util.MessyCodeCheck;

import de.greenrobot.event.EventBus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 联系人编辑界面
 *
 * @author 肖建斌
 */
public class MemberInfoEditActivity extends BaseActivity {

    private MemberDetail mMember;
    private int mAgePosition = -1;
    private String[] mAgeArray;

    @ViewInject(id = R.id.actionbar_home, click = "homeClick")
    ImageView mHome;
    @ViewInject(id = R.id.actionbar_title)
    TextView mTitle;
    @ViewInject(id = R.id.actionbar_action, click = "saveClick")
    TextView mAction;
    // body
    @ViewInject(id = R.id.et_member_name)
    EditText mName;
    @ViewInject(id = R.id.et_member_phonenumber)
    EditText mPhone;
    @ViewInject(id = R.id.tv_age, click = "ageClick")
    TextView mAge;
    @ViewInject(id = R.id.rg_gender)
    private RadioGroup mGender;
    @ViewInject(id = R.id.et_member_remark)
    EditText mRemark;

    private String boforechangeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_edit);
        FinalActivity.initInjectedView(this);

        mAgeArray = this.getResources().getStringArray(R.array.age_name);
        mTitle.setText("详细资料");
        mAction.setText("保存");

        initView();
    }


    private void initView() {

        String status = getIntent().getStringExtra("status");
        mMember = App.memberDetail;//全局详情


        if (mMember.gender != null && !"".equals(mMember.gender)) {
            if (mMember.gender.equals("1")) {
                mGender.check(R.id.male);
            } else if (mMember.gender.equals("2")) {
                mGender.check(R.id.female);
            }
        }

        if (!TextUtils.isEmpty(mMember.ageGroup) && !mMember.ageGroup.equals("0") && !mMember.ageGroup.equals("-1")) {
            mAgePosition = Integer.parseInt(mMember.ageGroup);
            int index = Integer.parseInt(mMember.ageGroup);
            mAge.setText(mAgeArray[index-1]);
        }

        mName.requestFocus();
        mName.setText(mMember.name);
        if (mMember.name != null && mMember.name.length() >0)
            mName.setSelection(mMember.name.length());

        mPhone.setText(mMember.mobile);
        if (status.equals("2")){
            mPhone.setEnabled(false);
        }
        mRemark.setText(mMember.description);

        // 输入表情监听
        mRemark.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                boforechangeStr = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (MessyCodeCheck.isMessyCode(s.toString())) {
                    mRemark.setText(boforechangeStr);
                }
            }
        });
    }

    //返回
    public void homeClick(View v) {
        finish();
    }

    //年龄
    public void ageClick(View v) {

        if(InputTools.KeyBoard(mPhone))
            InputTools.hideKeyboard(mPhone);

        AlertDialog.Builder builder = new AlertDialog.Builder(MemberInfoEditActivity.this);
        builder.setTitle("请选择年龄段");
        builder.setItems(mAgeArray, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAge.setText(mAgeArray[which]);
                mAgePosition = which + 1;
            }
        });

        builder.create().show();
    }

    private static final Pattern PATTERN = Pattern
            .compile("^0?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$");

    public void saveClick(View v) {
        String name = mName.getText().toString();
        String phoneNumber = mPhone.getText().toString();
        String remark = mRemark.getText().toString();

        if (TextUtils.isEmpty(name)) {
            showToast("姓名不能为空");
            return;
        }

        int btnId = mGender.getCheckedRadioButtonId();

        String ageSection = mAge.getText().toString();
        String age_select = this.getResources().getString(
                R.string.str_please_choose);
        if (ageSection.equals(age_select)) {
            showToast(R.string.str_please_choose_age);
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            showToast(R.string.str_not_allow_empty_phone_number);
            return;
        }
        Matcher matcher = PATTERN.matcher(phoneNumber);
        if (!matcher.matches()) {
            showToast(R.string.str_err_format_phonenumber);
            return;
        }

        showDialog("正在更新...");

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(MemberInfoEditActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(MemberInfoEditActivity.this).getStoreId());
        params.put("name", name);
        params.put("ageGroup", mAgePosition + "");
        params.put("gender", btnId == R.id.male ? "1" : "2");
        params.put("mobile", phoneNumber);
        params.put("id", mMember.id);
        params.put("description", remark);
        params.put("email", "");
        String avatarStr = "";

        if (mMember.pictureId.contains("=")) {
            int index = mMember.pictureId.indexOf("=");
            avatarStr = mMember.pictureId.substring(index + 1);
        }
        params.put("avatarId", avatarStr);//如果不传图片会被覆盖

        Callback callback = new Callback(tag, MemberInfoEditActivity.this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dimissDialog();

                if (!isSuccess) {
                    if (data.has("msg")) {
                        showToast(data.get("msg").getAsString());
                    }else{
                        showToast("修改信息失败");
                    }
                    return;
                }

                JsonElement elment = data.get("operationState");
                if (elment != null) {
                    if (elment.getAsString().equalsIgnoreCase("success")) {

                        //刷新会员详情
                        RfreshMemberInfoEvent refreshEvent = new RfreshMemberInfoEvent();
                        EventBus.getDefault().post(refreshEvent);
                        //刷新通讯录列表
                        EventBus.getDefault().post(new RefreshEvent());
                        MemberInfoEditActivity.this.finish();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                dimissDialog();
                super.onFailure(t, errorNo, strMsg);
            }

        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.UPDATE_MEMBER_URL, params,callback);
    }
}