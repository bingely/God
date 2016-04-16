package com.meetrend.haopingdian.activity;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.LeftItemBean;
import com.meetrend.haopingdian.adatper.ProductSearchSectionedAdapter;
import com.meetrend.haopingdian.adatper.ProductSectionedAdapter;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.ListViewUtil;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.EditTextWatcher;
import com.meetrend.haopingdian.widget.PinnedHeaderListView;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 商品库存 搜索
 * @author 肖建斌
 * 
 *  功能：展示 + 搜索
 *
 */
public class ProductSearchReportoryActivity extends BaseActivity{
	
	private final static String TAG = ProductSearchReportoryActivity.class.getName();
	
	private  LinearLayout.LayoutParams  PARAMS;
	
	@ViewInject(id = R.id.actionbar_home,click = "finishActivity")
	ImageView backView;
	@ViewInject(id = R.id.actionbar_title)
	TextView titleView;
	
	@ViewInject(id = R.id.clearbtn,click = "clearClick")
	ImageView clear;
	
	@ViewInject(id = R.id.left_container_layout)
	LinearLayout leftContainer;
	
	@ViewInject(id = R.id.pinnedListView)
	PinnedHeaderListView right_listview;
	
	@ViewInject(id = R.id.tea_search_edit)
	EditText searchEdit;
	@ViewInject(id = R.id.tea_searchbtn,click = "cancelClick")
	TextView searchBtn;
	
	@ViewInject(id = R.id.emptyview)
	TextView emptyview;

	@ViewInject(id = R.id.scrollview)
	ScrollView mScrollView;

	private boolean isScroll = true;
	
	private int size;

	private int select_res;
	private int unSelect_res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_kucun_search);
		FinalActivity.initInjectedView(ProductSearchReportoryActivity.this);

		init();

        searchEdit.addTextChangedListener(new EditTextWatcher() {
			public void afterTextChanged(Editable s) {
				
				if (s.length() == 0) {
					clear.setVisibility(View.GONE);
					searchBtn.setTextColor(Color.parseColor("#ff0000"));
					searchBtn.setText("取消");
				} else {
					searchBtn.setTextColor(Color.parseColor("#02bc00"));
					searchBtn.setText("搜索");
					clear.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void init(){
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		titleView.setText("库存搜索");
		select_res = R.drawable.icon_selected;
		unSelect_res = R.drawable.left_un_select_item_bg;
	}
	
	//结束页面
	public void cancelClick(View view){
		if (searchBtn.getText().toString().equals("搜索")) {
			loadData(searchEdit.getText().toString());
		}else {
			finish();
		}
	}
	
	//clear
	public void clearClick(View view){
		searchEdit.setText("");
	}
	
	public void loadData(String key) {
		showDialog();
		
		AjaxParams params = new AjaxParams();
		
		params.put("token", SPUtil.getDefault(ProductSearchReportoryActivity.this).getToken());
		params.put("storeId", SPUtil.getDefault(ProductSearchReportoryActivity.this).getStoreId());
		params.put("key", key);

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
				dimissDialog();
				
				if (isSuccess) {

					List<LeftItemBean> productTypeNames = new ArrayList<LeftItemBean>();
					List<TeaProductEntity> mList = new ArrayList<TeaProductEntity>();
					
					JsonArray jsonArr = data.get("catalogList").getAsJsonArray();
					Gson gson = new Gson();
					mList = gson.fromJson(jsonArr,new TypeToken<List<TeaProductEntity>>() {}.getType());
					if (null == mList || mList.size() == 0) {
						emptyview.setVisibility(View.VISIBLE);
					} else {
						emptyview.setVisibility(View.GONE);

						//right
						TeaProductEntity teaProductEntity = null;
						for (int i = 0; i < mList.size(); i++) {
							teaProductEntity = mList.get(i);
							LeftItemBean leftItemBean = new LeftItemBean();
							if (i == 0 ) 
								leftItemBean.isSelect = true;
							else
								leftItemBean.isSelect = false;
							
							leftItemBean.name = teaProductEntity.catalogName;
							productTypeNames.add(leftItemBean);//左边数据

							for (int j = 0; j < mList.get(i).nameList.size(); j++) {
								mList.get(i).nameList.get(j).isShowChildList = true;
							}
						}

						 final ProductSearchSectionedAdapter sectionedAdapter = new ProductSearchSectionedAdapter(ProductSearchReportoryActivity.this,
								 productTypeNames, mList);
					     right_listview.setAdapter(sectionedAdapter);

						 //left
						 mScrollView.setBackgroundColor(Color.parseColor("#ebedf0"));
						 int minBtnHeight =  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
								52.0f, getResources().getDisplayMetrics());
					     
					     	leftContainer.removeAllViews();
					     	TeaProductEntity entity = null;
							Button button = null;

					        size = mList.size();
					        for (int i = 0; i < size ; i++) {
					        	
								entity = mList.get(i);
								
								button = new Button(ProductSearchReportoryActivity.this);
								button.setId(i);
								button.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
								button.setTextColor(Color.parseColor("#000000"));
								button.setTextSize(16);
								button.setBackgroundResource(unSelect_res);
								button.setText(entity.catalogName);
								button.setMinHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
										52.0f, getResources().getDisplayMetrics()));//设置按钮的最小高度
					        	leftContainer.addView(button);
							}
					        
					        leftContainer.getChildAt(0).setBackgroundResource(select_res);
					        
					        right_listview.setOnScrollListener(new OnScrollListener() {
								
								@Override
								public void onScrollStateChanged(AbsListView arg0, int arg1) {
									
								}
								
								@Override
								public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
									
									RadioButton radioButton = null ;
									Button button = null ;
									
									if(isScroll){
										
										for (int i = 0; i < size; i++)
										{
											if (i == sectionedAdapter.getSectionForPosition(firstVisibleItem))
											{
												leftContainer.getChildAt(i).setBackgroundResource(select_res);
												
											}else{
												leftContainer.getChildAt(i).setBackgroundResource(unSelect_res);
											}
										}
											
									}else{
										isScroll = true;
									}
								}
							});
					        
					        
					        for (int i = 0; i < size; i++) {
								
					        	leftContainer.getChildAt(i).setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										isScroll = false;
										
										int id = v.getId();
									    int rightSection = 0;
										
										for (int j = 0; j < size; j++) {
											
											if (id != j) {
												leftContainer.getChildAt(j).setBackgroundResource(unSelect_res);
											}else{
												leftContainer.getChildAt(id).setBackgroundResource(select_res);
											}
										}
										
										for(int i=0; i< id; i++){
											rightSection += sectionedAdapter.getCountForSection(i)+1;
										}
										 right_listview.setSelection(rightSection);
									}
									
								});
					        	
							}
				        
						 dimissDialog();
					}
					
				} else {
					if (data.get("msg") != null) {
						showToast(data.get("msg").getAsString());
					}
				}
			}
		};
		
		CommonFinalHttp finalHttp = new CommonFinalHttp();
		finalHttp.get(Server.BASE_URL + Server.PRODUCT_LIST, params, callback);
	}
	
	//结束Activity
	public void finishActivity(View view){
		finish();
	}
	
}