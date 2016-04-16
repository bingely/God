package com.meetrend.haopingdian.adatper;

import java.util.List;

//import u.aly.co;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.SearchActivity;
import com.meetrend.haopingdian.activity.TeaSearchActivity;
import com.meetrend.haopingdian.bean.MemberSearchEntity;
import com.meetrend.haopingdian.bean.TalkSearchEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

public class AllSearchListAdapter extends SearchListAdapter {
	private LayoutInflater mLayoutInflater;
	private FinalBitmap loader;
	private final float scale;
	private Context context;
	private String newTexts;
	private int mempageIndex;//联系人当前页数
	private int talkpageIndex;//消息当前页数
	private String tag;
	private  String mType;

	List<TalkSearchEntity> talkList;
	List<MemberSearchEntity> memberList;

	int total = 0;
	int talkCount = 0;
	int memberCount = 0;

	private static final int TYPE_TILE = 0;
	private static final int TYPE_MEMBER = 1;
	private static final int TYPE_TALK = 2;
	private static final int TYPE_NEXT = 3;
	// 类型个数
	private static final int TYPE_MAX_COUNT = 4;

	public AllSearchListAdapter(Context context) {
		mempageIndex = 1;
		talkpageIndex = 1;
		tag = this.getClass().getSimpleName();
		mLayoutInflater = LayoutInflater.from(context);
		this.context = context;
		scale = context.getResources().getDisplayMetrics().density;
		float dimension = context.getResources().getDimension(
				R.dimen.message_list_image_size);
		int pixels = (int) (dimension * scale + 0.5f);
		loader = FinalBitmap.create(context);
		loader.configBitmapMaxHeight(pixels);
		loader.configBitmapMaxWidth(pixels);
		loader.configLoadingImage(R.drawable.loading_default);
		loader.configLoadfailImage(R.drawable.loading_failed);
	}

	public void setListData(List<TalkSearchEntity> talkList, List<MemberSearchEntity> memberList,String newTexts) {
		this.talkList = talkList;
		this.memberList = memberList;
		this.newTexts = newTexts;
		
		if (talkList != null) {
			talkCount = talkList.size();
		}
		if (memberList != null) {
			memberCount = memberList.size();
		}

	}

	@Override
	public void clear() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		total = 0;
		if (memberCount != 0) {
			total += memberCount + 2;
		}
		if (talkCount != 0) {
			total += talkCount + 2;
		}
		return total;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (memberCount > 0 && position == 0) {
			return TYPE_TILE;
		} else if (memberCount > 0 && position == memberCount + 1) {
			return TYPE_NEXT;
		} else if (talkCount > 0 && position == 0) {
			return TYPE_TILE;
		} else if (talkCount > 0 && memberCount == 0
				&& position == talkCount + 1) {
			return TYPE_NEXT;
		} else if (talkCount > 0 && memberCount > 0
				&& position == talkCount + memberCount + 3) {
			return TYPE_NEXT;
		} else if (position <= memberCount) {
			return TYPE_MEMBER;
		} else {
			int talkPosition = (memberCount == 0 ? memberCount
					: (memberCount + 2));
			if (talkCount > 0 && position == talkPosition) {
				return TYPE_TILE;
			} else {
				return TYPE_TALK;
			}
		}

	}

	// else if(position == memberCount){
	// return TYPE_NEXT;
	// }else if(position == talkCount){
	// return TYPE_NEXT;
	// }
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return TYPE_MAX_COUNT;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int type = getItemViewType(position);
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			switch (type) {
			case TYPE_TILE:
				convertView = mLayoutInflater.inflate(
						R.layout.item_search_title, parent, false);
				holder.titleText = (TextView) convertView
						.findViewById(R.id.tv_title_name);
				break;
			case TYPE_MEMBER:
				convertView = mLayoutInflater.inflate(R.layout.item_simple,
						parent, false);
				holder.memberImage = (ImageView) convertView
						.findViewById(R.id.iv_simple);
				holder.memberText = (TextView) convertView
						.findViewById(R.id.tv_simple);
				break;
			case TYPE_TALK:
				convertView = mLayoutInflater.inflate(
						R.layout.item_talk_search, parent, false);
				holder.talkImage = (ImageView) convertView
						.findViewById(R.id.iv_talk_search);
				holder.talkName = (TextView) convertView
						.findViewById(R.id.tv_talk_search_name);
				holder.talkContent = (TextView) convertView
						.findViewById(R.id.tv_talk_search_content);
				break;
			case TYPE_NEXT:
				convertView = mLayoutInflater.inflate(R.layout.item_next_page, parent, false);
				holder.nextBtn = (Button) convertView.findViewById(R.id.btn_next_page);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (type) {
		case TYPE_TILE:
			if (memberCount != 0 && position == 0) {
				holder.titleText.setText("联系人");
			} else {
				holder.titleText.setText("消息");
			}
			break;
		case TYPE_MEMBER:
			MemberSearchEntity memberEntity = memberList.get(position - 1);
			loader.display(holder.memberImage, Server.BASE_URL
					+ memberEntity.pictureId);
			holder.memberText.setText(memberEntity.customerName);
			break;
		case TYPE_TALK:
			int talkIndex = 0;
			if (memberCount != 0) {
				talkIndex = position - (memberCount + 3);
			} else {
				talkIndex = position - 1;
			}
			TalkSearchEntity talkEntity = talkList.get(talkIndex);
			loader.display(holder.talkImage, Server.BASE_URL + talkEntity.avatarId);
			holder.talkName.setText(talkEntity.name);
			holder.talkContent.setText(talkEntity.content);
			break;
		case TYPE_NEXT:
			if (memberCount != 0 && position == memberCount + 1) {
				holder.nextBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mType = "1";
						++mempageIndex;
						searchChange(newTexts,mType,mempageIndex,context);
					}
				});
			}else{
				holder.nextBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mType = "0";
						++talkpageIndex;
						searchChange(newTexts,mType,talkpageIndex,context);
					}
				});
			}
			break;
		}

		return convertView;
	}

	final static class ViewHolder {
		public TextView titleText;
		public Button nextBtn;

		public ImageView memberImage;
		public TextView memberText;

		public ImageView talkImage;
		public TextView talkName;
		public TextView talkContent;
	};
	
	public void searchChange(String newText,String type,final int pageIndex,Context context){
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(context).getToken());
		params.put("storeId", SPUtil.getDefault(context).getStoreId());
		params.put("keyword", newText);
		params.put("pageSize", "5");
		params.put("type", type);
		params.put("pageIndex", pageIndex+"");
		App.keyword = newText;

		Callback callback = new Callback(tag,context) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(context, "发送请求失败", Toast.LENGTH_SHORT).show();
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
				if("1".equals(mType)){
					JsonObject memberObject = data.get("memberData").getAsJsonObject();
					String memberJsonStr = memberObject.get("argsArray").toString();
					String pageCount = memberObject.get("pageCount").toString();
					if(pageIndex>Integer.parseInt(pageCount)){
						Toast.makeText(context, "当前已经是最后页", Toast.LENGTH_SHORT).show();
						return;
					}
					memberList = gson.fromJson(memberJsonStr, new TypeToken<List<MemberSearchEntity>>() { }.getType());
				}else if("0".equals(mType)){
					JsonObject talkObject = data.get("talkData").getAsJsonObject();
					String talkJsonStr = talkObject.get("records").toString();
					String pageCount = talkObject.get("pageCount").toString();
					if(pageIndex>Integer.parseInt(pageCount)){
						Toast.makeText(context, "当前已经是最后页", Toast.LENGTH_SHORT).show();
						return;
					}
					talkList = gson.fromJson(talkJsonStr, new TypeToken<List<TalkSearchEntity>>() { }.getType());
				}
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
				setListData(talkList, memberList,newTexts);
				AllSearchListAdapter.this.notifyDataSetChanged();
				break;
			case Code.FAILED:
				if (msg.obj == null) {
					Toast.makeText(context, "数据获取失败", Toast.LENGTH_SHORT).show();
				} 
				break;
			} 
		}
	};
}