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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.ProductType;
import com.meetrend.haopingdian.bean.UnitBean;
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
 * Created by a on 2016/3/14.
 * 添加库存，单位选择
 *
 */
public class UnitChooseActivity extends  BaseActivity{

    private final static String TAG = NewBuidEventActivity.class.getName();

    @ViewInject(id = R.id.actionbar_home,click = "backClick")
    ImageView backImageView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;

    @ViewInject(id = R.id.orederstore_type_listview)
    ListView mListView;

    private UnitAdapter typeAdapter;
    private List<UnitBean> typeList;
    private String unitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units_choose);
        FinalActivity.initInjectedView(this);

        titleView.setText("选择单位");

        unitId = getIntent().getStringExtra("unitId");
        if (unitId == null)
            unitId = "";
        getProductTypes(unitId);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UnitBean unitBean = typeList.get(i);
                Intent intent = new Intent();
                intent.putExtra("unitId", unitBean.id);
                intent.putExtra("unitName", unitBean.name);
                setResult(0X333, intent);
                finish();
            }
        });
    }

    private void getProductTypes(String unit){
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
                LogUtil.v(TAG, "商品分类列表 : " + t);

                dimissDialog();
                if (isSuccess) {
                    Gson gson = new Gson();
                    JsonArray jsonArr = data.get("array").getAsJsonArray();
                    typeList = gson.fromJson(jsonArr, new TypeToken<List<UnitBean>>() {}.getType());
                    if (null == typeList || typeList.size() == 0)
                        showToast("数据为空");
                    else{
                        typeAdapter = new UnitAdapter(UnitChooseActivity.this,typeList,unitId);
                        mListView.setAdapter(typeAdapter);
                    }
                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }
                }
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.UNIT_CHOOSE, params, callback);
    }


    public class UnitAdapter extends BaseAdapter {

        private Context context;
        private List<UnitBean> list;
        private String selectUnitId;

        public UnitAdapter(Context context, List<UnitBean> list, String selectUnit) {
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
        public UnitBean getItem(int position) {
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
            UnitBean bean = list.get(position);
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
