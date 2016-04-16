package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.GroupSelecPeopleActivity;
import com.meetrend.haopingdian.activity.MeInfoActivity;
import com.meetrend.haopingdian.activity.MemberInfoActivity;
import com.meetrend.haopingdian.bean.ExecutorEntity;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.bean.MemberBean;
import com.meetrend.haopingdian.bean.MemberGroup;
import com.meetrend.haopingdian.bean.MemberGroupInfo;
import com.meetrend.haopingdian.bean.MyBitmapEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.AddMemberEvent;
import com.meetrend.haopingdian.event.GroupCreatedEvent;
import com.meetrend.haopingdian.event.RefreshGropListEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.InputTools;
import com.meetrend.tea.db.DbOperator;

import de.greenrobot.event.EventBus;

/**
 * 编辑和创建群组
 * @author 肖建斌
 *
 */
public class GroupModifyFragment extends BaseFragment {
	
	private final static String TAG = GroupModifyFragment.class.getName();
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;
	@ViewInject(id = R.id.group_name)
	EditText mGroup_name;
	@ViewInject(id = R.id.gridview)
	GridView gridView;
	
	MemberGroup memberGroup = null;
	ArrayList<String> imgList = new ArrayList<String>();
	
	//当前群组成员的userid的集合
	private List<String> memberNameList = new ArrayList<String>();
	private List<Member> addList = new ArrayList<Member>();//需要添加的成员
	private int editType;
	private GridAdapter gridAdapter;
	private boolean removeState;//删除状态
    private List<MemberGroupInfo> origalList = new ArrayList<MemberGroupInfo>();
    
    //图片的合成
    List<MyBitmapEntity> tEntityList = null;
    
    private boolean isCreateGroup;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
	}
	
	//是否是系统分组
	private boolean isSystemlist;
	
	String tagName;//群组名称
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_group_modify, container, false);
		FinalActivity.initInjectedView(this, rootView);
		
		Bundle bundle = getArguments();
		
		//情景：添加群组成员
		if(bundle != null){
			mBarTitle.setText(R.string.title_modify_group);
			if (bundle.get("issystem") != null) {
				
				tagName = bundle.getString("tagName");
				isSystemlist = true;
				addList = App.members;
				gridAdapter = new GridAdapter();
				gridView.setAdapter(gridAdapter);
				mGroup_name.setEnabled(false);
				mBarAction.setVisibility(View.GONE);
				mGroup_name.setText(tagName);
				
			}else {
				
				InputTools.hideKeyboard(mGroup_name);
				memberGroup = (MemberGroup) bundle.getSerializable("group");
				mGroup_name.setText(memberGroup.tagName);
				mGroup_name.setSelection(mGroup_name.getText().toString().length());
				String name = memberGroup.tagName;
				if (memberGroup.tagName.equals("店小二")) {
					mGroup_name.setEnabled(false);
				}
				editType = bundle.getInt(Code.EDIT_TYPE);//获取编辑模式
				loadGroupInfo(memberGroup.tagRelationId);
			}
			
		}
		//情景：创建群组
		else{
			isCreateGroup = true;
			mGroup_name.requestFocus();
			mBarTitle.setText(R.string.title_create_group);
			gridAdapter = new GridAdapter();
			gridView.setAdapter(gridAdapter);
		}
		
		mBarAction.setText(R.string.done);
		gridView.setOnItemClickListener(new GridViewOnitemClick());
		return rootView;
	}
	
	//从联系人跳转传递的已选中的会员
	public void onEventMainThread(AddMemberEvent event) {
		if (addList.size() > 0) {
			addList.clear();
		}
		
		if (imgList.size() > 0) {
			imgList.clear();
		}
		
		addList = event.getList();
		
		
		if (memberNameList.size() > 0) {
			memberNameList.clear();
		}
		
		for (Member member : addList) {
			if (member.checkstatus == true) {
				imgList.add(member.pictureId);
				memberNameList.add(member.userId);
			}
		}
		//刷新成员界面
		gridAdapter = new GridAdapter();
		gridView.setAdapter(gridAdapter);
	}
	
	
	private class GridAdapter extends BaseAdapter{
		
		private LayoutInflater layoutInflater;
		private int datasize;
		
		public GridAdapter() {
			layoutInflater = LayoutInflater.from(getActivity());
			datasize = addList.size();
			
			//如果是系统分组的就不要出现"+"和"-"
			if (!isSystemlist) {
				Member member = new Member();
				Member addMember = new Member();
				addMember.pictureId = "add";
			    //Member addmember = new Member(addinnerMember, 1);
			    Member delMember = new Member();
			    delMember.pictureId = "del";
			    //Member delmember = new Member(delinnerMember, 1);
			    
			    addList.add(datasize,addMember);
			    addList.add(datasize +1, delMember);
			}
		}
		
		@Override
		public int getCount() {
			return addList.size();
		}

		@Override
		public Object getItem(int position) {
			return addList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.edit_member_head_item, null);
				holder.head = (SimpleDraweeView) convertView.findViewById(R.id.headimg);
				holder.delIcon = (ImageView) convertView.findViewById(R.id.delete);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
		    Member member = addList.get(position);
		    
		    if (member.pictureId.equals("add")) {
		    	holder.delIcon.setVisibility(View.GONE);
		    	holder.head.setImageResource(R.drawable.add_pic);
		    	
			}else if (member.pictureId.equals("del")) {
				holder.head.setImageResource(R.drawable.group_member_white_delete_icon);
				holder.delIcon.setVisibility(View.GONE);
				
			}else{
				if (removeState == true) {
					holder.delIcon.setVisibility(View.VISIBLE);
				}else {
					holder.delIcon.setVisibility(View.GONE);
				}
				holder.head.setImageURI(Uri.parse(Server.BASE_URL +member.pictureId));
				
			}
		    return convertView;
		}
	}
	
    public class GridViewOnitemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			
			Member member  = addList.get(position);
			if (member.pictureId.equals("add")) {
				if (removeState) {
					return;
				}
				Intent intent = new Intent(mActivity,GroupSelecPeopleActivity.class);
				intent.putExtra(Code.MODE, Code.SHOW_MULTI_CHECK_MODE);//多选
				//分为创建群组和添加组成员
				intent.putStringArrayListExtra("memberNameList", (ArrayList<String>) memberNameList);//传递成员的名字
				if (editType == Code.UPDATE_GROUP) {
					//intent.putStringArrayListExtra("memberNameList", (ArrayList<String>) memberNameList);//传递成员的名字
					if (memberGroup.tagName.equals("店小二")) {
						intent.putExtra(Code.GROUP_MODE, Code.ASSITANT);//是店小二类型，联系人界面只显示已绑定的用户
					}else {
						intent.putExtra(Code.GROUP_MODE, Code.NOT_ASSITANT);//是店小二类型，联系人界面只显示已绑定的用户
					}
				}
				//创建分组模式
				else {
					
					
				}
				startActivity(intent);
			}else if (member.pictureId.equals("del")) {
				if (addList.size() <= 2) {
					showToast("当前成员为零");
					return;
				}
				
				if (!removeState) {
					removeState = true;
					gridAdapter.notifyDataSetChanged();
				}else {
					removeState = false;
					gridAdapter.notifyDataSetChanged();
				}
				
			}else {
				if (removeState) {
					//删除操作
					memberNameList.remove(position);//删除会员
					imgList.remove(position);
					addList.remove(position);
					if (imgList.size() == 0) {
						removeState = false;
					}
					gridAdapter.notifyDataSetChanged();
				}else {
					
					
					Bundle bundle = new Bundle();
					bundle.putString("name", member.customerName);
					bundle.putString("id", member.userId);
					
					Intent intent = null;
					if (null != tagName) {
						intent = new Intent(mActivity, MeInfoActivity.class);
					}else {
						intent = new Intent(mActivity, MemberInfoActivity.class);
					}
					intent.putExtras(bundle);
					mActivity.startActivity(intent);
				}
			}
		}
    }	
	
	/**
	 * 组合图片
	 * 
	 * */
	public void onClickAction(View v){
		
		//因为有两张是删除和添加按钮
		if (!isSystemlist) {
			if (addList.size() < 3) {
				showToast("请选择成员");
				return;
			}
		}
		
		if (mGroup_name.getText().toString().equals("店小二")) {
			showToast("没有创建   店小二  群组的权限");
			return;
		}
		
		commit();
	}
	
	/**
	 * 创建群则或者修改
	 * 
	 * */
	private void commit(){
		
		if (editType == Code.UPDATE_GROUP) {
			gridAdapter.notifyDataSetChanged();
		}
		showDialog();
		Gson gson = new Gson();
		
		List<MemberBean> idslist = new ArrayList<MemberBean>();
		int size = addList.size();
		
		Member member = null;
		
		for (int i = 0; i < size; i++) {
			 member = addList.get(i);
					
			if (addList.get(i).checkstatus == true) {
				//店小二选择已绑定的用户
				if (mGroup_name.getText().toString().equals("店小二") && isCreateGroup == true) {
					
					if (member.status.equals("2")) {
						MemberBean bean = new MemberBean(member.userId);
						idslist.add(bean);
					}
					
				}else {
					MemberBean bean = new MemberBean(member.userId);
					idslist.add(bean);
				}
			}
		}
		
		if (idslist.size() == 0 && isCreateGroup == true && mGroup_name.getText().toString().equals("店小二")) {
			showToast("没有选择已绑定的成员");
			dimissDialog();
			return;
		}
		
		String idsArray = gson.toJson(idslist);//userid jsonArray
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("tagUser", idsArray);
		params.put("number", idsArray.length() + "");
		Log.i(TAG, idsArray.length() + "");
		
		String subString = "Ecp.Picture.view.img?pictureId=";
		//组建jsonArray
		JsonArray imagesArray = new JsonArray();
		for (Member mMember : addList) {
			JsonObject imgObject = new JsonObject(); 
			String pictrueId = mMember.pictureId;
			if (pictrueId.equals("del") || pictrueId.equals("add")) {
				continue;
			}
			String resultPid = null;
			if ("".equals(pictrueId)) {
				imgObject.addProperty("image", Code.DEFAULT_PID);
			}else {
				resultPid =  pictrueId.substring(31,pictrueId.length());
				imgObject.addProperty("image", resultPid);
			}
			imagesArray.add(imgObject);
		}
		//Log.i("------------imagesArray--------------", imagesArray.toString());
		params.put("images", imagesArray.toString());
		
		
		if (editType == Code.UPDATE_GROUP) {
			params.put("relationId", memberGroup.tagRelationId);
			params.put("newTagName", mGroup_name.getText().toString());
		}else {
			//创建分组
			params.put("tagName", mGroup_name.getText().toString());
		}
		
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				Message message = new Message();
				
				if (!isSuccess) {
					String msg = data.get("msg").getAsString();
					message.obj = msg;
					message.what = Code.FAILED;
					mHandler.sendMessage(message);
					return;
				}
				
				if(mGroup_name.getText().toString().equals("店小二")){
					List<ExecutorEntity> executorList = new ArrayList<ExecutorEntity>();
					Member member = null;
					for (int i = 0; i < addList.size() - 2; i++) {
						 member = addList.get(i);
							ExecutorEntity executor = new ExecutorEntity(member.userId,member.customerName, member.pictureId);
							executorList.add(executor);
					}
					
					DbOperator dbOperator = DbOperator.getInstance();
					dbOperator.clearTable(getActivity());
					dbOperator.saveExecutors(getActivity(), executorList);
				}
				
				message.obj = "操作成功";
				if (editType == Code.UPDATE_GROUP) {
					message.what = 111;
					mHandler.sendMessage(message);
				}else {
					message.what = Code.SUCCESS;
					mHandler.sendMessage(message);
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				if (null != strMsg) {
					showToast(strMsg);
				}
			}
		};
		
		FinalHttp finalHttp = new FinalHttp();
		if (editType == Code.UPDATE_GROUP) {
			finalHttp.get(Server.BASE_URL + Server.UPDATE_TAG, params, callback);
		}else {
			finalHttp.get(Server.BASE_URL + Server.ADD_TAG, params, callback);
		}
	}
	
    private void loadGroupInfo(String groupId){
    	AjaxParams params = new AjaxParams();
    	params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("relationId", groupId);
		
		Callback callback = new Callback(tag,getActivity()) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(tag, "msg init list info : " + t);

				if (!isSuccess) {
					mGroupIdsHandler.sendEmptyMessage(Code.FAILED);
				} else {
					String jsonArrayStr = data.get("jsonArray").toString();
					Gson gson = new Gson();
					origalList = gson.fromJson(jsonArrayStr, new TypeToken<List<MemberGroupInfo>>() {}.getType());
					
					//初始化addList的数据
					for (MemberGroupInfo item : origalList) {
						imgList.add(item.avatarId);//获取会员url
						memberNameList.add(item.id);
					    Member member = new Member();
					    member.pictureId = item.avatarId;
					    member.userId = item.id;
					    member.customerName = item.name;
					    //Member member2 = new Member(innerMember, 1);
					    member.checkstatus = true;
					    addList.add(member);
					}
					
					
					Message msg = new Message();
					msg.what = Code.SUCCESS;
					mGroupIdsHandler.sendMessage(msg);
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mGroupIdsHandler.sendEmptyMessage(Code.FAILED);
			}

		};	
		
		FinalHttp http = new FinalHttp();
		http.get(Server.BASE_URL + Server.GETTAGDETAIL, params, callback);
    }
		
    /***
	 * 获取群组成员ID
	 * 
	 * */
	private Handler mGroupIdsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.FAILED:
				showToast("获取数据失败");
				break;
			case Code.SUCCESS:
				gridAdapter = new GridAdapter();
				gridView.setAdapter(gridAdapter);
				break;
		}
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			dimissDialog();
			switch (msg.what) {

				case Code.SUCCESS:{
					showToast((String)msg.obj);
					EventBus.getDefault().post(new GroupCreatedEvent());
					mActivity.finish();
					break;
				}
				
				case 111:
				{
					showToast((String)msg.obj);
					EventBus.getDefault().post(new RefreshGropListEvent());//刷新群组列表
					getActivity().finish();
					break;
				}
				
			}
		}
	};
	
	
	class ViewHolder{
		SimpleDraweeView head;
		ImageView delIcon;
	}
	

	
	//返回
	public void onClickHome(View v){
		mActivity.finish();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}