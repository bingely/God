package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.GloalGroupSearchBean;
import com.meetrend.haopingdian.bean.SearchMember;
import com.meetrend.haopingdian.bean.SearchMsg;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.InputTools;
import com.meetrend.haopingdian.widget.EditTextWatcher;
import com.meetrend.haopingdian.widget.ProgressWheel;
import com.umeng.socialize.utils.Log;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 先搜索联系人，在搜索消息列表
 */
public class GlobalSearchActivity extends BaseActivity {

    private final static String TAG = GlobalSearchActivity.class.getName();

    @ViewInject(id = R.id.connact_people_listview)
    ListView connactListView;
    @ViewInject(id = R.id.msg_listview)
    ListView msgListView;
    @ViewInject(id = R.id.search_edit)
    EditText searchEdit;
    @ViewInject(id = R.id.searchbtn, click = "cancleClick")
    TextView searchBtn;
    @ViewInject(id = R.id.connact_title)
    TextView connactTitleView;
    @ViewInject(id = R.id.msg_title)
    TextView msgTitleView;
    @ViewInject(id = R.id.scrollview)
    ScrollView scrollView;
    @ViewInject(id = R.id.emptyview)
    RelativeLayout emptyLayout;
    @ViewInject(id = R.id.clearbtn, click = "clearClick")
    ImageView clear;
    @ViewInject(id = R.id.back_img, click ="FinishActivityClick")
    ImageView backView;
//    @ViewInject(id = R.id.actionbar_title)
//    TextView titleView;
    //群组消息标题
    @ViewInject(id = R.id.group_msg_title)
    TextView groupMsgtitleView;
    @ViewInject(id = R.id.group_msg_listview)
    ListView groupMsgListView;

    private final int pageSize = 5;
    private List<SearchMember> memberList;
    private List<SearchMsg> msgList;
    private List<GloalGroupSearchBean> groupMsgList;

    private MemmberAdapter memmberAdapter;
    private MsgAdapter msgAdapter;
    private GroupMsgAdapter groupMsgAdapter;

    // member footer1
    private ProgressWheel memberPro;
    private TextView memberTextHintView;
    private ImageView memberImg;
    private View memberFooterView;
    // msg footer2
    private ProgressWheel msgPro;
    private TextView msgTextHintView;
    private ImageView msgImg;
    private View msgFooterView;

    //groupmsg footer
    private ProgressWheel groupMsgPro;
    private TextView groupMsgHintView;
    private ImageView groupMsgImg;
    private View groupMsgFooterView;

    //第一次加载
    private boolean connactFirstFlag = true;
    private boolean msgFirstFlag = true;
    private boolean isgroupMsgFirstLoad = true;

    private int memberIndex = 1;
    private int memberCount;

    private int msgIndex = 1;
    private int msgCount;

    private int groupMsgIndex = 1;
    private int groupMsgCount;

    private int requestNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_globalsearch);
        FinalActivity.initInjectedView(this);
        //titleView.setText("搜索");

        memberList = new ArrayList<SearchMember>();
        msgList = new ArrayList<SearchMsg>();
        groupMsgList = new ArrayList<GloalGroupSearchBean>();

        //通讯录footer
        memberFooterView = LayoutInflater.from(GlobalSearchActivity.this).inflate(R.layout.listview_footer_layout, null);
        memberPro = (ProgressWheel) memberFooterView.findViewById(R.id.footer_press);
        memberTextHintView = (TextView) memberFooterView.findViewById(R.id.load_more_tv);
        memberImg = (ImageView) memberFooterView.findViewById(R.id.boottom_arrow);
        memberFooterView.setOnClickListener(new MemberFooterClickListener());
        memberFooterView.setVisibility(View.GONE);
        connactListView.addFooterView(memberFooterView);

        //单聊消息footer
        msgFooterView = LayoutInflater.from(GlobalSearchActivity.this).inflate(R.layout.listview_footer_layout, null);
        msgFooterView.setVisibility(View.GONE);
        msgPro = (ProgressWheel) msgFooterView.findViewById(R.id.footer_press);
        msgTextHintView = (TextView) msgFooterView.findViewById(R.id.load_more_tv);
        msgImg = (ImageView) msgFooterView.findViewById(R.id.boottom_arrow);
        msgFooterView.setOnClickListener(new MsgFoooterClickListener());
        msgListView.addFooterView(msgFooterView);

        //群聊消息footer
        groupMsgFooterView = LayoutInflater.from(GlobalSearchActivity.this).inflate(R.layout.listview_footer_layout, null);
        groupMsgFooterView.setVisibility(View.GONE);
        groupMsgPro = (ProgressWheel) groupMsgFooterView.findViewById(R.id.footer_press);
        groupMsgHintView = (TextView) groupMsgFooterView.findViewById(R.id.load_more_tv);
        groupMsgImg = (ImageView) groupMsgFooterView.findViewById(R.id.boottom_arrow);
        groupMsgFooterView.setOnClickListener(new GroupMsgFoooterClickListener());
        groupMsgListView.addFooterView(groupMsgFooterView);


        searchEdit.addTextChangedListener(new EditTextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    clear.setVisibility(View.GONE);
                    searchBtn.setText("取消");
                } else {
                    clear.setVisibility(View.VISIBLE);
                    searchBtn.setText("搜索");
                }
            }
        });

        searchBtn.setOnClickListener(new SearchBtnClickListener());
        memmberAdapter = new MemmberAdapter();
        msgAdapter = new MsgAdapter();
        groupMsgAdapter = new GroupMsgAdapter();
        connactListView.setAdapter(memmberAdapter);
        msgListView.setAdapter(msgAdapter);
        groupMsgListView.setAdapter(groupMsgAdapter);

        //通讯录
        connactListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SearchMember searchMember = memberList.get(position);
                Intent intent = new Intent(GlobalSearchActivity.this, MemberInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", searchMember.customerName);
                bundle.putString("id", searchMember.userId);//根据后台的更改，现在id改为用户id
                intent.putExtras(bundle);
                GlobalSearchActivity.this.startActivity(intent);
                finish();
            }
        });

        //消息列表
        msgListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchMsg msg = msgList.get(position);
                Intent intent = new Intent(GlobalSearchActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_id", msg.userId);
                bundle.putString("name", msg.name);
                bundle.putString("avatarId", msg.avatarId);
                intent.putExtras(bundle);
                GlobalSearchActivity.this.startActivity(intent);
                finish();
                //GlobalSearchActivity.this.overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
            }
        });

        groupMsgListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GloalGroupSearchBean bean = groupMsgList.get(i);
                if (null == bean)
                    return;
                Intent intent = new Intent(GlobalSearchActivity.this, MyGroupChatActivity.class);
                intent.putExtra("frommode", "1");
                intent.putExtra("gid", bean.groupId);
                intent.putExtra("title", "");
                intent.putExtra("count", "");
                GlobalSearchActivity.this.startActivity(intent);
                finish();
            }
        });
    }

    //clear
    public void clearClick(View view) {
        searchEdit.setText("");
    }

    public class SearchBtnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            msgFirstFlag = true;
            connactFirstFlag = true;
            isgroupMsgFirstLoad = true;

            memberCount = 0;
            memberIndex = 1;
            if (memberList.size() > 0) {
                memberList.clear();
            }

            msgCount = 0;
            memberIndex = 1;
            if (msgList.size() > 0) {
                msgList.clear();
            }

            groupMsgCount = 0;
            groupMsgIndex = 1;
            if (groupMsgList.size() > 0) {
                groupMsgList.clear();
            }

            if (searchBtn.getText().toString().equals("搜索")) {
                showDialog();
                //全局搜索（联系人，消息，群发消息）
                allsearchList(searchEdit.getText().toString());
            } else {
                InputTools.hideSoftKeyBoard(searchEdit);
                finish();
            }
        }

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Code.SUCCESS:
                //消息搜索

                break;

            default:
                break;
        }
    }

    private class MemberFooterClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            connactFirstFlag = false;
            ++memberIndex;
            memberPro.setVisibility(View.VISIBLE);
            memberTextHintView.setText("正在加载...");
            memberImg.setVisibility(View.GONE);
            requestConnactList(searchEdit.getText().toString());
        }
    }

    private class MsgFoooterClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            msgFirstFlag = false;
            ++msgIndex;
            msgPro.setVisibility(View.VISIBLE);
            msgTextHintView.setText("正在加载...");
            msgImg.setVisibility(View.GONE);
            requestMsgList(searchEdit.getText().toString());
        }
    }

    private class GroupMsgFoooterClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            isgroupMsgFirstLoad = false;
            ++groupMsgIndex;
            groupMsgPro.setVisibility(View.VISIBLE);
            groupMsgHintView.setText("正在加载...");
            groupMsgImg.setVisibility(View.GONE);
            requestGroupMsgList(searchEdit.getText().toString());
        }
    }

    /**
     * 全局搜索
     */
    private void allsearchList(String key) {

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(GlobalSearchActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(GlobalSearchActivity.this).getStoreId());
        params.put("keyword", key);
        params.put("type", "default"); //传default为全局搜索
        params.put("pageIndex", 1 + "");
        params.put("pageSize", 5 + "");

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.i(TAG + "全局搜索", t);

                dimissDialog();
                Gson gson = new Gson();

                //通讯录
                JsonObject memberObj = data.get("memberData").getAsJsonObject();
                memberCount = memberObj.get("pageCount").getAsInt();
                memberIndex = memberObj.get("pageIndex").getAsInt();
                Log.i(TAG + "---memberCount----", memberCount + "");
                Log.i(TAG + "---memberIndex----", memberIndex + "");
                String memberArray = memberObj.get("argsArray").toString();
                List<SearchMember> memberResultList = gson.fromJson(memberArray, new TypeToken<List<SearchMember>>() {
                }.getType());

                //消息
                JsonObject talkObj = data.get("talkData").getAsJsonObject();
                msgCount = talkObj.get("pageCount").getAsInt();
                msgIndex = talkObj.get("pageIndex").getAsInt();
                Log.i(TAG + "---msgCount----", msgCount + "");
                Log.i(TAG + "---msgIndex----", msgIndex + "");
                String msgArray = talkObj.get("records").toString();
                List<SearchMsg> msgResultList = gson.fromJson(msgArray, new TypeToken<List<SearchMsg>>() {
                }.getType());

                //群聊消息
                JsonObject groupTalkObj = data.get("groupMessageData").getAsJsonObject();
                groupMsgCount = talkObj.get("pageCount").getAsInt();
                groupMsgIndex = talkObj.get("pageIndex").getAsInt();
                Log.i(TAG + "---groupmsgCount----", msgCount + "");
                Log.i(TAG + "---groupmsgIndex----", msgIndex + "");
                String groupMsgArray = groupTalkObj.get("records").toString();
                List<GloalGroupSearchBean> groupMsgResultList = gson.fromJson(groupMsgArray,
                        new TypeToken<List<GloalGroupSearchBean>>() {
                        }.getType());

                if (memberResultList.size() == 0 && msgResultList.size() == 0 && groupMsgResultList.size() == 0) {
                    scrollView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                    return;
                } else {
                    scrollView.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                }

                //member数据
                if (memberResultList.size() == 0){
                    connactListView.setVisibility(View.GONE);
                    memberFooterView.setVisibility(View.GONE);
                    connactTitleView.setVisibility(View.GONE);
                }else{
                    memberList.addAll(memberResultList);
                    connactListView.setVisibility(View.VISIBLE);
                    memberFooterView.setVisibility(View.VISIBLE);
                    connactTitleView.setVisibility(View.VISIBLE);
                    memberPro.setVisibility(View.GONE);
                    memberTextHintView.setVisibility(View.VISIBLE);

                    if (memberIndex < memberCount) {
                        memberFooterView.setEnabled(true);
                        memberTextHintView.setText("点击加载更多");
                        memberImg.setVisibility(View.VISIBLE);
                    } else {
                        memberFooterView.setEnabled(false);
                        memberTextHintView.setText("没有更多");
                        memberImg.setVisibility(View.GONE);
                    }

                    memmberAdapter.notifyDataSetChanged();
                    ListViewUtil.setListViewHeightBasedOnChildren(connactListView);
                }

                //消息
                if (msgResultList.size() ==0){
                    msgListView.setVisibility(View.GONE);
                    msgFooterView.setVisibility(View.GONE);
                    msgTitleView.setVisibility(View.GONE);
                }else{
                    msgList.addAll(msgResultList);
                    msgListView.setVisibility(View.VISIBLE);
                    msgFooterView.setVisibility(View.VISIBLE);
                    msgPro.setVisibility(View.GONE);
                    msgTextHintView.setVisibility(View.VISIBLE);
                    msgTitleView.setVisibility(View.VISIBLE);


                    if (msgIndex < msgCount) {
                        msgFooterView.setEnabled(true);
                        msgTextHintView.setText("点击加载更多");
                        msgImg.setVisibility(View.VISIBLE);
                    } else {
                        msgFooterView.setEnabled(false);
                        msgTextHintView.setText("没有更多");
                        msgImg.setVisibility(View.GONE);
                    }
                    msgAdapter.notifyDataSetChanged();
                    ListViewUtil.setListViewHeightBasedOnChildren(msgListView);
                }

                //群发消息

                if (groupMsgResultList.size() == 0) {
                    groupMsgListView.setVisibility(View.GONE);
                    groupMsgFooterView.setVisibility(View.GONE);
                    groupMsgtitleView.setVisibility(View.GONE);
                } else {
                    groupMsgList.addAll(groupMsgResultList);
                    groupMsgtitleView.setVisibility(View.VISIBLE);
                    groupMsgListView.setVisibility(View.VISIBLE);
                    groupMsgFooterView.setVisibility(View.VISIBLE);
                    groupMsgHintView.setVisibility(View.VISIBLE);
                    groupMsgPro.setVisibility(View.GONE);
                    if (groupMsgIndex < groupMsgCount){
                        groupMsgHintView.setText("点击加载更多");
                        groupMsgFooterView.setEnabled(true);
                        groupMsgImg.setVisibility(View.VISIBLE);
                    }else{
                        groupMsgHintView.setText("没有更多");
                        groupMsgFooterView.setEnabled(false);
                        groupMsgImg.setVisibility(View.GONE);
                    }
                    groupMsgAdapter.notifyDataSetChanged();
                    ListViewUtil.setListViewHeightBasedOnChildren(groupMsgListView);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                if (strMsg != null) {
                    showToast(msg);
                    return;
                }
                super.onFailure(t, errorNo, strMsg);
            }
        };

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.get(Server.BASE_URL + Server.GLOABL_SEARCH_URL, params, callback);
    }

    //联系人列表请求
    private void requestConnactList(String key) {

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(GlobalSearchActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(GlobalSearchActivity.this).getStoreId());
        params.put("keyword", key);
        params.put("type", "1"); //0：聊天记录1：联系人2：库存
        params.put("pageIndex", memberIndex + "");//当前页码
        params.put("pageSize", 10 + "");


        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                JsonObject jsonObject = data.get("memberData").getAsJsonObject();
                memberCount = jsonObject.get("pageCount").getAsInt();
                memberIndex = jsonObject.get("pageIndex").getAsInt();
                int count = jsonObject.get("totalSize").getAsInt();

                Log.i(TAG + "---memberCount----", memberCount + "");
                Log.i(TAG + "---memberIndex----", memberIndex + "");

                String jsonArray = jsonObject.get("argsArray").toString();
                Gson gson = new Gson();
                List<SearchMember> result = gson.fromJson(jsonArray, new TypeToken<List<SearchMember>>() {
                }.getType());
                memberList.addAll(result);
                memmberAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(connactListView);

                if (memberIndex >= memberCount) {
                    memberFooterView.setEnabled(false);
                    memberPro.setVisibility(View.GONE);
                    memberTextHintView.setText("没有更多");
                    memberImg.setVisibility(View.GONE);
                } else {
                    memberFooterView.setEnabled(true);
                    memberPro.setVisibility(View.GONE);
                    memberTextHintView.setText("加载更多");
                    memberImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                ++requestNum;
                if (requestNum == 1) {
                    --memberIndex;
                    requestNum = 0;
                    memberFooterView.setEnabled(true);
                    memberFooterView.setVisibility(View.VISIBLE);
                    memberTextHintView.setVisibility(View.VISIBLE);
                    memberPro.setVisibility(View.GONE);
                    memberTextHintView.setText("加载异常请重试");
                    memberImg.setVisibility(View.GONE);
                    return;
                }
                super.onFailure(t, errorNo, strMsg);
            }
        };
        FinalHttp finalHttp = new FinalHttp();
        finalHttp.get(Server.BASE_URL + Server.GLOABL_SEARCH_URL, params, callback);
    }

    //消息列表请求
    private void requestMsgList(String key) {

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(GlobalSearchActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(GlobalSearchActivity.this).getStoreId());
        params.put("keyword", key);
        params.put("type", "0"); //0：聊天记录1：联系人3：群组消息
        params.put("pageIndex", msgIndex + "");
        params.put("pageSize", 10 + "");

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                JsonObject jsonObject = data.get("talkData").getAsJsonObject();
                msgCount = jsonObject.get("pageCount").getAsInt();
                msgIndex = jsonObject.get("pageIndex").getAsInt();
                String jsonArray = jsonObject.get("records").toString();
                Gson gson = new Gson();

                Log.i(TAG + "---msgCount----", msgCount + "");
                Log.i(TAG + "---msgIndex----", msgIndex + "");

                List<SearchMsg> result = gson.fromJson(jsonArray, new TypeToken<List<SearchMsg>>() {
                }.getType());
                msgList.addAll(result);

                msgAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(msgListView);

                msgPro.setVisibility(View.GONE);
                if (msgIndex >= msgCount) {
                    msgFooterView.setEnabled(false);
                    msgImg.setVisibility(View.GONE);
                    msgTextHintView.setText("没有更多");
                } else {
                    msgFooterView.setEnabled(true);
                    msgImg.setVisibility(View.VISIBLE);
                    msgTextHintView.setText("加载更多");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

        };

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.get(Server.BASE_URL + Server.GLOABL_SEARCH_URL, params, callback);
    }

    //群组消息列表请求
    private void requestGroupMsgList(String key) {

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(GlobalSearchActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(GlobalSearchActivity.this).getStoreId());
        params.put("keyword", key);
        params.put("type", "3");
        params.put("pageIndex", groupMsgIndex + "");//当前页码
        params.put("pageSize", 10 + "");

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                JsonObject jsonObject = data.get("groupMessageData").getAsJsonObject();
                groupMsgCount = jsonObject.get("pageCount").getAsInt();
                groupMsgIndex = jsonObject.get("pageIndex").getAsInt();
                String jsonArray = jsonObject.get("records").toString();
                Gson gson = new Gson();

                Log.i(TAG + "---groupmsgCount----", groupMsgCount + "");
                Log.i(TAG + "---groupmsgIndex----", groupMsgIndex + "");

                List<GloalGroupSearchBean> result = gson.fromJson(jsonArray, new TypeToken<List<GloalGroupSearchBean>>() {
                }.getType());
                groupMsgList.addAll(result);
                groupMsgAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(groupMsgListView);

                groupMsgPro.setVisibility(View.GONE);
                if (groupMsgIndex >= groupMsgCount) {
                    groupMsgHintView.setText("没有更多");
                    groupMsgImg.setVisibility(View.GONE);
                    groupMsgFooterView.setEnabled(false);
                } else {
                    groupMsgHintView.setText("加载更多");
                    groupMsgImg.setVisibility(View.VISIBLE);
                    groupMsgFooterView.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

        };

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.get(Server.BASE_URL + Server.GLOABL_SEARCH_URL, params, callback);
    }

    public void FinishActivityClick(View view) {
        finish();
    }

    //会员列表adapter
    private class MemmberAdapter extends BaseAdapter {

         LayoutInflater inflater = null;
         Drawable drawable;
         int drawpadding;

        public MemmberAdapter() {
            inflater = LayoutInflater.from(GlobalSearchActivity.this);
            Resources resources = GlobalSearchActivity.this.getResources();
            drawable = resources.getDrawable(R.drawable.forbid_send);
            DisplayMetrics metrics = resources.getDisplayMetrics();
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            drawpadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, metrics);
        }

        @Override
        public int getCount() {
            return memberList.size();
        }

        @Override
        public Object getItem(int position) {
            return memberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.search_connact_people_item_layout, null);
                holder.photo = (SimpleDraweeView) convertView.findViewById(R.id.memberphoto);
                holder.nameView = (TextView) convertView.findViewById(R.id.connact_name_view);
                holder.member_status = (TextView) convertView.findViewById(R.id.tv_member_status);//状态
                holder.unDitributeImg = (ImageView) convertView.findViewById(R.id.undistributeimg);//未分配
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            SearchMember member = memberList.get(position);
            holder.photo.setImageURI(Uri.parse(Server.BASE_URL + member.pictureId));

            holder.nameView.setText(member.customerName);
            if (member.canTalk){
                holder.nameView.setCompoundDrawables(null,null,null, null);
            } else {
                holder.nameView.setCompoundDrawables(null,null,drawable, null);
                holder.nameView.setCompoundDrawablePadding(drawpadding);
            }

            if (member.managerId.equals("")) {
                holder.unDitributeImg.setVisibility(View.VISIBLE);
            } else {
                holder.unDitributeImg.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(member.status)) {
                int status = Integer.parseInt(member.status);
                switch (status) {
                    case 0:
                        holder.member_status.setTextColor(Color.GRAY);
                        holder.member_status.setBackgroundResource(0);
                        holder.member_status.setText("未邀请");
                        break;
                    case 1:
                        holder.member_status.setTextColor(Color.GREEN);
                        holder.member_status.setBackgroundResource(0);
                        holder.member_status.setText("已邀请");
                        break;
                    case 2:
                        holder.member_status.setTextColor(Color.GRAY);
                        holder.member_status.setBackgroundResource(0);
                        holder.member_status.setText("已绑定");
                        break;
                }
            } else {
                holder.member_status.setTextColor(Color.GRAY);
                holder.member_status.setBackgroundResource(0);
                holder.member_status.setText("未邀请");
            }

            return convertView;
        }

    }

    //消息列表adapter
    private class MsgAdapter extends BaseAdapter {

        LayoutInflater inflater = null;

        public MsgAdapter() {
            inflater = LayoutInflater.from(GlobalSearchActivity.this);
        }

        @Override
        public int getCount() {
            return msgList.size();
        }

        @Override
        public Object getItem(int position) {
            return msgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.search_msg_item_layout, null);
                holder.photo = (SimpleDraweeView) convertView.findViewById(R.id.msg_photo);
                holder.nameView = (TextView) convertView.findViewById(R.id.msg_name_view);
                holder.contentView = (TextView) convertView.findViewById(R.id.endmsg_content_view);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            SearchMsg msg = msgList.get(position);
            holder.photo.setImageURI(Uri.parse(Server.BASE_URL + msg.avatarId));
            holder.nameView.setText(msg.name);
            holder.contentView.setText(msg.content);

            return convertView;
        }
    }

    //群组消息列表adapter
    private class GroupMsgAdapter extends BaseAdapter {

        LayoutInflater inflater = null;

        public GroupMsgAdapter() {
            inflater = LayoutInflater.from(GlobalSearchActivity.this);
        }

        @Override
        public int getCount() {
            return groupMsgList.size();
        }

        @Override
        public GloalGroupSearchBean getItem(int position) {
            return groupMsgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.search_msg_item_layout, null);
                holder.photo = (SimpleDraweeView) convertView.findViewById(R.id.msg_photo);
                holder.nameView = (TextView) convertView.findViewById(R.id.msg_name_view);
                holder.contentView = (TextView) convertView.findViewById(R.id.endmsg_content_view);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            GloalGroupSearchBean groupMsg = groupMsgList.get(position);
            holder.photo.setImageURI(Uri.parse(Server.BASE_URL + groupMsg.logo));
            holder.nameView.setText(groupMsg.groupName + "-" + groupMsg.userName);
            holder.contentView.setText(groupMsg.content);
            return convertView;
        }
    }

    final class Holder {

        SimpleDraweeView photo;
        TextView nameView;
        TextView contentView;
        TextView member_status;
        ImageView unDitributeImg;
    }


    @Override
    public void finish() {
        super.finish();
        GlobalSearchActivity.this.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
    }
}