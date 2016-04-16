package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.MarKetPopListAdapter;
import com.meetrend.haopingdian.adatper.MarketPriceAdapter;
import com.meetrend.haopingdian.bean.MarketPriceInfo;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.BaseTopPopWindow;
import com.meetrend.haopingdian.widget.MarketPreferenceListPopWindow;
import com.meetrend.haopingdian.widget.TeaEventThemesListPopWindow;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

/**
 * 市场参考价
 *
 * @author 肖建斌
 */
public class MarketReferenceActivity extends BaseActivity {

    private final static String TAG = MarketReferenceActivity.class.getName();

    @ViewInject(id = R.id.choose_bar_layout)
    LinearLayout choooseBarLayout;

    @ViewInject(id = R.id.actionbar_home, click = "finishActivity")
    ImageView backImgView;

    @ViewInject(id = R.id.actionbar_title)
    TextView tittleView;

    @ViewInject(id = R.id.right_actionbar_action, click = "searchClick")
    ImageView searchView;


    @ViewInject(id = R.id.titlelayout1)
    RelativeLayout yearlayout;
    @ViewInject(id = R.id.titlelayout2)
    RelativeLayout productlayout;
    @ViewInject(id = R.id.titlelayout3)
    RelativeLayout typelayout;

    @ViewInject(id = R.id.titleview1)
    TextView yeartitleView;
    @ViewInject(id = R.id.titleview2)
    TextView producttitleView;
    @ViewInject(id = R.id.titleview3)
    TextView typetitleView;

    @ViewInject(id = R.id.market_listview)
    PullToRefreshListView pullToRefreshListView;

    @ViewInject(id = R.id.container_layout)
    FrameLayout popContainerLayout;


    private View emptyView;

    private ListView listView;

    //pop
    private Drawable triangleDown;
    private Drawable triangleUp;
    private PopupWindow mPopup;

    private List<String> yearList;
    private List<String> techstList;
    private List<String> typeList;

    private boolean isFirst;

    private List<MarketPriceInfo> mList;
    private MarketPriceAdapter marketPriceAdapter = null;

    private int mPageSize;
    private int mPageIndex;
    private int mPageCount;

    private final static String DEFUALT_VALUE = "全部";
    private String typeValue =DEFUALT_VALUE;
    private String yearValue = DEFUALT_VALUE;
    private String techsVaue =DEFUALT_VALUE;

    private boolean isPopChange;//为true是pop切换，为false加载

    String key = null;

    private boolean isFirstShow = true;//pop第一次显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketreferenceprice);
        FinalActivity.initInjectedView(MarketReferenceActivity.this);
        showDialog();

        key = getIntent().getStringExtra("key");
        init();
        loadData("", "", "", key);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                MarketPriceInfo marketPriceInfo = (MarketPriceInfo) listView.getItemAtPosition(position);
                if (marketPriceInfo != null) {
                    Intent intent = new Intent(MarketReferenceActivity.this, MarketPreferenceDetailActivity.class);
                    intent.putExtra("id", marketPriceInfo.id);
                    startActivity(intent);
                }
            }
        });
    }


    OnRefreshListener2<ListView> listener2 = new OnRefreshListener2<ListView>() {
        //刷新
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        }

        //加载更多
        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            isPopChange = false;
            loadData(yearValue, typeValue, techsVaue, key);
        }
    };


    private void init() {

        tittleView.setText("市场参考价");

        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setOnRefreshListener(listener2);
        pullToRefreshListView.setMode(com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode.DISABLED);

        emptyView = LayoutInflater.from(MarketReferenceActivity.this).inflate(R.layout.emptyview_layout, null);

        triangleDown = this.getResources().getDrawable(R.drawable.spread);
        triangleDown.setBounds(0, 0, triangleDown.getMinimumWidth(),
                triangleDown.getMinimumHeight());
        triangleUp = this.getResources().getDrawable(R.drawable.packup);
        triangleUp.setBounds(0, 0, triangleUp.getMinimumWidth(),
                triangleUp.getMinimumHeight());

        yearList = new ArrayList<String>();
        techstList = new ArrayList<String>();
        typeList = new ArrayList<String>();

        mList = new ArrayList<MarketPriceInfo>();
        marketPriceAdapter = new MarketPriceAdapter(MarketReferenceActivity.this, mList);
        listView.setAdapter(marketPriceAdapter);

        yearlayout.setOnClickListener(new LinearLayoutOnclick());
        typelayout.setOnClickListener(new LinearLayoutOnclick());
        productlayout.setOnClickListener(new LinearLayoutOnclick());

        List<String> mList = new ArrayList<String>();
        mpopwindow = new MarketPreferenceListPopWindow(MarketReferenceActivity.this, mList);

        mpopwindow.setCallback(new BaseTopPopWindow.ToggleValueCallback() {
            @Override
            public void callback(boolean value) {
                toggle = value;

                changeBtnType(toggle, 0);
            }
        });

        mpopwindow.setSwitchCallBack(new MarketPreferenceListPopWindow.SwitchCallBack() {
            @Override
            public void switchPosition(int position, int choooseType, List<String> mList) {
                toggle = false;
                changeBtnType(false, 0);
                mpopwindow.StartOutAnimation();//隐藏

                isPopChange = true;
                mPageIndex = 0;
                switch (choooseType) {
                    case 0:
                        yearValue = yearList.get(position);
                        //Log.i("just choose yearvalue", yearValue);

                        if (yearValue.equals("全部"))
                            loadData(null, null, null, key);
                        else
                            loadData(yearValue, null, null, key);

                        break;
                    case 1:
                        techsVaue = techstList.get(position);
                        //Log.i("just choose techsVaue",techsVaue);

                        if (techsVaue.equals("全部"))
                        loadData(null, null, null, key);
                        else
                        loadData(null, null, techsVaue, key);

                        break;
                    case 2:
                        typeValue = typeList.get(position);
                        //Log.i("just choose typeValue", typeValue);

                        if (techsVaue.equals("全部"))
                            loadData(null, null, null, key);
                        else
                            loadData(null, typeValue, null, key);


                        break;
                    default:
                        break;
                }
            }
        });
    }

    //搜索
    public void searchClick(View view) {
        Intent intent = new Intent(MarketReferenceActivity.this, MarketPriceSearchActivity.class);
        startActivity(intent);
    }

    /**
     * toggle 为true标识窗口已经打开
     */
    private boolean toggle;
    private MarketPreferenceListPopWindow mpopwindow = null;
    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);

    private int preClikViewId;
    private class LinearLayoutOnclick implements OnClickListener {

        @Override
        public void onClick(View v) {

            if (isFirstShow) {
                popContainerLayout.addView(mpopwindow, 0, params);
                toggle = true;
                notifyAdapterDatas(v.getId());
                mpopwindow.startInAnimation();//下拉动画
                isFirstShow = false;
            } else {

                if (!toggle) {
                    notifyAdapterDatas(v.getId());
                    mpopwindow.startInAnimation();
                } else {
                    mpopwindow.StartOutAnimation();
                }
                toggle = !toggle;
            }

            changeBtnType(toggle, v.getId());
        }
    }

    //private int chooseBtnIndex;

    private void notifyAdapterDatas(int id) {
        switch (id) {
            //years
            case R.id.titlelayout1:
                mPageIndex = 0;
                mpopwindow.setSomeDatas(yearList, 0, yearValue);
                break;
            //techs
            case R.id.titlelayout2:
                mPageIndex = 1;
                mpopwindow.setSomeDatas(techstList, 1, techsVaue);
                break;
            //types
            case R.id.titlelayout3:
                mPageIndex = 2;
                mpopwindow.setSomeDatas(typeList, 2, typeValue);
                break;
            default:
                break;
        }
    }

    private void changeBtnType(boolean flag,int id){

        if (flag){
            //pop窗口将弹出
            switch (id) {
                case R.id.titlelayout1:
                    yeartitleView.setCompoundDrawables(null, null, triangleUp, null);
                    yeartitleView.setTextColor(Color.parseColor("#0dbe0b"));
                    producttitleView.setCompoundDrawables(null, null, triangleDown, null);
                    producttitleView.setTextColor(Color.parseColor("#252525"));
                    typetitleView.setCompoundDrawables(null, null, triangleDown, null);
                    typetitleView.setTextColor(Color.parseColor("#252525"));
                    //chooseBtnIndex = 0;
                    break;
                case R.id.titlelayout2:
                    producttitleView.setCompoundDrawables(null, null, triangleUp, null);
                    producttitleView.setTextColor(Color.parseColor("#0dbe0b"));
                    yeartitleView.setCompoundDrawables(null, null, triangleDown, null);
                    yeartitleView.setTextColor(Color.parseColor("#252525"));
                    typetitleView.setCompoundDrawables(null, null, triangleDown, null);
                    typetitleView.setTextColor(Color.parseColor("#252525"));
                    //chooseBtnIndex = 1;
                    break;
                case R.id.titlelayout3:
                    typetitleView.setCompoundDrawables(null, null, triangleUp, null);
                    typetitleView.setTextColor(Color.parseColor("#0dbe0b"));
                    yeartitleView.setCompoundDrawables(null, null, triangleDown, null);
                    yeartitleView.setTextColor(Color.parseColor("#252525"));
                    producttitleView.setCompoundDrawables(null, null, triangleDown, null);
                    producttitleView.setTextColor(Color.parseColor("#252525"));
                    //chooseBtnIndex = 2;
                    break;
                default:
                    break;
            }
        }else{
                yeartitleView.setCompoundDrawables(null, null, triangleDown, null);
                yeartitleView.setTextColor(Color.parseColor("#252525"));
                producttitleView.setCompoundDrawables(null, null, triangleDown, null);
                producttitleView.setTextColor(Color.parseColor("#252525"));
                typetitleView.setCompoundDrawables(null, null, triangleDown, null);
                typetitleView.setTextColor(Color.parseColor("#252525"));
        }
    }

    private boolean first = true;
    public void loadData(String year, String type, String techs, String key) {

        AjaxParams params = new AjaxParams();
        if (first){
            first = false;
        }else{
            params.put("year", year);
            params.put("technology", techs);
            params.put("type", type);
        }
        params.put("keys", key);
        params.put("token", SPUtil.getDefault(MarketReferenceActivity.this).getToken());
        params.put("pageSize", "20");
        params.put("pageIndex", ++mPageIndex + "");

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
                LogUtil.v(TAG, "data is : " + t);
                dimissDialog();


                if (isSuccess) {
                    JsonArray jsonArr = null;
                    try {
                        jsonArr = data.get("dataList").getAsJsonArray();
                        if (data.has("pageSize")) {
                            mPageSize = data.get("pageSize").getAsInt();
                        }

                        if (data.has("pageIndex")) {
                            mPageIndex = data.get("pageIndex").getAsInt();
                        }

                        if (data.has("pageCount")) {
                            mPageCount = data.get("pageCount").getAsInt();
                        }

                        if (!isFirst) {
                            String years = data.get("years").getAsString();
                            String types = data.get("types").getAsString();
                            String techs = data.get("technologys").getAsString();

                            String[] yearsArray = years.split(",");
                            String[] typesArray = types.split(",");
                            String[] techsArray = techs.split(",");

                            yearList.add(0,"全部");
                            int size = yearsArray.length;
                            for (int i = 0; i< size; i++)
                                yearList.add(yearsArray[i]);

                            size = typesArray.length;
                            typeList.add(0,"全部");
                            for (int i = 0; i< size; i++)
                                typeList.add(typesArray[i]);

                            size = techsArray.length;
                            techstList.add(0,"全部");
                            for (int i = 0; i< size; i++)
                                techstList.add(techsArray[i]);

                            isFirst = true;
                        }

                        Gson gson = new Gson();
                        List<MarketPriceInfo> dataList = gson.fromJson(jsonArr, new TypeToken<List<MarketPriceInfo>>() {}.getType());

                        if (isPopChange) {
                            if (mList.size() > 0) {
                                mList.clear();
                            }
                        }

                        if (null != dataList) {
                            mList.addAll(dataList);
                        }

                        marketPriceAdapter.notifyDataSetChanged();

                        if (mList.size() == 0) {
                            listView.removeFooterView(emptyView);
                            listView.addFooterView(emptyView);
                        } else {
                            listView.removeFooterView(emptyView);
                        }

                        //刷新结束
                        if (pullToRefreshListView.isRefreshing()) {
                            pullToRefreshListView.onRefreshComplete();
                        }

                        if (mPageIndex >= mPageCount) {
                            pullToRefreshListView.setMode(com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode.DISABLED);
                        } else {
                            pullToRefreshListView.setMode(com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_END);
                        }
                        dimissDialog();

                    } catch (Exception e) {
                        e.printStackTrace();
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
        finalHttp.get(Server.BASE_URL + Server.MARKET_PRICE_URL, params, callback);
    }

    public void finishActivity(View view) {

        if (toggle){
            toggle = false;
            mpopwindow.StartOutAnimation();
        }else{
            finish();
        }

    }

    @Override
    public void onBackPressed() {

        if (toggle){
            toggle = false;
             mpopwindow.StartOutAnimation();
        }else{
            finish();
        }
        super.onBackPressed();
    }
}