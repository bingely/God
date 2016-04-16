package com.meetrend.haopingdian.activity;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.AllSearchListAdapter;
import com.meetrend.haopingdian.bean.MemberSearchEntity;
import com.meetrend.haopingdian.bean.TalkSearchEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ProgressDialog;
import com.meetrend.haopingdian.widget.UISearchView;
import com.meetrend.haopingdian.widget.UISearchView.OnCloseListener;
import com.meetrend.haopingdian.widget.UISearchView.OnQueryTextListener;

public class SearchActivity extends BaseActivity {
	@ViewInject(id = R.id.sv_search)
	UISearchView mSearch;
	@ViewInject(id = R.id.tv_none_search)
	TextView mNone;
	@ViewInject(id = R.id.pb_search)
	ProgressDialog mProgress;
	// @ViewInject(id = R.id.content_layout)
	// LinearLayout mContent;
	// @ViewInject(id = R.id.vp_search_list)
	// ViewPager mPager;
	// @ViewInject(id = R.id.ti_indicator_search)
	// TitlePageIndicator mIndicator;
	@ViewInject(id = R.id.lv_search)
	ListView mSearchListView;
	

	AllSearchListAdapter mAdapter = null;

	List<TalkSearchEntity> talkList;
	List<MemberSearchEntity> memberList;
	private int pageIndex;
	private String newTexts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_search);
		FinalActivity.initInjectedView(this);

		hideAll();

		pageIndex = 1;
		
		mSearch.setOnQueryTextListener(mQueryTextListener);
		mSearch.setOnCloseListener(mCloseListener);

		mAdapter = new AllSearchListAdapter(this);

		mSearchListView.setAdapter(mAdapter);
	}

	// 事件处理
	private void hideAll() {
		mProgress.setVisibility(View.GONE);
		mNone.setVisibility(View.GONE);
	}

	public void cancelClick(View v) {
		this.finish();
	}
	
	public void mBtnDownClick(View v){
		++pageIndex;
		showToast(pageIndex+"");
		searchChange(mSearch.getText().toString());
	}

	OnCloseListener mCloseListener = new OnCloseListener() {
		@Override
		public void onClose() {
			hideAll();
		}
	};

	OnQueryTextListener mQueryTextListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextChange(String newText) {
			newTexts = newText;
			if (newText.length() == 0) {
				hideAll();
			} else {
				searchChange(newText);
			}
			return false;
		}

	};

	public void searchChange(String newText){
		mProgress.setVisibility(View.VISIBLE);
		mNone.setVisibility(View.GONE);

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(SearchActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(SearchActivity.this).getStoreId());
		params.put("keyword", newText);
		params.put("pageSize", "5");
		params.put("pageIndex", pageIndex+"");
		App.keyword = newText;

		Callback callback = new Callback(tag,this) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast("发送请求失败");
			}

			@Override
			public void onSuccess(String str) {
				super.onSuccess(str);
				LogUtil.v(tag, "search info : " + str);
				if (!isSuccess) {
					String err = data.get("msg").getAsString();
					Message msg = new Message();
					msg.what = Code.FAILED;
					msg.obj = err;
					mHandler.sendMessage(msg);
					return;
				}

				Gson gson = new Gson();
				JsonObject talkObject = data.get("talkData")
						.getAsJsonObject();
				JsonObject memberObject = data.get("memberData")
						.getAsJsonObject();

				String talkJsonStr = talkObject.get("records")
						.toString();
				talkList = gson.fromJson(talkJsonStr, new TypeToken<List<TalkSearchEntity>>() { }.getType());

				String talkStr = talkObject.get("pageCount").toString();
				String memberStr = memberObject.get("argsArray").toString();
				if ("0".equals(talkStr)&&"[]".equals(memberStr)) {
					mNone.setVisibility(View.VISIBLE);
					mNone.setText("无结果,没有找到与“" + App.keyword + "”相关的记录");
				}
				String memberJsonStr = memberObject.get("argsArray").toString();
				memberList = gson.fromJson(memberJsonStr,
						new TypeToken<List<MemberSearchEntity>>() {
						}.getType());

				mHandler.sendEmptyMessage(Code.SUCCESS);
			}
		};

		FinalHttp http = new FinalHttp();
		http.get(Server.BASE_URL + Server.GLOABL_SEARCH_URL, params, callback);
	}
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.SUCCESS:
				mProgress.setVisibility(View.GONE);
				mAdapter.setListData(talkList, memberList,newTexts);
				mAdapter.notifyDataSetChanged();
				break;
			case Code.FAILED:
				if (msg.obj == null) {
					showToast("数据获取失败");
				} else {
					showToast(msg.obj.toString());
					mProgress.setVisibility(View.GONE);
					mNone.setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessage(Code.FAILED);
				}
				break;
			}
		}
	};
}