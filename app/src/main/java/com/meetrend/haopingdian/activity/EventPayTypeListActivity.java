package com.meetrend.haopingdian.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.EventTypeBean;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class EventPayTypeListActivity extends BaseActivity {

    @ViewInject(id = R.id.actionbar_home, click = "backClick")
    ImageView backImageView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;

    @ViewInject(id = R.id.listview)
    ListView mListView;

    List<EventTypeBean> mEventList;

    String curentSelectValue;//当前选中value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pay_types);
        FinalActivity.initInjectedView(this);
        titleView.setText("收费方式");

        curentSelectValue = getIntent().getStringExtra("fvalue");
        if (null == curentSelectValue || "未设置".equals(curentSelectValue))
            curentSelectValue = "";

        mEventList = new ArrayList<EventTypeBean>();
        EventTypeBean payBean = new EventTypeBean();
        payBean.FText = "收费";
        payBean.FValue = "noFree";
        mEventList.add(payBean);

        EventTypeBean fressBean = new EventTypeBean();
        fressBean.FText = "免费";
        fressBean.FValue = "free";
        mEventList.add(fressBean);

        EventTypesSpinnerAdapter mAdapter = new EventTypesSpinnerAdapter(EventPayTypeListActivity.this, mEventList, curentSelectValue);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {

                Intent intent = new Intent();
                EventTypeBean eventTypeBean = mEventList.get(position);

                intent.putExtra("fvalue", eventTypeBean.FValue);
                intent.putExtra("ftext", eventTypeBean.FText);
                setResult(0X010, intent);
                finish();
            }
        });
    }

    public class EventTypesSpinnerAdapter extends BaseAdapter {

        private Context context;
        private List<EventTypeBean> list;
        private String id;

        public EventTypesSpinnerAdapter(Context context, List<EventTypeBean> list, String id) {
            this.context = context;
            this.list = list;
            this.id = id;
        }

        public void setEventTypeId(String id) {
            this.id = id;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public EventTypeBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.list_popup_simple, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.list_popup_title_name);
                holder.select = (ImageView) convertView.findViewById(R.id.list_popup_title_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            EventTypeBean schema = list.get(position);
            holder.title.setText(schema.FText);

            if (schema.FValue.equals(id)) {
                holder.title.setTextColor(Color.parseColor("#e33b3d"));
                holder.select.setVisibility(View.VISIBLE);
            } else {
                holder.title.setTextColor(Color.parseColor("#252525"));
                holder.select.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            public TextView title;
            public ImageView select;
        }
    }

    //结束
    public void backClick(View view) {
        finish();
    }

}
