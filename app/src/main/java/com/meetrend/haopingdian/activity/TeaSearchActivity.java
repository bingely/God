package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.Products;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.FINISH_EVENT;
import com.meetrend.haopingdian.event.TeaAddEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.InputTools;
import com.meetrend.haopingdian.widget.EditTextWatcher;
import com.meetrend.haopingdian.widget.MyListView;
import de.greenrobot.event.EventBus;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 茶品搜索
 * 
 * @author 肖建斌
 * 
 */
public class TeaSearchActivity extends BaseActivity {
	
	@ViewInject(id = R.id.back_img,click = "finishActivity")
	ImageView backView;
//	@ViewInject(id = R.id.actionbar_title)
//	TextView titleView;

	@ViewInject(id = R.id.search_edit)
	EditText searchEdit;
	@ViewInject(id = R.id.searchbtn)
	TextView searchBtn;
	@ViewInject(id = R.id.search_exbandlistview)
	ExpandableListView listview;
	@ViewInject(id = R.id.emptyview)
	RelativeLayout emptyView;
	
	@ViewInject(id = R.id.clearbtn,click = "clearClick")
	ImageView clear;

	MyExpandableAdapter mAdapter = null;
	List<Products> mProducts;

	LayoutInflater mLayoutInflater = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tea_search);
		FinalActivity.initInjectedView(this);

		InputTools.ShowKeyboard(searchEdit);
		

		mAdapter = new MyExpandableAdapter(new ArrayList<TeaProductEntity>());
		listview.setAdapter(mAdapter);

		searchEdit.addTextChangedListener(new EditTextWatcher(){
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					clear.setVisibility(View.GONE);
					searchBtn.setText("取消");
				}else {
					clear.setVisibility(View.VISIBLE);
					searchBtn.setText("搜索");
				}
			}
		});

		// 搜索
		searchBtn.setOnClickListener(new SearchBtnClickListener());

		mLayoutInflater = LayoutInflater.from(this);

		listview.setGroupIndicator(null);
	}

	public class SearchBtnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (searchBtn.getText().toString().equals("搜索")) {
				if (hasDialog()) {
					return;
				}
				showDialog();
				loadData(searchEdit.getText().toString());
			} else {
				finish();
			}
		}
	}
	
	public void clearClick(View view){
		searchEdit.setText("");
	}

	public void loadData(String key) {

		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(TeaSearchActivity.this)
				.getToken());
		params.put("storeId", SPUtil.getDefault(TeaSearchActivity.this)
				.getStoreId());
		params.put("key", key);

		Callback callback = new Callback(tag, this) {

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
				LogUtil.v(tag, "login info : " + t);
				
				if (isSuccess) {

					JsonArray jsonArr = data.get("catalogList").getAsJsonArray();
					Gson gson = new Gson();
					List<TeaProductEntity> datalist = gson.fromJson(jsonArr,new TypeToken<List<TeaProductEntity>>() {}.getType());

					if (datalist.size() == 0) {
						
						emptyView.setVisibility(View.VISIBLE);
						
					} else {
						
						if (emptyView.getVisibility() == View.VISIBLE) {
							emptyView.setVisibility(View.GONE);
						}
						
						MyExpandableAdapter searchAdapter = new MyExpandableAdapter(datalist);
						listview.setAdapter(searchAdapter);
						//mAdapter.setList(datalist);
						//mAdapter.notifyDataSetChanged();

						int groupCount = listview.getCount();
						for (int i = 0; i < groupCount; i++) {

							listview.expandGroup(i);
						}
						
					}
				} else {
					if (data.get("msg") != null) {
						showDialog(data.get("msg").getAsString());
					}
				}

				dimissDialog();
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.PRODUCT_LIST, params, callback);
	}


	class MyExpandableAdapter extends BaseExpandableListAdapter {
		
		private List<TeaProductEntity> teaList;
		
		public MyExpandableAdapter(List<TeaProductEntity> paramList){
			this.teaList  = paramList;
		}
		
		public void setList(List<TeaProductEntity> list){
			this.teaList = list;
		}
		
		@Override
		public Products getChild(int arg0, int arg1) {
			return teaList.get(arg0).nameList.get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return 0;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return teaList.get(arg0).nameList.size();
					
		}

		@Override
		public TeaProductEntity getGroup(int arg0) {
			return teaList.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return teaList.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean arg2, View childView, ViewGroup arg4) {

			SecondViewHolder sHolder = null;
			if (childView == null || childView.getTag() == null) {
				sHolder = new SecondViewHolder();
				childView = mLayoutInflater.inflate(
						R.layout.search_tea_list_childview_layout, arg4, false);
				sHolder.second_menu_containerLayout = (RelativeLayout) childView
						.findViewById(R.id.second_menu_container);
				sHolder.childTeaImg = (SimpleDraweeView) childView
						.findViewById(R.id.sencond_img);
				sHolder.childTeaName = (TextView) childView
						.findViewById(R.id.secon_txt);
				sHolder.sencond_right_icon = (ImageView) childView
						.findViewById(R.id.sencond_right_icon);
				sHolder.mgroupimage = (ImageView) childView
						.findViewById(R.id.mGroupimage);
				sHolder.listView = (MyListView) childView
						.findViewById(R.id.third_listview);
				childView.setTag(sHolder);
			} else {
				sHolder = (SecondViewHolder) childView.getTag();
			}
			
			final Products product = (Products) getChild(groupPosition,childPosition);
					
			List<Pici> list = product.productList;// 批次数据集合

			sHolder.childTeaName.setText(product.productName);
			sHolder.mgroupimage.setImageResource(R.drawable.second_bottom_arrow);// 默认向下
			sHolder.childTeaImg.setImageURI(Uri.parse(Server.BASE_URL+ product.productList.get(0).productImage));
					
			
			if (list.size() == 1) {
				 
				 sHolder.mgroupimage.setVisibility(View.GONE);//隐藏
				 sHolder.childTeaName.setText(list.get(0).fullName);//显示产品全名
				 
			}else {
				
				sHolder.mgroupimage.setVisibility(View.VISIBLE);//显示
				sHolder.childTeaName.setText(product.productName);
				
				sHolder.listView.setAdapter(new ThirdListAdapter(list));
				//ListViewUtil.setListViewHeightBasedOnChildren(sHolder.listView);
				sHolder.listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						EventBus.getDefault().post(new FINISH_EVENT());
						EventBus.getDefault().post(
								new TeaAddEvent(product, product.productList
										.get(position),1.000));
						TeaSearchActivity.this.finish();
					}

				});
			}
			
			 //判断是否显示第三级菜单(这里是ListView)
			 if (!product.isShowChildList) {
				 sHolder.mgroupimage.setImageResource(R.drawable.second_right_arrow);
				 sHolder.listView.setVisibility(View.VISIBLE);
			 }else {
				 sHolder.mgroupimage.setImageResource(R.drawable.second_bottom_arrow);
				 sHolder.listView.setVisibility(View.GONE);
			 }

			sHolder.second_menu_containerLayout
					.setOnClickListener(new AddThirdMenuToLayout(sHolder, product, list, sHolder.mgroupimage));

			return childView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View v,
				ViewGroup arg3) {
			ParentViewHolder mHolder = null;
			if (v == null || v.getTag() == null) {
				mHolder = new ParentViewHolder();
				v = mLayoutInflater.inflate(R.layout.item_group_tea, null);
				mHolder.groupTeaName = (TextView) v
						.findViewById(R.id.tv_tea_name);
				mHolder.mgroupimage = (ImageView) v
						.findViewById(R.id.mGroupimage);
				v.setTag(mHolder);
			} else {
				mHolder = (ParentViewHolder) v.getTag();
			}
			mHolder.groupTeaName.setText(getGroup(groupPosition).catalogName);
			if (isExpanded) {
				mHolder.mgroupimage.setImageResource(R.drawable.boottom_arrow);
			} else {
				mHolder.mgroupimage.setImageResource(R.drawable.right_arrow);
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


	//第二季菜单click事件
	public class AddThirdMenuToLayout implements OnClickListener {

		SecondViewHolder secondViewHolder;
		List<Pici> list;
		ImageView imageView;
		Products products;

		public AddThirdMenuToLayout(SecondViewHolder secondViewHolder,
				Products products, List<Pici> list,ImageView imageView) {
				
			this.secondViewHolder = secondViewHolder;
			this.list = list;
			this.products = products;
			this.imageView = imageView;
		}

		@Override
		public void onClick(View v) {

			if (list.size() == 1) {
				try {
					EventBus.getDefault().post(new FINISH_EVENT());
					EventBus.getDefault().post(new TeaAddEvent(products, list.get(0),1.000));
					TeaSearchActivity.this.finish();
				} catch (Exception e) {
					Toast.makeText(TeaSearchActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
				}

			} else {

				if (secondViewHolder.listView.getVisibility() == View.GONE) {
					products.isShowChildList = false;//取值和TeaTypeListActivity相反
					imageView.setImageResource(R.drawable.second_right_arrow);
					secondViewHolder.listView.setVisibility(View.VISIBLE);
				} else {
					products.isShowChildList = true;
					imageView.setImageResource(R.drawable.second_bottom_arrow);
					secondViewHolder.listView.setVisibility(View.GONE);
				}

			}

		}
	}

	// 一级菜单
	class ParentViewHolder {
		TextView groupTeaName;
		ImageView mgroupimage;
	}

	// 二级菜单和三级菜单混合
	class SecondViewHolder {
		RelativeLayout second_menu_containerLayout;
		ImageView childTeaImg;
		TextView childTeaName;
		ImageView sencond_right_icon;
		ImageView mgroupimage;
		
		//三级菜单
		MyListView listView;
	}

	//三级菜单listview适配器
	public class ThirdListAdapter extends BaseAdapter {

		List<Pici> list;
		LayoutInflater layoutInflater = null;

		public ThirdListAdapter(List<Pici> list) {
			this.list = list;
			layoutInflater = LayoutInflater.from(TeaSearchActivity.this);
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

		@SuppressWarnings("unused")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ThirdHolder thirdHolder = null;
			if (thirdHolder == null) {
				thirdHolder = new ThirdHolder();
				convertView = layoutInflater.inflate(
						R.layout.tea_search_third_item_layout, null);
				thirdHolder.thirdImg = (ImageView) convertView
						.findViewById(R.id.third_img);
				thirdHolder.thirdName = (TextView) convertView
						.findViewById(R.id.pici_name);
				convertView.setTag(thirdHolder);
			} else {
				thirdHolder = (ThirdHolder) convertView.getTag();
			}

			Pici pici = list.get(position);
			
			//thirdHolder.thirdName.setText(pici.model1Name + " "
					//+ pici.model2Name);
			
			 if (TextUtils.isEmpty(pici.model2Value)) {
				 thirdHolder.thirdName.setText(pici.model1Value);
			 }else {
				 thirdHolder.thirdName.setText(pici.model1Value + " "+ pici.model2Value);
			 }
			
			thirdHolder.thirdImg.setImageURI(Uri.parse(Server.BASE_URL
					+ pici.productImage));

			return convertView;
		}

	}

	class ThirdHolder {
		ImageView thirdImg;
		TextView thirdName;
	}
	
	public void finishActivity(View view){
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		TeaSearchActivity.this.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
	}
}