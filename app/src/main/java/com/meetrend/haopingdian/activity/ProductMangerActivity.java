package com.meetrend.haopingdian.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.LeftItemBean;
import com.meetrend.haopingdian.adatper.LeftListAdapter;
import com.meetrend.haopingdian.adatper.ProductManageListAdapter;
import com.meetrend.haopingdian.adatper.ProductSearchSectionedAdapter;
import com.meetrend.haopingdian.adatper.ProductSectionedAdapter;
import com.meetrend.haopingdian.bean.EventTypeBean;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.ManagerchildProductEvent;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.event.RefreshProductsEvent;
import com.meetrend.haopingdian.memberlistdb.MemberListDbOperator;
import com.meetrend.haopingdian.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.Canstant;
import com.meetrend.haopingdian.util.UMshareUtil;
import com.meetrend.haopingdian.widget.BaseTopPopWindow;
import com.meetrend.haopingdian.widget.PinnedHeaderListView;
import com.meetrend.haopingdian.widget.ProductManagerPopWindow;
import com.meetrend.haopingdian.widget.TeaEventThemesListPopWindow;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import de.greenrobot.event.EventBus;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 商品管理
 *
 * @author 肖建斌
 *         <p/>
 *         功能：展示 + 搜索
 */
public class ProductMangerActivity extends BaseActivity {

    private final static String TAG = ProductReportoryActivity.class.getName();

    private final static int LEFT = 1;
    private final static int RIGHT = 2;

    @ViewInject(id = R.id.actionbar_home, click = "finishActivity")
    ImageView backView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;

    @ViewInject(id = R.id.left_container_layout)
    LinearLayout leftContainer;

    @ViewInject(id = R.id.pinnedListView)
    PinnedHeaderListView right_listview;

    @ViewInject(id = R.id.header_search_layout, click = "searchClick")
    RelativeLayout searchLayout;


    @ViewInject(id = R.id.choose_img, click = "chooseListTypesClick")
    ImageView chooseTypeBtn;

    @ViewInject(id = R.id.product_add, click = "addClick")
    ImageView mAddBtn;

    @ViewInject(id = R.id.popwindow_container)
    FrameLayout popwindowContainer ;
    @ViewInject(id = R.id.container)
    LinearLayout container ;

    private ProductManageListAdapter sectionedAdapter;


    private boolean isScroll = true;

    private List<LeftItemBean> productTypeNames;

    private List<TeaProductEntity> mList;

    int size;
    int redColor = Color.parseColor("#000000");
    ProductHandler productHandler;

    private int select_res;
    private int unSelect_res;

    private List<EventTypeBean> eventTypeList;

    ProductManagerPopWindow popWindow = null;

    private UMSocialService mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);
        EventBus.getDefault().register(this);
        FinalActivity.initInjectedView(ProductMangerActivity.this);

        init();
        loadData();
    }

    public void onEventMainThread(RefreshProductsEvent event) {
        loadData();
    }

    //自列表操作回调
    public void onEventMainThread(ManagerchildProductEvent event) {

        dealCallBack(event.pici, event.type);
    }

    private void init(){
        titleView.setText("商品管理");
        productHandler = new ProductHandler(ProductMangerActivity.this);
        productTypeNames = new ArrayList<LeftItemBean>();
        mList = new ArrayList<TeaProductEntity>();

        eventTypeList = new ArrayList<EventTypeBean>();
        EventTypeBean eventTypeBean1 = new EventTypeBean();
        eventTypeBean1.FText = "全部";
        eventTypeBean1.FValue = "";
        eventTypeList.add(eventTypeBean1);

        EventTypeBean eventTypeBean2 = new EventTypeBean();
        eventTypeBean2.FText = "微信已上架";
        eventTypeBean2.FValue = "1";
        eventTypeList.add(eventTypeBean2);

        EventTypeBean eventTypeBean3 = new EventTypeBean();
        eventTypeBean3.FText = "微信已下架";
        eventTypeBean3.FValue = "0";
        eventTypeList.add(eventTypeBean3);

        EventTypeBean eventTypeBean4 = new EventTypeBean();
        eventTypeBean4.FText = "已售罄";
        eventTypeBean4.FValue = "2";
        eventTypeList.add(eventTypeBean4);
        popWindow = new ProductManagerPopWindow(ProductMangerActivity.this,titleView,eventTypeList);

        select_res = R.drawable.icon_selected;
        unSelect_res = R.drawable.left_un_select_item_bg;

        popWindow.setCallback(new BaseTopPopWindow.ToggleValueCallback() {
            @Override
            public void callback(boolean value) {
                toggle = value;
            }
        });

        //点击list回调
        popWindow.setSwitchCallBack(new ProductManagerPopWindow.SwitchCallBack() {
            @Override
            public void switchPosition(int position) {
                toggle = false;
                currentSelect = position;

                if (eventTypeList.get(currentSelect).FText.equals("全部")) {
                    titleView.setText("商品管理");
                }else {
                    titleView.setText(eventTypeList.get(currentSelect).FText);
                }
                weixinValue = eventTypeList.get(currentSelect).FValue;
                //to do 加载数据
                loadData();
            }
        });
    }

    /**使用SSO授权必须添加如下代码 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0X444 && resultCode == 0X444){
            //天加成功后回调
            loadData();
            return;
        }


        if (requestCode == 0X666 && resultCode == 0X666){
            //编辑成功后回调
            loadData();
            return;
        }


        if (null != mController){
            UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
            if(ssoHandler != null){
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    //添加
    public void addClick(View view){
        if (toggle){
            toggle = false;
            popWindow.StartOutAnimation();
        }else{
            Intent intent = new Intent(this,ProductEditOrAddActivity.class);
            intent.putExtra(Canstant.PRODUCT_MANAGER_OPEN_TYPE,2);
            startActivityForResult(intent,0X444);
        }

    }

    //编辑;
    public void popEdit(Pici pici,boolean isCanEdit){
        Intent intent = new Intent(this,ProductEditOrAddActivity.class);
        intent.putExtra(Canstant.PRODUCT_MANAGER_OPEN_TYPE,1);
        intent.putExtra("productId",pici.productId);
        intent.putExtra("isCanEdit",isCanEdit);
        startActivityForResult(intent, 0X666);
    }

    //上架
    public void popPutOn(Pici pici){
        producPutOnorDownOrDel("up", pici.inventoryId,null);
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

    class ProductHandler extends Handler {

        WeakReference<Activity> mActivityRerence = null;

        public ProductHandler(Activity activity) {
            mActivityRerence = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity msgActivity = mActivityRerence.get();

            int minBtnHeight =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    52.0f, getResources().getDisplayMetrics());

            switch (msg.what) {
                case LEFT:
                    size = mList.size();
                    TeaProductEntity entity = null;
                    Button button = null;

                    leftContainer.removeAllViews();//清除所有子view

                    for (int i = 0; i < size; i++) {

                        entity = mList.get(i);

                        button = new Button(ProductMangerActivity.this);
                        button.setId(i);
                        button.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                        button.setTextColor(redColor);
                        button.setTextSize(16);
                        button.setBackgroundResource(unSelect_res);
                        button.setText(entity.catalogName);
                        button.setMinHeight(minBtnHeight);
                        leftContainer.addView(button);
                    }

                    leftContainer.getChildAt(0).setBackgroundResource(select_res);

                    for (int i = 0; i < size; i++) {

                        leftContainer.getChildAt(i).setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                isScroll = false;

                                int id = v.getId();
                                int rightSection = 0;

                                //设置背景
                                for (int j = 0; j < size; j++) {

                                    if (id != j) {
                                        leftContainer.getChildAt(j).setBackgroundResource(unSelect_res);
                                    } else {
                                        leftContainer.getChildAt(id).setBackgroundResource(select_res);
                                    }
                                }

                                //确定selection的位置
                                for (int i = 0; i < id; i++) {
                                    rightSection += sectionedAdapter.getCountForSection(i) + 1;
                                }
                                right_listview.setSelection(rightSection);
                            }

                        });
                    }

                    productHandler.sendEmptyMessage(RIGHT);


                    break;
                case RIGHT:

                    //右边Listview显示
                    sectionedAdapter = new ProductManageListAdapter(ProductMangerActivity.this, productTypeNames, mList);
                    right_listview.setAdapter(sectionedAdapter);

                    sectionedAdapter.setPopCallBack(new ProductManageListAdapter.PopCallBack() {
                        @Override
                        public void eidtCallback(int type, Pici pici) {
                            dealCallBack(pici, type);
                        }
                    });

                    sectionedAdapter.setPopChildCallBack(new ProductManageListAdapter.PopChildCallBack() {
                        @Override
                        public void eidtchildCallback(int type, Pici pici) {
                            dealCallBack(pici, type);
                        }
                    });

                    right_listview.setOnScrollListener(new OnScrollListener() {

                        @Override
                        public void onScrollStateChanged(AbsListView arg0, int arg1) {
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            if (size == 0 || leftContainer.getChildCount() <= 0) {
                                return;
                            }

                            if (isScroll) {

                                for (int i = 0; i < size; i++) {
                                    //判断该项是哪个组
                                    if (i == sectionedAdapter.getSectionForPosition(firstVisibleItem)) {
                                        leftContainer.getChildAt(i).setBackgroundResource(select_res);

                                    } else {
                                        leftContainer.getChildAt(i).setBackgroundResource(unSelect_res);
                                    }
                                }

                            } else {
                                isScroll = true;
                            }
                        }
                    });

                    dimissDialog();
                    break;

                default:
                    break;
            }
        }
    }

    private void dealCallBack(Pici pici,int type){
        switch (type){
            case 0:
                //编辑
                isCanDel(pici, 0);
//              popEdit(pici);

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
                //popDel(pici);
                isCanDel(pici,3);

                break;
            case 4:
                //分享
                popShare(pici);
                break;
            default:
                break;
        }
    }


    //搜索
    public void searchClick(View view) {
        Intent intent = new Intent(this,ProductManagerSearchActivity.class);
        startActivity(intent);
        ProductMangerActivity.this.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
    }

    private boolean toggle;
    private boolean isFrstIn = true;
    //当前选中
    private int currentSelect;

    public void chooseListTypesClick(View v) {

        if (isFrstIn){
            isFrstIn = false;
            FrameLayout.LayoutParams poParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            popwindowContainer.addView(popWindow, 0, poParams);
            popWindow.startInAnimation();//展示
            toggle = true;
        }else{

            if (!toggle){
                popWindow.startInAnimation();
            }else{
                popWindow.StartOutAnimation();
            }
            toggle = !toggle;
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
        params.put("token", SPUtil.getDefault(ProductMangerActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductMangerActivity.this).getStoreId());


        if(type.equals("del")){

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
                    else if(type.equals("del"))
                        showToast("删除成功");
                        loadData();
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
        params.put("token", SPUtil.getDefault(ProductMangerActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductMangerActivity.this).getStoreId());
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
                    String path = data.get("path").getAsString();//图片打开链接
                    String remark = data.get("remark").getAsString();

                    if (mController == null){
                        //Server.BASE_URL + pici.productImage
                        Log.i("分享图片url",Server.BASE_URL + pici.productImage);
                        mController = new UMshareUtil().initProductManagerShare(ProductMangerActivity.this,title,remark,"http://",path);
                    }

                    //人人网分享时跳转到此website的页面
                    mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
                    //分享弹框
                    mController.openShare(ProductMangerActivity.this, false);


                } else {
                    dimissDialog();
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

    /**
     * 判断产品是否可以删除
     * 0编辑，3删除
     */
    public void isCanDel(final Pici pici,final int type){
        AjaxParams params = new AjaxParams();

        params.put("token", SPUtil.getDefault(ProductMangerActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductMangerActivity.this).getStoreId());
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
                         popEdit(pici,isDel);

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


    /**
     * 列表数据加载
     */
    private String weixinValue ="";
    public void loadData() {
        showDialog();
        AjaxParams params = new AjaxParams();

        params.put("token", SPUtil.getDefault(ProductMangerActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductMangerActivity.this).getStoreId());
        params.put("weixin",weixinValue);
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
                LogUtil.v(TAG, "茶列表数据 : " + t);

                if (isSuccess) {

                    if (mList.size() >0)
                        mList.clear();

                    if (productTypeNames.size() > 0)
                        productTypeNames.clear();

                    leftContainer.removeAllViews();

                    JsonArray jsonArr = data.get("catalogList").getAsJsonArray();

                    String commonUrl = data.get("url").getAsString();
                    SPUtil.getDefault(ProductMangerActivity.this).saveCommonUrl(commonUrl);

                    Gson gson = new Gson();
                    mList = gson.fromJson(jsonArr, new TypeToken<List<TeaProductEntity>>() {
                    }.getType());
                    if (mList == null || mList.size() == 0) {
                        showToast("暂无数据");
                        container.setVisibility(View.GONE);
                    } else {
                        if (container.getVisibility() == View.GONE)
                            container.setVisibility(View.VISIBLE);

                        TeaProductEntity teaProductEntity = null;
                        for (int i = 0; i < mList.size(); i++) {
                            teaProductEntity = mList.get(i);
                            LeftItemBean leftItemBean = new LeftItemBean();
                            if (i == 0)
                                leftItemBean.isSelect = true;
                            else
                                leftItemBean.isSelect = false;

                            leftItemBean.name = teaProductEntity.catalogName;
                            productTypeNames.add(leftItemBean);
                        }
                        productHandler.sendEmptyMessage(LEFT);
                    }

                    dimissDialog();

                } else {

                    dimissDialog();
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }
                }
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.PRODUCT_LIST, params, callback);
    }

    //结束Activity
    public void finishActivity(View view) {

        if (toggle){
            toggle = false;
            popWindow.StartOutAnimation();
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        if (toggle){
            toggle = false;
            popWindow.StartOutAnimation();
        }else{
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}