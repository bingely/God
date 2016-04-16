package com.meetrend.haopingdian.activity;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.EventTypeBean;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新建界面之
 * <p/>
 * 活动主题选择界面
 *
 * @author 肖建斌
 */
public class EventTypeListActivity extends BaseActivity {

    private final static String TAG = NewBuidEventActivity.class.getName();

    @ViewInject(id = R.id.actionbar_home, click = "backClick")
    ImageView backImageView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;

    @ViewInject(id = R.id.listview)
    ListView mListView;

    //活动主题的id,编辑活动才有
    private String id;

    //当前选中item
    private int selectPositon = -1;

    //
    List<EventTypeBean> eventTypeList;
    EventTypesSpinnerAdapter eventTypesSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventtypelist);
        FinalActivity.initInjectedView(this);

        titleView.setText("活动主题");

        id = getIntent().getStringExtra("event_id");

        eventTypeListRequest();
    }

    //结束
    public void backClick(View view) {
        finish();
        EventTypeListActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 活动类型接口
     */
    public void eventTypeListRequest() {

        showDialog();

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(EventTypeListActivity.this)
                .getToken());
        params.put("parentEntityId", "ee348e50-c000-475d-a5d3-494177335707");// 该参数固定不变
        params.put("pageIndex", 1 + "");

        Callback callback = new Callback(tag, this) {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                dimissDialog();
                if (strMsg != null) {
                    Toast.makeText(EventTypeListActivity.this, strMsg, Toast.LENGTH_SHORT).show();
                }
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dimissDialog();

                if (isSuccess) {
                    JsonArray jsonArray = data.get("classify").getAsJsonArray();
                    Gson gson = new Gson();
                    eventTypeList = gson.fromJson(jsonArray, new TypeToken<List<EventTypeBean>>() {
                    }.getType());
                    if (id == null) {
                        id = "";
                    }
                    eventTypesSpinnerAdapter = new EventTypesSpinnerAdapter(EventTypeListActivity.this, eventTypeList, id);
                    mListView.setAdapter(eventTypesSpinnerAdapter);

                    mListView.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {

                            selectPositon = position;

                            //eventTypesSpinnerAdapter.setEventTypeId(eventTypeBean.FValue);
                            //eventTypesSpinnerAdapter.notifyDataSetChanged();

                            Intent intent = new Intent();
                            EventTypeBean eventTypeBean = eventTypeList.get(selectPositon);

                            intent.putExtra("fvalue", eventTypeBean.FValue);
                            intent.putExtra("ftext", eventTypeBean.FText);
                            setResult(0X111, intent);
                            finish();
                        }
                    });


                } else {
                    if (data.has("msg")) {
                        String msg = data.get("msg").getAsString();
                        Toast.makeText(EventTypeListActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventTypeListActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.get(Server.BASE_URL + Server.EVENTTYPE_LIST, params, callback);

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
                selectPositon = position;
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