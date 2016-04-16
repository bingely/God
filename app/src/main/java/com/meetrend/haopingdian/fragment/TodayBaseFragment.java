package com.meetrend.haopingdian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.TodayIncomeActivity;
import com.meetrend.haopingdian.adatper.InCommeAdapter;
import com.meetrend.haopingdian.adatper.PayNumAdapter;
import com.meetrend.haopingdian.adatper.PriceAdapter;
import com.meetrend.haopingdian.bean.InCommeBean;
import com.meetrend.haopingdian.event.RefreshIncommeEvent;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.util.LineChartViewUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.widget.LineChartView;
import com.meetrend.haopingdian.widget.MyListView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 今日收入basefragment
 */
public class TodayBaseFragment extends  BaseFragment{

    public  final static String TYPE = "type";

    @ViewInject(id = R.id.currentTime_textview)
    TextView curentTimeView;

    @ViewInject(id = R.id.mplineview)
    LineChartView mChart;

    @ViewInject(id = R.id.group_radio_btns)
    RadioGroup radioGroup;

    @ViewInject(id = R.id.mylistview)
    MyListView listview;
    @ViewInject(id = R.id.currentTime_textview)
    TextView currentTimeTv;

    @ViewInject(id = R.id.scrollview)
    ScrollView mscrollview;


    private List<InCommeBean> sevenList;
    private List<InCommeBean> thirtyList;
    private List<InCommeBean> nityList;

    private List<InCommeBean> resultList;


    private int lengthPosition = 1;
    private int typePosition = 0;

    private HashMap<String, InCommeBean> hashMap;

    @Override
    public void onResume() {
        super.onResume();
        mscrollview.scrollTo(0,0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            mscrollview.scrollTo(0,0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_todayincomme_layout, container, false);
        FinalActivity.initInjectedView(this, rootView);

        typePosition = getArguments().getInt(TYPE);
        ++typePosition;

        lengthPosition = 2;

        if (null == hashMap)
            hashMap = TodayIncomeActivity.hashMap;

        if (null == resultList){

            resultList = TodayIncomeActivity.mList;

            dealData();
            BaseAdapter baseAdapter = null;
            switch (typePosition){
                case 1:
                    baseAdapter = new InCommeAdapter(resultList,getActivity());
                    break;
                case 2:
                    baseAdapter = new PayNumAdapter(resultList,getActivity());
                    break;
                case 3:
                    baseAdapter = new PriceAdapter(resultList,getActivity());
                    break;
            }

            listview.setAdapter(baseAdapter);
            //ListViewUtil.setListViewHeightBasedOnChildren(listview);
            mscrollview.scrollTo(0,0);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.button1:

                        lengthPosition = 1;
                        dealData();

                        break;
                    case R.id.button2:

                        lengthPosition = 2;
                        dealData();

                        break;
                    case R.id.button3:

                        lengthPosition = 3;
                        dealData();
                        break;

                    default:
                        break;
                }
            }
        });

        // 触摸统计图
        mChart.setLinstener(new LineChartView.ShowLinstener() {

            @Override
            public void show(InCommeBean point) {

                if (null == point) {
                    return;
                }

                RefreshIncommeEvent refreshIncommeEvent = new RefreshIncommeEvent();

                switch (typePosition){
                    case 1:

                        refreshIncommeEvent.typePosition = 1;
                        break;

                    case 2:

                        refreshIncommeEvent.typePosition = 2;
                        break;

                    case 3:

                        refreshIncommeEvent.typePosition = 3;
                        break;
                }

                if (NumerUtil.isFloat(point.Payed + "") || NumerUtil.isInteger(point.Payed + "")){
                    refreshIncommeEvent.pay = NumerUtil.setSaveTwoDecimals(point.Payed + "");
                }else{
                    refreshIncommeEvent.pay = "0.00";
                }

                refreshIncommeEvent.paynum = (int) Math.floor(point.Num) + "";

                if (NumerUtil.isFloat(point.AVG + "") || NumerUtil.isInteger(point.AVG + "")){
                    refreshIncommeEvent.price = NumerUtil.setSaveTwoDecimals(point.AVG + "");
                }else{
                    refreshIncommeEvent.price = "0.00";
                }

                EventBus.getDefault().post(refreshIncommeEvent);
                curentTimeView.setText("当前时间：" + point.Date);
            }
        });

        return rootView;
    }


    private void dealData(){

        //设置坐标点的y值
        List<Double> yFloats = new ArrayList<Double>();
        //设置坐标点的x值
        List<String> xStrs = new ArrayList<String>();

        double maxValue = 0.0f;
        double minValue = 0.0f;

        if (lengthPosition == 1) {

            if (null == sevenList) {
                sevenList = getNumIncommeBeanList(findRecentLyDate(7), hashMap);
            }

            resultList = sevenList;

            for (InCommeBean orderBean : resultList) {

                xStrs.add(orderBean.Date);

                if (typePosition == 1)
                    yFloats.add(orderBean.Payed);

                else if (typePosition == 2)
                    yFloats.add(orderBean.Num);

                else
                    yFloats.add(orderBean.AVG);
            }

        }else if (lengthPosition == 2) {

            if (null == thirtyList) {
                thirtyList = getNumIncommeBeanList(findRecentLyDate(30), hashMap);
            }

            resultList = thirtyList;

            for (InCommeBean orderBean : resultList) {

                if (typePosition == 1) {

                    yFloats.add(orderBean.Payed);

                }else if (typePosition == 2) {

                    yFloats.add(orderBean.Num);

                }else {

                    yFloats.add(orderBean.AVG);
                }
            }

            xStrs = getNumList(resultList);

        }else {
            if (null == nityList) {
                nityList = getNumIncommeBeanList(findRecentLyDate(90), hashMap);
            }

            resultList = nityList;
            for (InCommeBean orderBean : resultList) {

                if (typePosition == 1) {

                    yFloats.add(orderBean.Payed);

                }else if (typePosition == 2) {

                    yFloats.add(orderBean.Num);

                }else {

                    yFloats.add(orderBean.AVG);
                }
            }
            xStrs = getNumList(resultList);
        }

        curentTimeView.setText("当前时间：" + resultList.get(resultList.size() - 1).Date);

        maxValue = Collections.max(yFloats);
        minValue = Collections.min(yFloats);
        mChart.setYData(yFloats);
        if(typePosition == 1){
            maxValue =  LineChartViewUtil.makeYKEDUMaxValue((long) maxValue, 10);//最大值
        }else if(typePosition == 2){
            maxValue =  LineChartViewUtil.makeYKEDUMaxValue((long) maxValue,5);//最大值
        }else{
            maxValue =  LineChartViewUtil.makeYKEDUMaxValue((long) maxValue,10);//最大值
        }
        mChart.setYMaxValue(maxValue);
        mChart.setYMinValue(minValue);
        mChart.setIncommeBeans(resultList);

        mChart.setIsDrawScrollLine(true);
        mChart.setChatLineIsInit();
        mChart.setTime(xStrs);//时间中包含刷新mChat代码
    }

    /**
     *
     * @param dymList 前 多少天
     * @param mHashMap 原始数据转成的HashMap
     * @return
     */
    private List<InCommeBean> getNumIncommeBeanList(List<InCommeBean> dymList,HashMap<String, InCommeBean> mHashMap){

        for (int j = 0; j < dymList.size(); j++) {

            InCommeBean inCommeBean = dymList.get(j);

            if (null == mHashMap.get(inCommeBean.Date)) {
                inCommeBean.AVG = 0.0f;
                inCommeBean.Num = 0.0f;
                inCommeBean.Payed = 0.0f;
            }else {
                InCommeBean inCommeBean2 = mHashMap.get(inCommeBean.Date);
                inCommeBean.AVG = inCommeBean2.AVG;
                inCommeBean.Date = inCommeBean2.Date;
                inCommeBean.Num = inCommeBean2.Num;
                inCommeBean.Payed = inCommeBean2.Payed;
            }
        }
        return dymList;
    }



    public  List<String> getNumList(List<InCommeBean> timeList){

        List<String> list = new ArrayList<String>();

        int size = timeList.size();
        for (int i = 0; i < 7; i++) {

            if (i == 0) {
                list.add(timeList.get(0).Date);
                continue;
            }

            if (size == 30) {
                list.add(timeList.get(i*5-1).Date);

            }else if (size == 90) {
                list.add(timeList.get(i*15-1).Date);
            }

        }

        return list;
    }

    /**
     *
     * @param num 最近num天
     */
    private List<InCommeBean> findRecentLyDate(int num){

        List<InCommeBean> mList = new ArrayList<InCommeBean>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = num ; i > 0; i--) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1-i);
            Date date = c.getTime();
            String dateStr = sdf.format(date);
            InCommeBean inCommeBean = new InCommeBean();
            inCommeBean.Date = dateStr;
            mList.add(inCommeBean);
        }

        return mList;
    }
}
