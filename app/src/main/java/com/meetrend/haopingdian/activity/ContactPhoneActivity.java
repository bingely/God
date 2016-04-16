package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.PeopleAdapter;
import com.meetrend.haopingdian.bean.People;
import com.meetrend.haopingdian.bean.TempPeople;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.TextWatcherChangeListener;
import com.meetrend.haopingdian.widget.PinnedHeaderListView;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;

import de.greenrobot.event.EventBus;

public class ContactPhoneActivity extends BaseActivity {

    @ViewInject(id = R.id.title_layout)
    LinearLayout titleLayout;
    @ViewInject(id = R.id.actionbar_title)
    TextView mTitle;
    @ViewInject(id = R.id.actionbar_action, click = "actionClick")
    TextView mAction;
    @ViewInject(id = R.id.actionbar_home, click = "homeClick")
    ImageView mHome;

    @ViewInject(id = R.id.lv_contact_phone)
    ListView mListView;

    private static final String[] PROJECTION = {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.SORT_KEY_PRIMARY};
    private PeopleAdapter mAdapter;
    private List<People> resultList;//排序后的手机通讯录

    //字母条
    private static final Pattern PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ALPHABET_LENGTH = ALPHABET.length();

    private HashMap<String, Integer> selector;// 存放含有索引字母的位置
    //private int height;

    //字母
    @ViewInject(id = R.id.alphat_layout)
    private SideBar alphatSideBar;
    @ViewInject(id = R.id.tv_alphabet_ui_tableview)
    private TextView alphatToast;

    @ViewInject(id = R.id.search_edit)
    EditText searchEditText;
    @ViewInject(id = R.id.no_searchlist_view)
    private TextView emptyHintView;
    List<People> dymList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_contact_phone);
        FinalActivity.initInjectedView(this);

        mTitle.setText("通讯录客户");
        mAction.setText("添加");

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = this.getContentResolver().query(uri, PROJECTION, null, null, Phone.SORT_KEY_PRIMARY);
        if (cursor.getCount() == 0) {
            showToast("通讯录无联系人");
            finish();
            return;
        }

        List<People> list = new ArrayList<People>();
        cursor.moveToFirst();
        do {
            String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
            People item = new People(name, phoneNumber, "");
            list.add(item);
        } while (cursor.moveToNext());
        this.startManagingCursor(cursor);//关闭

        alphatSideBar.setVisibility(View.VISIBLE);

        alphatSideBar.setTextView(alphatToast);
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

        //设置联系人的状态
        int size = App.memberPhoneList.size();
        for (int i = 0; i < size; i++) {
            String phoneNum = App.memberPhoneList.get(i);

            for (int j = 0; j < list.size(); j++) {
                People people = list.get(j);
                if (phoneNum != null && phoneNum.equals(people.phoneNumber)) {
                    people.setStatus(1);
                    break;
                }
            }
        }

        resultList = People.convert(list);
        mAdapter = new PeopleAdapter(this, resultList);
        mListView.setAdapter(mAdapter);


        int rsize = resultList.size();
        selector = new HashMap<String, Integer>();
        for (int i = 0; i < rsize; ++i) {
            for (int j = 0; j < ALPHABET_LENGTH; j++) {
                String str = ALPHABET.charAt(j) + "";
                if (resultList.get(i).pinyin.equals(str)) {
                    selector.put(str, i); //值 ：位置
                    break;
                }
            }
        }


        //搜索监听
        searchEditText.addTextChangedListener(new TextWatcherChangeListener() {
            @Override
            public void afterTextChanged(Editable newText) {

                if (!"".equals(newText.toString())) {
                    List<People> temList = new ArrayList<People>();
                    for (int i = 0; i < resultList.size(); i++) {
                        String name = resultList.get(i).name;
                        if (name.contains(newText)) {
                            temList.add(resultList.get(i));
                        }
                    }

                    dymList = People.convert(temList);
                    mAdapter.setList(dymList);
                    mAdapter.notifyDataSetChanged();
                    //显示
                    if (emptyHintView.getVisibility() == View.GONE && temList.size() == 0)
                        emptyHintView.setVisibility(View.VISIBLE);

                } else {
                    if (emptyHintView.getVisibility() == View.VISIBLE )
                        emptyHintView.setVisibility(View.GONE);
                    mAdapter.setList(resultList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                People people = null;
                if (searchEditText.getText().toString().equals("")) {

                    people = resultList.get(i);
                    if (people.phoneNumber.equals("")) {
                        return;
                    }
                    if (people.isCheck) {
                        people.isCheck = false;
                        mAdapter.notifyDataSetChanged();
                    } else {
                        people.isCheck = true;
                        mAdapter.notifyDataSetChanged();
                    }

                } else {
                    people = dymList.get(i);
                    if (people.phoneNumber.equals("")) {
                        return;
                    }

                    if (people.isCheck) {
                        people.isCheck = false;
                        for (People p : resultList){
                            if (people.phoneNumber.equals(p.phoneNumber)){
                                    p.isCheck = false;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        people.isCheck = true;
                        for (People p : resultList){
                            if (people.phoneNumber.equals(p.phoneNumber)){
                                p.isCheck = true;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }



                }



            }
        });
    }

    public void homeClick(View v) {
        this.finish();
    }

    //选中项count
    public int getCheckedItemCount() {
        return mListView.getCheckedItemCount();
    }

    //获取所有选中项
    public List<People> getCheckMemberList() {
        List<People> templist = new ArrayList<People>();
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.valueAt(i)) {
                int index = checkedItems.keyAt(i);
                People item = resultList.get(index);
                templist.add(item);
            }
        }
        return templist;
    }


    /**
     * 导入联系人
     */
    public void actionClick(View view) {

        List<People> list = new ArrayList<People>();
        if (searchEditText.getText().toString().equals("")){
            for (People p:resultList) {
                 if (p.phoneNumber.equals(""))
                 continue;
                list.add(p);
            }
        }else{
            for (People p:dymList) {
                if (p.phoneNumber.equals(""))
                    continue;
                list.add(p);
            }
        }

        if (list.size() <= 0) {
            showToast("未选中操作项");
            return;
        }

        showDialog();

        List<TempPeople> tempList = new ArrayList<TempPeople>();
        for (int j = 0; j < list.size(); j++) {

            Matcher matcher = PATTERN.matcher(list.get(j).phoneNumber);
            if (!matcher.matches()) {
                Toast.makeText(ContactPhoneActivity.this, list.get(j).name + "的电话号码格式不正确", Toast.LENGTH_SHORT).show();
                dimissDialog();
                return;
            }
            TempPeople tempPeople = new TempPeople(list.get(j).name, list.get(j).phoneNumber);
            tempList.add(tempPeople);
        }
        Gson gson = new Gson();
        String tempPeopleStr = gson.toJson(tempList);

        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(ContactPhoneActivity.this).getToken());
        params.put("storeId", SPUtil.getDefault(ContactPhoneActivity.this).getStoreId());
        params.put("data", tempPeopleStr);

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                //LogUtil.v(tag, "import member from cellphone : " + t);
                try {
                    String msg = data.get("msg").toString();
                    if (isSuccess) {
                        showToast(msg);
                        dimissDialog();
                        EventBus.getDefault().post(new RefreshEvent());
                        finish();
                    } else {
                        showToast(msg);
                        dimissDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                showToast(strMsg);
                dimissDialog();
            }
        };

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.get(Server.BASE_URL + Server.IMPORT_MEMBER_URL, params, callback);
    }
}