package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.SignSingleChooseAdapter;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.SendMemberEvent;
import com.meetrend.haopingdian.memberlistdb.MemberListDbOperator;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NetUtil;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 签到 选择会员
 *
 * @author 肖建斌
 */
public class ChooseSingleMemberActivity extends BaseActivity {

    private final static int LOAD_DATA = 0x925;// 分页加载数据
    public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ALPHABET_LENGTH = ALPHABET.length();

    @ViewInject(id = R.id.actionbar_home, click = "finishActivity")
    ImageView backView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;
    @ViewInject(id = R.id.actionbar_action, click = "sureClick")
    TextView sureView;

    @ViewInject(id = R.id.search_edit)
    EditText searchEdit;

    @ViewInject(id = R.id.sign_listview)
    ListView mListView;
    @ViewInject(id = R.id.alphat_layout)
    SideBar alphatSideBar;
    @ViewInject(id = R.id.tv_alphabet_ui_tableview)
    TextView alphatToast;

    private SignSingleChooseAdapter mAdapter = null;
    private HashMap<String, Integer> selector;

    private int mCurrentIndex = 1;
    private int mPageCount;
    // 上次请求的时间戳
    private String lastRequestTime;
    //值为true 全部数据 值为false 增量数据 （修改 和 添加）
    private boolean isAllData;

    private List<Member> allDataList;
    private ArrayList<Member> memberList;
    //当前搜索到的数据
    private ArrayList<Member> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesinglemember);
        FinalActivity.initInjectedView(this);

        titleView.setText("关联联系人");
        sureView.setText("确定");

        allDataList = new ArrayList<Member>();
        memberList = new ArrayList<Member>();
        selector = new HashMap<String, Integer>();

        mAdapter = new SignSingleChooseAdapter(memberList, this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Member member = null;

                if (searchEdit.getText().toString().equals("")) {

                    int size = memberList.size();

                    for (int i = 0; i < size; i++) {

                        member = memberList.get(i);

                        if (position == i) {

                            if (member.checkstatus)
                                member.checkstatus = false;
                            else
                                member.checkstatus = true;

                        } else {
                            member.checkstatus = false;
                        }
                    }

                } else {

                    int size = searchList.size();

                    for (int i = 0; i < size; i++) {

                        member = searchList.get(i);

                        if (position == i) {

                            if (member.checkstatus)
                                member.checkstatus = false;
                            else
                                member.checkstatus = true;

                        } else {
                            member.checkstatus = false;
                        }
                    }

                }

                mAdapter.notifyDataSetChanged();
            }
        });

        // 字母条触摸事件
        alphatSideBar.setTextView(alphatToast);
        alphatSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String key) {

                if (selector.containsKey(key)) {
                    int pos = selector.get(key);
                    if (mListView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有
                        mListView.setSelectionFromTop(pos + mListView.getHeaderViewsCount(), 0);

                    } else {
                        mListView.setSelectionFromTop(pos, 0);// 滑动到第一项
                    }
                } else {
                    // 滑动到搜索栏
                    mListView.smoothScrollToPosition(-1);
                }
            }
        });

        // do something
        searchEdit.addTextChangedListener(new TextWatcher() {

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
                    // 会员列表
                    searchList = MemberListDbOperator.getInstance().searchMembers(ChooseSingleMemberActivity.this,
                            changeText.toString());
                    searchList = Member.convert(searchList);
                    mAdapter.setList(searchList);
                    mAdapter.notifyDataSetChanged();

                } else {
                    mAdapter.setList(memberList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        if (NetUtil.hasConnected(ChooseSingleMemberActivity.this)) {

            lastRequestTime = SPUtil.getDefault(ChooseSingleMemberActivity.this).getLastRequestTime();
            requestCurrentMemberList();

        } else {

            //没网的情况，加载数据库数据
            List<Member> list = MemberListDbOperator.getInstance().getMemberList(ChooseSingleMemberActivity.this);

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

            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
            dimissDialog();
        }

    }

    public void sureClick(View view) {

        Member member = null;

        if (!searchEdit.getText().toString().equals("")) {

            int size = searchList.size();
            for (int i = 0; i < size; i++) {
                member = searchList.get(i);
                if (member.checkstatus) {
                    EventBus.getDefault().post(new SendMemberEvent(member));
                    finish();
                    return;
                }
            }

        } else {

            int size = memberList.size();
            for (int i = 0; i < size; i++) {

                member = memberList.get(i);
                if (member.checkstatus) {
                    EventBus.getDefault().post(new SendMemberEvent(member));
                    finish();
                    return;
                }
            }
        }

        showToast("请选择关联联系人");
    }

    private void requestCurrentMemberList() {

        Callback callback = new Callback(tag, this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {

                    String jsonArrayStr = data.get("argsArray").toString();
                    mPageCount = data.get("pageCount").getAsInt();
                    lastRequestTime = data.get("lastRequestTime").getAsString();
                    isAllData = data.get("isAllData").getAsBoolean();
                    SPUtil.getDefault(ChooseSingleMemberActivity.this).saveLastRequestTime(lastRequestTime);

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
                        if (mCurrentIndex < mPageCount) {
                            mHandler.sendEmptyMessage(LOAD_DATA);
                        }

                        //如果是全量数据，则清除掉数据库中的数据
                        if (mCurrentIndex >= mPageCount) {
                            MemberListDbOperator.getInstance().clearMemberDatas(ChooseSingleMemberActivity.this);//清空数据库数据
                            MemberListDbOperator.getInstance().saveMembers(ChooseSingleMemberActivity.this, allDataList);//将有所最新的数据保存值本地数据库
                        }

                        //排序
                        memberList = Member.convert(allDataList);

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

                        //遍历数据库,若是数据库中查不到该记录，则添加，若能找到该记录则删除，并更新
                        for (Member member : list) {

                            if (MemberListDbOperator.getInstance().findOneMember(ChooseSingleMemberActivity.this, member.userId)) {
                                //删除该条信息，
                                MemberListDbOperator.getInstance().clearOndMember(ChooseSingleMemberActivity.this, member);
                                //插入新的数据
                                MemberListDbOperator.getInstance().saveOneMember(ChooseSingleMemberActivity.this, member);
                            } else {
                                //插入新的数据
                                MemberListDbOperator.getInstance().saveOneMember(ChooseSingleMemberActivity.this, member);
                            }
                        }

                        memberList = (ArrayList<Member>) MemberListDbOperator.getInstance().getMemberList(ChooseSingleMemberActivity.this);

                        //排序
                        memberList = Member.convert(memberList);

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

                dimissDialog();

                memberList = (ArrayList<Member>) MemberListDbOperator.getInstance().getMemberList(ChooseSingleMemberActivity.this);

                //排序
                memberList = Member.convert(memberList);

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
        };

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(ChooseSingleMemberActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ChooseSingleMemberActivity.this).getStoreId());
        params.put("keyword", "");
        params.put("pageSize", "500");
        params.put("pageIndex", mCurrentIndex + "");
        params.put("lastRequestTime", SPUtil.getDefault(ChooseSingleMemberActivity.this).getLastRequestTime());//上次请求的时间戳
        params.put("clientSize", MemberListDbOperator.getInstance().getSQLiteMemberListSize(ChooseSingleMemberActivity.this) + "");

        CommonFinalHttp http = new CommonFinalHttp();
        http.get(Server.BASE_URL + Server.APP_MEMBER_USER_LIST, params, callback);
    }


    public void finishActivity(View view) {
        finish();
    }

}