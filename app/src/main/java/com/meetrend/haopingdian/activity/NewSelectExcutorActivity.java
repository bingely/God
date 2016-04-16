package com.meetrend.haopingdian.activity;

import java.util.HashMap;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.NewExecutorListAdapter;
import com.meetrend.haopingdian.bean.Executor;
import com.meetrend.haopingdian.bean.ExecutorEntity;
import com.meetrend.haopingdian.event.MemberInfoEvent;
import com.meetrend.haopingdian.event.ReFreshOrderExcutorEvent;
import com.meetrend.haopingdian.event.SendExcutorNameEvent;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;
import com.meetrend.tea.db.DbOperator;

import de.greenrobot.event.EventBus;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 *  新式 执行人选择
 *
 */
public class NewSelectExcutorActivity extends BaseActivity {
	
	private final static String TAG = NewSelectExcutorActivity.class.getName();

	public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int ALPHABET_LENGTH = ALPHABET.length();
	
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置

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
	
	//确定
	@ViewInject(id = R.id.iv_contact_add_contact, click = "crossClick")
	ImageView mCross;

	private List<Executor> mList;
	private NewExecutorListAdapter mAdapter;
	
	/**
	 * 从上个界面传递的执行人id
	 */
	private String exename;
	
	/**
	 * 选中的position
	 */
	private int selectPosition = -1;
	
	/**
	 * 来自哪个界面
	 * 1 标识 从店内下单进入
	 * 2 标识从订单详情进入
	 * 3从会员详情信息进入
	 */
	private int mSendSource;
	
	
			

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_executor);
		FinalActivity.initInjectedView(this);
		
		mTitle.setText("选择执行人");
		mAction.setText("确定");
		
		//将执行人数据保存值数据库
		DbOperator dbOperator = DbOperator.getInstance();
		List<ExecutorEntity> slist = dbOperator.getExecutorList(NewSelectExcutorActivity.this);
		mList = ExecutorEntity.convert(slist);
		if (mList == null) {
			Toast.makeText(NewSelectExcutorActivity.this, "数据异常", 200).show();
			return;
		}
		Intent intent = getIntent();
		exename = intent.getStringExtra("exename");//id
		mSendSource = intent.getIntExtra("from", -1);
		
		Log.i(TAG, exename);
		Log.i(TAG, mSendSource+"");
		
		if (mSendSource == -1) {
			Toast.makeText(NewSelectExcutorActivity.this, "数据异常", 200).show();
			return;
		}
		
		if (mSendSource == 3) {
			mTitle.setText("分配客户经理");
		}
		
		for (Executor executor : mList) {
			if (executor.entity != null) {
				if (executor.entity.id.equals(exename)) {
					 executor.isSelected = true;
				}
			}
		}
		
		mAdapter = new NewExecutorListAdapter(NewSelectExcutorActivity.this, mList);
		mListView.setAdapter(mAdapter);
		alphatSideBar.setVisibility(View.VISIBLE);
		
		init();
	}

	

	private void init() {
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					
				Executor executor = (Executor)mListView.getItemAtPosition(position);
				if (executor.isSelected) {
					executor.isSelected = false;
				}else {
					executor.isSelected = true;
					selectPosition = position;
					//其他的都为false
					for (int i = 0; i < mList.size(); i++) {
						if (i != position) {
							mList.get(i).isSelected = false;
						}
					}
				}
				
				mAdapter.notifyDataSetChanged();
			}
		});
		
		
		
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
				if(mList.get(i).entity != null){
					String name = mList.get(i).entity.userName;
					if(name.equals(exename)){
						SPUtil.getDefault(NewSelectExcutorActivity.this).savePotion(i);
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

	
	/**
	 * 确定操作
	 * 
	 */
	public void ActionClick(View v) {
		
		if (selectPosition < 0 || selectPosition > mList.size() -1) {
			return;
		}
		
		int count = 0;
		for (Executor executor : mList) {
			if (null != executor.entity && executor.isSelected) {
				++count; 
			}
		}
		
		if (count == 0) {
			Toast.makeText(NewSelectExcutorActivity.this, "请选则执行人！", 200).show();
			return;
		}
		
		
		//店内下单
		if (mSendSource == 1) {
			SendExcutorNameEvent event = new SendExcutorNameEvent();
			//提交选中的
			Executor executor = mList.get(selectPosition);
			
			String dealUserName = executor.entity.userName;
			String userId = executor.entity.id;
			event.setName(dealUserName);
			event.setId(userId);
			EventBus.getDefault().post(event);
		}
		
		//来自会员详情信息
		else if (mSendSource == 3) {
			
			//提交选中的
			Executor executor = mList.get(selectPosition);
			
			String dealUserName = executor.entity.userName;
			String userId = executor.entity.id;
			MemberInfoEvent memberInfoEvent = new MemberInfoEvent();
			memberInfoEvent.id = userId;
			memberInfoEvent.name = dealUserName;
			EventBus.getDefault().post(memberInfoEvent);
		}
		//订单详情跳转的
		else {
			Executor executor = mList.get(selectPosition);
			ReFreshOrderExcutorEvent reFreshOrderExcutorEvent = new ReFreshOrderExcutorEvent();
			reFreshOrderExcutorEvent.excutorId = executor.entity.id;
			reFreshOrderExcutorEvent.excutorName = executor.entity.userName;
			EventBus.getDefault().post(reFreshOrderExcutorEvent);
		}
		
		
		finish();
	}

	//back
	public void homeClick(View view) {
		NewSelectExcutorActivity.this.finish();
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}