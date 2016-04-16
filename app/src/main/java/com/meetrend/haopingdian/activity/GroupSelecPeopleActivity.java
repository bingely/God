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
import com.meetrend.haopingdian.adatper.GroupAdapter;
import com.meetrend.haopingdian.adatper.GroupSelectPeopleAdapter;
import com.meetrend.haopingdian.adatper.GroupSelectPeopleAdapter.CheckListItemListener;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.bean.MemberGroup;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.AddMemberEvent;
import com.meetrend.haopingdian.memberlistdb.MemberListDbOperator;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NetUtil;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;

import de.greenrobot.event.EventBus;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * 群组编辑创建选择会员界面
 * 
 * @author 肖建斌
 * 
 */
public class GroupSelecPeopleActivity extends BaseActivity {

	private final static int LOAD_DATA = 0x925;// 分页加载数据

	public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int ALPHABET_LENGTH = ALPHABET.length();

	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	private ArrayList<Member> memberList;

	private GroupSelectPeopleAdapter mAdapter;

	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;

	@ViewInject(id = R.id.lv_member)
	ListView mListView;

	@ViewInject(id = R.id.search_edit)
	EditText searchEditText;

	/**
	 * 添加
	 */
	@ViewInject(id = R.id.actionbar_action, click = "crossClick")
	TextView mCross;

	@ViewInject(id = R.id.tv_memberlist_emtpy)
	TextView mEmpty;

	@ViewInject(id = R.id.search_contact_phone)
	private SearchView mSearchView;

	@ViewInject(id = R.id.layout_contact_phone, click = "searchClick")
	private FrameLayout mSearchLayout;

	@ViewInject(id = R.id.memberlist_titlelayout)
	private RelativeLayout titleLayout;

	@ViewInject(id = R.id.alphat_layout)
	private SideBar alphatSideBar;
	@ViewInject(id = R.id.tv_alphabet_ui_tableview)
	private TextView alphatToast;

	//
	@ViewInject(id = R.id.no_searchlist_view)
	private TextView emptyHintView;

	private com.meetrend.haopingdian.tool.CharacterParser characterParser;

	private int groupMode = -1;// 群组模式
	private List<String> hasSelectMemberList;// 从添加群组成员界面传递党组的成员userId集合

	private ListView headListView;
	//private GroupAdapter groupAdapter;

	private SparseBooleanArray checkedItems;// 回调选中

	private int mCurPageIndex = 1;
	private int mPageCount;
	// 上次请求的时间戳
	private String lastRequestTime;
	// 客户端本地的数据
	private long mClienSize;

	/**
	 * 值为true 全部数据 值为false 增量数据 （修改 和 添加）
	 */
	private boolean isAllData;
	
	private List<Member> allDataList;
	private List<Member> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_conactselectpeople);
		FinalActivity.initInjectedView(this);

		//showDialog();

		allDataList = new ArrayList<Member>();
		mCross.setText("添加");
		mTitle.setText("选择联系人");
		memberList = new ArrayList<Member>();
		Intent intent = getIntent();

		try {
			groupMode = intent.getIntExtra(Code.GROUP_MODE, -1);// 是否是店小二
		} catch (Exception e) {
			e.printStackTrace();
			groupMode = -1;
		}

		try {
			hasSelectMemberList = intent
					.getStringArrayListExtra("memberNameList");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 字母条触摸事件
		alphatSideBar.setTextView(alphatToast);
		alphatSideBar
				.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

					@Override
					public void onTouchingLetterChanged(String key) {

						if (selector.containsKey(key)) {
							int pos = selector.get(key);
							if (mListView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有
								mListView.setSelectionFromTop(
										pos + mListView.getHeaderViewsCount(),
										0);
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
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable changeText) {

				if (!"".equals(changeText.toString())) {

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
					mAdapter.setList(memberList);
					mAdapter.notifyDataSetChanged();
					if (emptyHintView.getVisibility() == View.VISIBLE)
						emptyHintView.setVisibility(View.GONE);
				}
			}
		});
		
		
		if (NetUtil.hasConnected(GroupSelecPeopleActivity.this)) {
			lastRequestTime = SPUtil.getDefault(GroupSelecPeopleActivity.this)
					.getLastRequestTime();
			mClienSize = MemberListDbOperator.getInstance()
					.getSQLiteMemberListSize(GroupSelecPeopleActivity.this);
			requestCurrentMemberList();
		} else {
			// 没网的情况，加载数据库数据
			List<Member> list = MemberListDbOperator.getInstance()
					.getMemberList(GroupSelecPeopleActivity.this);
			mAdapter.setList(list);
			mAdapter.notifyDataSetChanged();
			dimissDialog();
		}

	}


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.SUCCESS: {
				dimissDialog();

				if (hasSelectMemberList != null
						&& hasSelectMemberList.size() > 0) {

					Member mMember = null;

					for (int i = 0; i < memberList.size(); i++) {
						for (int j = 0; j < hasSelectMemberList.size(); j++) {
							mMember = memberList.get(i);

							if (hasSelectMemberList.get(j).equals(
									mMember.userId)) {
								memberList.get(i).checkstatus = true;
							}
						}
					}
				}

				mAdapter = new GroupSelectPeopleAdapter(
						GroupSelecPeopleActivity.this, memberList, groupMode);
				mListView.setAdapter(mAdapter);
				mAdapter.setCheckListItemListener(new CheckListItemListener() {

					@Override
					public void checkItem(Member member) {

						for (Member m : memberList) {
							if (m.userId != null) {
								if (m.userId.equals(member.userId)) {
									m.checkstatus = member.checkstatus;
									break;
								}
							}
						}

					}
				});

			}
				break;
			case Code.FAILED:
				showToast("无法获取联系人数据");

				dimissDialog();
				// mWait.setReLoadVisibility(true);
				break;
			case Code.GROUP_LIST_EMPTY:
				showToast("群组数据为空");
				break;
//			case Code.GROUP_LIST_SUCCESS:
//				groupAdapter = new GroupAdapter(GroupSelecPeopleActivity.this,
//						groupList);
//				headListView.setAdapter(groupAdapter);
//				ListViewUtil.setListViewHeightBasedOnChildren(headListView);
//
//				dimissDialog();
//				break;
			}

		}
	};

	// 选中成员后添加操作
	public void crossClick(View v) {

		//if (memberList == null || memberList.size() == 0) {
		//	showToast("未选中操作项");
		//	return;
		//}

		List<Member> sendList = new ArrayList<Member>();

		Member mSendMember = null;

		for (int i = 0; i < memberList.size(); i++) {

			mSendMember = memberList.get(i);
			if (mSendMember.checkstatus == true) {
				sendList.add(mSendMember);
			}
		}
		
		if (sendList.size() == 0) {
			 showToast("请选择成员");
			return;
		}

		AddMemberEvent event = new AddMemberEvent(sendList);
		EventBus.getDefault().post(event);
		GroupSelecPeopleActivity.this.finish();
	}

	// 选中项count
	public int getCheckedItemCount() {
		return mListView.getCheckedItemCount();
	}

	// 获取所有选中项
	public List<Member> getCheckMemberList() {
		List<Member> list = new ArrayList<Member>();
		for (int i = 0; i < checkedItems.size(); i++) {
			if (checkedItems.valueAt(i)) {
				int index = checkedItems.keyAt(i);
				Member item = memberList.get(index);
				list.add(item);
			}
		}
		return list;
	}


	/**
	 * 获得联系人列表
	 * 
	 * */
	private void requestCurrentMemberList() {

		Callback callback = new Callback(tag, GroupSelecPeopleActivity.this) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (isSuccess) {

					String jsonArrayStr = data.get("argsArray").toString();
					mPageCount = data.get("pageCount").getAsInt();
					lastRequestTime = data.get("lastRequestTime").getAsString();
					isAllData = data.get("isAllData").getAsBoolean();
					SPUtil.getDefault(GroupSelecPeopleActivity.this)
							.saveLastRequestTime(lastRequestTime);

					Gson gson = new Gson();
					List<Member> list = gson.fromJson(jsonArrayStr,new TypeToken<List<Member>>() {}.getType());

					// 为点小二，则取出已经绑定会员集合
					List<Member> afterList = null;
					if (groupMode == Code.ASSITANT) {
						ArrayList<Member> tempList = new ArrayList<Member>();
						for (Member member : list) {
							if (member.status.equals("2")) {
								tempList.add(member);
							}
						}

						afterList = tempList;

					} else {

						afterList = list;
					}

					if (isAllData == true) {
						
						allDataList.addAll(afterList);
						
						memberList.addAll(afterList);// 对象Member的pinyinName字段为空

						// 如果当前的分页数小于总页数，则开始加载数据
						if (mCurPageIndex < mPageCount) {
							mHandler.sendEmptyMessage(LOAD_DATA);
						}

						// 如果是全量数据，则清除掉数据库中的数据
						if (mCurPageIndex >= mPageCount) {
							MemberListDbOperator.getInstance()
									.clearMemberDatas(
											GroupSelecPeopleActivity.this);// 清空数据库数据
							MemberListDbOperator.getInstance()
									.saveMembers(GroupSelecPeopleActivity.this,
											memberList);// 将有所最新的数据保存值本地数据库
							mClienSize = MemberListDbOperator
									.getInstance().getSQLiteMemberListSize(
											GroupSelecPeopleActivity.this);// 获取数据库记录的总条数
						}
						
						//排序
						memberList = Member.convert(allDataList);
						
						int size = memberList.size();
						selector = new HashMap<String, Integer>();
						for (int i = 0; i < size; ++i) {
							for (int j = 0; j < ALPHABET_LENGTH; j++) {
								String str = ALPHABET.charAt(j) + "";
								if (memberList.get(i).pinyinName.equals(str)) {
									selector.put(str, i); // 值 ：位置
								}
							}
						}

					} else {
						mClienSize = MemberListDbOperator
								.getInstance().getSQLiteMemberListSize(
										GroupSelecPeopleActivity.this);// 获取数据库记录的总条数
						// 遍历数据库,若是数据库中查不到该记录，则添加，若能找到该记录则删除，并更新
						for (Member member : afterList) {

							if (MemberListDbOperator.getInstance()
									.findOneMember(
											GroupSelecPeopleActivity.this,
											member.userId)) {
								// 删除该条信息，
								MemberListDbOperator.getInstance()
										.clearOndMember(
												GroupSelecPeopleActivity.this,
												member);
								// 插入新的数据
								MemberListDbOperator.getInstance()
										.saveOneMember(
												GroupSelecPeopleActivity.this,
												member);
							} else {
								// 插入新的数据
								MemberListDbOperator.getInstance()
										.saveOneMember(
												GroupSelecPeopleActivity.this,
												member);
							}
						}

						memberList = (ArrayList<Member>) MemberListDbOperator
								.getInstance().getMemberList(
										GroupSelecPeopleActivity.this);
						
						//排序
						memberList = Member.convert(memberList);
						
						int size = memberList.size();
						selector = new HashMap<String, Integer>();
						for (int i = 0; i < size; ++i) {
							for (int j = 0; j < ALPHABET_LENGTH; j++) {
								String str = ALPHABET.charAt(j) + "";
								if (memberList.get(i).pinyinName.equals(str)) {
									selector.put(str, i); // 值 ：位置
								}
							}
						}
						
						// 排序
						//memberList = Member.convert(memberList);
					}
					mHandler.sendEmptyMessage(Code.SUCCESS);

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
		params.put("token", SPUtil.getDefault(GroupSelecPeopleActivity.this)
				.getToken());
		params.put("storeId", SPUtil.getDefault(GroupSelecPeopleActivity.this)
				.getStoreId());
		params.put("keyword", "");
		params.put("pageSize", "500");
		params.put("pageIndex", mCurPageIndex + "");
		params.put("lastRequestTime",
				SPUtil.getDefault(GroupSelecPeopleActivity.this)
						.getLastRequestTime());// 上次请求的时间戳
		params.put("clientSize", mClienSize + "");

		CommonFinalHttp http = new CommonFinalHttp();
		http.get(Server.BASE_URL + Server.APP_MEMBER_USER_LIST, params,
				callback);
	}

	/**
	 * 返回
	 * 
	 * @author bob
	 * */
	public void homeClick(View v) {
		GroupSelecPeopleActivity.this.finish();
	}

}