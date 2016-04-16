package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ChatActivity;
import com.meetrend.haopingdian.activity.GlobalSearchActivity;
import com.meetrend.haopingdian.activity.MsgSelectMemberActivity;
import com.meetrend.haopingdian.activity.MyGroupChatActivity;
import com.meetrend.haopingdian.adatper.NewMsgListAdapter;
import com.meetrend.haopingdian.bean.NewMessage;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.MessageEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NetUtil;
import com.meetrend.haopingdian.util.PxDipFormat;
import com.meetrend.swipemenulistview.SwipeMenu;
import com.meetrend.swipemenulistview.SwipeMenuCreator;
import com.meetrend.swipemenulistview.SwipeMenuItem;
import com.meetrend.swipemenulistview.SwipeMenuListView;
import com.meetrend.swipemenulistview.SwipeMenuListView.LoadMoreListener;
import com.meetrend.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.meetrend.swipemenulistview.SwipeMenuListView.RefreshListener;

import de.greenrobot.event.EventBus;

/**
 * 消息
 * @author 肖建斌
 *
 */
public class MessageListFragment extends BaseFragment implements RefreshListener {//,OnScrollListener
		
	private final static String TAG = MessageListFragment.class.getName();
	
	private final static int PAGE_SIZE = 30;

	private boolean isFirstIn = true;
	private boolean refresh = true;
	@ViewInject(id = R.id.iv_mass_msg, click = "onClickMassMsg")
	TextView mMassMsg;
	@ViewInject(id = R.id.lv_message_list)
	SwipeMenuListView mListView;
	@ViewInject(id = R.id.iv_store_name)
	TextView mStoreName;
	@ViewInject(id = R.id.msg_title_bar)
	RelativeLayout titlaRelativeyout;


	private int mPageIndex = 1;
	private int mPageCount = 0;
	private View headView;
	private RelativeLayout searchLayout;
	private boolean isCanLoadMore = true;
	private List<NewMessage> newMsgList ;
	private NewMsgListAdapter newListAdapter = null;
	private boolean isPollRefresh;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		showDialog();
		 
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_msglist, container,false);
		FinalActivity.initInjectedView(this, rootView);
		newMsgList = new ArrayList<NewMessage>();
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String name = App.storeInfo.storeName;
		 mStoreName.setText(name);//标题

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				/*SwipeMenuItem deleteItem = new SwipeMenuItem(
						mActivity.getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(dp2px(90));
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);*/
				
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						mActivity.getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));//红色背景
						
				deleteItem.setWidth(PxDipFormat.dip2px(getActivity(),90));
				//deleteItem.setIcon(R.drawable.ic_delete);
				deleteItem.setTitle("删 除");
				deleteItem.setTitleColor(Color.WHITE);
				deleteItem.setTitleSize(16);
				menu.addMenuItem(deleteItem);
			}
		};
		
		mListView.needLoadMore = true;
		mListView.setMenuCreator(creator);
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				
				NewMessage newMessage = newMsgList.get(index);
				switch (index) {
				case 0:
					if (!NetUtil.hasConnected(getActivity())) {
						showToast("请检查网络连接");
						return;
					}
					// 删除item
					if (newMessage.isgroup) {
						Log.i("----hh----","group");
						delGroupRecord(newMessage.groupId, index);
					} else {
						delItemMsg(newMessage.userId, newMessage);
					}
					break;
				}
			}
		});
		
		//加载更多接口回调
		mListView.setLoadMoreListener(mLoadMoreListener);

		newListAdapter = new NewMsgListAdapter(getActivity(), newMsgList);
		mListView.setAdapter(newListAdapter);
		
		// add headView
		headView = LayoutInflater.from(mActivity).inflate(
				R.layout.head_search_layout, null);
		searchLayout = (RelativeLayout) headView
				.findViewById(R.id.header_search_layout);
		searchLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// to do 动画


				Intent intent = new Intent(getActivity(),
						GlobalSearchActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
			}
		});
		
		mListView.addHeaderView(headView);

		mListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(msgItemClickListener);
		
		// 消息加载
		loadAllMsg();
	}

	//分页
	public LoadMoreListener mLoadMoreListener = new LoadMoreListener() {
		
		@Override
		public void load() {
			
			if (isCanLoadMore) {
				
				if (!NetUtil.hasConnected(getActivity())) {
					return;
				}
				
				++ mPageIndex;
				loadAllMsg();
			}
		}
	};
	
	//下拉刷新
	@Override
	public void onRefresh() {
		
		if (newMsgList.size() > 0) {
			newMsgList.clear();
		}

		mPageIndex = 1;
		
		loadAllMsg();
	}
	
	// 从聊天界面跳转到本界面，实现会话列表刷新的功能
	public void onEventMainThread(MessageEvent event) {
		
//		if (newMsgList.size() > 0) {
//			newMsgList.clear();
//		}
//		mPageIndex = 0;
//		loadAllMsg();
	}


	// 删除消息 MessageRecord messageRecord 要删除的对象
	private void delItemMsg(String userId, final NewMessage messageRecord) {
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("userId", userId);

		Callback callback = new Callback(tag, getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast(null == strMsg ? "删除失败" : strMsg);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				if (isSuccess) {
					newMsgList.remove(messageRecord);
					newListAdapter.setList(newMsgList);
					if (newMsgList.size() == 0) {

					}
					newListAdapter.notifyDataSetChanged();
				} else {
					String msg = data.get("msg").getAsString();
					showToast(msg);
				}
			}
		};
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.DELMSG, params, callback);
	}

	/**
	 * 
	 */
	private void delGroupRecord(String groupid,final int postion) {
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("groupId", groupid);// groupid

		Callback callback = new Callback(tag, getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast(null == strMsg ? "删除失败" : strMsg);
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				if (isSuccess) {
					//showToast("删除成功");
					newMsgList.remove(postion);
					newListAdapter.setList(newMsgList);
					newListAdapter.notifyDataSetChanged();
				} else {
					if (data.get("msg") != null) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
					}
					
				}
			}
		};
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.DEL_GMSG, params, callback);
	}

	/**
	 * 消息数据请求
	 * 
	 * */
	private void loadAllMsg() {

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("pageIndex", mPageIndex + "");
		params.put("pageSize", PAGE_SIZE + "");
		
		Callback callback =  new Callback(tag, getActivity()) {
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dimissDialog();

				refresh = false;
				if (mListView.getState() == SwipeMenuListView.REFRESHING) {
					mListView.onRefreshComplete();
				}
			}

			@Override
			public void onSuccess(String t) {
				
				dimissDialog();
				super.onSuccess(t);

					if (!isSuccess) {
						if (!data.has("msg")) {

						}else {

						}
					} else {
						
						String jsonArray = data.get("records").toString();
						Gson gson = new Gson();
						List<NewMessage> tlist = gson.fromJson(jsonArray,new TypeToken<List<NewMessage>>() {}.getType());
						
						mPageIndex = data.get("pageIndex").getAsInt();
						mPageCount = data.get("pageCount").getAsInt();
						
						if (mPageIndex == mPageCount || mPageCount == 0 || tlist.size() < PAGE_SIZE) {
							isCanLoadMore = false;
						}else {
							isCanLoadMore = true;
						}

						if (isPollRefresh){
							List<NewMessage> list = new ArrayList<NewMessage>();
							//区分群组消息和个人消息
							for (NewMessage msg : tlist) {
								if ("".equals(msg.userId)) {
									msg.isgroup = true;
								} else {
									msg.isgroup = false;
								}
								list.add(msg);
							}
							newListAdapter.setList(list);
							isPollRefresh = false;
						}else{
							for (NewMessage msg : tlist) {
								if ("".equals(msg.userId)) {
									msg.isgroup = true;
								} else {
									msg.isgroup = false;
								}
								newMsgList.add(msg);
							}

							if (mPageCount == 0 && null != newMsgList){
								newMsgList.clear();
							}
							newListAdapter.setList(newMsgList);
						}

						newListAdapter.notifyDataSetChanged();
						
						if (mListView.getState() == SwipeMenuListView.REFRESHING) {
							mListView.onRefreshComplete();
						}

						if (isFirstIn){
							isFirstIn = false;

							new Thread(){
								@Override
								public void run() {
									while (refresh){
										tsleep(8*1000);
										mPageIndex = 1;
										isPollRefresh = true;
										loadAllMsg();
									}
								}
							}.start();
						}
					}
					

			}
		};

		FinalHttp request = new CommonFinalHttp();
		request.get(Server.BASE_URL + Server.MESSAGE_LIST, params, callback);
	}


	private void tsleep(long time){
		try{
			Thread.sleep(time);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private OnItemClickListener msgItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			if (position < 2) {
				return;
			}
			
			NewMessage msg = (NewMessage) mListView.getItemAtPosition(position);
			
			if (null == msg) {
				return;
			}
			
			if (msg.isgroup) {// 群发
				Intent intent = new Intent(mActivity, MyGroupChatActivity.class);
				intent.putExtra("gid", msg.groupId);
				intent.putExtra("frommode", "1");// 来自消息列表
				intent.putExtra("title", msg.name);
				intent.putExtra("count", msg.presonNumber);
				mActivity.startActivity(intent);
			} else {
				// 单聊
				Intent intent = new Intent(mActivity, ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("user_id", msg.userId);
				bundle.putString("name", msg.name);
				bundle.putString("avatarId", msg.image);
				bundle.putBoolean("canTalk", msg.canTalk);
				intent.putExtras(bundle);
				mActivity.startActivity(intent);
			}
		}
	};

	// 群发功能
	public void onClickMassMsg(View v) {
		Intent intent = new Intent(mActivity,MsgSelectMemberActivity.class);
		mActivity.startActivity(intent);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
		refresh = false;
	}
}