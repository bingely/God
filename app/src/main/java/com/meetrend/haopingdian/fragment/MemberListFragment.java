package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ConnactSearchListActivity;
import com.meetrend.haopingdian.activity.ContactPhoneActivity;
import com.meetrend.haopingdian.activity.MemberAddActivity;
import com.meetrend.haopingdian.activity.MemberInfoActivity;
import com.meetrend.haopingdian.activity.MemberGroupActivity;
import com.meetrend.haopingdian.adatper.MemberListAdapter;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.memberlistdb.MemberListDbOperator;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NetUtil;
import com.meetrend.haopingdian.widget.SideBar;
import com.meetrend.haopingdian.widget.SideBar.OnTouchingLetterChangedListener;
import com.meetrend.swipemenulistview.SwipeMenu;
import com.meetrend.swipemenulistview.SwipeMenuCreator;
import com.meetrend.swipemenulistview.SwipeMenuItem;
import com.meetrend.swipemenulistview.SwipeMenuListView;
import com.meetrend.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.meetrend.swipemenulistview.SwipeMenuListView.RefreshListener;

import de.greenrobot.event.EventBus;

/**
 * 
 * 通讯录
 * 
 */
public class MemberListFragment extends BaseFragment implements RefreshListener{

	public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int ALPHABET_LENGTH = ALPHABET.length();
	
	private final static int LOAD_DATA = 0x925;//分页加载数据
	
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mBarHome;
	
	@ViewInject(id = R.id.lv_member)
	SwipeMenuListView mSwipeListView;

	@ViewInject(id = R.id.iv_contact_add_contact, click = "crossClick")
	TextView mCross;
	
	//添加
	@ViewInject(id = R.id.bottom_member_added)
	RelativeLayout mMemberAdd;
	
	@ViewInject(id = R.id.alphat_layout)
	private SideBar alphatSideBar;
	@ViewInject(id = R.id.tv_alphabet_ui_tableview)
	private TextView alphatToast;
	
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	private ArrayList<Member> memberList;
	private MemberListAdapter mAdapter;
	private PopupWindow mPopupMenu;
	
	// 分页（目前不用实现）
	private int mCurrentIndex = 1;
	private int mPageCount;
	private View footerView;
	private TextView mNumView;

	private int showMode = -1;// 通讯录的显示样式

	// 群组
	private View headView;
	RelativeLayout searchLayout;
	private RelativeLayout connactGoupLayout;
	
	//上次请求的时间戳
	private String lastRequestTime;
	//客户端本地的数据
	private long mClienSize;
	
	
	/**
	 * 值为true 全部数据
	 * 值为false 增量数据 （修改 和 添加）
	 */
	private boolean isAllData;
	
	private List<Member> allDataList = new ArrayList<Member>();
	
	//private View msgFooterView;
	//private TextView footerHintView;
	
	private View notNetLayoutView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_memberlist,container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		 //msgFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.msg_emptyview_layout, null);
		 //footerHintView = (TextView) msgFooterView.findViewById(R.id.msg_foot_hintview);
		 
		 notNetLayoutView  = LayoutInflater.from(getActivity()).inflate(R.layout.no_net_layout, null);
		
		return rootView;
	}
	
	//下拉刷新回调
	@Override
	public void onRefresh() {
		
		if (memberList.size() > 0) {
			memberList.clear();
		}
		
		if (allDataList.size() > 0) {
			allDataList.clear();
		}
		
		mCurrentIndex = 1;
		requestCurrentMemberList();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
			
		showDialog();
		
		memberList = new ArrayList<Member>();
		selector = new HashMap<String, Integer>();
		
		mBarHome.setVisibility(View.GONE);
		
		Bundle bundle = getArguments();
		try {
			showMode = bundle.getInt(Code.MODE);// 显示模式的标识
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 群组列表
		headView = LayoutInflater.from(mActivity).inflate(
				R.layout.connact_header_layout, null);
		connactGoupLayout = (RelativeLayout) headView
				.findViewById(R.id.connact_group_layout);
		connactGoupLayout.setVisibility(View.VISIBLE);
		
		searchLayout = (RelativeLayout) headView
				.findViewById(R.id.header_search_layout);
		searchLayout.setVisibility(View.VISIBLE);

		connactGoupLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, MemberGroupActivity.class);
				startActivity(intent);
			}
		});
		
			footerView = LayoutInflater.from(mActivity).inflate(
				R.layout.connact_listview_item_layout, mSwipeListView, false);
		mNumView = (TextView) footerView
				.findViewById(R.id.show_connact_num_view);
		mNumView.setOnClickListener(null);

		//添加下拉刷新view
		mSwipeListView.addHeaderView(headView);
		mSwipeListView.addFooterView(footerView);
		
		mSwipeListView.setOnRefreshListener(this);
		
		alphatSideBar.setTextView(alphatToast);
		alphatSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String key) {
				
				if (null == selector || selector.size() == 0) {
					return;
				}
				
				if (selector.containsKey(key)) {
					int pos = selector.get(key);
					if (mSwipeListView.getHeaderViewsCount() > 0) {
						mSwipeListView.setSelection(pos + mSwipeListView.getHeaderViewsCount());

					} else {
						mSwipeListView.setSelection(pos);// 滑动到第一项
					}
				}else {
					//滑动到搜索栏
					mSwipeListView.smoothScrollToPosition(-1);
				}
			}
		});

		mSwipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position < 2) {
					return;
				}
				
				Member member = (Member) mSwipeListView.getItemAtPosition(position);
				if (member == null) {
					return;
				}
				
				Intent intent = new Intent(mActivity, MemberInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", member.customerName);
				bundle.putString("id", member.userId);
				intent.putExtras(bundle);
				mActivity.startActivity(intent);
			}
		});

		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {

				SwipeMenuItem deleteItem = new SwipeMenuItem(
						mActivity.getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));//红色背景
						
				deleteItem.setWidth(dp2px(90));
				//deleteItem.setIcon(R.drawable.ic_delete);
				deleteItem.setTitle("删 除");
				deleteItem.setTitleColor(Color.WHITE);
				deleteItem.setTitleSize(16);
				menu.addMenuItem(deleteItem);
			}
		};

		
		mSwipeListView.setMenuCreator(creator);

		mSwipeListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				Member member = (Member) mAdapter.getItem(position);
				switch (index) {
				case 0:
					
					if (!NetUtil.hasConnected(getActivity())) {
						showToast("请检查网络连接");
						return;
					}
					
					if (member.status.equals("2")) {
						showToast("已绑定会员不能删除");
						return;
					}
					deleteItem(member);
					break;
				}
			}
		});


		searchLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ConnactSearchListActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
			}
		});
		
		mAdapter = new MemberListAdapter(mActivity, memberList,showMode);
		mSwipeListView.setAdapter(mAdapter);
		
		
		if (NetUtil.hasConnected(getActivity())) {
			//MemberListDbOperator.getInstance().clearMemberDatas(getActivity());//清空数据库数据
			lastRequestTime = SPUtil.getDefault(getActivity()).getLastRequestTime();
			mClienSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(getActivity());
			requestCurrentMemberList();
		}else {
			
			//没网的情况，加载数据库数据
			List<Member> list = MemberListDbOperator.getInstance().getMemberList(getActivity());
			
			mNumView.setText(list.size()+"位联系人");
			
			int membersCount = memberList.size();
			for (int i = 0; i < membersCount; ++i) {

				for (int j = 0; j < ALPHABET_LENGTH; j++) {
					String str = ALPHABET.charAt(j)+"";
					if (memberList.get(i).pinyinName.equals(str)) {
						selector.put(str, i);
						break;
					}
				}
			}
			
			if (!NetUtil.hasConnected(getActivity())) {
				 mSwipeListView.removeHeaderView(notNetLayoutView);
				 mSwipeListView.addHeaderView(notNetLayoutView);
				 notNetLayoutView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=null;
		                //判断手机系统的版本  即API大于10 就是3.0或以上版本 
		                if(android.os.Build.VERSION.SDK_INT>10){
		                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		                }else{
		                    intent = new Intent();
		                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
		                    intent.setComponent(component);
		                    intent.setAction("android.intent.action.VIEW");
		                }
		                
		                getActivity().startActivity(intent);
					}
				});
			}
			
			mAdapter.setList(list);
			mAdapter.notifyDataSetChanged();
			dimissDialog();
		}
	}


	public void onEventMainThread(RefreshEvent event) {
		
		showDialog();
		
		if (memberList.size() > 0) {
			memberList.clear();
		}
		
        mCurrentIndex = 1;
        
        mClienSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(getActivity());//获取数据库记录的总条数
		requestCurrentMemberList();
	}
	
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			++ mCurrentIndex;
			requestCurrentMemberList();
		}
	};
	
	
	private void  requestCurrentMemberList() {

		Callback callback = new Callback(tag, getActivity()) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (isSuccess) {
					
					String jsonArrayStr = data.get("argsArray").toString();
					mPageCount = data.get("pageCount").getAsInt();
					lastRequestTime = data.get("lastRequestTime").getAsString();
					isAllData = data.get("isAllData").getAsBoolean();
					SPUtil.getDefault(getActivity()).saveLastRequestTime(lastRequestTime);
					
					//Log.i("------------MemberListFragment----lastRequestTime---", lastRequestTime);
					//Log.i("------------MemberListFragment----isAllData---", isAllData + "");
					
					Gson gson = new Gson();
					List<Member> list = gson.fromJson(jsonArrayStr,new TypeToken<List<Member>>() {}.getType());
					
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
							MemberListDbOperator.getInstance().clearMemberDatas(getActivity());//清空数据库数据
							MemberListDbOperator.getInstance().saveMembers(getActivity(), allDataList);//将有所最新的数据保存值本地数据库
							mClienSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(getActivity());//获取数据库记录的总条数
							dimissDialog();
						}
						
						//排序
						memberList = Member.convert(allDataList);
						
						int membersCount = memberList.size();
						for (int i = 0; i < membersCount; ++i) {

							for (int j = 0; j < ALPHABET_LENGTH; j++) {
								String str = ALPHABET.charAt(j)+"";
								if (memberList.get(i).pinyinName.equals(str)) {
									selector.put(str, i);
									break;
								}
							}
						}
						
						mAdapter.setList(memberList);
						mNumView.setText(mClienSize+ "位联系人");
						mAdapter.notifyDataSetChanged();
						
					}else {
						mClienSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(getActivity());//获取数据库记录的总条数
						//遍历数据库,若是数据库中查不到该记录，则添加，若能找到该记录则删除，并更新
						for (Member member : list) {
							
							if (MemberListDbOperator.getInstance().findOneMember(getActivity(), member.userId)) {
								//删除该条信息，
								MemberListDbOperator.getInstance().clearOndMember(getActivity(), member);
								//插入新的数据
								MemberListDbOperator.getInstance().saveOneMember(getActivity(), member);
							}else {
								//插入新的数据
								MemberListDbOperator.getInstance().saveOneMember(getActivity(), member);
							}
						}	
						
						memberList = (ArrayList<Member>) MemberListDbOperator.getInstance().getMemberList(getActivity());
						mNumView.setText(memberList.size()+ "位联系人");
						
						//排序
						memberList = Member.convert(memberList);
						
						int membersCount = memberList.size();
						for (int i = 0; i < membersCount; ++i) {

							for (int j = 0; j < ALPHABET_LENGTH; j++) {
								String str = ALPHABET.charAt(j)+"";
								if (memberList.get(i).pinyinName.equals(str)) {
									selector.put(str, i);
									break;
								}
							}
						}
						
						
						mAdapter.setList(memberList);
						mAdapter.notifyDataSetChanged();
						dimissDialog();
					}
					
					 mSwipeListView.removeHeaderView(notNetLayoutView);
					//刷新完毕
					if (mSwipeListView.getState() == SwipeMenuListView.REFRESHING) {
						mSwipeListView.onRefreshComplete();
					}
					
					
					
				} else {
					
					if (data.has("msg")) {
						showToast(data.get("msg").getAsString());
					}else {
						showToast("加载数据失败");
					}
				}
				
				
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
					
					dimissDialog();
					
					memberList = (ArrayList<Member>) MemberListDbOperator.getInstance().getMemberList(getActivity());
					mNumView.setText(memberList.size()+ "位联系人");
					
					//排序
					memberList = Member.convert(memberList);
					
					int membersCount = memberList.size();
					for (int i = 0; i < membersCount; ++i) {

						for (int j = 0; j < ALPHABET_LENGTH; j++) {
							String str = ALPHABET.charAt(j)+"";
							if (memberList.get(i).pinyinName.equals(str)) {
								selector.put(str, i);
								break;
							}
						}
					}
					
					mNumView.setText(MemberListDbOperator.getInstance().getSQLiteMemberListSize(getActivity())+ "位联系人");
					mAdapter.setList(memberList);
					mAdapter.notifyDataSetChanged();
					
					if (mSwipeListView.getState() == SwipeMenuListView.REFRESHING) {
						mSwipeListView.onRefreshComplete();
					}
					
					if (footerView.getVisibility() == View.VISIBLE) {
						footerView.setVisibility(View.GONE);
					}
					
					//if (null != strMsg) {
					//	showToast(strMsg);
					//}
					
					if (mSwipeListView.getState() == SwipeMenuListView.REFRESHING) {
						mSwipeListView.onRefreshComplete();
					}
					
					if (!NetUtil.hasConnected(getActivity())) {
						 mSwipeListView.removeHeaderView(notNetLayoutView);
						 mSwipeListView.addHeaderView(notNetLayoutView);
						 return;
					}
			}
		};

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("keyword", "");
		params.put("pageSize", "500");
		params.put("pageIndex", mCurrentIndex + "");
		params.put("lastRequestTime", SPUtil.getDefault(getActivity()).getLastRequestTime());//上次请求的时间戳
		long memberSize = MemberListDbOperator.getInstance().getSQLiteMemberListSize(getActivity());
		params.put("clientSize",  memberSize + "");
		

		CommonFinalHttp http = new CommonFinalHttp();
		http.get(Server.BASE_URL + Server.APP_MEMBER_USER_LIST, params,callback);
				
	}
	

	public void searchClick(View v) {
		// 通讯录搜索
		Intent intent = new Intent(getActivity(),
				ConnactSearchListActivity.class);
		startActivity(intent);
	}

	/**
	 * 群组的oclick envent
	 */
	public void onClickGroup(View v) {
		Intent intent = new Intent(mActivity, MemberGroupActivity.class);
		mActivity.startActivity(intent);
	}

	/**
	 * 返回
	 * 
	 * */
	public void homeClick(View v) {
		mActivity.finish();
	}

	/**
	 * popwindow的操作
	 * */
	public void crossClick(View v) {
		if (mPopupMenu == null) {
			View view = LayoutInflater.from(mActivity).inflate(
					R.layout.pop_layout, null);
			TextView addMySelfBtn = (TextView) view
					.findViewById(R.id.add_myself_btn);// 手动添加
			TextView addFromMobileBtn = (TextView) view
					.findViewById(R.id.add_frommobile_btn);// 从通讯录导入
			
			//手动添加
			addMySelfBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mPopupMenu.isShowing()) {
						mPopupMenu.dismiss();
					}
					Intent intent = new Intent(mActivity,
							MemberAddActivity.class);
					
					mActivity.startActivity(intent);
				}
			});

			//从通讯录导入
			addFromMobileBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (mPopupMenu.isShowing()) {
						mPopupMenu.dismiss();
					}
					
					if (App.memberPhoneList != null) {
						App.memberPhoneList.clear();
					}
					
					App.memberPhoneList = new ArrayList<String>();
					int memberSize = memberList.size();
					Member mMember = null;
					for (int i = 0; i < memberSize; i++) {
						mMember = memberList.get(i);
						App.memberPhoneList.add(mMember.mobile);
					}
					
					Intent intent = new Intent(mActivity,ContactPhoneActivity.class);
					mActivity.startActivity(intent);
					
				}
			});

			mPopupMenu = new PopupWindow(view,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			mPopupMenu.setTouchable(true);
			mPopupMenu.setFocusable(true);
			mPopupMenu.setOutsideTouchable(true);
			mPopupMenu.setBackgroundDrawable(new BitmapDrawable());
			mPopupMenu.showAsDropDown(v);
		} else {
			mPopupMenu.showAsDropDown(v);
		}
	}

	// 删除
	private void deleteItem(final Member member) {

		showDialog();

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		String id = member.memberId;
		params.put("id", id);

		Callback callback = new Callback(tag, getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				try {
					
					if (!isSuccess) {

						if (data.has("msg")) {
							showToast(data.get("msg").toString());
							dimissDialog();
							return;
						}

						showToast("删除会员失败");
						dimissDialog();
						return;
					}

					String flag = data.get("operationState").getAsString();
					if (flag.equalsIgnoreCase("success")) {
						
						MemberListDbOperator.getInstance().clearOndMember(getActivity(), member);//删除该条记录
						memberList = (ArrayList<Member>) MemberListDbOperator.getInstance().getMemberList(getActivity());
						mNumView.setText(memberList.size()+"位联系人");

						//重新分组
						memberList = Member.convert(memberList);

						mAdapter.setList(memberList);
						mAdapter.notifyDataSetChanged();
						dimissDialog();
						
					} else {
						showToast("删除会员失败");
						dimissDialog();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast("删除会员失败");
			}
		};
		CommonFinalHttp http = new CommonFinalHttp();
		http.get(Server.BASE_URL + Server.DELETE_MEMBER_URL, params, callback);
	}
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,getResources().getDisplayMetrics());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}