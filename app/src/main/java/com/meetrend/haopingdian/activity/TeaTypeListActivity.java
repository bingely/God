package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.FINISH_EVENT;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;

import de.greenrobot.event.EventBus;

/**
 * 选茶列表
 *
 *@author 肖建斌
 */
public class TeaTypeListActivity extends BaseActivity{
	
	private final static String TAG = TeaTypeListActivity.class.getName().toString();
	
	MyExpandableAdapter mAdapter = null;
	ArrayList<TeaProductEntity> list = null;
	ExpandableListView expandableListView = null;
	List<Products> mProducts;
	
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.actionbar_action, click = "actionClick")
	TextView mAction;
	@ViewInject(id = R.id.header_search_layout, click = "searchClick")
	RelativeLayout mSearch;
	LayoutInflater mLayoutInflater = null;
	int currentGroupPosition = -1;
	int currentChildePosition = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_tea_list);
		FinalActivity.initInjectedView(this);

		showDialog();
		
		mTitle.setText("商品库");
		mAction.setVisibility(View.GONE);
		mLayoutInflater = LayoutInflater.from(this);

		expandableListView = (ExpandableListView) findViewById(R.id.list);
		loadData();
	}
	
	public void onEventMainThread(FINISH_EVENT finish){
		this.finish();
	}


	public void homeClick(View v) {
		finish();
	}

	public void searchClick(View v){
		startActivity(new Intent(TeaTypeListActivity.this,TeaSearchActivity.class));
		TeaTypeListActivity.this.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
	}
	
	public void loadData() {
		
		AjaxParams params = new AjaxParams();
		
		params.put("token", SPUtil.getDefault(TeaTypeListActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(TeaTypeListActivity.this).getStoreId());

		Callback callback = new Callback(tag,this) {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				LogUtil.v(TAG, "茶列表数据 : " + t);
				dimissDialog();
				
				if (isSuccess) {
					
					JsonArray jsonArr = data.get("catalogList").getAsJsonArray();
					Gson gson = new Gson();
					list = gson.fromJson(jsonArr,new TypeToken<List<TeaProductEntity>>() {}.getType());
					if (list == null || list.size() == 0) {
						showToast("没有数据");
						//mHandler.sendEmptyMessage(Code.EMPTY);
					} else {
						mHandler.sendEmptyMessage(Code.SUCCESS);
					}
					
				} else {
					if (data.get("msg") != null) {
						showDialog(data.get("msg").getAsString());
					}
				}
			}
		};
		
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.PRODUCT_LIST, params, callback);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Code.SUCCESS:
				mAdapter = new MyExpandableAdapter();
				expandableListView.setAdapter(mAdapter);
				dimissDialog();
				break;
			}
		}
	};


	class MyExpandableAdapter extends BaseExpandableListAdapter {

		@Override
		public Products getChild(int arg0, int arg1) {
			return list.get(arg0).nameList.get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return 0;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return list.get(arg0).nameList == null ? 0 : list.get(arg0).nameList.size();
		}

		@Override
		public TeaProductEntity getGroup(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return list.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,boolean isExpanded, View childView, ViewGroup viewGroup) {
			 
			 SecondViewHolder sHolder = null;
			 if (childView == null) {
				 sHolder = new SecondViewHolder();
				 childView = mLayoutInflater.inflate(R.layout.expandlistview_second_menu_item_layout, viewGroup,false);
				 sHolder.second_menu_containerLayout = (RelativeLayout) childView.findViewById(R.id.second_menu_container);
			     sHolder.third_menu_container = (LinearLayout) childView.findViewById(R.id.third_menu_container);
			     sHolder.childTeaImg = (SimpleDraweeView) childView.findViewById(R.id.sencond_img);
			     sHolder.childTeaName = (TextView) childView.findViewById(R.id.secon_txt);
			     sHolder.mgroupimage = (ImageView) childView.findViewById(R.id.mGroupimage);
			     childView.setTag(sHolder);
			 }else {
				 sHolder = (SecondViewHolder)childView.getTag();
			 }
			 
			 Products product = (Products) getChild(groupPosition, childPosition);
			 List<Pici> list = product.productList;//批次数据集合
			 
			 if (list.size() == 1) {
				 sHolder.mgroupimage.setVisibility(View.GONE);//隐藏
				 sHolder.childTeaName.setText(list.get(0).fullName);//显示产品全名
			}else {
				sHolder.mgroupimage.setVisibility(View.VISIBLE);//显示
				sHolder.childTeaName.setText(product.productName);
			}
			 
			 //二级菜单的图片显示
			 sHolder.childTeaImg.setImageURI(Uri.parse(Server.BASE_URL +list.get(0).productImage));

			 //判断是否显示第三级菜单
			 if (product.isShowChildList) {
				 sHolder.mgroupimage.setImageResource(R.drawable.second_bottom_arrow);
				 sHolder.third_menu_container.setVisibility(View.VISIBLE);
			 }else {
				 sHolder.mgroupimage.setImageResource(R.drawable.second_right_arrow);
				 sHolder.third_menu_container.setVisibility(View.GONE);
			 }
			
			 //展开第三级菜单onClick
			 sHolder.second_menu_containerLayout.setOnClickListener(new AddThirdMenuToLayout(sHolder,product,list,sHolder.mgroupimage));
			 
			return childView;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View v,ViewGroup arg3) {
			
			ParentViewHolder mHolder = null;
			if (v == null) {
				mHolder = new ParentViewHolder();
				v = mLayoutInflater.inflate(R.layout.item_group_tea, null);
				mHolder.groupTeaName = (TextView) v.findViewById(R.id.tv_tea_name);
				mHolder.mgroupimage=(ImageView)v.findViewById(R.id.mGroupimage);
				v.setTag(mHolder);
			} else {
				mHolder = (ParentViewHolder) v.getTag();
			}
			
			 TeaProductEntity teaProductEntity = getGroup(groupPosition);
			 mHolder.groupTeaName.setText(teaProductEntity.catalogName);
			 
			 if (teaProductEntity.nameList.size() == 0) {
				 mHolder.mgroupimage.setVisibility(View.GONE);
			 }else {
				 mHolder.mgroupimage.setVisibility(View.VISIBLE);
				 if(isExpanded){
					mHolder.mgroupimage.setImageResource(R.drawable.boottom_arrow);
		         }else{
		        	mHolder.mgroupimage.setImageResource(R.drawable.right_arrow);
		         }
			}
			 
			return v;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}
	}
	
	/**
	 * 添加第三层childview
	 * 
	 * @author 肖建斌
	 *
	 */
	public class AddThirdMenuToLayout implements OnClickListener{
		
		SecondViewHolder secondViewHolder;
		List<Pici> list;
		Products products;
		ImageView imageView;
		
		public AddThirdMenuToLayout(SecondViewHolder secondViewHolder,Products products,List<Pici> list,
				ImageView imageView){
			
			this.secondViewHolder = secondViewHolder;
			this.list = list;
			this.products = products;
			this.imageView = imageView;
		}

		@Override
		public void onClick(View v) {
			
			//如果只有一个产品并且model1Name,model2Name为空的情况，直接点击结束
			if (list.size() == 1) {
				try {
				    EventBus.getDefault().post(new TeaAddEvent(products,list.get(0),1.000));
				    TeaTypeListActivity.this.finish();
			    } catch (Exception e) {
				   Toast.makeText(TeaTypeListActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
			    }
				
			}else{
				
				if (secondViewHolder.third_menu_container.getVisibility() == View.GONE) {
					
					 products.isShowChildList = true;
					 secondViewHolder.third_menu_container.setVisibility(View.VISIBLE);
					 imageView.setImageResource(R.drawable.second_bottom_arrow);
					 //ExpandCollapseHelper.animateExpanding(secondViewHolder.third_menu_container);
					 
					 if (secondViewHolder.third_menu_container.getChildCount() > 0) {
						 secondViewHolder.third_menu_container.removeAllViews();
					 }
					 
					 for (int i = 0; i < list.size(); i++) {
						 
						 final Pici pici = list.get(i);
						 View thridview = LayoutInflater.from(TeaTypeListActivity.this).inflate(R.layout.expandlistview_third_item_layout, null);
						 RelativeLayout containerLayout = (RelativeLayout) thridview.findViewById(R.id.third_menu_layout);
						 SimpleDraweeView thridImg = (SimpleDraweeView) thridview.findViewById(R.id.third_img);
						 TextView thridtv = (TextView) thridview.findViewById(R.id.pici_name);
						 
						 thridImg.setImageURI(Uri.parse(Server.BASE_URL + pici.productImage));
						 
						 if (TextUtils.isEmpty(pici.model2Value)) {
							 thridtv.setText(pici.model1Value);
						 }else {
							 thridtv.setText(pici.model1Value + " "+ pici.model2Value);
						 }
						 
						 //点击切换界面
						 containerLayout.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								try {
									    EventBus.getDefault().post(new TeaAddEvent(products,pici,1.000));
									    TeaTypeListActivity.this.finish();
								} catch (Exception e) {
									Toast.makeText(TeaTypeListActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
								}
								
							}
						});
						 //container.addView(thridview);
						 secondViewHolder.third_menu_container.addView(thridview);
					 }
					 
				}else {
					products.isShowChildList = false;
					secondViewHolder.third_menu_container.setVisibility(View.GONE);
					imageView.setImageResource(R.drawable.second_right_arrow);
					//ExpandCollapseHelper.animateCollapsing(secondViewHolder.third_menu_container);
				}
			}
		}
	}
	
	// 一级菜单
	class ParentViewHolder {
		TextView groupTeaName;
		ImageView mgroupimage;
	}
	
    //二级菜单和三级菜单混合
	class SecondViewHolder {
		RelativeLayout second_menu_containerLayout;
		SimpleDraweeView childTeaImg;
		TextView childTeaName;
		LinearLayout third_menu_container;//三级菜单父视图
		ImageView mgroupimage;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}