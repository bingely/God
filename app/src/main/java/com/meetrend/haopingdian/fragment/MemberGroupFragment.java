package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.GroupModifyActivity;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.bean.MemberGroup;
import com.meetrend.haopingdian.bean.MemberGroupInfo;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.GroupCreatedEvent;
import com.meetrend.haopingdian.event.RefreshGropListEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NetUtil;
import com.meetrend.swipemenulistview.SwipeMenu;
import com.meetrend.swipemenulistview.SwipeMenuCreator;
import com.meetrend.swipemenulistview.SwipeMenuItem;
import com.meetrend.swipemenulistview.SwipeMenuListView;
import com.meetrend.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.meetrend.swipemenulistview.SwipeMenuListView.RefreshListener;

import de.greenrobot.event.EventBus;

/**
 * 群组列表Fragment
 * 
 * @author joy
 * 
 */
public class MemberGroupFragment extends BaseFragment  implements RefreshListener{

	private final static String TAG = MemberGroupFragment.class.getSimpleName();

	//
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	//
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	//
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	//
	@ViewInject(id = R.id.lv_group_member_list)
	SwipeMenuListView mListView;
	//
	
	@ViewInject(id = R.id.listview_empty_layout)
	LinearLayout emptyLayout;

	List<MemberGroup> list = new ArrayList<MemberGroup>();
	MemberGroupListAdapter mAdapter = null;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_member_group,
				container, false);
		FinalActivity.initInjectedView(this, rootView);
		showDialog();
		
		mBarTitle.setText(R.string.title_group);
		mBarAction.setText(R.string.title_created);

		mAdapter = new MemberGroupListAdapter(mActivity);
		mListView.setAdapter(mAdapter);

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				/*// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						mActivity.getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);*/
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
		mListView.setMenuCreator(creator);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				
				MemberGroup memberGroup = (MemberGroup)mListView.getAdapter().getItem(position);
				
				//是系统分组
				if (memberGroup.isSystemTag) {
					if (memberGroup.userArray.size() > 0) {
						
						ArrayList<Member> list =  new ArrayList<Member>();
						for (int i = 0; i < memberGroup.userArray.size(); i++) {
							MemberGroupInfo memberGroupInfo = memberGroup.userArray.get(i);
							
							 Member member = new Member(memberGroupInfo.avatarId,memberGroupInfo.id, memberGroupInfo.name,memberGroupInfo.type);
							 //Member tempMember = new Member(innerMember, 1);
							 list.add(member);
						}
						App.members = list;
						Intent intent = new Intent(getActivity(), GroupModifyActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("issystem", 1);
						bundle.putString("tagName", memberGroup.tagName);
						intent.putExtras(bundle);
						startActivity(intent);
					}
					
				}else {
					
					Intent intent = new Intent(mActivity, GroupModifyActivity.class);
					Bundle bundle = new Bundle();
					memberGroup.setUserArray(null);// 序列化对象中不能集合属性
					bundle.putSerializable("group", memberGroup);
					bundle.putInt(Code.EDIT_TYPE, Code.UPDATE_GROUP);// 编辑模式
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
			
		});

		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {

				MemberGroup memberGroup = (MemberGroup) mAdapter
						.getItem(position);
				switch (index) {
				case 0:
					
					if (!NetUtil.hasConnected(getActivity())) {
						showToast("请检查网络连接");
						return;
					}
					
					if (memberGroup.tagName.equals("店小二")) {
						showToast("不能删除店小二");
						return;
					}
					deleteTag(memberGroup);
					break;
				}
			}
		});

		mListView.setOnRefreshListener(this);
		loadData();
		return rootView;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	/**
	 * 删除标签
	 * 
	 * */
	private void deleteTag(final MemberGroup memberGroup) {

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("relationId", memberGroup.tagRelationId);

		Callback callback = new Callback(tag, getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				if (null != strMsg) {
					showToast(strMsg);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				if (isSuccess) {
					list.remove(memberGroup);
					mAdapter.notifyDataSetChanged();
					showToast("删除成功");
					if (list.size() == 0) {
						emptyLayout.setVisibility(View.VISIBLE);
						mListView.setVisibility(View.GONE);
					}
				}else {
					
					if (data.has("msg")) {
						showToast(data.get("msg").toString());
					}
				}
			}
		};

		FinalHttp request = new FinalHttp();
		request.get(Server.BASE_URL + Server.DELETE_TAG, params, callback);
	}

	private void loadData() {

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());

		AjaxCallBack<String> callback = new AjaxCallBack<String>() {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dimissDialog();
				
				if (null != strMsg) {
					showToast(strMsg);
				}
			}

			@Override
			public void onSuccess(String t) {
				
				dimissDialog();
				
				// LogUtil.v(tag, "member group list info : " + t);
				JsonParser parser = new JsonParser();
				JsonObject root = parser.parse(t).getAsJsonObject();
				JsonObject data = root.get("data").getAsJsonObject();
				boolean isSuccess = Boolean.parseBoolean(root.get("success")
						.getAsString());
				// {"data":{"msg":"您暂无标签信息!"},"success":true}
				if (list.size() > 0) {
					list.clear();
				}
				if (!isSuccess) {
					// mHandler.sendEmptyMessage(Code.FAILED);
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
						dimissDialog();
						mListView.onRefreshComplete();
						return;
					}
				} else {
					if (data.has("msg")) {
						String msg = data.get("msg").getAsString();
						showToast(msg);
						dimissDialog();
						return;
					}
					String jsonArrayStr = data.get("tagList").toString();
					Log.e(TAG, jsonArrayStr);
					Gson gson = new Gson();
					List<MemberGroup> tlist = gson.fromJson(jsonArrayStr,
							new TypeToken<List<MemberGroup>>() {
							}.getType());
					//Log.i(TAG+"------------------------", tlist.size()+"");
					list.addAll(tlist);
					if (list.size() == 0) {
						//mHandler.sendEmptyMessage(Code.EMPTY);
						emptyLayout.setVisibility(View.VISIBLE);
						mListView.setVisibility(View.GONE);
					} else {
						mHandler.sendEmptyMessage(Code.SUCCESS);
					}
					mListView.onRefreshComplete();
				}
			}

		};

		FinalHttp request = new FinalHttp();
		request.get(Server.BASE_URL + Server.MY_TAG_LIST, params, callback);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dimissDialog();
			switch (msg.what) {
			case Code.SUCCESS: {
				mAdapter.notifyDataSetChanged();
				break;
			}
			case Code.FAILED: {
				if (msg.obj == null) {
					showToast(R.string.login_failed);
				} else {
					showToast((String) msg.obj);
				}
				break;
			}
			}
		}
	};

	/**
	 * 分组
	 * 
	 * @author bob
	 * 
	 * */
	public void onClickHome(View v) {
		mActivity.finish();
	}

	/**
	 * 创建群组
	 * 
	 * @author bob
	 * 
	 * */
	public void onClickAction(View v) {
		Intent intent = new Intent(mActivity, GroupModifyActivity.class);
		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onEventMainThread(GroupCreatedEvent event) {
		loadData();
	}

	public void onEventMainThread(RefreshGropListEvent event) {
		loadData();
	}

	public class MemberGroupListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		String tag = this.getClass().getSimpleName();

		public MemberGroupListAdapter(Context context) {
			mLayoutInflater = LayoutInflater.from(context);
		}

		public void refreshListValue() {
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final MemberGroup memberGroup = list.get(position);
			ViewHolder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.item_member_group, null);
				holder.memberGroup_photo = (SimpleDraweeView) convertView
						.findViewById(R.id.iv_member_group_photo);
				holder.memberGroup_name = (TextView) convertView
						.findViewById(R.id.tv_member_group_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.memberGroup_name.setText(memberGroup.tagName + "("
					+ memberGroup.number + ")");
			
			holder.memberGroup_photo.setImageURI(Uri.parse(Server.BASE_URL+ memberGroup.image));
			
			return convertView;
		}

		class ViewHolder {
			public SimpleDraweeView memberGroup_photo;
			public TextView memberGroup_name;
//			public CheckBox member_checkbox;
		};
	}

	@Override
	public void onRefresh() {
		loadData();
		
	}
}
