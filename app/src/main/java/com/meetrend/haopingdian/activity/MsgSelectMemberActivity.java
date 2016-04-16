package com.meetrend.haopingdian.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.GroupAdapter;
import com.meetrend.haopingdian.adatper.GroupAdapter.GroupCheckCallBack;
import com.meetrend.haopingdian.adatper.NewMassSelectAdapter;
import com.meetrend.haopingdian.adatper.NewMassSelectAdapter.ChildCheckCallBack;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.bean.MemberGroup;
import com.meetrend.haopingdian.bean.MemberGroupInfo;
import com.meetrend.haopingdian.bean.SelectMemberBean;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.memberlistdb.MemberListDbOperator;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NetUtil;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;
import com.meetrend.swipemenulistview.SwipeMenuListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * 群发列表选择 和 活动发送成员选择
 *
 */
public class MsgSelectMemberActivity extends BaseActivity {

    private final static int LOAD_DATA = 0x925;//分页加载数据

    @ViewInject(id = R.id.search_edit)
    EditText searchEditText;

    @ViewInject(id = R.id.all_search_layout)
    FrameLayout all_search_layout;

    @ViewInject(id = R.id.lv_member)
    ListView mListView;

    @ViewInject(id = R.id.actionbar_home, click = "homeClick")
    ImageView mBarHome;

    @ViewInject(id = R.id.alphat_layout)
    SideBar alphatSideBar;
    @ViewInject(id = R.id.tv_alphabet_ui_tableview)
    TextView mToast;

    @ViewInject(id = R.id.iv_contact_add_contact, click = "crossClick")
    TextView mCross;

    @ViewInject(id = R.id.tv_memberlist_emtpy)
    TextView mEmpty;
    @ViewInject(id = R.id.bottom_member_added)
    RelativeLayout mMemberAdd;
    // 确定按钮
    @ViewInject(id = R.id.mass_ok, click = "onClickOk")
    TextView mOk;
    // 全选CheckBox
    @ViewInject(id = R.id.selectAll)
    CheckBox mSelectAll;

    @ViewInject(id = R.id.search_contact_phone)
    private SearchView mSearchView;
    @ViewInject(id = R.id.layout_contact_phone, click = "searchClick")
    private FrameLayout mLayout;
    @ViewInject(id = R.id.memberlist_titlelayout)
    private RelativeLayout titleLayout;

    @ViewInject(id = R.id.no_searchlist_view)
    private TextView emptyHintView;


    public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ALPHABET_LENGTH = ALPHABET.length();
    private HashMap<String, Integer> selector;// 存放含有索引字母的位置

    private List<MemberGroup> groupList;
    private ArrayList<Member> memberList;
    // 群组
    private View headView;
    private ListView headListView;
    private GroupAdapter groupAdapter;
    private NewMassSelectAdapter mAdapter;

    private int selectNum;// 选中的人数

    private boolean isBind;// 从发送活动页面进入


    private String eventId;// 活动id
    private int flag = 1;// 1发送活动，2群发

    private final static int FAILED = 0X367;
    private final static int SUCCESS = 0X368;

    private int mCurPageIndex = 1;
    private int mPageCount;
    //上次请求的时间戳
    private String lastRequestTime;
    //客户端本地的数据
    private long mClienSize;

    private long realMembersSize;//实际的members数量

    /**
     * 值为true 全部数据
     * 值为false 增量数据 （修改 和 添加）
     */
    private boolean isAllData;

    private List<Member> allDataList;
    private int realMemberNum;

    //搜索的临时数据
    private List<Member> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masssendselectmember);
        FinalActivity.initInjectedView(this);

        allDataList = new ArrayList<Member>();
        mCross.setVisibility(View.GONE);

        groupList = new ArrayList<MemberGroup>();
        memberList = new ArrayList<Member>();
        selector = new HashMap<String, Integer>();

        try {

            String from = getIntent().getStringExtra("from");
            eventId = getIntent().getStringExtra("eventid");
            if (from != null) {
                isBind = true;
                all_search_layout.setVisibility(View.VISIBLE);
                mOk.setText("发 送");
                flag = 2; // 改修改之前是显示已绑定的成员，先改为显示所有的成员
            } else {
                flag = 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        headView = LayoutInflater.from(this).inflate(R.layout.msg_select_top_layout, null);
        //头部listView
        headListView = (ListView) headView.findViewById(R.id.group_listview);
        mListView.addHeaderView(headView);
        mMemberAdd.setVisibility(View.VISIBLE);

        //获取群组数据
        getGroupData();

        alphatSideBar.setTextView(mToast);
        alphatSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String key) {

                if (selector.containsKey(key)) {
                    int pos = selector.get(key);
                    if (mListView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有
                        mListView.setSelectionFromTop(
                                pos + mListView.getHeaderViewsCount(), 0);
                    } else {
                        mListView.setSelectionFromTop(pos, 0);// 滑动到第一项
                    }
                } else {
                    //滑动到搜索栏
                    mListView.smoothScrollToPosition(-1);
                }
            }
        });

        mSelectAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //全选
                if (mSelectAll.isChecked()) {

                    //处于搜索模式
                    if (!searchEditText.getText().toString().equals("")) {

                        for (int i = 0; i < list.size(); i++) {

                            Member listMember = list.get(i);
                            listMember.checkstatus = true;

                            for (int j = 0; j < memberList.size(); j++) {

                                Member member = memberList.get(j);

                                if (null == member.userId)
                                    continue;
                                if (!member.checkstatus && member.userId.equals(listMember.userId)) {
                                    member.checkstatus = true;
                                }
                            }
                        }

                        mAdapter.setList(list);
                        mAdapter.notifyDataSetChanged();

                        mOk.setText("确定(" + list.size() + ")");

                    } else {

                        int selectCount = 0;

                        // 勾选群组
                        for (MemberGroup gmember : groupList) {
                            gmember.checkstatus = true;
                        }

                        groupAdapter.setList(groupList);
                        groupAdapter.notifyDataSetChanged();

                        for (Member member : memberList) {
                            if (null == member.userId)
                                continue;
                            if (!member.checkstatus) {
                                member.checkstatus = true;
                                ++selectCount;
                            }
                        }
                        mOk.setText("确定(" + selectCount + ")");
                    }

                    mAdapter.setList(memberList);
                    mAdapter.notifyDataSetChanged();

                } else {

                    if (!searchEditText.getText().toString().equals("")) {

                        for (int i = 0; i < list.size(); i++) {

                            Member listMember = list.get(i);
                            listMember.checkstatus = false;

                            for (int j = 0; j < memberList.size(); j++) {

                                Member member = memberList.get(j);

                                if (null == member.userId)
                                    continue;
                                if (!member.checkstatus && member.userId.equals(listMember.userId)) {
                                    member.checkstatus = false;
                                }
                            }
                        }

                        mAdapter.setList(list);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        // 取消勾选群组
                        for (MemberGroup gmember : groupList) {
                            gmember.checkstatus = false;
                        }
                        groupAdapter.setList(groupList);
                        groupAdapter.notifyDataSetChanged();

                        for (Member member : memberList) {
                            if (null == member.userId)
                                continue;
                            if (member.checkstatus) {
                                member.checkstatus = false;
                            }
                        }
                        mAdapter.setList(memberList);
                        mAdapter.notifyDataSetChanged();
                    }

                    mOk.setText("确定(" + 0 + ")");

                }

            }
        });

        // 全选和反选
//        mSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//
//                if (isChecked) {
//
//                    //处于搜索模式
//                    if (!searchEditText.getText().toString().equals("")) {
//
//                        for (int i = 0; i < list.size(); i++) {
//
//                            Member listMember = list.get(i);
//                            listMember.checkstatus = true;
//
//                            for (int j = 0; j < memberList.size(); j++) {
//
//                                Member member = memberList.get(j);
//
//                                if (null == member.userId)
//                                    continue;
//                                if (!member.checkstatus && member.userId.equals(listMember.userId)) {
//                                    member.checkstatus = true;
//                                }
//                            }
//                        }
//
//                        mAdapter.setList(list);
//                        mAdapter.notifyDataSetChanged();
//
//                        mOk.setText("确定(" + list.size() + ")");
//
//                    } else {
//
//                        int selectCount = 0;
//
//                        // 勾选群组
//                        for (MemberGroup gmember : groupList) {
//                            gmember.checkstatus = true;
//                        }
//
//                        groupAdapter.setList(groupList);
//                        groupAdapter.notifyDataSetChanged();
//
//                        for (Member member : memberList) {
//                            if (null == member.userId)
//                                continue;
//                            if (!member.checkstatus) {
//                                member.checkstatus = true;
//                                ++selectCount;
//                            }
//                        }
//                        mOk.setText("确定(" + selectCount + ")");
//                    }
//
//                    mAdapter.setList(memberList);
//                    mAdapter.notifyDataSetChanged();
//
//                } else {
//
//                    if (!searchEditText.getText().toString().equals("")) {
//
//                        for (int i = 0; i < list.size(); i++) {
//
//                            Member listMember = list.get(i);
//                            listMember.checkstatus = false;
//
//                            for (int j = 0; j < memberList.size(); j++) {
//
//                                Member member = memberList.get(j);
//
//                                if (null == member.userId)
//                                    continue;
//                                if (!member.checkstatus && member.userId.equals(listMember.userId)) {
//                                    member.checkstatus = false;
//                                }
//                            }
//                        }
//
//                        mAdapter.setList(list);
//                        mAdapter.notifyDataSetChanged();
//
//                    } else {
//                        // 取消勾选群组
//                        for (MemberGroup gmember : groupList) {
//                            gmember.checkstatus = false;
//                        }
//                        groupAdapter.setList(groupList);
//                        groupAdapter.notifyDataSetChanged();
//
//                        for (Member member : memberList) {
//                            if (null == member.userId)
//                                continue;
//                            if (member.checkstatus) {
//                                member.checkstatus = false;
//                            }
//                        }
//
//                        mAdapter.setList(memberList);
//                        mAdapter.notifyDataSetChanged();
//                    }
//
//                    mOk.setText("确定(" + 0 + ")");
//
//                }
//
//
//            }
//        });

        //成员搜索
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable changeText) {

                if (!"".equals(changeText.toString())) {

                    if (mSelectAll.getVisibility() == View.VISIBLE)
                        mSelectAll.setVisibility(View.GONE);

                    //搜索的时候隐藏群组列表
                    headListView.setVisibility(View.GONE);

                    // 会员列表
                    //searchList = MemberListDbOperator.getInstance().searchMembers(MsgSelectMemberActivity.this, changeText.toString());
                    list = new ArrayList<Member>();
                    for (Member member : memberList) {
                        if (null != member.userId && member.customerName.contains(changeText.toString())) {
                            list.add(member);
                        }
                    }

                    //mAdapter.setList(Member.convert(searchList));
                    mAdapter.setList(list = Member.convert(list));

                    mAdapter.notifyDataSetChanged();
                    if (emptyHintView.getVisibility() == View.GONE && list.size() == 0)
                        emptyHintView.setVisibility(View.VISIBLE);

                } else {

                    if (mSelectAll.getVisibility() == View.GONE)
                        mSelectAll.setVisibility(View.VISIBLE);

                    headListView.setVisibility(View.VISIBLE);
                    mAdapter.setList(memberList);
                    mAdapter.notifyDataSetChanged();
                    if (emptyHintView.getVisibility() == View.VISIBLE)
                        emptyHintView.setVisibility(View.GONE);
                }
            }

        });

        mAdapter = new NewMassSelectAdapter(MsgSelectMemberActivity.this, memberList, flag);
        mListView.setAdapter(mAdapter);

        // 成员是否选中监听
        mAdapter.setChildCheckCallBack(new ChildCheckCallBack() {

            @Override
            public void isCheck(boolean ischeck, Member tmember) {

                for (Member member : memberList) {
                    if (member.userId != null) {
                        if (member.userId.equals(tmember.userId)) {

                            member.checkstatus = ischeck;
                            int account = 0;
                            for (Member m : memberList) {
                                if (m.checkstatus) {
                                    ++account;
                                }
                            }
                            selectNum = account;
                            mOk.setText("确定(" + selectNum + ")");
                        }
                    }
                }

                if (selectNum == realMembersSize){
                    mSelectAll.setChecked(true);
                }else{
                    mSelectAll.setChecked(false);
                }
            }

        });

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:

                    if (NetUtil.hasConnected(MsgSelectMemberActivity.this)) {
                        lastRequestTime = SPUtil.getDefault(MsgSelectMemberActivity.this).getLastRequestTime();
                        mClienSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(MsgSelectMemberActivity.this);
                        requestCurrentMemberList();
                    } else {
                        //没网的情况，加载数据库数据
                        List<Member> list = MemberListDbOperator.getInstance().getMemberList(MsgSelectMemberActivity.this);
                        mAdapter.setList(list);
                        mAdapter.notifyDataSetChanged();
                        dimissDialog();
                    }

                    break;

                case FAILED:
                    dimissDialog();
                    showToast("数据加载失败");
                    break;
                case LOAD_DATA:
                    ++mCurPageIndex;
                    requestCurrentMemberList();
                    break;
            }

        }
    };

    // 发送活动详情至会员
    private void sendEventDetail(String jsonArray) {

        Callback callback = new Callback(tag, this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {
                    dimissDialog();
                    showToast("发送活动成功");
                } else {
                    String msg = data.get("msg").getAsString();// 错误信息
                    dimissDialog();
                    showToast(msg);
                }
                finish();
            }
        };

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil
                .getDefault(MsgSelectMemberActivity.this).getToken());
        params.put("bonusId", eventId);
        params.put("userList", jsonArray);// userid
        CommonFinalHttp http = new CommonFinalHttp();
        http.get(Server.BASE_URL + Server.SEND_EVENT, params, callback);
    }

    /**
     * 获得联系人列表
     *
     * @author joy 6/16
     */
    private void requestCurrentMemberList() {

        Callback callback = new Callback(tag, MsgSelectMemberActivity.this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {

                    String jsonArrayStr = data.get("argsArray").toString();
                    mPageCount = data.get("pageCount").getAsInt();
                    lastRequestTime = data.get("lastRequestTime").getAsString();
                    isAllData = data.get("isAllData").getAsBoolean();
                    SPUtil.getDefault(MsgSelectMemberActivity.this).saveLastRequestTime(lastRequestTime);

                    Gson gson = new Gson();
                    List<Member> list = gson.fromJson(jsonArrayStr, new TypeToken<List<Member>>() {
                    }.getType());

                    if (alphatSideBar.getVisibility() == View.GONE) {
                        alphatSideBar.setVisibility(View.VISIBLE);
                    }

                    if (selector.size() > 0) {
                        selector.clear();
                    }

                    if (isAllData == true) {
                        allDataList.addAll(list);
                        memberList.addAll(list);//对象Member的pinyinName字段为空

                        //如果当前的分页数小于总页数，则开始加载数据
                        if (mCurPageIndex < mPageCount) {
                            mHandler.sendEmptyMessage(LOAD_DATA);
                        }

                        //如果是全量数据，则清除掉数据库中的数据
                        if (mCurPageIndex >= mPageCount) {
                            realMemberNum = allDataList.size();
                            MemberListDbOperator.getInstance().clearMemberDatas(MsgSelectMemberActivity.this);//清空数据库数据
                            MemberListDbOperator.getInstance().saveMembers(MsgSelectMemberActivity.this, memberList);//将有所最新的数据保存值本地数据库
                            mClienSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(MsgSelectMemberActivity.this);//获取数据库记录的总条数
                        }

                        realMembersSize = allDataList.size();

                        //排序
                        memberList = Member.convert(allDataList);
                        //dymList = memberList;

                        int membersCount = memberList.size();
                        for (int i = 0; i < membersCount; ++i) {

                            for (int j = 0; j < ALPHABET_LENGTH; j++) {
                                String str = ALPHABET.charAt(j) + "";
                                if (memberList.get(i).pinyinName.equals(str)) {
                                    selector.put(str, i);
                                    break;
                                }
                            }
                        }
                        mAdapter.setList(memberList);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        mClienSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(MsgSelectMemberActivity.this);//获取数据库记录的总条数
                        realMembersSize = mClienSize;
                        //遍历数据库,若是数据库中查不到该记录，则添加，若能找到该记录则删除，并更新
                        for (Member member : list) {

                            if (MemberListDbOperator.getInstance().findOneMember(MsgSelectMemberActivity.this, member.userId)) {
                                //删除该条信息，
                                MemberListDbOperator.getInstance().clearOndMember(MsgSelectMemberActivity.this, member);
                                //插入新的数据
                                MemberListDbOperator.getInstance().saveOneMember(MsgSelectMemberActivity.this, member);
                            } else {
                                //插入新的数据
                                MemberListDbOperator.getInstance().saveOneMember(MsgSelectMemberActivity.this, member);
                            }

                        }

                        memberList = (ArrayList<Member>) MemberListDbOperator.getInstance().getMemberList(MsgSelectMemberActivity.this);
                        allDataList.addAll(memberList);

                        //排序
                        memberList = Member.convert(memberList);
                        //虚拟数据
                        //dymList = memberList;

                        int membersCount = memberList.size();
                        for (int i = 0; i < membersCount; ++i) {

                            for (int j = 0; j < ALPHABET_LENGTH; j++) {
                                String str = ALPHABET.charAt(j) + "";
                                if (memberList.get(i).pinyinName.equals(str)) {
                                    selector.put(str, i);
                                    break;
                                }
                            }
                        }
                        mAdapter.setList(memberList);
                        mAdapter.notifyDataSetChanged();
                    }

                } else {

                    if (data.has("msg")) {
                        showToast(data.get("msg").getAsString());
                    } else {
                        showToast("加载数据失败");
                    }
                }
                dimissDialog();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                if (null != strMsg) {
                    showToast(strMsg);
                }

                dimissDialog();
            }
        };

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(MsgSelectMemberActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(MsgSelectMemberActivity.this).getStoreId());
        params.put("keyword", "");
        params.put("pageSize", "500");
        params.put("pageIndex", mCurPageIndex + "");
        params.put("lastRequestTime", SPUtil.getDefault(MsgSelectMemberActivity.this).getLastRequestTime());//上次请求的时间戳
        params.put("clientSize", mClienSize + "");

        CommonFinalHttp http = new CommonFinalHttp();
        http.get(Server.BASE_URL + Server.APP_MEMBER_USER_LIST, params, callback);
    }

    /**
     * 获得群组列表
     *
     * @author joy 6/16
     */
    private void getGroupData() {

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil
                .getDefault(MsgSelectMemberActivity.this).getToken());
        params.put("storeId",
                SPUtil.getDefault(MsgSelectMemberActivity.this)
                        .getStoreId());

        Callback callback = new Callback(tag, this) {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                dimissDialog();

                if (null != strMsg) {
                    showToast(strMsg);
                }
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (!isSuccess) {
                    mHandler.sendEmptyMessage(FAILED);
                } else {
                    String jsonArrayStr = data.get("tagList").toString();
                    Gson gson = new Gson();
                    groupList = gson.fromJson(jsonArrayStr, new TypeToken<List<MemberGroup>>() {
                    }.getType());
                    int size = groupList.size();
                    MemberGroup m = null;
                    for (int i = 0; i<size;i++) {
                        m = groupList.get(i);
                        if (m.isSystemTag){
                            groupList.remove(i);
                        }
                    }

                    groupAdapter = new GroupAdapter(
                            MsgSelectMemberActivity.this, groupList);
                    headListView.setAdapter(groupAdapter);
                    ListViewUtil.setListViewHeightBasedOnChildren(headListView);

                    // 监听群组选中
                    groupAdapter
                            .setGroupCheckCallBack(new GroupCheckCallBack() {
                                @Override
                                public void isCheck(MemberGroup memberGroup,
                                                    boolean ischeck) {

                                    for (MemberGroup gmember : groupList) {

                                        if (gmember.tagRelationId == null || gmember.tagRelationId.equals(memberGroup.tagRelationId)) {

                                            gmember.checkstatus = ischeck;

                                            Member member = null;
                                            for (MemberGroupInfo memberGroupInfo : gmember.getUserArray()) {

                                                for (int i = 0; i < memberList.size(); i++) {

                                                    member = memberList.get(i);
                                                    if (memberGroupInfo.id.equals(member.userId)) {

                                                        memberList.get(i).checkstatus = ischeck;
                                                        mAdapter.notifyDataSetChanged();
                                                        break;
                                                    }
                                                }
                                            }

                                            int account = 0;
                                            for (Member m : memberList) {
                                                if (m.checkstatus) {
                                                    ++account;
                                                }
                                            }

                                            selectNum = account;
                                            mOk.setText("确定(" + selectNum + ")");
                                            break;
                                        }
                                    }
                                }
                            });

                    mHandler.sendEmptyMessage(SUCCESS);
                }
            }

        };

        CommonFinalHttp request = new CommonFinalHttp();
        request.get(Server.BASE_URL + Server.MY_TAG_LIST, params, callback);
    }

    /**
     * 选择联系人确定按钮
     */
    public void onClickOk(View v) {

        //发送活动消息
        if (isBind) {

            // 获取已经选中的会员
            JsonArray jsonArray = new JsonArray();
            Member member = null;
            for (int i = 0; i < memberList.size(); i++) {
                member = memberList.get(i);
                if (member.checkstatus) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("userId", member.userId);
                    jsonArray.add(jsonObject);
                }
            }

            if (jsonArray.size() == 0) {
                showToast("请选择会员");
                return;
            }

            showDialog();
            String jstr = jsonArray.toString();
            sendEventDetail(jstr);

        } else {

            //选择通讯录成员群发消息
            List<Member> tmemberList = new ArrayList<Member>();
            StringBuilder builder = new StringBuilder();

            Member member = null;
            for (int i = 0; i < memberList.size(); i++) {
                member = memberList.get(i);
                if (member.checkstatus) {
                    tmemberList.add(member);
                }
            }

            if (tmemberList.size() < 2) {
                showToast("至少选择两个成员");
                return;
            }

            if (tmemberList.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    builder.append(tmemberList.get(i).customerName);
                }
                builder.append("...");
            } else {
                for (int i = 0; i < tmemberList.size(); i++) {
                    builder.append(tmemberList.get(i).customerName);
                }
            }

            Gson gson = new Gson();
            Intent intent = new Intent(MsgSelectMemberActivity.this,MyGroupChatActivity.class);
            intent.putExtra("count", tmemberList.size() + "");
            intent.putExtra("title", builder.toString());
            //intent.putExtra("list", (Serializable) tmemberList);
            App.tmemberList = tmemberList;
            intent.putExtra("frommode", "2");// 来自消息列表
            startActivity(intent);
            finish();
        }
    }

    public void homeClick(View v) {
        MsgSelectMemberActivity.this.finish();
    }

}