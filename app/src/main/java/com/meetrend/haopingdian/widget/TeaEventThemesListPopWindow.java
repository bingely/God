package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.meetrend.haopingdian.adatper.EventTypesSpinnerAdapter;
import com.meetrend.haopingdian.adatper.UISpinnerAdapter;
import com.meetrend.haopingdian.bean.EventTypeBean;
import com.meetrend.haopingdian.bean.Schema;
import java.util.List;


public class TeaEventThemesListPopWindow extends BaseTopPopWindow{

    List<EventTypeBean> mList;


    public interface SwitchCallBack{
        public abstract void switchPosition(int position);
    }

    public SwitchCallBack switchCallBack = null;

    public void setSwitchCallBack(SwitchCallBack switchCallBack){
        if (switchCallBack  != null)
            this.switchCallBack = switchCallBack;
    }

    public TeaEventThemesListPopWindow(Context context,TextView titleView,List<EventTypeBean> mDatas) {
        super(context);
        this.titleView = titleView;
        mList = mDatas;

        initDatas(context);
    }

    public TeaEventThemesListPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDatas(context);
    }

    public TeaEventThemesListPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDatas(context);
    }

    private void initDatas(Context context){

        mAdapter  = new EventTypesSpinnerAdapter(context, mList, titleView);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pisition, long l) {

                EventTypeBean schema = mList.get(pisition);
                if (switchCallBack != null)
                    switchCallBack.switchPosition(pisition);

                StartOutAnimation();
            }
        });
    }

    public void setmList(List<EventTypeBean> mList) {
        this.mList = mList;
    }
}
