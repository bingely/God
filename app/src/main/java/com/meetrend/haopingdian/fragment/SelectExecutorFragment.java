package com.meetrend.haopingdian.fragment;

import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.SelectExcutorActivity;
import com.meetrend.haopingdian.adatper.ExecutorListAdapter;
import com.meetrend.haopingdian.bean.Executor;
import com.meetrend.haopingdian.bean.ExecutorEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.ExecutorSelectEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;

import de.greenrobot.event.EventBus;

/**
 * 选择执行人 Fragment
 * @author 肖建斌
 *
 */
public class SelectExecutorFragment extends BaseFragment {
	
	public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int ALPHABET_LENGTH = ALPHABET.length();
	private int position = -1;
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置

	String page;
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;

	@ViewInject(id = R.id.actionbar_action, click = "ActionClick")
	TextView mAction;

	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;

	@ViewInject(id = R.id.listview_executor)
	ListView mListView;
	
	@ViewInject(id = R.id.iv_contact_add_contact, click = "crossClick")
	ImageView mCross;
	
	//字母索引
	@ViewInject(id = R.id.alphat_layout)
	private SideBar alphatSideBar;
	@ViewInject(id = R.id.tv_alphabet_ui_tableview)
	private TextView alphatToast;

	private List<Executor> mList;
	private ExecutorListAdapter mAdapter;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		mActivity = (FragmentActivity) this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_select_executor,
				container, false);
		FinalActivity.initInjectedView(this, rootView);
		showDialog();
		
		mList = ExecutorEntity.convert(App.executorList);
		initView();
		alphatSideBar.setVisibility(View.VISIBLE);
		dimissDialog();
		return rootView;
	}


	public void onEventMainThread(ExecutorSelectEvent event) {
		
	}

	private void initView() {
		mTitle.setText("选择执行人");
		mAction.setText("确定");
		mAdapter = new ExecutorListAdapter(mActivity, mList, mHandler,"changes");
		mListView.setAdapter(mAdapter);
		Bundle bundle = getArguments();
		if(bundle != null){
			page = bundle.getString("page");
		}
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


	public void ActionClick(View v) {
		try {
			if(TextUtils.isEmpty(page)){
				
			}else{
				if(page.equals("orderinfo")){
					AjaxParams params = new AjaxParams();
					params.put("token", SPUtil.getDefault(getActivity()).getToken());
					//params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
					params.put("orderId", App.onlineOrderDetail.id);
					if(position == -1){
						showToast("必须选择执行人");
						EventBus.getDefault().post(new ExecutorSelectEvent(""));
						App.onlineOrderDetail.executeUserId = "";
						params.put("executeUserId", "");
					}else{
						Executor executor = mList.get(position);
						App.onlineOrderDetail.executeUserName = executor.entity.userName;
						params.put("executeUserId", executor.entity.id);
						EventBus.getDefault().post(new ExecutorSelectEvent(executor.entity.id));
						mActivity.getSupportFragmentManager().popBackStack();
					}
			
					Callback callback = new Callback(tag,getActivity()) {
			
						@Override
						public void onSuccess(String t) {
							super.onSuccess(t);
							LogUtil.d(tag, "transfer info : " + t);
							if (!isSuccess) {
								if(position == -1){
									showToast("必须选择执行人");
								}else{
									showToast("操作失败");
								}
							} else {
								mActivity.getSupportFragmentManager().popBackStack();
							}
						}
			
						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							showToast("操作失败");
						}
			
					};
			
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.get(Server.BASE_URL + Server.CHANGE_EXECUTOR, params,
							callback);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		if(position == -1){
			showToast("必须选择执行人");
			EventBus.getDefault().post(new ExecutorSelectEvent(""));
			App.onlineOrderDetail.executeUserId = "";
		}else{
			Executor executor = mList.get(position);
			App.onlineOrderDetail.executeUserName = executor.entity.userName;
			EventBus.getDefault().post(new ExecutorSelectEvent(executor.entity.id));
			mActivity.getSupportFragmentManager().popBackStack();
		}
	}

	public void homeClick(View view) {
		mActivity.getSupportFragmentManager().popBackStack();
	}

	@Override
	public void processHandleMessage(Message msg) {
		super.processHandleMessage(msg);
		position = msg.what;
		mAdapter.setDate(position);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}
