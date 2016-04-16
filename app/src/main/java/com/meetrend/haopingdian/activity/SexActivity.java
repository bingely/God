package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

import de.greenrobot.event.EventBus;

public class SexActivity extends BaseActivity {
	
	
	MyExpandableAdapter mAdapter = null;
	
	ArrayList<TeaProductEntity> list = null;
	
	ListView mListView = null;
	
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.actionbar_action, click = "actionClick")
	TextView mAction;
	
	LayoutInflater	mLayoutInflater = null;
	
	FinalBitmap mLoader = null;
	
	int currentGroupPosition = -1;
	int currentChildePosition = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
//	        setContentView(R.layout.activity_sex_list);
	        FinalActivity.initInjectedView(this);
	        
	        mTitle.setText("性别");
	        mAction.setText("确定");
	    	mLayoutInflater = LayoutInflater.from(this);
	    	
	    	mLoader = FinalBitmap.create(this);
	    	mLoader.configLoadingImage(R.drawable.loading_default);
			mLoader.configLoadfailImage(R.drawable.loading_failed);
	       
	        mListView = (ListView) findViewById(R.id.list);
	        loadData();
	}
	
	public void actionClick(View v){
		if(currentGroupPosition!=-1 && currentChildePosition!=-1){
			Products products = list.get(currentGroupPosition).nameList.get(currentChildePosition);
			EventBus.getDefault().post(new TeaAddEvent(products,null,0.000));
			finish();
		}
		
	}
	
	public void homeClick(View v){
		finish();
	}
	
	public void loadData(){
			
			AjaxParams params = new AjaxParams();
			params.put("token", SPUtil.getDefault(SexActivity.this).getToken());
			params.put("storeId", SPUtil.getDefault(SexActivity.this).getStoreId());
			
			Callback callback = new Callback(tag,this) {

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					mHandler.sendEmptyMessage(Code.FAILED);
				}

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					LogUtil.v(tag, "login info : " + t);
					if (isSuccess) {
						JsonObject jsonObj = data.get("data").getAsJsonObject();
						JsonArray jsonArr = jsonObj.get("productCatalogs").getAsJsonArray();
						Gson gson = new Gson();
						list = gson.fromJson(jsonArr, new TypeToken<List<TeaProductEntity>>() {
						}.getType());
						if(list==null || list.size()==0){
							mHandler.sendEmptyMessage(Code.EMPTY);
						}else{
							mHandler.sendEmptyMessage(Code.SUCCESS);
						}
					} else {
						Message msg = new Message();
						msg.what = Code.FAILED;
						msg.obj = data.get("msg");
						mHandler.sendMessage(msg);
					}
				}
			};

			FinalHttp finalHttp = new FinalHttp();
			finalHttp.get(Server.BASE_URL + Server.PRODUCT_LIST, params, callback);
	}
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.SUCCESS:
				mAdapter = new MyExpandableAdapter();
//			    mListView.setAdapter(mAdapter);
				break;
			case Code.FAILED: 
				if (msg.obj == null) {
					showToast("数据获取失败");
				} else {
					showToast((String) msg.obj);
				}
				break;
			}
		}
	};
	

	class MyExpandableAdapter extends BaseExpandableListAdapter{

		ViewHolder mHolder = null;
		
		@Override
		public Products getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return list.get(arg0).nameList.get(arg1);
		}

		
		@Override
		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int groupPostion, int childPostion, boolean arg2, View v,
				ViewGroup arg4) {
			// TODO Auto-generated method stub
			
			if(v==null || v.getTag()==null){
				mHolder = new ViewHolder();
				v = mLayoutInflater.inflate(R.layout.item_child_tea, null);
				mHolder.childTeaName = (TextView)v.findViewById(R.id.tv_tea_name);
				mHolder.childTeaImg = (ImageView)v.findViewById(R.id.img_tea_photo);
				mHolder.childTeaRd = (RadioButton)v.findViewById(R.id.rb_tea);
				
				v.setTag(mHolder);
			}else{
				mHolder = (ViewHolder)v.getTag();
			}
			
			Products products = getChild(groupPostion, childPostion);
			
			mHolder.childTeaName.setText(products.productName);
//			
			mHolder.childTeaRd.setTag(R.layout.item_group_tea,groupPostion);
			mHolder.childTeaRd.setTag(R.layout.item_child_tea,childPostion);
			mHolder.childTeaRd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					currentGroupPosition = Integer.parseInt(arg0.getTag(R.layout.item_group_tea).toString());
					currentChildePosition = Integer.parseInt(arg0.getTag(R.layout.item_child_tea).toString());
					
					mAdapter.notifyDataSetChanged();
				}
			});
			mHolder.childTeaName.setText(products.productName);
			mLoader.display(mHolder.childTeaImg, products.productList.get(0).productImage);
			
			if(currentGroupPosition!=-1 && currentChildePosition!=-1 && groupPostion == currentGroupPosition && currentChildePosition == childPostion){
				mHolder.childTeaRd.setChecked(true);
			}else{
				mHolder.childTeaRd.setChecked(false);
			}
			return v;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0).nameList==null?0:list.get(arg0).nameList.size();
		}

		@Override
		public TeaProductEntity getGroup(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean arg1, View v,
				ViewGroup arg3) {
			// TODO Auto-generated method stub
			if(v==null || v.getTag()==null){
				mHolder = new ViewHolder();
				v = mLayoutInflater.inflate(R.layout.item_group_tea, null);
				mHolder.groupTeaName = (TextView)v.findViewById(R.id.tv_tea_name);
				
				v.setTag(mHolder);
			}else{
				mHolder = (ViewHolder)v.getTag();
			}
			
			mHolder.groupTeaName.setText(getGroup(groupPosition).catalogName);
			
			return v;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return false;
		}
		
		
		class ViewHolder{
			TextView groupTeaName;
			
			TextView childTeaName;
			ImageView childTeaImg;
			RadioButton childTeaRd;
		}
	}
}