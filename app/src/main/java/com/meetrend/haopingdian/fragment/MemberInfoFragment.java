package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.meetrend.haopingdian.App;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.ChatActivity;
import com.meetrend.haopingdian.activity.MemberInfoActivity;
import com.meetrend.haopingdian.activity.MemberInfoEditActivity;
import com.meetrend.haopingdian.activity.NewSelectExcutorActivity;
import com.meetrend.haopingdian.activity.PictrueBrowserActivity;
import com.meetrend.haopingdian.bean.MemberDetail.Mhistory;
import com.meetrend.haopingdian.bean.MemberDetail.Orderlt;
import com.meetrend.haopingdian.bean.MemberDetail;
import com.meetrend.haopingdian.enumbean.PageStatus;
import com.meetrend.haopingdian.enumbean.PayStatus;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.MemberInfoEvent;
import com.meetrend.haopingdian.event.RefreshEvent;
import com.meetrend.haopingdian.event.RfreshMemberInfoEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.DialogUtil;
import com.meetrend.haopingdian.widget.GridView;
import com.meetrend.haopingdian.widget.MyListView;
import de.greenrobot.event.EventBus;

/**
 * 通讯录会员详细资料
 *
 */
public class MemberInfoFragment extends BaseFragment {
	
	private final static String TAG = MemberInfoFragment.class.getName();
	
	private MemberDetail mMember;
	private String mName, mId;
	private String type;//标识员工和会员
	
	// actionbar
	@ViewInject(id = R.id.actionbar_home, click = "homeClick")
	ImageView mHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mTitle;
	@ViewInject(id = R.id.actionbar_action, click = "actionClick")
	TextView mAction;
	
	@ViewInject(id = R.id.iv_member_detail_photo,click = "photoClick")
	SimpleDraweeView photo;
	@ViewInject(id = R.id.iv_member_detail_gender)
	ImageView gender;
	@ViewInject(id = R.id.iv_member_detail_name)
	TextView mMemberName;
	@ViewInject(id = R.id.et_member_detail_phone, click = "mobileClick")
	TextView mobile;
	@ViewInject(id = R.id.tv_member_detail_note)
	TextView mNote;
	
	@ViewInject(id = R.id.btn_reinvite, click = "reinviteClick")
	TextView mReinviteBtn;	
	@ViewInject(id = R.id.btn_mobilemsg, click = "mobileMsgClick")
	TextView mMobileMsg;
	@ViewInject(id = R.id.btn_sendmsg, click = "sendMsgClick")
	TextView mSendMsgBtn;
	
	@ViewInject(id = R.id.msg_layout)
	LinearLayout msgLayout;
	
	@ViewInject(id = R.id.tv_age)
	TextView mAge;
	@ViewInject(id = R.id.layout_member_info)
	LinearLayout mLayout;

	@ViewInject(id = R.id.txt_OrderRecord)
	TextView v_OrderRecord;
	@ViewInject(id = R.id.listview)
	MyListView lv_OrderRecord;
	@ViewInject(id = R.id.v_choose)
	LinearLayout v_choose;
	@ViewInject(id = R.id.img_choose)
	GridView img_choose;
	@ViewInject(id = R.id.sv_view)
	ScrollView sv_view;
	@ViewInject(id = R.id.above_gridview_line)
	View lineView;
	@ViewInject(id = R.id.gridlayout)
	LinearLayout gridLayout;
	
	@ViewInject(id = R.id.iv_member_wx_name)
	TextView wxNcNameView;
	
	@ViewInject(id = R.id.undestributeimg)
	ImageView unDestributeImg;
	
	@ViewInject(id = R.id.ditributelayout,click = "distributeCick")
	LinearLayout distributeLayout;
	
	@ViewInject(id = R.id.customer_manager_tview)
	TextView ditributeTview;
	
	@ViewInject(id = R.id.top_hint_layout)
	LinearLayout topHintLayout;
	
	@ViewInject(id = R.id.list_progressbar)
	TextView listProgressBar;
	
	
	private String[] mAgeArray;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		EventBus.getDefault().register(this);
		
		View rootView = inflater.inflate(R.layout.fragment_member_detail, container, false);
		FinalActivity.initInjectedView(this, rootView);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mName = this.getArguments().getString("name");
		mId = this.getArguments().getString("id");
		
		mAgeArray = this.getResources().getStringArray(R.array.age_name);
		mTitle.setText("详细资料");
		
		v_OrderRecord.setVisibility(View.GONE);
		v_choose.setVisibility(View.GONE);
		lineView.setVisibility(View.GONE);
		gridLayout.setVisibility(View.GONE);
		
		getMemberDetail();
	}
	
	//选择分配人
	public void distributeCick(View view){
		
		Intent intent = new Intent(getActivity(), NewSelectExcutorActivity.class);
		intent.putExtra("exename", mMember.managerId);//managerId
		intent.putExtra("from", 3);//标识从该界面进入
		startActivity(intent);
	}
	
	//执行人名字
	public String name;
	
	public void onEventMainThread(MemberInfoEvent event) {
		
		String id = event.id;
		name = event.name;
		
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		
		builder.append("entityIds:[");
		builder.append(mMember.id);
		builder.append("]");
		
		builder.append(",");
		
		builder.append("data:{");
		builder.append("FManagerId:");
		builder.append(id+"}");
		
		builder.append("}");
		
		commitDistributRequest(builder.toString());
	}
	
	public void onEventMainThread(RfreshMemberInfoEvent event) {
		getMemberDetail();
	}
	
	Handler handler = new Handler();

	//加载推荐人，订单列表
	public Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			getDirect();
			getOrderRecord();
			listProgressBar.setVisibility(View.GONE);
			sv_view.smoothScrollTo(0, 0);//置顶
		}
	};
	
	
	
	private void getMemberDetail() {
		
		showDialog();
		
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(getActivity()).getToken());
        params.put("id", mId);
        
        Callback callback = new Callback(tag,getActivity()) {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
            	dimissDialog();
            	if (null != strMsg) {
            		showToast(strMsg);
				}
            }

            @Override
            public void onSuccess(String str) {
            	super.onSuccess(str);
				
            	if (isSuccess) {
            		
					mMember = App.memberDetail = new Gson().fromJson(data, MemberDetail.class);
					mLayout.setVisibility(View.VISIBLE);
					initView();

					//未分配图标的显示问题
					String managerId = mMember.managerId;
					if ("".equals(managerId)) {
						unDestributeImg.setVisibility(View.VISIBLE);
						ditributeTview.setText("马上分配");
					}else {
						unDestributeImg.setVisibility(View.GONE);
						//显示客户经理
						ditributeTview.setText(mMember.managerName);
					}

					//会员可以编辑资料，员工不可以
					if (null == mMember.type || "member".equals(mMember.type)) {
						mAction.setVisibility(View.VISIBLE);
						mAction.setText("编辑");
					}
					else {
						mAction.setVisibility(View.GONE);
					}

					if (mMember.canTalk) {
						topHintLayout.setVisibility(View.GONE);
						mSendMsgBtn.setEnabled(true);
					}else {

						ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 0f, 1.0f,
								ScaleAnimation.RELATIVE_TO_SELF, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0f);
						scaleAnimation.setDuration(500);
						scaleAnimation.setFillAfter(true);
						topHintLayout.setAnimation(scaleAnimation);
						scaleAnimation.startNow();
						topHintLayout.setVisibility(View.VISIBLE);
						mSendMsgBtn.setEnabled(false);
					}

					dimissDialog();

					if (mMember.direct.size()>0 || mMember.history.size()>0) {
						listProgressBar.setVisibility(View.VISIBLE);
					}

					handler.postDelayed(mRunnable, 300);

				} else {
					
					if (data.has("msg")) {
						showToast(data.get("msg").toString());
					}else {
						showToast("无法获取数据");
					}
				}			
            }
        };
        
        CommonFinalHttp http = new CommonFinalHttp();
        http.get(Server.BASE_URL + Server.MEMBER_INFO_URL, params, callback);
	}
	
	/**
	 * 更新客户经理即分配人
	 */
	private void commitDistributRequest(String json){
		
		showDialog("正在提交分配人信息...");
		
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(getActivity()).getToken());
        params.put("args", json);
        
        Callback callback = new Callback(tag,getActivity()) {
		 @Override
         public void onFailure(Throwable t, int errorNo, String strMsg) {
			 dimissDialog();
         }

         @Override
         public void onSuccess(String str) {
        	 super.onSuccess(str);
         	Log.i(TAG +"分配人提交结果", str);
         	dimissDialog();
         	
         	if (isSuccess) {
         		ditributeTview.setText(name);
				Toast.makeText(getActivity(), "分配人更新成功", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getActivity(), "分配人更新失败", Toast.LENGTH_SHORT).show();
			}
         }
     };
     
     FinalHttp http = new FinalHttp();
     http.get(Server.BASE_URL + Server.COMMIT_DISTRIBUTE_REQUEST, params, callback);
     
	}
	
	/**
	 * 得到推荐人
	 */
	public void getDirect(){
		if(mMember.direct.size()>0){
			
			v_choose.setVisibility(View.VISIBLE);
			lineView.setVisibility(View.VISIBLE);
			gridLayout.setVisibility(View.VISIBLE);
			img_choose.setAdapter(new BaseAdapter() {
				private LayoutInflater layoutInflater = LayoutInflater.from(mActivity);

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					ViewHolder holder = null;
					if (convertView == null) {
						holder = new ViewHolder();
						convertView = layoutInflater.inflate(R.layout.item_choose, null);
						holder.name = (TextView) convertView.findViewById(R.id.tex_name);
						holder.pictureId = (SimpleDraweeView) convertView.findViewById(R.id.img_headimg);
						convertView.setTag(holder);
					} else {
						holder = (ViewHolder) convertView.getTag();
					}
					holder.name.setText(mMember.direct.get(position).name);
					holder.pictureId.setImageURI(Uri.parse(Server.BASE_URL + mMember.direct.get(position).pictureId));
					return convertView;
				}

				@Override
				public long getItemId(int arg0) {
					return 0;
				}

				@Override
				public Object getItem(int arg0) {
					return mMember.direct.get(arg0);
				}

				@Override
				public int getCount() {
					return mMember.direct.size();
				}

				final class ViewHolder {
					public TextView name;
					public SimpleDraweeView pictureId;
				}

				;
			});

			//推荐人
			img_choose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					 MemberDetail.Direct direct  = mMember.direct.get(i);
					Intent intent = new Intent(getActivity(), MemberInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("name",direct.name);
					bundle.putString("id", direct.userId);
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().finish();
				}
			});
		}
	}
		
	/**
	 * 得到订单记录
	 */
	public void getOrderRecord(){
		if(mMember.history.size()>0){
			v_OrderRecord.setVisibility(View.VISIBLE);
			lv_OrderRecord.setAdapter(new OrderListRecordAdapter(mActivity, mMember.history));
		}
	}
	
	
	class OrderListRecordAdapter extends BaseAdapter {
		private FinalBitmap loader;
		private List<Mhistory> list;
		private LayoutInflater mLayoutInflater;
		private PageStatus pageStatus;

		public OrderListRecordAdapter(Context context, ArrayList<Mhistory> list) {
			mLayoutInflater = LayoutInflater.from(context);
			this.list = list;
			loader = FinalBitmap.create(context);
			loader.configBitmapMaxHeight(50);
			loader.configBitmapMaxWidth(50);
			loader.configLoadingImage(R.drawable.loading_default);
			loader.configLoadfailImage(R.drawable.loading_failed);
		}

		public void setType(PageStatus pageStatus){
			this.pageStatus = pageStatus;
		}
		
		public void setList(List<Mhistory> list){
			this.list = list;
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
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_order_record, null);
				holder = new ViewHolder();
				
				holder.mOrdersName = (TextView) convertView.findViewById(R.id.order_Record_number);
				holder.mOrdersTeaName = (TextView) convertView.findViewById(R.id.order_Record_dummy);
				holder.mOrdersTime = (TextView) convertView.findViewById(R.id.order_Record_create_time);
				holder.mStatuss = (ImageView) convertView.findViewById(R.id.order_Record_status);
				holder.lv_orderinfo = (MyListView) convertView.findViewById(R.id.lv_orderinfo);
				holder.txt_total = (TextView) convertView.findViewById(R.id.txt_total);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Mhistory mDetail = list.get(position);
			holder.mOrdersName.setText("订单编号: " + mDetail.orderName);
			holder.mOrdersTeaName.setText("订单人:"+mDetail.userId);
			holder.mOrdersTime.setText("下单时间:"+mDetail.createTime);
			PayStatus payStatus = PayStatus.get(mDetail.payStatus);
			int resId;
			switch (payStatus) {
			case UNPAY:
				resId = payStatus.UNPAY.getResourceId();
				break;
			case PAYED:
				resId = payStatus.PAYED.getResourceId();
				break;
			default:
				resId = payStatus.getResourceId();
			}
			holder.mStatuss.setBackgroundResource(resId);
			
			OrderSingleAdapter singleAdapter = new OrderSingleAdapter(getActivity(),mDetail.detail);
			holder.lv_orderinfo.setAdapter(singleAdapter);
			holder.txt_total.setText("合计：¥"+mDetail.receivableAmount);
			
			return convertView;
		}

		final class ViewHolder {
			
			//订单编号
			public TextView mOrdersName;
			//订单人头像
			public ImageView mAvatars;
			//订单人
			public TextView mOrdersTeaName;
			//订单时间
			public TextView mOrdersTime;
			//订单状态
			public ImageView mStatuss;
			
			public MyListView lv_orderinfo;
			//合计
			public TextView txt_total;
		};

	}
	
	class OrderSingleAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private ArrayList<MemberDetail.Orderlt> orderlts = new ArrayList<MemberDetail.Orderlt>();
		
		public OrderSingleAdapter(Context context,
				ArrayList<Orderlt> orderlts) {
			super();
			mInflater = LayoutInflater.from(context);
			this.orderlts = orderlts;
		}

		@Override
		public int getCount() {
			return orderlts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return orderlts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_order_info_head, null);
				holder.avatarId = (SimpleDraweeView) convertView.findViewById(R.id.order_info_avatar);
				holder.productName = (TextView) convertView.findViewById(R.id.order_info_teaname);
				holder.price = (TextView) convertView.findViewById(R.id.order_info_price);
				holder.quantity = (TextView) convertView.findViewById(R.id.order_info_num);
				holder.total = (TextView) convertView.findViewById(R.id.order_info_total);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.avatarId.setImageURI(Uri.parse(Server.BASE_URL + orderlts.get(position).avatarId));
			holder.productName.setText(orderlts.get(position).productName);
			holder.price.setText("单价:"+orderlts.get(position).price);
			holder.quantity.setText("数量:"+orderlts.get(position).quantity);
			if (!TextUtils.isEmpty(orderlts.get(position).quantity) && !TextUtils.isEmpty(orderlts.get(position).price)) {
				holder.total.setText("总价:"+Float.parseFloat(orderlts.get(position).price)*Float.parseFloat(orderlts.get(position).quantity));
			}else {
				holder.total.setText("总价:0.0");
			}
			return convertView;
		}
		
		final class ViewHolder {
			/**
			 * 茶品牌名
			 */
			public TextView productName;
			/**
			 * 产品图片
			 */
			public SimpleDraweeView avatarId;
			/**
			 * 数量(饼)
			 */
			public TextView quantity;
			/**
			 * 数量(件)
			 */
			public TextView offerPieceQty;
			/**
			 * 产品批次
			 */
			public TextView productPici;
			/**
			 * 单位名称
			 */
			public TextView unitName;
			/**
			 * 产品单价
			 */
			public TextView price;
			/**
			 * 产品总价
			 */
			public TextView total;
		};
	}

	private int status;//0：未邀请1：已邀请2：已绑定
	private void initView() {

		photo.setImageURI(Uri.parse(Server.BASE_URL + mMember.pictureId));

			if (mMember.gender.equals("1")) {
				gender.setImageResource(R.drawable.male);
			} else if (mMember.gender.equals("2")) {
				gender.setImageResource(R.drawable.female);
			} else {
				gender.setVisibility(View.INVISIBLE);
			}

			//微信昵称
			mMemberName.setText(mMember.name);
			if (null == mMember.nickName || "".equals(mMember.nickName)) {
				wxNcNameView.setVisibility(View.GONE);
			}else {
				wxNcNameView.setText("微信昵称: "+mMember.nickName);
			}
			
			mobile.setText(mMember.mobile);
			mNote.setText(mMember.description);

			if (!mMember.ageGroup.equals("")&& !mMember.ageGroup.equals("0") && !mMember.ageGroup.equals("-1")) {
				int index = Integer.parseInt(mMember.ageGroup);
				mAge.setText(mAgeArray[index - 1]);
			}

			if (TextUtils.isEmpty(mMember.mobile)){
				//电话号码为空，不能邀请和发短信
				mMobileMsg.setEnabled(false);
				mReinviteBtn.setEnabled(false);
			}

			if (!TextUtils.isEmpty(mMember.status)) {
				status = Integer.parseInt(mMember.status);
				switch (status) {
				case 0:
					mReinviteBtn.setVisibility(View.VISIBLE);
					mMobileMsg.setVisibility(View.VISIBLE);
					mSendMsgBtn.setVisibility(View.GONE);
					break;
				case 1:
					mReinviteBtn.setVisibility(View.VISIBLE);
					mReinviteBtn.setText("再次发送邀请");
					mMobileMsg.setVisibility(View.VISIBLE);
					mSendMsgBtn.setVisibility(View.GONE);
					break;
				case 2:
					mReinviteBtn.setVisibility(View.GONE);
					mMobileMsg.setVisibility(View.VISIBLE);
					mSendMsgBtn.setVisibility(View.VISIBLE);
					break;
				}
			}else{
				mReinviteBtn.setVisibility(View.VISIBLE);
			}

	}
	
	//用户头像click
	public void photoClick(View view){
		Intent intent = new Intent(getActivity(), PictrueBrowserActivity.class);
		intent.putExtra("img_url", Server.BASE_URL + mMember.pictureId);
		getActivity().startActivity(intent);
	}
	
	public void homeClick(View view) {
		mActivity.finish();
	}

	//编辑
	public void actionClick(View view) {
		
		Intent intent = new Intent(getActivity(), MemberInfoEditActivity.class);
		intent.putExtra("status",status+"");//成员状态
		startActivity(intent);
	}
	
	
	//发送手机短信
	public void mobileMsgClick(View view){
		
		Uri smsToUri = Uri.parse("smsto:"+ mMember.mobile);  
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
		startActivity(intent);  
	}
	

	/**
	 * 
	 * 发送邀请
	 * */
	public void reinviteClick(View view) {

		DialogUtil dialogUtil = new DialogUtil(getActivity());
		if (mReinviteBtn.getText().toString().equals("再次发送邀请"))
			dialogUtil.inviteMemberDialog("再次发送邀请？","取消","确定");
		else
			dialogUtil.inviteMemberDialog("将发送邀请码短信至15820101621","取消","确定");

		dialogUtil.setMakeSureIn(new DialogUtil.MakeSureIn() {
			@Override
			public void sure() {
				inviteRequest();
			}
		});
	}

	private void inviteRequest(){
		showDialog("邀请中...");
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("mobile", mMember.mobile);//关键
		params.put("name", mMember.name);
		params.put("nickName", mMember.nickName);
		params.put("id", mMember.id);

		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				LogUtil.v(tag, "invite member info " + t);
				super.onSuccess(t);
				dimissDialog();

				if (isSuccess) {
					showToast("发送邀请成功");
					EventBus.getDefault().post(new RefreshEvent());
					//刷新通讯录列表
					getActivity().finish();
				} else {
					showToast("发送邀请失败");
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showToast("发送邀请失败");
			}
		};

		FinalHttp http = new FinalHttp();
		http.get(Server.BASE_URL + Server.INVITE_MEMBER_URL, params, callback);
	}
	
	
	/**
	 * 
	 * 发送消息
	 * 
	 * */
	public void sendMsgClick(View v){
		Bundle bundle = new Bundle();
		bundle.putString("user_id", mMember.userId);
		bundle.putString("name", mMember.name);
		bundle.putString("avatarId", mMember.pictureId);
		bundle.putBoolean("canTalk", mMember.canTalk);
		Intent intent = new Intent(mActivity, ChatActivity.class);
		intent.putExtras(bundle);
		mActivity.startActivity(intent);
	}

	public void mobileClick(View view) {
		AlertDialog.Builder  builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("拨打电话 " + mMember.mobile + "?");
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:" + mMember.mobile));
				startActivity(intent);
			}
		});
		builder.create().show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		 if (mRunnable != null)
		 	handler.removeCallbacks(mRunnable);
		 App.memberDetail = null;
	}
}