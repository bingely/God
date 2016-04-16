package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.PayCodeListAdapter;
import com.meetrend.haopingdian.bean.PayCodeBean;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshPayCodeInfoEvent;
import com.meetrend.haopingdian.event.RefreshPayCodeListEvent;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by a on 2016/1/28.
 *
 * 门店收款码
 */
public class StorePayCodeListActivity extends  BaseActivity implements AdapterView.OnItemClickListener{


    @ViewInject(id = R.id.actionbar_home, click = "finishActivity")
    ImageView mBarHome;

    @ViewInject(id = R.id.actionbar_title)
    TextView mBarTitle;

    //添加
    @ViewInject(id = R.id.actionbar_action,click = "addClick")
    ImageView addBtn;

    @ViewInject(id = R.id.codelist)
    PullToRefreshListView mPullListView;

    private ListView mListview = null;
    private View emptyFooterView;

    private PayCodeListAdapter mAdapter = null;
    private List<PayCodeBean> mList;

   // private int mPageCount;
   // private int mPageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storepaycodelist);
        //EventBus.getDefault().register(this);
        FinalActivity.initInjectedView(this);

        showDialog();
        mBarTitle.setText("门店收款码");
        mList = new ArrayList<PayCodeBean>();
        mListview = mPullListView.getRefreshableView();
        mAdapter = new PayCodeListAdapter(this,mList);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(this);
        mPullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        //mPullListView.setOnRefreshListener(listener2);
        //requestPayCodeList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mList != null && mList.size() >0)
            mList.clear();

            requestPayCodeList();
    }

    //    PullToRefreshBase.OnRefreshListener2<ListView> listener2 = new PullToRefreshBase.OnRefreshListener2<ListView>(){
//
//        @Override
//        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        }
//
//        @Override
//        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            ++ mPageIndex;
//            requestPayCodeList();
//        }
//    };

    //添加
    public void addClick(View view){
        Intent intent = new Intent();
        intent.setClass(this, StorePayCodeInfoEditActivity.class);
        startActivity(intent);
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        position = position - mListview.getHeaderViewsCount();
        PayCodeBean payCodeBean = mList.get(position);
        Intent intent = new Intent(StorePayCodeListActivity.this,StorePayCodeInfoActivity.class);
        intent.putExtra("paycodeid",payCodeBean.payCodeId);
        intent.putExtra("paycodename",payCodeBean.payCodeName);
        intent.putExtra("paycodeamount", payCodeBean.payCodeAmount);
        startActivity(intent);
    }

    //刷新列表
//    public void onEventMainThread(RefreshPayCodeListEvent refreshEvent){
//        //showDialog();
//        if(mList.size() >0)
//            mList.clear();
//        requestPayCodeList();
//    }

    private void requestPayCodeList() {

        AjaxParams params = new AjaxParams();

        params.put("token", SPUtil.getDefault(this).getToken());
        params.put("storeId", SPUtil.getDefault(this).getStoreId());
        //params.put("pageSize", 15+"");
        //params.put("pageIndex", mPageIndex +"");

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (mPullListView.isRefreshing()) {
                    mPullListView.onRefreshComplete();
                }

                if (isSuccess) {
                    Gson gson = new Gson();
                    //mPageIndex = data.get("pageIndex").getAsInt();
                    //mPageCount = data.get("pageCount").getAsInt();
                    JsonArray jsonArrayStr = data.get("array").getAsJsonArray();
                    List<PayCodeBean> tlist = gson.fromJson(jsonArrayStr,new TypeToken<List<PayCodeBean>>() {}.getType());
                    mList.addAll(tlist);

                    if (mList.size() == 0) {
                        mListview.removeFooterView(emptyFooterView);
                        emptyFooterView = LayoutInflater.from(StorePayCodeListActivity.this).inflate(R.layout.emptyview_layout, null);
                        mListview.addFooterView(emptyFooterView);
                    }else{
                        mListview.removeFooterView(emptyFooterView);
                        mAdapter.notifyDataSetChanged();
                    }

                    //if (mPageIndex >= mPageCount) {
                    //    mPullListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    //}else {
                    //    mPullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                   // }

                }else {

                    if (data.has("msg")) {
                        showToast(data.get("msg").getAsString());
                    }else {
                        showToast(R.string.load_fail_str);
                    }
                }
                dimissDialog();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    showToast(strMsg);
                }
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.PAY_CODE_LIST, params, callback);
    }

    public void finishActivity(View view){
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
    }
}
