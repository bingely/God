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
import com.meetrend.haopingdian.adatper.ProductSectionedAdapter;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.PinnedHeaderListView;

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
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 商品库存
 *
 * @author 肖建斌
 *         <p/>
 *         功能：展示 + 搜索
 */
public class ProductReportoryActivity extends BaseActivity {

    private final static String TAG = ProductReportoryActivity.class.getName();

    //private  LinearLayout.LayoutParams  PARAMS;

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

    private ProductSectionedAdapter sectionedAdapter;

    private boolean isScroll = true;

    private List<LeftItemBean> productTypeNames;

    private List<TeaProductEntity> mList;

    int size;
    int redColor = Color.parseColor("#000000");;
    ProductHandler productHandler;

    private int select_res;
    private int unSelect_res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_reportory);
        FinalActivity.initInjectedView(ProductReportoryActivity.this);

        titleView.setText("商品库存");
        showDialog();
        productHandler = new ProductHandler(ProductReportoryActivity.this);
        productTypeNames = new ArrayList<LeftItemBean>();
        mList = new ArrayList<TeaProductEntity>();
        select_res = R.drawable.icon_selected;
        unSelect_res = R.drawable.left_un_select_item_bg;

        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();




    }

    private final static int LEFT = 1;
    private final static int RIGHT = 2;


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

                    for (int i = 0; i < size; i++) {

                        entity = mList.get(i);

                        button = new Button(ProductReportoryActivity.this);
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
                    sectionedAdapter = new ProductSectionedAdapter(ProductReportoryActivity.this, productTypeNames, mList);
                    right_listview.setAdapter(sectionedAdapter);
                    //ListViewUtil.setListViewHeightBasedOnChildren(right_listview);
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


    //搜索
    public void searchClick(View view) {

        Intent intent = new Intent(ProductReportoryActivity.this, ProductSearchReportoryActivity.class);
        startActivity(intent);
    }


    public void loadData() {

        AjaxParams params = new AjaxParams();

        params.put("token", SPUtil.getDefault(ProductReportoryActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ProductReportoryActivity.this).getStoreId());

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

                    JsonArray jsonArr = data.get("catalogList").getAsJsonArray();
                    Gson gson = new Gson();
                    mList = gson.fromJson(jsonArr, new TypeToken<List<TeaProductEntity>>() {
                    }.getType());
                    if (mList == null || mList.size() == 0) {
                        showToast("没有数据");
                    } else {

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
        finish();
    }

}