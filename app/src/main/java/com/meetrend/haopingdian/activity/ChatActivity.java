package com.meetrend.haopingdian.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.ChatListAdapter;
import com.meetrend.haopingdian.bean.ChatEntity;
import com.meetrend.haopingdian.bean.Faceicon;
import com.meetrend.haopingdian.enumbean.MsgStatus;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.fragment.FaceFragment;
import com.meetrend.haopingdian.fragment.FaceGridFragment;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.meetrend.haopingdian.speechrecord.AudioButton;
import com.meetrend.haopingdian.speechrecord.AudioButton.RecorderFinishListener;
import com.meetrend.haopingdian.speechrecord.AudioButton.StartHintRecordListener;
import com.meetrend.haopingdian.speechrecord.MediaPlayerManager;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.RecordHelper;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.MessyCodeCheck;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;

/**
 * 单聊界面
 *
 * @author 肖建斌
 */
public class ChatActivity extends BaseActivity implements FaceGridFragment.OnFaceiconClickedListener,
        FaceGridFragment.OnBackspaceClickedListener, OnScrollListener {

    private final static String TAG = ChatActivity.class.getName();

    private static final int REQUESTCODE_ADDON = 0x911;

    //标题栏
    @ViewInject(id = R.id.actionbar_title)
    TextView mTitle;
    @ViewInject(id = R.id.actionbar_home, click = "homeClick")
    ImageView mHome;

    @ViewInject(id = R.id.single_chat_listview)
    ListView chatListview;

    //声音和键盘的切换
    @ViewInject(id = R.id.iv_sound_mode, click = "recordSwitchClick")
    ImageView mSoundMode;

    @ViewInject(id = R.id.tv_record_mode)
    AudioButton mRecord;
    @ViewInject(id = R.id.et_content_chat, click = "editorClick")
    EditText mEditor;
    @ViewInject(id = R.id.iv_emoji_mode, click = "emojiSwitchClick")
    ImageView mFaceMode;
    @ViewInject(id = R.id.iv_attchment, click = "addonClick")
    ImageView mAddonMode;
    @ViewInject(id = R.id.tv_send, click = "sendMsgClick")
    TextView mSendMode;
    // 底部隐藏栏
    @ViewInject(id = R.id.footer_chat)
    FrameLayout mFaceLayout;
    FaceFragment mFace;

    //聊天对象详情
    @ViewInject(id = R.id.person_detail_icon, click = "lookFromUserDetailClick")
    ImageView mMsgPersonDetail;

    @ViewInject(id = R.id.msg_check_box, click = "msgCheckClick")
    CheckBox msgCheckImg;


    // 滑动取消录音距离
    private int mDistance;
    private RecordHelper mRecordHelper;
    // 消息列表
    private ChatListAdapter mAdapter;
    private List<ChatEntity> mList;
    private String maxId = "", minId = "";
    // 解析MessageListFragment SearchActivity
    private String mUserId, mName, mAvartarId, mFromMemberId, mFromUserId;
    // 控制输入法键盘
    private InputMethodManager imm;

    private ExecutorService mES = Executors.newSingleThreadExecutor();

    //是否是下拉刷新
    private boolean isRefreshMode;

    //不能聊天提示
    private LinearLayout canTalkFooterLayout;
    //private TextView noTalkView;

    //是否可以聊天的权限
    private boolean canTalk;

    /**
     * 可以加载更多标识
     */
    private boolean isCanLoadMore;

    //消息加载进度条
    RelativeLayout progressWheel;

    private boolean fillterMsg = true;

    private boolean isPoll;//是否轮询
    PollHandler pollHandler;
    private boolean firstIn = true;

    private boolean isLoadMore = false;//正在加载数据
    private boolean isSelectTop;//listview selec
    private boolean isPollLoadData;//正在轮询刷新数据
    private boolean isfilterData;//正在过滤数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDistance = displayMetrics.heightPixels / 3;
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chat);
        FinalActivity.initInjectedView(this);
        pollHandler = new PollHandler(ChatActivity.this);

        showDialog();

        //qq聊天表情
        mFace = (FaceFragment) this.getSupportFragmentManager().findFragmentById(R.id.fragment_face_chat);

        //不可以聊天提示
        canTalkFooterLayout = (LinearLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.footerview_layout, null);

        // 从通讯录会员详情传递的数据
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mUserId = bundle.getString("user_id");
            mFromMemberId = mUserId;
            mName = bundle.getString("name");
            mAvartarId = bundle.getString("avatarId");
            canTalk = bundle.getBoolean("canTalk");
            if (!canTalk) {
                chatListview.addFooterView(canTalkFooterLayout);
            }
        }

        mTitle.setText(mName);
        mRecordHelper = new RecordHelper();
        mEditor.addTextChangedListener(mInputWatcher);

        mList = new ArrayList<ChatEntity>();
        mAdapter = new ChatListAdapter(this, mList);
        chatListview.setAdapter(mAdapter);
        loadMsgHistoryList();

        //开始
        mRecord.setStartHintRecordListener(new StartHintRecordListener() {

            @Override
            public void startHintRecord() {

                final MediaPlayer mPlayer = MediaPlayer.create(ChatActivity.this, R.raw.notificationsound);
                mPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayer.release();
                    }
                });

            }
        });

        //结束录音回调 mPlayer.start();
        mRecord.setRecorderFinishListener(new RecorderFinishListener() {

            @Override
            public void recorderFinish(float time, String filePath) {

                sendMultiMediaTask("voice", filePath, time + "");
            }
        });


        View headerView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.chat_listview_head_layout, null);
        progressWheel = (RelativeLayout) headerView.findViewById(R.id.chat_listview_header_process);
        progressWheel.setVisibility(View.GONE);
        headerView.setOnClickListener(null);
        chatListview.addHeaderView(headerView);

        chatListview.setOnScrollListener(this);

        //消息过滤
        msgCheckImg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //mList.clear();
                isfilterData = true;
                pageIndex = 0;

                if (isChecked) {
                    fillterMsg = false;
                } else {
                    fillterMsg = true;
                }

                loadMsgHistoryList();
            }
        });
    }

    boolean isOuto() {
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return manager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    OnRefreshListener<ListView> listener = new OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            isRefreshMode = true;
            loadMsgHistoryList();
        }
    };


    //查看对方的详情
    public void lookFromUserDetailClick(View view) {

        Bundle bundle = new Bundle();
        bundle.putString("id", mFromMemberId);
        Intent intent = new Intent(ChatActivity.this, MemberInfoActivity.class);
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        ChatActivity.this.startActivity(intent);
    }

    private int pageIndex;
    private int pageCount;

    private void loadMsgHistoryList() {
        try {
            AjaxParams params = new AjaxParams();
            params.put("token", SPUtil.getDefault(ChatActivity.this).getToken());
            params.put("storeId", SPUtil.getDefault(ChatActivity.this).getStoreId());
            params.put("userId", mUserId);
            params.put("pageIndex", (++pageIndex) + "");
            params.put("pageSize", 20 + "");

            if (!msgCheckImg.isChecked()) {
                params.put("filterMsg", "true");
            }

            final Callback callback = new Callback(tag) {

                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    progressWheel.setVisibility(View.GONE);
                    dimissDialog();

                    if (!isSuccess) {

                        if (data.has("msg")) {
                            //showToast(data.get("msg").getAsString());
                        } else {
                            //showToast(R.string.load_fail_str);
                        }

                    } else {

                        pageCount = data.get("pageCount").getAsInt();
                        pageIndex = data.get("pageIndex").getAsInt();

                        if (pageIndex >= pageCount) {
                            isCanLoadMore = false;
                        } else {
                            isCanLoadMore = true;
                        }

                        String jsonArrayStr = data.get("records").toString();
                        Gson gson = new Gson();
                        List<ChatEntity> list = gson.fromJson(jsonArrayStr, new TypeToken<List<ChatEntity>>() {
                        }.getType());

                        for (ChatEntity item : list) {
                            // 消息发送失败
                            if (item.replyStatus.equals("2"))
                                item.status = MsgStatus.FAILED;

                                // 消息发送成功
                            else if (item.replyStatus.equals("1"))
                                item.status = MsgStatus.SUCCESS;
                                // 发送中，或者失败
                            else
                                item.status = MsgStatus.FAILED;
                        }

                        //轮询时刷新数据
                        if (isPollLoadData) {
                            mAdapter.setList(list);
                            mAdapter.notifyDataSetChanged();
                            isPollLoadData = false;
                            chatListview.setSelection(list.size() - 1);
                            return;
                        } else {
                            //过滤数据
                            if (isfilterData) {
                                isfilterData = false;
                                mAdapter.setList(list);
                                mAdapter.notifyDataSetChanged();
                                chatListview.setSelection(list.size() - 1);
                                return;
                            } else {

                                //下拉加载数据或者第一次加载数据
                                mList.addAll(list);
                                mAdapter.notifyDataSetChanged();

                                //第一次进来加载数据
                                if (firstIn) {
                                    if (null != list && list.size() > 0)
                                        chatListview.setSelection(mList.size() - 1);
                                    pollHandler.sendEmptyMessage(1);
                                    firstIn = false;
                                } else {
                                    //下拉加载数据
                                    chatListview.setSelection(list.size() - 1);
                                    isLoadMore = false;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    dimissDialog();
                    isLoadMore = false;
                    progressWheel.setVisibility(View.GONE);
                }
            };

            CommonFinalHttp http = new CommonFinalHttp();
            http.get(Server.BASE_URL + Server.TALK_DETAIL_URL, params, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PollHandler extends Handler {

        WeakReference<Activity> mActivityRerence = null;

        public PollHandler(Activity activity) {
            mActivityRerence = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity msgActivity = mActivityRerence.get();

            switch (msg.what) {
                case 1:
                    if (null != msgActivity) {
                        new Thread() {
                            @Override
                            public void run() {
                                while (isPoll) {
                                    ChatActivity.this.sleep(6 * 1000);
                                    if (isLoadMore)
                                        continue;//若果正在加载数据，则不刷新
                                    isPollLoadData = true;
                                    pageIndex = 0;
                                    Log.i("msghistory-datas", "--datas--");
                                    loadMsgHistoryList();
                                }
                            }
                        }.start();
                    }

                    break;
            }
        }
    }

    /**
     * 控制发送按钮和发送图片附件按钮的显示、隐藏
     */
    private TextWatcher mInputWatcher = new TextWatcherChangeListener() {

        @Override
        public void afterTextChanged(Editable s) {
            if (MessyCodeCheck.isMessyCode(s.toString())) {
                // showToast("是乱码");
                mEditor.setText("");
                return;
            }

            if (s.length() == 0) {
                mAddonMode.setVisibility(View.VISIBLE);
                mSendMode.setVisibility(View.GONE);
            } else {
                mAddonMode.setVisibility(View.GONE);
                mSendMode.setVisibility(View.VISIBLE);
            }

        }
    };

    enum RECORD_STATUS {
        NONE, INIT, RECORDING, CANCEL, SHORT, SENDING
    }

    ;

    void sleep(long time) {
        try {
            Thread.sleep(5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片，语音线程
     */
    private void sendMultiMediaTask(String type, String absPath, String time){

        final String name = Thread.currentThread().getName();
        String createTime = System.currentTimeMillis() + "";
        ChatEntity entity = new ChatEntity(type, createTime, absPath,
                App.imgid, mUserId, mFromMemberId, "", MsgStatus.SENDING, name, time);

        List<ChatEntity> list = new ArrayList<ChatEntity>();
        mList.add(0, entity);
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
        chatListview.setSelection(mList.size() - 1);


        JsonObject object = new JsonObject();
        object.addProperty("storeId", SPUtil.getDefault(ChatActivity.this).getStoreId());
        object.addProperty("toUserId", mUserId);
        object.addProperty("type", type);

        AjaxParams params = new AjaxParams();
        params.put("args", object.toString());

        File sendFile = new File(absPath);
        if (sendFile == null){
            showToast("获取文件失败");
            return;
        }else{
            long contentLength = sendFile.length()/1024/1024;
            Log.i(TAG+"  send pitrue size",contentLength+"");
            if (contentLength > 2){
                showToast("文件大小超过2M,不支持发送");
                return;
            }
        }

        try{
            params.put("file", sendFile);
        }catch (Exception e){}

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess)
                    msgNotifyChange(name, MsgStatus.SUCCESS);
                else
                    msgNotifyChange(name, MsgStatus.FAILED);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                msgNotifyChange(name, MsgStatus.FAILED);
            }
        };

        FinalHttp http = new FinalHttp();
        http.post(Server.BASE_URL + Server.SEND_MULTIMSG_URL + "?token="
                + SPUtil.getDefault(ChatActivity.this).getToken(), params,callback);
    }

    //更新消息状态
    public void msgNotifyChange(String threadName, MsgStatus msgStatus) {

        for (ChatEntity item : mList) {
            if (item.status == MsgStatus.SENDING && item.threadName.equals(threadName)) {
                item.status = msgStatus;// 设置消息的状态
                mAdapter.notifyDataSetChanged();
                chatListview.setSelection(mAdapter.getCount() - 1);
                break;
            }
        }
    }

    // 发送消息
    class SendTextMessageTask implements Runnable {
        private String content, name, type;

        public SendTextMessageTask(String content, String type) {
            this.content = content;
            this.type = type;
        }

        @Override
        public void run() {
            AjaxParams params = new AjaxParams();
            params.put("token", SPUtil.getDefault(ChatActivity.this).getToken());
            params.put("storeId", SPUtil.getDefault(ChatActivity.this)
                    .getStoreId());
            params.put("toUserId", mUserId);
            params.put("type", type);
            params.put("content", content);


            Callback callback = new Callback(tag) {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);

                    if (isSuccess)
                        msgNotifyChange(name, MsgStatus.SUCCESS);
                    else
                        msgNotifyChange(name, MsgStatus.FAILED);
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    msgNotifyChange(name, MsgStatus.FAILED);
                }
            };
            FinalHttp http = new FinalHttp();
            http.post(Server.BASE_URL + Server.SEND_MSG_URL, params, callback);
        }
    }

    // 表情点击处理
    @Override
    public void onFaceiconClick(Faceicon faceicon) {
        FaceFragment.input(mEditor, faceicon);
    }

    @Override
    public void onBackspaceClick() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0,
                0, KeyEvent.KEYCODE_ENDCALL);
        mEditor.dispatchKeyEvent(event);
    }

    public void homeClick(View view) {
        this.finish();
    }

    // 按钮点击事件处理
    public void editorClick(View view) {
        // 如果软键盘处于活跃状态，若此时表情处于显示状态，则表情布局隐藏
        if (imm.isActive()) {
            if (mFaceLayout.getVisibility() == View.VISIBLE) {
                mFaceLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 发送消息按钮的click事件
     */
    public void sendMsgClick(View view) {
        String content = mEditor.getText().toString();
        if (TextUtils.isEmpty(content)) {
            showToast("内容不能为空");
            return;
        }

        mEditor.setText("");
        mAddonMode.setVisibility(View.VISIBLE);
        mSendMode.setVisibility(View.GONE);

        String createTime = System.currentTimeMillis() + "";
        SendTextMessageTask sendTextMessageTask = new SendTextMessageTask(content, "text");
        sendTextMessageTask.name = createTime;

        ChatEntity entity = new ChatEntity("text", createTime, content,
                App.imgid, mUserId, mFromMemberId, "", MsgStatus.SENDING, sendTextMessageTask.name, null);

        mList.add(0, entity);
        mAdapter.notifyDataSetChanged();
        chatListview.setSelection(mList.size() - 1);
        Log.i("-----sendTextMsg------", "log");

        mES.submit(sendTextMessageTask);
    }

    // 发送图片
    public void addonClick(View v) {
        if (mFaceLayout.getVisibility() == View.VISIBLE){
            mFaceLayout.setVisibility(View.GONE);
        }
        this.startActivityForResult(new Intent(this, AddPictrueActivity.class), REQUESTCODE_ADDON);
        this.overridePendingTransition(R.anim.activity_popup, R.anim.dialog_out_anim);
    }

    //语音和键盘的切换
    public void recordSwitchClick(View v) {
        mFaceLayout.setVisibility(View.GONE);
        if (v.getTag() == null) {
            mSoundMode.setImageResource(R.drawable.chat_mode_text);
            imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
            mEditor.setVisibility(View.GONE);
            mRecord.setVisibility(View.VISIBLE);
            v.setTag("");
        } else {
            mSoundMode.setImageResource(R.drawable.chat_mode_record);
            imm.showSoftInput(mEditor, 0);
            mEditor.setVisibility(View.VISIBLE);
            mRecord.setVisibility(View.GONE);
            mEditor.requestFocus();
            mEditor.requestFocusFromTouch();
            imm.showSoftInput(mEditor, 0);
            v.setTag(null);
        }
    }

    // 文本 和 表情输入切换
    public void emojiSwitchClick(View v) {
        mEditor.setVisibility(View.VISIBLE);
        mRecord.setVisibility(View.GONE);

        if (v.getTag() == null) {
            imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
            try {
                Thread.sleep(80);// 解决此时会黑一下屏幕的问题
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mFaceLayout.setVisibility(View.VISIBLE);
            v.setTag("");
        } else {
            imm.showSoftInput(mEditor, 0);
            mFaceLayout.setVisibility(View.GONE);
            v.setTag(null);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_ADDON) {
            //发送图片消息
            String pictruePath = intent.getStringExtra("path");
            if (TextUtils.isEmpty(pictruePath)) {
                showToast("获取文件路径失败");
                return;
            }
            sendMultiMediaTask("image", pictruePath, null);
        }
        super.onActivityResult( requestCode, resultCode,intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if (mFaceLayout.getVisibility() == View.VISIBLE) {
                mFaceLayout.setVisibility(View.GONE);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayerManager.resume();
        isPoll = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.pause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        MediaPlayerManager.relase();

        //MessageEvent messageEvent = new MessageEvent();
        //EventBus.getDefault().post(messageEvent);

        isPoll = false;
    }

    private int firstVisibleItem;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //加载条件，1有数据 ，2可见项为第一项的，3并且是停止状态
        if (isCanLoadMore == true && firstVisibleItem == 0 && scrollState == SCROLL_STATE_IDLE) {
            progressWheel.setVisibility(View.VISIBLE);
            isLoadMore = true;
            loadMsgHistoryList();
        } else if (scrollState == SCROLL_STATE_FLING) {
            isLoadMore = true;//手指触摸的时候假定此时是下拉数据，阻止轮询刷新数据
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

}