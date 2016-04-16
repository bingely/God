package com.meetrend.haopingdian.activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.GroupChatListAdapter;
import com.meetrend.haopingdian.bean.Faceicon;
import com.meetrend.haopingdian.bean.GroupChatEntity;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.bean.MyBitmapEntity;
import com.meetrend.haopingdian.enumbean.MsgStatus;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.MessageEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.BitmapUtil;
import com.meetrend.haopingdian.util.MessyCodeCheck;
import com.umeng.socialize.utils.Log;

import org.json.JSONObject;
import org.json.simple.JSONValue;

import de.greenrobot.event.EventBus;

public class MyGroupChatActivity extends BaseActivity {

    @ViewInject(id = R.id.actionbar_title)
    TextView mTitle;
    @ViewInject(id = R.id.actionbar_home, click = "homeClick")
    ImageView mHome;
    @ViewInject(id = R.id.group_chat_icon, click = "actionClick")
    ImageView mAction;

    @ViewInject(id = R.id.chatgrouplistview)
    ListView mListView;
    @ViewInject(id = R.id.tv_record_mode)
    TextView mRecord;
    @ViewInject(id = R.id.et_content_chat, click = "editorClick")
    EditText mEditor;
    @ViewInject(id = R.id.tv_send, click = "sendMsgClick")
    TextView mSendMode;

    // 消息列表
    private GroupChatListAdapter mAdapter;
    private List<GroupChatEntity> mList;

    private ExecutorService mES = Executors.newSingleThreadExecutor();
    private boolean needPostMsgEvent = false;

    private ArrayList<String> userIdList;
    private ArrayList<String> groupIdList;

    ExecutorService mExecutor = null;
    //群发消息对象ID
    JsonArray sendUserList = new JsonArray();

    String currentUserId = "";
    String currentUserName = "";
    //成员
    int userIdCount = 0;
    //群组
    int groupCount = 0;

    private String groupId;
    private List<Member> groupMembers;
    private String title;
    private String fromMode;//从哪个界面进入


    //图片的合成
    List<MyBitmapEntity> tEntityList = null;
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private String pictrueId;
    private String bitmapPath;

    private String membersArray = "";
    private int size;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.acitivity_mygroupchat);
        FinalActivity.initInjectedView(this);

        showDialog();

        Intent intent = getIntent();
        fromMode = intent.getStringExtra("frommode");
        currentUserId = SPUtil.getDefault(this).getId();//当前用户userid
        currentUserName = SPUtil.getDefault(this).getLoginName();//当前用户名字
        //来自历史记录列表
        String count = "";
        if (fromMode.equals("1")) {
            groupId = intent.getStringExtra("gid");
            title = intent.getStringExtra("title");
            count = intent.getStringExtra("count");
            if (count.equals("") || count == null) {
                count = "0";
            }
            RequesGroupMsg(groupId);
        } else {
            //来自消息群发
            count = intent.getStringExtra("count");
            title = intent.getStringExtra("title");
            groupMembers = App.tmemberList;
            Log.i("@@@@@@@@@@@@@",groupMembers.size()+"");
            request();
        }

        mTitle.setText("群发消息(" + count + ")");

        mList = new ArrayList<GroupChatEntity>();
        mAdapter = new GroupChatListAdapter(this, mList);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 查看群组成员
     */
    public void actionClick(View v) {
        Intent intent = new Intent(this, ShowGroupListPictrueActivity.class);
        if (fromMode.equals("1")) {

            intent.putExtra("fromMode", 1 + "");
            intent.putExtra("gid", groupId);
            startActivity(intent);

        } else {

            intent.putExtra("fromMode", 2 + "");
            intent.putExtra("group", (Serializable) groupMembers);
            intent.putExtra("title", title);
            startActivity(intent);

        }
    }

    /**
     * 发送消息
     */
    public void sendMsgClick(View view) {
        String content = mEditor.getText().toString();
        if (TextUtils.isEmpty(content)) {
            showToast("内容不能为空");
            return;
        }
        mEditor.setText("");
        sendGroupMsg(content);
    }

    /**
     * 创建临时会话群组
     */
    private void createTempGroup(String title, int size) {

        JsonArray usersArray = new JsonArray();
        JsonArray imagesArray = new JsonArray();

        for (Member member : groupMembers) {
            JsonObject userObj = new JsonObject();
            userObj.addProperty("userId", member.userId);
            userObj.addProperty("userName", member.customerName);
            usersArray.add(userObj);

            JsonObject imgObject = new JsonObject();
            String pictrueId = member.pictureId;
            String resultPid = null;

            if ("".equals(pictrueId)) {
                resultPid = "";
                imgObject.addProperty("image", resultPid);
            } else {
                resultPid = pictrueId.substring(31, pictrueId.length());
                imgObject.addProperty("image", resultPid);
            }

            imagesArray.add(imgObject);
        }

        Callback callback = new Callback(tag, this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {
                    showToast("创建临时会话群组成功");
                    groupId = data.get("groupId").getAsString();
                    dimissDialog();
                } else {

                    if (data.has("msg")) {
                        showToast(data.get("msg").toString());
                    } else {
                        showToast("创建失败");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dimissDialog();

                if (null != strMsg) {
                    showToast(strMsg);
                }
            }
        };

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(MyGroupChatActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(MyGroupChatActivity.this).getStoreId());
        params.put("groupName", title);
        params.put("presonNumber", size + "");
        params.put("images", imagesArray.toString());
        params.put("groupUsers", usersArray.toString());

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.post(Server.BASE_URL + Server.CREATE_GROUP, params, callback);
    }

    private void request() {

        JsonArray usersArray = new JsonArray();
        JsonArray imagesArray = new JsonArray();

        for (Member member : groupMembers) {
            JsonObject userObj = new JsonObject();
            userObj.addProperty("userId", member.userId);
            userObj.addProperty("userName", member.customerName);
            usersArray.add(userObj);

            JsonObject imgObject = new JsonObject();
            String pictrueId = member.pictureId;
            String resultPid = null;

            if ("".equals(pictrueId)) {
                resultPid = "";
                imgObject.addProperty("image", resultPid);
            } else {
                resultPid = pictrueId.substring(31, pictrueId.length());
                imgObject.addProperty("image", resultPid);
            }

            imagesArray.add(imgObject);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("storeId=" + SPUtil.getDefault(MyGroupChatActivity.this).getStoreId() + "&");
        stringBuilder.append("groupName=" + title + "&");
        stringBuilder.append("presonNumber=" + size + "&");
        stringBuilder.append("images=" + imagesArray.toString() + "&");
        stringBuilder.append("groupUsers=" + usersArray.toString());
        String string = stringBuilder.toString();
        new NetThread(Server.BASE_URL + Server.CREATE_GROUP + "?token=" + SPUtil.getDefault(MyGroupChatActivity.this).getToken(), string).start();
    }


    /**
     * 群聊历史聊天记录
     */
    private void RequesGroupMsg(String groupId) {

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(MyGroupChatActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(MyGroupChatActivity.this).getStoreId());
        params.put("groupId", groupId);

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dimissDialog();

                if (!isSuccess) {
                    showToast("获取聊天记录失败");
                    return;
                }
                String jsonArrayStr = data.get("historyMsgs").toString();
                int groupNum = data.get("totalNumber").getAsInt();
                mTitle.setText("群发消息(" + groupNum + ")");
                Gson gson = new Gson();
                mList = gson.fromJson(jsonArrayStr, new TypeToken<List<GroupChatEntity>>() {
                }.getType());
                for (GroupChatEntity item : mList) {
                    item.avatarId = App.imgid;
                }

                //时间戳排序
                Collections.sort(mList, new Comparator<GroupChatEntity>() {

                    @Override
                    public int compare(GroupChatEntity lhs, GroupChatEntity rhs) {
                        return lhs.sendTime.compareTo(rhs.sendTime);
                    }
                });

                mAdapter.setList(mList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                dimissDialog();
                super.onFailure(t, errorNo, strMsg);
            }
        };
        FinalHttp http = new FinalHttp();
        http.post(Server.BASE_URL + Server.HISTORY_MESSAGE, params, callback);
    }


    public byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }

    public class NetThread extends Thread {

        public String path;
        public String params;

        public NetThread(String path, String params) {
            this.path = path;
            this.params = params;
        }

        @Override
        public void run() {

            try {

                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");// 提交模式
                conn.setDoOutput(true);// 是否输入参数
                byte[] bypes = params.getBytes();
                conn.getOutputStream().write(bypes);// 输入参数
                InputStream inStream = conn.getInputStream();

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                byte[] data = outStream.toByteArray();//网页的二进制数据
                final String string = new String(data);

                outStream.close();
                inStream.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dimissDialog();
                        Gson gson = new Gson();
                        JsonObject o = new JsonParser().parse(string).getAsJsonObject();
                        if (o.get("success").getAsBoolean()) {
                            JsonObject jsonObject = o.get("data").getAsJsonObject();
                            groupId = jsonObject.get("groupId").getAsString();
                            showToast("创建群组成功");
                        } else {
                            showToast("创建群组失败");
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息
     */

    private void sendGroupMsg(String content) {

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(MyGroupChatActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(MyGroupChatActivity.this).getStoreId());
        params.put("groupId", groupId);
        params.put("sendType", "default");//默认
        params.put("sendContent", content);
        params.put("userId", currentUserId);
        params.put("pictureId", "");

        final String name = Thread.currentThread().getName();
        String createTime = System.currentTimeMillis() + "";
        GroupChatEntity entity = new GroupChatEntity("", "text", createTime, content, "",
                currentUserName, MsgStatus.SENDING, name, App.imgid);

        mList.add(entity);
        mMsgHandler.sendEmptyMessage(Code.INIT);

        Callback callback = new Callback(tag, this) {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {
                    needPostMsgEvent = true;
                }

                if (data.has("msg")) {
                    String msg = data.get("msg").getAsString();
                    showToast(msg);
                }

                Message msg = new Message();
                msg.obj = name;
                msg.what = isSuccess ? Code.SUCCESS : Code.FAILED;
                mMsgHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Message msg = new Message();
                msg.obj = name;
                msg.what = Code.FAILED;
                mMsgHandler.sendMessage(msg);
            }
        };

        FinalHttp http = new FinalHttp();
        http.post(Server.BASE_URL + Server.NEW_SEND_MULTI_MSG, params, callback);
    }


    /***
     * 发送消息
     */
    private Handler mMsgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Code.SUCCESS: {
                    String name = (String) msg.obj;
                    for (GroupChatEntity item : mList) {
                        if (item.status == MsgStatus.SENDING && item.threadName.equals(name)) {
                            item.status = msg.what == Code.SUCCESS ? MsgStatus.SUCCESS : MsgStatus.FAILED;
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    break;
                }

                case Code.FAILED: {
                    String name = (String) msg.obj;
                    for (GroupChatEntity item : mList) {
                        if (item.status == MsgStatus.SENDING && item.threadName.equals(name)) {
                            item.status = msg.what == Code.SUCCESS ? MsgStatus.SUCCESS : MsgStatus.FAILED;
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    break;
                }
                case Code.INIT: {
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }

        }
    };

    // 按钮点击事件处理
    public void editorClick(View view) {
        mEditor.requestFocus();
    }

    public void homeClick(View view) {
        this.finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        App.tmemberList = null;
        EventBus.getDefault().post(new MessageEvent());
    }


}