package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshPayCodeInfoEvent;
import com.meetrend.haopingdian.event.RefreshPayCodeListEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import de.greenrobot.event.EventBus;

/**
 * 门店收款码编辑
 */
public class StorePayCodeInfoEditActivity extends  BaseActivity{
    private final static String TAG = StorePayCodeInfoActivity.class.getName();

    @ViewInject(id = R.id.actionbar_home,click = "finishActivity")
    ImageView backView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleTextView;
    @ViewInject(id = R.id.actionbar_action,click = "actionClick")
    TextView editTextView;

    @ViewInject(id = R.id.edit_circle_img)
    SimpleDraweeView mEditCircleImg;

    @ViewInject(id = R.id.center_layout)
    LinearLayout centerEditLayout;
    @ViewInject(id = R.id.name_edit)
    EditText nameEdit;
    @ViewInject(id = R.id.money_edit)
    EditText amountEdit;

    @ViewInject(id = R.id.beizhu_edit)
    EditText mBeizhuEdit;
    @ViewInject(id = R.id.edit_layout,click = "editPicClick")
    RelativeLayout mEditLayout;

    private String payCodeId;
    private String logoId;
    private String payCodename;
    private String payCodeAmount;
    private String desStr;

    private int fromcode ;//1标识新建

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storema_edit);

        FinalActivity.initInjectedView(this);

        titleTextView.setText(R.string.store_title);
        editTextView.setText(R.string.title_finish_hint);

        Intent intent = getIntent();

        //编辑
        if (null != intent.getStringExtra("paycodeid")){

            payCodeId = intent.getStringExtra("paycodeid");
            logoId = intent.getStringExtra("logo");
            payCodename = intent.getStringExtra("name");
            desStr = intent.getStringExtra("desStr");

            //门店收款码列表的详情进入
            if (null != intent.getStringExtra("paycodeamount")){
                payCodeAmount = intent.getStringExtra("paycodeamount");
                centerEditLayout.setVisibility(View.VISIBLE);
                nameEdit.setText(payCodename);
                amountEdit.setText(payCodeAmount);
            }

            mBeizhuEdit.setText(desStr);

        }else{
            fromcode = 1;
            centerEditLayout.setVisibility(View.VISIBLE);
        }

        //添加


    }

    @Override
    protected void onStart() {
        super.onStart();

        mEditCircleImg.setImageURI(Uri.parse(Server.BASE_URL + "Ecp.Picture.view.img?pictureId=" + logoId));
    }

    //完成
    public void actionClick(View view){
        upDateStoreEwMInfo();
    }

    public void editPicClick(View view){

        startActivityForResult(new Intent(this,AddPictrueActivity.class), 0x912);
        this.overridePendingTransition(R.anim.activity_popup, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 0x912) {

            try {
                String pictruePath = data.getStringExtra("path");
                if (TextUtils.isEmpty(pictruePath)) {
                    Toast.makeText(this, R.string.load_pic_fail, Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog();
                upLoadPictrue(pictruePath);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void upDateStoreEwMInfo(){

        showDialog();
        Callback callback = new Callback(tag,this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {

                    finish();
                    EventBus.getDefault().post(new RefreshPayCodeInfoEvent());

                }else{

                    if (data.has("msg")) {
                        Log.i(TAG + "----msg", data.get("msg").getAsString());
                        showToast(data.get("msg").getAsString());
                    }
                    else
                        showToast(R.string.load_fail_str);
                }
                dimissDialog();
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
        params.put("storeId", SPUtil.getDefault(this).getStoreId());
        params.put("token", SPUtil.getDefault(this).getToken());
        params.put("payCodeId", payCodeId);//必,二维码
        //Log.i(TAG + " payCodeId", payCodeId);
        params.put("pictureId", returnPictureId);//logo
       // Log.i(TAG + " returnPictureId", returnPictureId);
        params.put("name", nameEdit.getText().toString());//必
        //Log.i(TAG + " name", name);
        params.put("amount",amountEdit.getText().toString());
        // Log.i(TAG + " payCodeAmount", payCodeAmount);
        params.put("description",mBeizhuEdit.getText().toString());

        CommonFinalHttp commonFinalHttp = new CommonFinalHttp();
        if (fromcode == 1){
            commonFinalHttp.get(Server.BASE_URL + Server.CTEATE_PAYCODE_INFO, params, callback);
            EventBus.getDefault().post(new RefreshPayCodeListEvent());
        } else{
            commonFinalHttp.get(Server.BASE_URL + Server.MODIFY_STORE_SKM, params, callback);
        }
    }

    /**
     * 需要上传的本地图片的路径
     * @param absPath  路径
     */
    private String returnPictureId;

    private void upLoadPictrue(String absPath){

        AjaxParams params = new AjaxParams();
        params.put("storeId", SPUtil.getDefault(this).getStoreId());

        try {

            File file = new File(absPath);
            if (null == file) {
                dimissDialog();
                Toast.makeText(this, R.string.load_pic_fail, Toast.LENGTH_SHORT).show();
            }
            params.put("file", file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.w(tag, e.getMessage());
        }

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try {

                    JSONObject jsonObject = new JSONObject(t);
                    if (isSuccess) {

                        String url = data.get("url").getAsString();
                        returnPictureId = data.get("pictureId").getAsString();

                        if (!TextUtils.isEmpty(url)) {
                            mEditCircleImg.setImageURI(Uri.parse(Server.BASE_URL + url));
                        }else {
                            Toast.makeText(StorePayCodeInfoEditActivity.this, R.string.pic_update_fail, Toast.LENGTH_SHORT).show();
                        }

                    }else {

                        if (data.has("msg")) {
                            Toast.makeText(StorePayCodeInfoEditActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(StorePayCodeInfoEditActivity.this, R.string.pic_update_fail, Toast.LENGTH_SHORT).show();
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
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    Toast.makeText(StorePayCodeInfoEditActivity.this, strMsg, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(StorePayCodeInfoEditActivity.this,R.string.net_has_problem , Toast.LENGTH_SHORT).show();
                }
            }

        };

        FinalHttp http = new FinalHttp();
        http.post(Server.BASE_URL + Server.UPLOAD_URL +
                "?token=" + SPUtil.getDefault(StorePayCodeInfoEditActivity.this).getToken(), params, callback);
    }

    public void finishActivity(View view){
        finish();
    }
}
