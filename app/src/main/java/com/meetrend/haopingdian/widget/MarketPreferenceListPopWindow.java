package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.meetrend.haopingdian.adatper.MarKetPopListAdapter;
import com.meetrend.haopingdian.util.DensytyUtil;
import com.umeng.socialize.utils.Log;

import java.util.List;

public class MarketPreferenceListPopWindow extends BaseTopPopWindow{


    private List<String> mList;
    private int chooseType = 0; //0标识年份、生产工艺、类型

    public SwitchCallBack switchCallBack = null;

    public MarKetPopListAdapter marKetPopListAdapter;

    /**
     * 选择列表项回调接口
     */
    public interface SwitchCallBack{
        public abstract void switchPosition(int position,int choooseType,List<String> mList);
    }

    public void setSwitchCallBack(SwitchCallBack switchCallBack){
        if (switchCallBack  != null)
            this.switchCallBack = switchCallBack;
    }

    public MarketPreferenceListPopWindow(Context context,List<String> list) {
        super(context);
        mList = list;
        init(context);
    }

    public MarketPreferenceListPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarketPreferenceListPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){

         marKetPopListAdapter = new MarKetPopListAdapter(context,mList,"");
         mListView.setAdapter(marKetPopListAdapter);

         mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                 if (null != switchCallBack){

                     switch (chooseType) {
                         case 0:
                             chooseType = 0;
                             break;
                         case 1:
                             chooseType = 1;
                             break;
                         case 2:
                             chooseType = 2;
                             break;
                         default:
                             break;
                     }

                     switchCallBack.switchPosition(position,chooseType,mList);
                 }
             }
         });
    }

    /**
     *
     * @param mlist 数据
     * @param chooseType 选择类型
     * @param selectValue 选中的值
     */
    public void setSomeDatas(List<String> mlist,int chooseType,String selectValue){
        this.chooseType = chooseType;
        marKetPopListAdapter.setList(mlist);
        marKetPopListAdapter.setHasSettingValue(selectValue);
        marKetPopListAdapter.notifyDataSetChanged();

        //WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        //int height = wm.getDefaultDisplay().getHeight();
        //params.height = height*3/5;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mListView.getLayoutParams();
        if (mlist.size() > 6){
            View listItem = mListView.getAdapter().getView(0, null, mListView);
            listItem.measure(0, 0);
            params.height = listItem.getMeasuredHeight()*7;
            mListView.setLayoutParams(params);
        }else{
            params.height = LayoutParams.WRAP_CONTENT;
            mListView.setLayoutParams(params);
        }
    }
}
