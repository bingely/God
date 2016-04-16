package com.meetrend.haopingdian.activity;

import java.util.HashMap;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.ExecutorListAdapter;
import com.meetrend.haopingdian.bean.Executor;
import com.meetrend.haopingdian.bean.ExecutorEntity;
import com.meetrend.haopingdian.event.ExecutorSelectEvent;
import com.meetrend.haopingdian.event.SendExcutorNameEvent;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;
import com.meetrend.tea.db.DbOperator;

import de.greenrobot.event.EventBus;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 *  执行人选择
 *
 */
public class SelectExcutorActivity extends BaseActivity {

	// 字母条
	//private static final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
	public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int ALPHABET_LENGTH = ALPHABET.length();
	private int position;
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置

	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_action, click = "ActionClick")
	TextView mAction;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;

	@ViewInject(id = R.id.listview_executor)
	ListView mListView;
	
	//字母索引
	@ViewInject(id = R.id.alphat_layout)
	private SideBar alphatSideBar;
	@ViewInject(id = R.id.tv_alphabet_ui_tableview)
	private TextView alphatToast;
	
	@ViewInject(id = R.id.iv_contact_add_contact, click = "crossClick")
	ImageView mCross;

	private List<Executor> mList;
	private ExecutorListAdapter mAdapter;
	
	private String exename;//上一个界面的达到的执行人

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_executor);
		FinalActivity.initInjectedView(this);
		
		//将执行人数据保存值数据库
		DbOperator dbOperator = DbOperator.getInstance();
		List<ExecutorEntity> slist = dbOperator.getExecutorList(SelectExcutorActivity.this);
		if (mList == null) {
			  Toast.makeText(SelectExcutorActivity.this, "数据异常", 200).show();
			return;
		}
		
		exename = getIntent().getStringExtra("exename");
		mList = ExecutorEntity.convert(slist);
		
		
		initView();
	}

	

	private void initView() {
		
		mTitle.setText("选择执行人");
		mAction.setText("确定");
		mAdapter = new ExecutorListAdapter(SelectExcutorActivity.this, mList,mHandler,"");
		mListView.setAdapter(mAdapter);
		alphatSideBar.setVisibility(View.VISIBLE);
		
		
		
		selector = new HashMap<String, Integer>();
		int excutorCount = mList.size();
		
		for (int i = 0; i < excutorCount; ++i) {
			for (int j = 0; j < ALPHABET_LENGTH; j++) {
				String str = ALPHABET.charAt(j)+"";
				if (mList.get(i).pinyinName.equals(str)) {
					selector.put(str, i);
					break;
				}
			}

		}
		
		if(!TextUtils.isEmpty(exename)){
			for(int i = 0;i<mList.size();i++){
				if(mList.get(i).entity!=null){
					String name = mList.get(i).entity.userName;
					if(name.equals(exename)){
						SPUtil.getDefault(SelectExcutorActivity.this).savePotion(i);
						break;
					}
				}
			}
		}
		
		
		//字母条的touch事件
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
				}else {
					//滑动到搜索栏
					mListView.smoothScrollToPosition(-1);
				}
			}
		});
		
	}


	public void homeClick(View view) {
		SelectExcutorActivity.this.finish();
	}

	/**
	 * 确定操作
	 * @param v
	 */
	public void ActionClick(View v) {
		SendExcutorNameEvent event = new SendExcutorNameEvent();
		
		if(position == -1 || position == 0){
			Executor executor = mList.get(position);
			event.setId(executor.entity.id);
			event.setName(executor.entity.userName);
			EventBus.getDefault().post(event);
			finish();
			return;
		}
		
		Executor executor = mList.get(position);
		String dealUserName = executor.entity.userName;
		String userId = executor.entity.id;
		event.setName(dealUserName);
		event.setId(userId);
		EventBus.getDefault().post(event);
		finish();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		position = msg.what;
		SPUtil.getDefault(SelectExcutorActivity.this).savePotion(position);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}