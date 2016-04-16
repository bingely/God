package com.meetrend.haopingdian.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.ProductDetailBean;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.bean.UnitBean;
import com.meetrend.haopingdian.bean.YunFeiBen;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import java.util.List;

/**
 * 运费模板选择
 */
public class ChooseYunFeiMoBanActivity extends BaseActivity{


    private final static String TAG = NewBuidEventActivity.class.getName();

    @ViewInject(id = R.id.actionbar_home,click = "backClick")
    ImageView backImageView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;

    @ViewInject(id = R.id.orederstore_type_listview)
    ListView mListView;

    private ListAdapter typeAdapter;
    private List<YunFeiBen> typeList;
    private String unitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseyunfeimoban);
        FinalActivity.initInjectedView(this);
        titleView.setText("运费模板");

        unitId =  getIntent().getStringExtra("yunfeiMoBanId");
        if (null == unitId)
            unitId = "";
        requestList();
    }

    private void requestList(){

        showDialog();
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(this).getToken());
        params.put("storeId", SPUtil.getDefault(this).getStoreId());

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

                if (isSuccess) {
                    Gson gson = new Gson();
                    String json = data.get("array").toString();
                    final List<YunFeiBen> mList = gson.fromJson(json, new TypeToken<List<YunFeiBen>>() {}.getType());
                    ListAdapter listAdapter = new ListAdapter(ChooseYunFeiMoBanActivity.this,mList,unitId);
                    mListView.setAdapter(listAdapter);

                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            YunFeiBen yunFeiBen = mList.get(i);
                            Intent intent = new Intent();
                            intent.putExtra("yunfeimobanId", yunFeiBen.id);
                            intent.putExtra("yunfeimobanname", yunFeiBen.name);
                            setResult(0X888, intent);
                            finish();
                        }
                    });

                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }
                }
                dimissDialog();
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.YUNFEIMOBAN, params, callback);
    }

    public class ListAdapter extends BaseAdapter {

        private Context context;
        private List<YunFeiBen> list;
        private String selectUnitId;

        public ListAdapter(Context context, List<YunFeiBen> list, String selectUnit) {
            this.context = context;
            this.list = list;
            this.selectUnitId = selectUnit;
        }

        public void setHasSelectId(String selectUnit) {
            this.selectUnitId = selectUnit;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public YunFeiBen getItem(int position) {
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
            YunFeiBen bean = list.get(position);
            holder.title.setText(bean.name);

            if (selectUnitId.equals(bean.id)) {
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
    public void backClick(View view){
        finish();
    }
}
