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
import com.meetrend.haopingdian.bean.ProductType;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.List;

/**
 *性别选择
 *
 */
public class SexChooseActivity extends  BaseActivity{

    @ViewInject(id = R.id.actionbar_home,click = "backClick")
    ImageView backImageView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;

    @ViewInject(id = R.id.orederstore_type_listview)
    ListView mListView;

    String[] sexTypes = new String[]{"男","女","保密"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sex);
        FinalActivity.initInjectedView(this);

        String sex = getIntent().getStringExtra("sex");
        titleView.setText("性别");

        SexAdapter sexAdapter = new SexAdapter(this,sexTypes,sex);
        mListView.setAdapter(sexAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String sex = sexTypes[i];
                Intent intent = new Intent();
                intent.putExtra("sex", sex);
                setResult(0X456, intent);
                finish();
            }
        });
    }

    public void backClick(View view){
        finish();
    }

    public class SexAdapter extends BaseAdapter {

        private Context context;
        private String[] names;
        private String selectId;

        public SexAdapter(Context context, String[] names, String selectId) {
            this.context = context;
            this.names = names;
            this.selectId = selectId;
        }

        public void setHasSelectId(String id) {
            this.selectId = selectId;
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public String getItem(int position) {
            return names[position];
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

            String name = names[position];
            holder.title.setText(name);

            if (name.equals(selectId)) {
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
}
