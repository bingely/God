package com.meetrend.haopingdian.activity;

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
import com.meetrend.haopingdian.bean.GroupMember;
import com.meetrend.haopingdian.bean.Member;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 群聊显示头像
 * 
 * @author 肖建斌
 *
 */
public class ShowGroupListPictrueActivity extends BaseActivity{
	
	@ViewInject(id = R.id.actionbar_home,click = "back")
	ImageView imageView;
	@ViewInject(id = R.id.group_list_gridview)
	GridView gridView;
	@ViewInject(id=R.id.group_name_tv)
	TextView groupTv;
	
	private List<GroupMember> groupMemberList;
	List<Member> list;
	String frommode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_grouplist_pictrue_activity);
		FinalActivity.initInjectedView(this);
		showDialog();
		final Intent intent = getIntent();
		frommode = intent.getStringExtra("fromMode");
		
		if (frommode.equals("1")) {
			groupMemberList = new ArrayList<GroupMember>();
			String gid = intent.getStringExtra("gid");
			getGroupMemberId(gid);
		}else {
			//从点击确定开牛进入群聊界面
			list = (List<Member>)getIntent().getSerializableExtra("group");
			String title = intent.getStringExtra("title");
			groupTv.setText(title);
			gridView.setAdapter(new GridAdapter(list));
			dimissDialog();
		}

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

				Intent in = new Intent(ShowGroupListPictrueActivity.this,MemberInfoActivity.class);
				Bundle bundle = new Bundle();
				if (frommode.equals("1")){
					GroupMember groupMember = groupMemberList.get(i);
					bundle.putString("name", groupMember.userName);
					bundle.putString("id", groupMember.userId);
				}else{
					Member member = list.get(i);
					bundle.putString("name", member.customerName);
					bundle.putString("id", member.userId);
				}
				in.putExtras(bundle);
				startActivity(in);
			}
		});
	}
	
	public  void getGroupMemberId(String groupId){
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(ShowGroupListPictrueActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(ShowGroupListPictrueActivity.this).getStoreId());
		params.put("groupId", groupId);
		
		Callback callback = new Callback(tag,this) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (!isSuccess) {
					showToast("会员加载失败");
				} else {
					JsonArray jsonArrayStr = data.get("groupUsers").getAsJsonArray();
					Gson gson = new Gson();
					groupMemberList  = gson.fromJson(jsonArrayStr, new TypeToken<List<GroupMember>>() {}.getType());
					gridView.setAdapter(new HistoryGridAdapter(groupMemberList));

					dimissDialog();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}
		};			
		FinalHttp http = new FinalHttp();
		http.get(Server.BASE_URL + Server.GET_GROUP_INFO, params, callback);
	}

	
	public class GridAdapter extends BaseAdapter{
		
		private List<Member> list;
		private LayoutInflater layoutInflater;
		private FinalBitmap loader = null;
		
		public GridAdapter(List<Member> list){
			this.list  = list;
			layoutInflater = LayoutInflater.from(ShowGroupListPictrueActivity.this);
			loader = FinalBitmap.create(ShowGroupListPictrueActivity.this);
			loader.configLoadingImage(R.drawable.loading_default);
			loader.configLoadfailImage(R.drawable.loading_failed);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Member getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Member member = list.get(position);
			
			ViewHolder viewHelper = null;
			if (convertView == null) {
				viewHelper = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.gridview_masssend_group_item, null);
				viewHelper.headPhoto = (SimpleDraweeView) convertView.findViewById(R.id.group_head_img);
				viewHelper.textView = (TextView) convertView.findViewById(R.id.group_member_name);
				convertView.setTag(viewHelper);
			}else {
				viewHelper = (ViewHolder) convertView.getTag();
			}
			loader.display(viewHelper.headPhoto, Server.BASE_URL +member.pictureId);
			viewHelper.textView.setText(member.customerName);
			return convertView;
		}
		
	}
	
	public class HistoryGridAdapter extends BaseAdapter{
		
		private List<GroupMember> list;
		private LayoutInflater layoutInflater;
		private FinalBitmap loader = null;
		
		public HistoryGridAdapter(List<GroupMember> list){
			this.list  = list;
			layoutInflater = LayoutInflater.from(ShowGroupListPictrueActivity.this);
			loader = FinalBitmap.create(ShowGroupListPictrueActivity.this);
			loader.configLoadingImage(R.drawable.loading_default);
			loader.configLoadfailImage(R.drawable.loading_failed);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public GroupMember getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			GroupMember groupMember = list.get(position);
			
			ViewHolder viewHelper = null;
			if (convertView == null) {
				viewHelper = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.gridview_masssend_group_item, null);
				viewHelper.headPhoto = (SimpleDraweeView) convertView.findViewById(R.id.group_head_img);
				viewHelper.textView = (TextView) convertView.findViewById(R.id.group_member_name);
				convertView.setTag(viewHelper);
			}else {
				viewHelper = (ViewHolder) convertView.getTag();
			}
			//loader.display(viewHelper.headPhoto, Server.BASE_URL +groupMember.pictureId);
			viewHelper.headPhoto.setImageURI(Uri.parse(Server.BASE_URL +groupMember.pictureId));
			viewHelper.textView.setText(groupMember.userName);
			return convertView;
		}
	}
	
	class ViewHolder{
		SimpleDraweeView  headPhoto;
		TextView textView;
	}
	
	public void back(View view){
		ShowGroupListPictrueActivity.this.finish();
	}

}