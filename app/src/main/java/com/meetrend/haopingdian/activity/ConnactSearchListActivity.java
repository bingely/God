package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.SearchConnactMember;
import com.meetrend.haopingdian.bean.TagUser;
import com.meetrend.haopingdian.bean.TagUser.User;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.InputTools;
import com.meetrend.haopingdian.widget.EditTextWatcher;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 通讯录搜索
 * 
 * @author 肖建斌
 * 
 */
public class ConnactSearchListActivity extends BaseActivity {

	@ViewInject(id = R.id.search_edit)
	EditText searchEdit;
	@ViewInject(id = R.id.searchbtn)
	TextView searchBtn;
	@ViewInject(id = R.id.mysearchlistview)
	ListView listview;
	@ViewInject(id = R.id.emptyview)
	RelativeLayout emptyView;
	
	@ViewInject(id = R.id.clearbtn,click = "clearClick")
	ImageView clear;
	
	@ViewInject(id = R.id.back_img, click ="FinishActivityClick")
	ImageView backView;
//
//	@ViewInject(id = R.id.actionbar_title)
//	TextView titleView;

	private List<SearchConnactMember> allList = new ArrayList<SearchConnactMember>();

	private int currentIndex = 1;
	private int pageCount;

	private SearchgAdapter searchAdapter = null;

	private boolean first;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conact_search);
		FinalActivity.initInjectedView(this);
		//titleView.setText("搜索");

		searchEdit.addTextChangedListener(new EditTextWatcher() {
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					clear.setVisibility(View.GONE);
					searchBtn.setText("取消");
				} else {
					clear.setVisibility(View.VISIBLE);
					searchBtn.setText("搜索");
				}
			}
		});

		searchBtn.setOnClickListener(new SearchBtnClickListener());

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SearchConnactMember searchMember = allList.get(position);
				Intent intent = new Intent(ConnactSearchListActivity.this,
						MemberInfoActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("name", searchMember.customerName);
				bundle.putString("id", searchMember.userId);
				intent.putExtras(bundle);
				ConnactSearchListActivity.this.startActivity(intent);
				finish();
			}
		});
		searchAdapter = new SearchgAdapter();
		listview.setAdapter(searchAdapter);
	}
	
	public void FinishActivityClick(View view){
		finish();
	}
	
	//clear
	public void clearClick(View view){
		searchEdit.setText("");
	}

	public class SearchBtnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			first = true;
			if (searchBtn.getText().toString().equals("搜索")) {
				requestCurrentMemberList(searchEdit.getText().toString());
			} else {
				InputTools.hideSoftKeyBoard(searchEdit);// 强制隐藏软键盘
				finish();
			}
		}
	}

	/**
	 * 获得联系人列表
	 * 
	 * @author joy 6/16
	 * */
	private void requestCurrentMemberList(String key) {

		Callback callback = new Callback(tag, this) {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (isSuccess) {

					if (allList.size() > 0) {
						allList.clear();
					}

					// 普通用户
					String jsonArrayStr = data.get("argsArray").toString();
					Gson gson = new Gson();
					List<SearchConnactMember> list = gson.fromJson(
							jsonArrayStr,
							new TypeToken<List<SearchConnactMember>>() {
							}.getType());
					for (SearchConnactMember item : list) {
						item.tag = false;
						// tagName 为 null
					}
					allList.addAll(list);

					// 标签用户
					if (data.has("tag")) {
						String tagArray = data.get("tag").toString();// 标签
						List<TagUser> taguserList = gson.fromJson(tagArray,
								new TypeToken<List<TagUser>>() {
								}.getType());
						for (TagUser tagUser : taguserList) {
							String tagName = tagUser.tagName;
							for (int j = 0; j < tagUser.userArray.size(); j++) {
								User u = tagUser.userArray.get(j);
								SearchConnactMember searchConnactMember = new SearchConnactMember(
										u.name, "", u.id, u.memberId,
										u.avatarId, "", true, tagName);
								allList.add(searchConnactMember);
							}
						}
					}

					if (allList.size() == 0) {
						emptyView.setVisibility(View.VISIBLE);
					} else {
						emptyView.setVisibility(View.GONE);
					}
					searchAdapter.notifyDataSetChanged();
				} else {
					showToast("没有任何结果");
				}

			}
		};

		AjaxParams params = new AjaxParams();

		params.put("token", SPUtil.getDefault(ConnactSearchListActivity.this)
				.getToken());
		params.put("storeId", SPUtil.getDefault(ConnactSearchListActivity.this)
				.getStoreId());

		params.put("keyword", key);
		params.put("pageIndex", currentIndex + "");
		params.put("pageSize", 10 + "");

		FinalHttp http = new FinalHttp();
		http.get(Server.BASE_URL + Server.APP_MEMBER_USER_LIST, params,
				callback);
	}

	// 搜索adapter
	private class SearchgAdapter extends BaseAdapter {

		LayoutInflater inflater = null;

		public SearchgAdapter() {
			inflater = LayoutInflater.from(ConnactSearchListActivity.this);
		}

		@Override
		public int getCount() {
			return allList.size();
		}

		@Override
		public Object getItem(int position) {
			return allList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = inflater.inflate(
						R.layout.connact_search_list_item_layout, null);
				holder.photo = (SimpleDraweeView) convertView
						.findViewById(R.id.search_photo);
				holder.nameView = (TextView) convertView
						.findViewById(R.id.search_name_view);
				holder.signView = (TextView) convertView
						.findViewById(R.id.signnameview);
				holder.stuView = (TextView) convertView
						.findViewById(R.id.status_tv);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			SearchConnactMember member = allList.get(position);
			holder.photo.setImageURI(Uri.parse(Server.BASE_URL
					+ member.pictureId));

			if (!TextUtils.isEmpty(member.customerName))
				holder.nameView.setText(member.customerName);
			else {
				if (!TextUtils.isEmpty(member.mobile))
					holder.nameView.setText(member.mobile);
			}


			if (member.tagName == null || "".equals(member.tag)) {
				holder.signView.setVisibility(View.GONE);
			} else {
				holder.signView.setText("分组:" + member.tagName);
			}

			if (!TextUtils.isEmpty(member.status)) {
				int status = Integer.parseInt(member.status);
				switch (status) {
				case 0:
					holder.stuView.setTextColor(Color.GRAY);
					holder.stuView.setBackgroundResource(0);
					holder.stuView.setText("未邀请");
					break;
				case 1:
					holder.stuView.setTextColor(Color.GREEN);
					holder.stuView.setBackgroundResource(0);
					holder.stuView.setText("已邀请");
					break;
				case 2:
					holder.stuView.setTextColor(Color.GRAY);
					holder.stuView.setBackgroundResource(0);
					holder.stuView.setText("已绑定");
					break;
				}
			} else {
				holder.stuView.setTextColor(Color.GRAY);
				holder.stuView.setBackgroundResource(0);
				holder.stuView.setText("未邀请");
			}

			return convertView;
		}
	}

	final class Holder {

		SimpleDraweeView photo;
		TextView nameView;
		TextView signView;
		TextView stuView;

	}

	@Override
	public void finish() {
		super.finish();
		ConnactSearchListActivity.this.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
	}

}