package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.LeftItemBean;
import com.meetrend.haopingdian.adatper.ProductManageListAdapter;
import com.meetrend.haopingdian.adatper.ProductManagerSearchSectionedAdapter;
import com.meetrend.haopingdian.adatper.ProductSearchSectionedAdapter;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshProductsEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.Canstant;
import com.meetrend.haopingdian.util.UMshareUtil;
import com.meetrend.haopingdian.widget.EditTextWatcher;
import com.meetrend.haopingdian.widget.PinnedHeaderListView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by a on 2016/3/15.
 *
 * 商品管理 搜索
 *
 */
public class ProductManagerSearchActivity extends  BaseActivity{

    private final static String TAG = ProductSearchReportoryActivity.class.getName();

    private  LinearLayout.LayoutParams  PARAMS;

    @ViewInject(id = R.id.back_img,click = "finishActivity")
    ImageView backView;
//    @ViewInject(id = R.id.actionbar_title)
//    TextView titleView;

    @ViewInject(id = R.id.clearbtn,click = "clearClick")
    ImageView clear;

    @ViewInject(id = R.id.left_container_layout)
    LinearLayout leftContainer;

    @ViewInject(id = R.id.pinnedListView)
    PinnedHeaderListView right_listview;

    @ViewInject(id = R.id.search_edit)
    EditText searchEdit;
    @ViewInject(id = R.id.searchbtn,click = "cancelClick")
    TextView searchBtn;

    @ViewInject(id = R.id.emptyview)
    TextView emptyview;

    @ViewInject(id = R.id.scrollview)
    ScrollView mScrollView;

    private boolean isScroll = true;

    private int size;

    private int select_res;
    private int unSelect_res;

    private UMSocialService mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager_search);

        FinalActivity.initInjectedView(this);

        init();

        searchEdit.addTextChangedListener(new EditTextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    clear.setVisibility(View.GONE);
                    searchBtn.setText("取消");
                } else {
                    searchBtn.setText("搜索");
                    clear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void init(){
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        select_res = R.drawable.icon_selected;
        unSelect_res = R.drawable.left_un_select_item_bg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != mController){
            UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
            if(ssoHandler != null){
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 产品上架或者下载 s删除
     * @param type up:上架;down:下架 ;del删除
     * @param entityIds 产品的库存id的jsonarray格式
     */
    public void producPutOnorDownOrDel(final String type,String entityIds,Pici pici){

        showDialog();

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(ProductManagerSearchActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductManagerSearchActivity.this).getStoreId());


        if(type.equals("del")){
//            String delStr = "{"
//                    +"\""+"entityIds"+"\""+":"+
//                    "["+ "\""+entityIds+"\""+"]"
//                    +"}";
//            params.put("args",delStr);
            params.put("inventoryId",pici.inventoryId);
            params.put("productId",pici.productId);
        }else{
            String josnStr = "{"
                    +"\""+"entityIds"+"\""+":"+
                    "["+ "\""+entityIds+"\""+"]"
                    +","
                    +"\""+"type"+"\""+":"
                    +"\""+type+"\""
                    +"}";
            params.put("args",josnStr);
        }


        Callback callback = new Callback(tag, this) {

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
                    if(type.equals("up"))
                        showToast("上架成功");
                    else if (type.equals("down"))
                        showToast("下架成功");
                    else if(type.equals("del")){
                        showToast("删除成功");
                    }
                    EventBus.getDefault().post(new RefreshProductsEvent());//发送消息通知列表刷新
                    finish();
                } else {
                    dimissDialog();
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }else{
                        showToast("操作失败");
                    }
                }
            }
        };
        CommonFinalHttp finalHttp = new CommonFinalHttp();
        if (type.equals("del"))
            finalHttp.get(Server.BASE_URL + Server.PRODUCT_DEL, params, callback);
        else
            finalHttp.get(Server.BASE_URL + Server.PRODUCT_UP_DOWN, params, callback);

    }

    //分享产品

    public void shareProduct(final Pici pici){

        showDialog();

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(ProductManagerSearchActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductManagerSearchActivity.this).getStoreId());
        params.put("productId", pici.productId);

        Callback callback = new Callback(tag, this) {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    showToast(strMsg);
                }else
                    showToast("分享失败");
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtil.v(TAG, "产品分享: " + t);

                if (isSuccess) {

                    String title = data.get("title").getAsString();
                    String path = data.get("path").getAsString();
                    String remark = data.get("remark").getAsString();

                    if (mController == null){
                        //Server.BASE_URL + pici.productImage
                        mController = new UMshareUtil().initProductManagerShare(ProductManagerSearchActivity.this,title,remark,"http://",path);
                    }
                    //人人网分享时跳转到此website的页面
                    mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
                    //分享弹框
                    mController.openShare(ProductManagerSearchActivity.this, false);

                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }else{
                        showToast("分享失败");
                    }
                }
                dimissDialog();
            }
        };
        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.PRODUCT_SHAARE, params, callback);
    }

    //结束页面
    public void cancelClick(View view){
        if (searchBtn.getText().toString().equals("搜索")) {
            loadData(searchEdit.getText().toString());
        }else {
            finish();
        }
    }

    //clear
    public void clearClick(View view){
        searchEdit.setText("");
    }

    public void loadData(String key) {
        showDialog();

        AjaxParams params = new AjaxParams();

        params.put("token", SPUtil.getDefault(this).getToken());
        params.put("storeId", SPUtil.getDefault(this).getStoreId());
        params.put("key", key);

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

                    List<LeftItemBean> productTypeNames = new ArrayList<LeftItemBean>();
                    List<TeaProductEntity> mList = new ArrayList<TeaProductEntity>();

                    JsonArray jsonArr = data.get("catalogList").getAsJsonArray();
                    Gson gson = new Gson();
                    mList = gson.fromJson(jsonArr,new TypeToken<List<TeaProductEntity>>() {}.getType());
                    if (null == mList || mList.size() == 0) {
                        emptyview.setVisibility(View.VISIBLE);
                    } else {
                        emptyview.setVisibility(View.GONE);

                        //right
                        TeaProductEntity teaProductEntity = null;
                        for (int i = 0; i < mList.size(); i++) {
                            teaProductEntity = mList.get(i);
                            LeftItemBean leftItemBean = new LeftItemBean();
                            if (i == 0 )
                                leftItemBean.isSelect = true;
                            else
                                leftItemBean.isSelect = false;

                            leftItemBean.name = teaProductEntity.catalogName;
                            productTypeNames.add(leftItemBean);//左边数据

                            for (int j = 0; j < mList.get(i).nameList.size(); j++) {
                                mList.get(i).nameList.get(j).isShowChildList = true;
                            }
                        }

                        final ProductManagerSearchSectionedAdapter sectionedAdapter = new ProductManagerSearchSectionedAdapter(ProductManagerSearchActivity.this,
                                productTypeNames, mList);
                        right_listview.setAdapter(sectionedAdapter);

                        //接口回调
                        sectionedAdapter.setPopCallBack(new ProductManagerSearchSectionedAdapter.PopCallBack() {
                            @Override
                            public void eidtCallback(int type, Pici pici) {
                                dealCallBack(pici, type);
                            }
                        });

                        sectionedAdapter.setPopChildCallBack(new ProductManagerSearchSectionedAdapter.PopChildCallBack() {
                            @Override
                            public void eidtchildCallback(int type, Pici pici) {
                                dealCallBack(pici, type);
                            }
                        });

                        //left
                        mScrollView.setBackgroundColor(Color.parseColor("#ebedf0"));
                        int minBtnHeight =  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                52.0f, getResources().getDisplayMetrics());

                        leftContainer.removeAllViews();
                        TeaProductEntity entity = null;
                        Button button = null;

                        size = mList.size();
                        for (int i = 0; i < size ; i++) {

                            entity = mList.get(i);

                            button = new Button(ProductManagerSearchActivity.this);
                            button.setId(i);
                            button.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                            button.setTextColor(Color.parseColor("#000000"));
                            button.setTextSize(16);
                            button.setBackgroundResource(unSelect_res);
                            button.setText(entity.catalogName);
                            button.setMinHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    52.0f, getResources().getDisplayMetrics()));//设置按钮的最小高度
                            leftContainer.addView(button);
                        }

                        leftContainer.getChildAt(0).setBackgroundResource(select_res);

                        right_listview.setOnScrollListener(new AbsListView.OnScrollListener() {

                            @Override
                            public void onScrollStateChanged(AbsListView arg0, int arg1) {

                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {

                                RadioButton radioButton = null ;
                                Button button = null ;

                                if(isScroll){

                                    for (int i = 0; i < size; i++)
                                    {
                                        if (i == sectionedAdapter.getSectionForPosition(firstVisibleItem))
                                        {
                                            leftContainer.getChildAt(i).setBackgroundResource(select_res);

                                        }else{
                                            leftContainer.getChildAt(i).setBackgroundResource(unSelect_res);
                                        }
                                    }

                                }else{
                                    isScroll = true;
                                }
                            }
                        });


                        for (int i = 0; i < size; i++) {

                            leftContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    isScroll = false;

                                    int id = v.getId();
                                    int rightSection = 0;

                                    for (int j = 0; j < size; j++) {

                                        if (id != j) {
                                            leftContainer.getChildAt(j).setBackgroundResource(unSelect_res);
                                        }else{
                                            leftContainer.getChildAt(id).setBackgroundResource(select_res);
                                        }
                                    }

                                    for(int i=0; i< id; i++){
                                        rightSection += sectionedAdapter.getCountForSection(i)+1;
                                    }
                                    right_listview.setSelection(rightSection);
                                }

                            });

                        }

                        dimissDialog();
                    }

                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }
                }
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.PRODUCT_LIST, params, callback);
    }

    /**
     * 判断产品是否可以删除
     * 0编辑，3删除
     */
    public void isCanDel(final Pici pici,final int type){
        AjaxParams params = new AjaxParams();

        params.put("token", SPUtil.getDefault(ProductManagerSearchActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductManagerSearchActivity.this).getStoreId());
        params.put("productId",pici.productId);
        Callback callback = new Callback(tag, this) {

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
                if (isSuccess) {

                    boolean isDel = data.get("result").getAsBoolean();//是否可以删除产品
                    if (type == 0){
                        popEdit(pici, isDel);

                    }else if (type == 3){

                        if (isDel)
                            popDel(pici);//删除
                        else
                            showToast("不可以删除");
                    }

                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }
                }
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.JUDGE_PRODUCT_IS_CAN_DEL, params, callback);

    }


    private void dealCallBack(Pici pici,int type){
        switch (type){
            case 0:
                //编辑
                isCanDel(pici,0);
                ProductManagerSearchActivity.this.finish();
                break;
            case 1:
                //上架
                popPutOn(pici);

                break;
            case 2:
                //下架
                popPutDown(pici);

                break;
            case 3:
                //删除
                isCanDel(pici,3);
                //popDel(pici);

                break;
            case 4:
                //分享
                popShare(pici);
                break;
            default:
                break;
        }
    }

    //编辑;
    public void popEdit(Pici pici,boolean isCanEdit){
        Intent intent = new Intent(this,ProductEditOrAddActivity.class);
        intent.putExtra(Canstant.PRODUCT_MANAGER_OPEN_TYPE,1);
        intent.putExtra("productId", pici.productId);
        intent.putExtra("searchEditMode",true);
        intent.putExtra("isCanEdit",isCanEdit);
        startActivity(intent);
        finish();
    }

    //上架
    public void popPutOn(Pici pici){
        producPutOnorDownOrDel("up", pici.inventoryId, null);
    }
    //下架
    public void popPutDown(Pici pici){
        producPutOnorDownOrDel("down", pici.inventoryId,null);
    }

    //删除
    public void popDel(Pici pici){
        producPutOnorDownOrDel("del", pici.inventoryId,pici);
    }

    //分享
    public void popShare(Pici pici){
        shareProduct(pici);
    }

    //结束Activity
    public void finishActivity(View view){
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        ProductManagerSearchActivity.this.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
    }
}
