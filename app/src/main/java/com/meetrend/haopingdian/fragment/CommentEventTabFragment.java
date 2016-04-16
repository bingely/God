package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.NewEventDetailActivity;
import com.meetrend.haopingdian.bean.CommentUser;
import com.meetrend.haopingdian.bean.Faceicon;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.QQFaceDelEvent;
import com.meetrend.haopingdian.event.QQFaceInputEvent;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshListView;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.Mode;
import com.meetrend.haopingdian.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.TimeUtil;
import com.meetrend.haopingdian.widget.FaceiconTextView;
import com.meetrend.haopingdian.widget.RoundImageView;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 活动详情
 * 
 * 评论
 * 
 * @author 肖建斌
 * 
 */
public class CommentEventTabFragment extends BaseChildOrderListFragment {
	
	@ViewInject(id = R.id.listview)
	PullToRefreshListView mListView;
	
	@ViewInject(id = R.id.numview)
	TextView headTextView;
	@ViewInject(id = R.id.hintview)
	TextView headHintView;
	
	
	private boolean isPrepare;// 做好准备
	private boolean hasLoad = false;// 是否加载过
	View rootView;
	
	//活动id
	private String eventId;
	
	//刷新、加载
	private int pageIndex = 0;
	private int pagecount;
	private final static int PAGECOUNT = 20;
	private  boolean isReFreshMode;
	
	//qq表情
	InputMethodManager imm;
	QQFaceFragment mFace;
	@ViewInject(id = R.id.footer_chat)
	FrameLayout mFaceLayout;
	@ViewInject(id = R.id.et_content_chat, click = "editorClick")
	EditText mEditor;
	@ViewInject(id = R.id.iv_emoji_mode, click = "emojiSwitchClick")//表情按钮
	ImageView mFaceMode;
	@ViewInject(id = R.id.tv_send, click = "sendMsgClick")
	TextView mSendView;
	
	@ViewInject(id = R.id.edit_layout)
	LinearLayout edit_layout;
	@ViewInject(id = R.id.over_view)
	View overView;
	
	@ViewInject(id = R.id.numviewLayout)
	RelativeLayout numviewLayout;

	//private boolean softboardIsVisible;
	//private boolean qqFaceIsVisible;
	
	private List<CommentUser> commentsList;
	private CommentAdapter commentAdapter;
	
	private boolean firstIn = true;
	
	//当前选中的item id
	private String currentUerId;
	private String currentUserName;
	
	private ListView listView;
	//数据为空
	View emptyFooterView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_eventcommenttabfragment,container, false);
			FinalActivity.initInjectedView(this, rootView);
			headTextView.setText("0");
			headHintView.setText("个评论");
			
			commentsList = new ArrayList<CommentUser>();
			commentAdapter = new CommentAdapter(getActivity());
			mListView.setAdapter(commentAdapter);
			mListView.setMode(Mode.BOTH);
			mListView.setOnRefreshListener(listener2);
			listView = mListView.getRefreshableView();
			
			overView.getBackground().setAlpha(120);//0-255
			
			overView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (overView.getVisibility() == View.VISIBLE) {
							overView.setVisibility(View.GONE);
						}
						
						if (mFaceLayout.getVisibility() == View.VISIBLE) {
							mFaceLayout.setVisibility(View.GONE);
						}
						
						if (currentUerId != null) {
							currentUerId = null;
						}
						
						if (mEditor.getHint() != null) {
							mEditor.setHint("");
						}
						
						if (!mEditor.getText().toString().equals("")) {
							mEditor.setText("");
						}
						
						imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
						//InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						//imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						
						edit_layout.setVisibility(View.GONE);
						mEditor.clearFocus();
					}
					
					return true;
				}
			});
			
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					
					if (commentsList.size() == 0) {
						return;
					}
					
					//InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					
					if (edit_layout.getVisibility() == View.GONE) {
						
						overView.setVisibility(View.VISIBLE);
						
						edit_layout.setVisibility(View.VISIBLE);
						mEditor.requestFocus();
						
						CommentUser commentUser = (CommentUser) listView.getAdapter().getItem(position);
						
						currentUerId = commentUser.id;
						currentUserName = commentUser.commentUser;
						
						mEditor.setText("");
						mEditor.setHint("@回复"+ currentUserName +":");
						
						//显示软键盘
						imm.showSoftInput(mEditor, 0);
						//imm.toggleSoftInput( InputMethodManager.SHOW_IMPLICIT, 0 );
						
					}else {
						
						if (currentUerId != null) {
							currentUerId = null;
						}
						
						if (mEditor.getHint() != null) {
							mEditor.setHint("");
						}
						
						if (!mEditor.getText().toString().equals("")) {
							mEditor.setText("");
						}
						
						edit_layout.setVisibility(View.GONE);
						mEditor.clearFocus();
						imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
						
						//imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
					}
				}
			});
			
			imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			mFace = (QQFaceFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_face_chat);
			
			eventId = ((NewEventDetailActivity)getActivity()).eventID;
			isPrepare = true;
			requestList();
		}

		ViewGroup parent = (ViewGroup) rootView.getParent();
		
		if (parent != null) {
			parent.removeView(rootView);
		}

		return rootView;
	}

	@Override
	public void requestList() {
		if (!isPrepare || !isVisible || hasLoad) {
			return;
		}
		
		requestComments();
	}
	
	
	
	//创建评论
	public void sendMsgClick(View view){
		if (currentUerId == null) {
			Toast.makeText(getActivity(), "您还没有选择你要回复的评论", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (TextUtils.isEmpty(mEditor.getText().toString())) {
			Toast.makeText(getActivity(), "没有回复内容", Toast.LENGTH_SHORT).show();
			return;
		}
		
		hasLoad = false;
		//刷新
		isReFreshMode = true;
		pageIndex = 1;
		replayComments(currentUerId, mEditor.getText().toString());
	
	}
	
	//数据为空时的点击刷新
	public void emptyClick(View view){
		hasLoad = false;
		pageIndex = 1;
		requestComments();
	}
	
	OnRefreshListener2<ListView> listener2 = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			hasLoad = false;
			//刷新
			isReFreshMode = true;
			pageIndex = 1;
			requestComments();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			hasLoad = false;
			
			isReFreshMode = false;
			++pageIndex;
			requestComments();
		}
	};
	
	public void editorClick(View view) {
		
		//如果软键盘处于活跃状态，若此时表情处于显示状态，则表情布局隐藏
		if (imm.isActive()) {
			if (mFaceLayout.getVisibility() == View.VISIBLE) {
				mFaceLayout.setVisibility(View.GONE);
			}
		}
	}
	
	// 软键盘和表情切换
	public void emojiSwitchClick(View v) {
		mEditor.setVisibility(View.VISIBLE);

		if (v.getTag() == null) {
			imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
			try {
				Thread.sleep(80);// 解决此时会黑一下屏幕的问题
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mFaceLayout.setVisibility(View.VISIBLE);
			//qqFaceIsVisible = true;
			v.setTag("");
		} else {
			imm.showSoftInput(mEditor, 0);
			mFaceLayout.setVisibility(View.GONE);
			imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
			v.setTag(null);
		}
	}
	
	//回复
	private void replayComments(String commentId,String content){
		
		showDialog("回复中...");
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("id", commentId);
		params.put("content", content);
		
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (isSuccess) {
					currentUerId = null;
					overView.setVisibility(View.GONE);
					mEditor.setText("");
					mEditor.setHint("");
					Toast.makeText(getActivity(), "回复成功", Toast.LENGTH_SHORT).show();
					requestComments();
				}else {
					Toast.makeText(getActivity(), "回复失败，请重试", Toast.LENGTH_SHORT).show();
				}
			}
		};
		CommonFinalHttp request = new CommonFinalHttp();
		request.get(Server.BASE_URL + Server.REPLAY_DISCUSS, params, callback);
	}
	
	//获取评论人列表
	private void requestComments(){
		
		if (firstIn) {
			showDialog();
			firstIn = false;
		}
		
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("bonusId", eventId);
		params.put("pageSize", PAGECOUNT + "");
		params.put("pageIndex", (pageIndex) + "");
		
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				
				hasLoad = true;
				dimissDialog();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				dimissDialog();
				
				if (mListView.isRefreshing()) {
					mListView.onRefreshComplete();
				}
				
				if (isSuccess) {
					
					hasLoad = true;
					
					edit_layout.setVisibility(View.GONE);
					
					Gson  gson = new Gson();
					JsonArray  jsonArray = data.get("commentArray").getAsJsonArray();
					pagecount = data.get("pageCount").getAsInt();//总页数
					pageIndex = data.get("pageIndex").getAsInt();//当前页码
					List<CommentUser> datas = gson.fromJson(jsonArray, new TypeToken<List<CommentUser>>() {}.getType());
					
					if (isReFreshMode) {
						
						if (commentsList.size() > 0) {
							commentsList.clear();
							commentsList.addAll(datas);
						}
						
					}else {
						commentsList.addAll(datas);
					}
					
					if (commentsList.size() == 0) {
						
						numviewLayout.setVisibility(View.GONE);
						//数据为空
						//emptylayout.setVisibility(View.VISIBLE);
						
						listView.removeFooterView(emptyFooterView);
						emptyFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.emptyview_layout, null);
						listView.addFooterView(emptyFooterView);
						
					}else {
						numviewLayout.setVisibility(View.VISIBLE);
						//emptylayout.setVisibility(View.GONE);
						listView.removeFooterView(emptyFooterView);
						commentAdapter.notifyDataSetChanged();
						headTextView.setText(commentsList.size() +"");//评论的个数
					}
					
					if (pageIndex >= pagecount || commentsList.size() < PAGECOUNT) {
						mListView.setMode(Mode.PULL_FROM_START);
					}
					
					if (mListView.isRefreshing()) {
						mListView.onRefreshComplete();
					}
					
					listView.setSelection(0);
					
				}else {
					showToast("数据加载失败");
				}
			}
		};
		CommonFinalHttp request = new CommonFinalHttp();
		request.get(Server.BASE_URL + Server.GET_COMMENT_LIST, params, callback);
	}
	
	

	//删除
	public void onEventMainThread(QQFaceDelEvent qqFaceDelEvent) {
		KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
		mEditor.dispatchKeyEvent(event);
	}
	
	//表情输入
	public void onEventMainThread(QQFaceInputEvent qqFaceInputEvent) {
		Faceicon faceicon = qqFaceInputEvent.getFaceicon();
		QQFaceFragment.input(mEditor, faceicon);
	}
	
	//@Override
	//public void onBackspaceClick() {
		//KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
		//mEditor.dispatchKeyEvent(event);
	//}

	//@Override
	//public void onFaceiconClick(Faceicon faceicon) {
	//	QQFaceFragment.input(mEditor, faceicon);
	//}
	
		//适配器
		public class CommentAdapter extends BaseAdapter {
			
			LayoutInflater mLayoutInflater = null;
			Context mContext = null;
			FinalBitmap loader = null;
			
			public CommentAdapter(Context mContext) {
				mLayoutInflater = LayoutInflater.from(mContext);
				this.mContext = mContext;
				loader = FinalBitmap.create(getActivity());
			}

			@Override
			public int getCount() {
				return commentsList.size();
			}

			@Override
			public CommentUser getItem(int position) {
				return commentsList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@SuppressWarnings("static-access")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder viewHolder = null;
				
				if (convertView == null) {
					viewHolder = new ViewHolder();
					convertView = mLayoutInflater.from(mContext).inflate(R.layout.comment_layout_item, null);
					viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.comment_head_view);
					viewHolder.nameView = (TextView) convertView.findViewById(R.id.comment_name_view);
					viewHolder.timeView = (TextView) convertView.findViewById(R.id.comment_diss_time_view);
					viewHolder.contentView = (FaceiconTextView) convertView.findViewById(R.id.comment_content_view);
					convertView.setTag(viewHolder);
				}else {
					viewHolder = (ViewHolder)convertView.getTag();
				}
				
				CommentUser commentUser = commentsList.get(position);
				//loader.display(viewHolder.imageView, Server.BASE_URL + commentUser.head);
				viewHolder.imageView.setImageURI(Uri.parse(Server.BASE_URL + commentUser.head));
				//评价
				if (commentUser.type == 0) {
					viewHolder.nameView.setTextColor(Color.parseColor("#02bc00"));
					viewHolder.nameView.setText(commentUser.commentUser);
				}
				//回复
				else {
					String content = commentUser.commentUser +"@回复" + commentUser.replyUser;
					SpannableStringBuilder spannable=new SpannableStringBuilder(content);  
			        CharacterStyle span_1=new ForegroundColorSpan(Color.parseColor("#02bc00"));  
			        CharacterStyle span_2=new ForegroundColorSpan(Color.parseColor("#02bc00"));  
			        spannable.setSpan(span_1, 0, commentUser.commentUser.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
			        spannable.setSpan(span_2, commentUser.commentUser.length()+3, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
			        viewHolder.nameView.setText(spannable);
				}
				
				viewHolder.contentView.setText(commentUser.commentContent+"");
				
				//时间啊显示处理
				String time = commentUser.createTime;
				String hintTime = TimeUtil.judgeTime(time);
				viewHolder.timeView.setText(hintTime);
				
				return convertView;
			}
			
		}
		
		class ViewHolder {
			SimpleDraweeView imageView;
			TextView nameView;
			TextView timeView;
			FaceiconTextView contentView;
		}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
	
}