package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.meetrend.haopingdian.adatper.UISpinnerAdapter;
import com.meetrend.haopingdian.bean.Schema;

import java.util.List;

/**
 * 订单列表popWindow
 */
public class OrdersListTopPopWindow extends BaseTopPopWindow{

    List<Schema> mList;


    /**
     * 选择列表项回调接口
     */
    public interface SwitchCallBack{
        public abstract void switchPosition(int position);
    }

    public SwitchCallBack switchCallBack = null;

    public void setSwitchCallBack(SwitchCallBack switchCallBack){
        if (switchCallBack  != null)
            this.switchCallBack = switchCallBack;
    }

    public OrdersListTopPopWindow(Context context,TextView titleView,List<Schema> mDatas) {
        super(context);
        this.titleView = titleView;
        mList = mDatas;

        initDatas(context);
    }

    public OrdersListTopPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDatas(context);
    }

    public OrdersListTopPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDatas(context);
    }

    private void initDatas(Context context){

        mAdapter = new UISpinnerAdapter(context, mList,titleView);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pisition, long l) {
                    Schema schema = mList.get(pisition);

                    if (switchCallBack != null)
                        switchCallBack.switchPosition(pisition);

                    //TO DO
                    StartOutAnimation();
            }
        });

    }

    public void setmList(List<Schema> mList) {
        this.mList = mList;
    }
}
