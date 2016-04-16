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

/**
 * 运费的选择方式
 */


public class CarriageChooseTypeActivity extends BaseActivity{

    @ViewInject(id = R.id.actionbar_home, click = "backClick")
    ImageView backImageView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;
    @ViewInject(id = R.id.listview)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pay_types);
        FinalActivity.initInjectedView(this);

        String selectName = getIntent().getStringExtra("yname");

        titleView.setText("运费模板");
        final List<String> nameList = new ArrayList<String>();
        nameList.add("固定运费");
        nameList.add("运费模板");
        CariageTypesAdapter cariageTypesAdapter = new CariageTypesAdapter(this,nameList,selectName);
        mListView.setAdapter(cariageTypesAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("yname",nameList.get(i));
                setResult(0X777,intent);
                finish();
            }
        });
    }

    public class CariageTypesAdapter extends BaseAdapter {

        private Context context;
        private List<String> list;
        private String selectName;

        public CariageTypesAdapter(Context context, List<String> list, String selectName) {
            this.context = context;
            this.list = list;
            this.selectName = selectName;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
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
            String name = list.get(position);
            holder.title.setText(name);

            if (name.equals(selectName)) {
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
