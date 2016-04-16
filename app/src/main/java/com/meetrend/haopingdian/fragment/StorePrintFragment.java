package com.meetrend.haopingdian.fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import com.google.gson.Gson;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.DiviceListAdapter;
import com.meetrend.haopingdian.bean.Device;
import com.meetrend.haopingdian.bean.OnlineOrderDetail;
import com.meetrend.haopingdian.bean.OnlineOrderDetail.JsonArray;
import com.meetrend.haopingdian.bean.WorkState;
import com.meetrend.haopingdian.enumbean.OrderStatus;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.NumerUtil;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 小票打印
 * 
 * @author 肖建斌
 *
 */
public class StorePrintFragment extends BaseFragment{
	
	private final static String TAG = StorePrintFragment.class.getName();
	
	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;
	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;
	
	/**
	 * 传递的端口号,定值
	 */
	private String sendPort = "10000";
	
	/**
	 * 传递的ip
	 */
	private String sendIp ;
	
	/**
	 * 订单号
	 */
	@ViewInject(id = R.id.order_num)
	TextView orderNumView;
	
	/**
	 * 下单人
	 */
	@ViewInject(id = R.id.order_name)
	private TextView orderPeopleView;
	
	/**
	 * 下单人的头像
	 */
	@ViewInject(id = R.id.order_people_img)
	ImageView orderImg;
	
	/**
	 * 下单的时间
	 */
	@ViewInject(id = R.id.order_date)
	TextView orderTime;
	
	/**
	 * 订单的状态
	 */
	
	@ViewInject(id = R.id.buy_state_icon)
	ImageView orderStateIcon;
	
	
	/**
	 * 单个产品视图
	 * 
	 */
	@ViewInject(id = R.id.one_product_layout)
	LinearLayout oneLinear;
	@ViewInject(id = R.id.tea_imgview)
	ImageView oneTeaImageView;

	@ViewInject(id = R.id.tea_name_view)
	TextView oneTeaNameView;

	@ViewInject(id = R.id.tea_price)
	TextView oneTeaPrice;

	@ViewInject(id = R.id.tea_num_view)
	TextView oneTeaNum;

	@ViewInject(id = R.id.tea_total_price_view)
	TextView oneTeaNumAllMomeyView;

	/**
	 * 多个产品视图
	 */
	@ViewInject(id = R.id.more_product_container)
	RelativeLayout moreRelative;
	@ViewInject(id = R.id.more_product_img_layout)
	LinearLayout imgsContainer;

	/**
	 * 显示执行人view
	 */
	@ViewInject(id = R.id.excutor_name)
	TextView excutorPeopleView;



	/**
	 * 客户名字
	 */
	@ViewInject(id = R.id.customer_name)
	TextView customerNameView;

	/**
	 * 客户电话号码
	 */
	@ViewInject(id = R.id.customer_phone)
	TextView phoneView;


	/**
	 * 积分
	 */
	@ViewInject(id = R.id.jifen_view)
	TextView jifenView;


	/**
	 * 优惠券
	 */
	@ViewInject(id = R.id.choose_jifen_quan_view)
	TextView jifenQuanView;



	/**
	 * 商品金额
	 */
	@ViewInject(id = R.id.all_products_money_view)
	TextView allMoneyView;

	/**
	 * 实收金额
	 */
	@ViewInject(id = R.id.detail_all_products_give_money_view)
	TextView giveMoneyEdit;

	/**
	 * 底部栏实收金额
	 */
	@ViewInject(id = R.id.detail_bottom_should_givew_money_view)
	TextView bottomShouldGiveMoneyView;

	/**
	 * 结账
	 */
	@ViewInject(id = R.id.printBtn, click = "printClick")
	TextView printBtn;
	
	/**
	 * 选择的茶数量
	 */
	@ViewInject(id = R.id.tea_list_count)
	TextView teaCountView;
	//单号
	String orderId;
	
	OnlineOrderDetail onlineOrderDetail = null;
	
	private FinalBitmap finalBp;
	
	private final static int SEARCH_LUYOU_FAIALED = 0x189;//广播失败
	private final static int SEARCH_DEVICES_FAIALED = 0x188;//搜索打印设备失败
	private final static int PRINT_FINISH = 0x187;//打印成功
	
	private final static int INIT_DATA_SUCCESS = 0x182;//数据初始化成功
	private final static int DIVICE_EXCEPTION = 0x181;//数据初始化成功
	
	private final static int NO_DIVICE_CAN_CONNACT = 0x180;//没有可连接的设备
	
	private final static int START_PRINT = 0x458;//开始打印
	
	private final static int UPDATE_LIST = 0X459;//更新列表
	
	private PrintDialog printdialog = null;
	
	/**
	 * 打印之前选中过的ip打印
	 */
	private String hasSelectIp;
	
	private String wgIP;//网关ip
	private UpDateDvListThread upDateDvListThread = null;
	private CheckHasDiviceThread checkHasDiviceThread = null;
	private DatagramPacket packet;
	private DatagramSocket socket;
	private DatagramPacket getpacket;
	private byte data2[] = new byte[4 * 1024];
	boolean sendDatagram = true;

	//存储的设备集合
	List<Device> dvList = new ArrayList<Device>();

	boolean isConnected=false;
	Socket client;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	
	//二维码图片
	private Bitmap mErweiMaBitmap;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_storeprint, container, false);
		FinalActivity.initInjectedView(this, rootView);
		mActivity = this.getActivity();
		mBarTitle.setText("订单详情");
		return rootView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		finalBp = FinalBitmap.create(getActivity());
		finalBp.configLoadingImage(R.drawable.loading_default);
		finalBp.configLoadfailImage(R.drawable.loading_failed);
		
		orderId = SPUtil.getDefault(getActivity()).getOrderId();
		if (!TextUtils.isEmpty(orderId)) {
			detail(orderId);
		}else {
			Toast.makeText(getActivity(), "订单id为空", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 *  订单详情
	 *  @param orderId
	 */
	private void detail(String orderId){
		
		showDialog("打印初始化...");
		
		AjaxParams params = new AjaxParams();
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("oid", orderId);
		
		Callback callback = new Callback(tag,getActivity()) {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				if (!isSuccess) {
					showToast("打印数据初始化失败,关闭页面请重试！");
					dimissDialog();
					return;
				}

				Gson gson = new Gson();
					
				onlineOrderDetail = gson.fromJson(data, OnlineOrderDetail.class);
				
				Log.i("------二维码图片地址--footerUrl--------------", onlineOrderDetail.footerUrl);
				
				String localPath = SPUtil.getDefault(getActivity()).getErWeiMaPath();
				//下载图片
				if (!TextUtils.isEmpty(onlineOrderDetail.footerUrl)) {
					 
					 Bitmap localBitmap = BitmapFactory.decodeFile(localPath);
					
					if (TextUtils.isEmpty(localPath) || null == localBitmap) {
						DownLoadPictrueThread downLoadPictrueThread = new DownLoadPictrueThread(onlineOrderDetail.footerUrl);
						downLoadPictrueThread.start();
					}
				}
				
				if (onlineOrderDetail.jsonArray.size() == 1) {
					
					oneLinear.setVisibility(View.VISIBLE);
					moreRelative.setVisibility(View.GONE);

					finalBp.configBitmapMaxHeight(oneTeaImageView.getLayoutParams().height);
					finalBp.configBitmapMaxWidth(oneTeaImageView.getLayoutParams().width);
					JsonArray item = onlineOrderDetail.jsonArray.get(0);

					finalBp.display(oneTeaImageView, Server.BASE_URL
							+ item.avatarId);// 茶图片
					oneTeaNameView.setText(item.fullName);// 茶品名
					oneTeaNum.setText("数量："+ NumerUtil.saveThreeDecimal(item.quantity)+"件");//件数
					oneTeaPrice.setText("单价：¥" + item.piecePrice);// 单价
					oneTeaNumAllMomeyView.setText("总价：¥" + NumerUtil.getNum(onlineOrderDetail.detailAmount));// 总价

				} else {
					oneLinear.setVisibility(View.GONE);
					moreRelative.setVisibility(View.VISIBLE);
					int count = onlineOrderDetail.jsonArray.size();
					teaCountView.setText(NumerUtil.saveOneDecimal(count+"")+"件");

					for (int i = 0; i < count; i++) {
						JsonArray entity = onlineOrderDetail.jsonArray.get(i);
						ImageView childImg = (ImageView) imgsContainer
								.getChildAt(i);
						childImg.setVisibility(View.VISIBLE);
						finalBp.configBitmapMaxHeight(childImg.getLayoutParams().height);
						finalBp.configBitmapMaxWidth(childImg.getLayoutParams().width);
						finalBp.display(childImg, Server.BASE_URL
								+ entity.avatarId);// 茶图片
					}

					moreRelative.setOnClickListener(new OnClickListener() {


						@Override
						public void onClick(View v) {
							Bundle bundle = new Bundle();
							bundle.putParcelableArrayList("list", onlineOrderDetail.jsonArray);
							bundle.putInt("type", 2);

							MoreTeatListFragment moreTestListFragment = new MoreTeatListFragment();
							moreTestListFragment.setArguments(bundle);
							getActivity()
									.getSupportFragmentManager()
									.beginTransaction()
									.addToBackStack(null)
									.add(R.id.print_container, moreTestListFragment)
									.commit();
						}
					});
				}
				 
				//订单编号
				orderNumView.setText("订单编号："+onlineOrderDetail.orderName);
				
				//下单人
				orderPeopleView.setText("下单人："+onlineOrderDetail.createUserName);
				 
				//执行人
				excutorPeopleView.setText(onlineOrderDetail.executeUserName);
				
				//下单时间
				orderTime.setText("下单时间："+onlineOrderDetail.createTime);
				
				OrderStatus status = OrderStatus.get(onlineOrderDetail.status);
				
				int resId = -1;
				
				switch (status) {
				//待确认
				case UN_CONFIRMED:
					
					resId = OrderStatus.UN_CONFIRMED.getResourceId();
					break;
				//待付款
				case WAIT_APY:
					resId = OrderStatus.WAIT_APY.getResourceId();
					break;
					//待发货
				case HAVE_PAY:
					 resId = OrderStatus.HAVE_PAY.getResourceId();
					break;
				//已完成
				case FINISHED:
					resId = OrderStatus.FINISHED.getResourceId();
					break;
				//已取消
				case CANCELED:
					resId = OrderStatus.CANCELED.getResourceId();
					break;
				}
				
				//状态图片
				orderStateIcon.setImageResource(resId);
				
				//客户名字
				customerNameView.setText(onlineOrderDetail.userName);
				//客户电话
				phoneView.setText(onlineOrderDetail.shipPhone);//App.servicePhone
				
				//商品金额
				allMoneyView.setText(NumerUtil.getNum(onlineOrderDetail.detailAmount));
				//实收金额
				giveMoneyEdit.setText(NumerUtil.getNum(onlineOrderDetail.paidAmount));
				//底部金额显示
				bottomShouldGiveMoneyView.setText("总价 ：¥"+ NumerUtil.getNum(onlineOrderDetail.receivableAmount));
				
				 dimissDialog();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				dimissDialog();
				if (null != strMsg) {
					showToast(strMsg);
				}
				
			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL+Server.ONLINE_ORDER_DETAIL_URL, params, callback);
	}
	
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			
			case SEARCH_LUYOU_FAIALED:
				showToast("向路由器器发送广播异常");
				dimissDialog();
				break;
				
			case SEARCH_DEVICES_FAIALED:
				 upDateDvListThread.interrupt();//中断线程
				 dimissDialog();
				 showToast("没有搜索到可打印小票的设备");
				break;
				
			case START_PRINT:  
				printdialog.dismiss();
				//打印小票线程
				new PrintDatasThread().start();
				
				break;
			case PRINT_FINISH:
				
				printdialog = null;
				//sendIp = "";
				dimissDialog();
				isConnected = true;
				//showToast("本次打印结束");
				//socket.close();
				
				break;
				
			case INIT_DATA_SUCCESS:
				 dimissDialog();
				 showToast("打印数据初始化成功");
				break;
			case DIVICE_EXCEPTION:
				dimissDialog();
				showToast("没有打印设备或者打印异常");
				break;
				
			case NO_DIVICE_CAN_CONNACT:
				dimissDialog();
				showToast("没有可连接的设备");
				
				showDialog("正在搜索设备...");
				wgIP = getIp();
				// 发送广播消息 搜索设备
				String strIp = wgIP;
				strIp = strIp.substring(0, strIp.lastIndexOf('.'));
				strIp += ".255";
				
				searchDevice(strIp);
				dvList = new ArrayList<Device>();
				break;
				
			case UPDATE_LIST:
			
				dimissDialog();
				if (dvList.size() > 0) {
					printdialog = new PrintDialog(getActivity(), dvList,R.style.discount_dialog_theme);
					printdialog.show();
				}else {
					//没有搜索到设备
					showToast("没有搜索到设备");
				}
				
				break;

			default:
				break;
			}
			
		};
	};
	
	private boolean hasChooseDeviceIp;
	
	/**
	 * 小票打印Onclick
	 */
	public void printClick(View view){
		  
	   if (null == dvList || dvList.size() == 0) {
			
			showDialog("正在打印...");
			
			wgIP = getIp();
			// 发送广播消息 搜索设备
			String strIp = wgIP;
			strIp = strIp.substring(0, strIp.lastIndexOf('.'));
			strIp += ".255";
			
			searchDevice(strIp);
			dvList = new ArrayList<Device>();
			return;
		}else {
			if (TextUtils.isEmpty(sendIp)) {
				//显示
				printdialog = new PrintDialog(getActivity(), dvList,R.style.dialog_theme);
				printdialog.show();
				
			}else {
				//选择之前打印过的设备
				new PrintDatasThread().start();
			}
		}
		
	}
	
	//确定选择打印机按钮
	public class SurePrintClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if (null != printdialog && printdialog.isShowing()) 
				printdialog.dismiss();
			
			showDialog("正在打印...");
			
			sendIp = selectIp;
			handler.sendEmptyMessage(START_PRINT);
		}
	}
	
	private String selectIp;
	
	public class PrintDialog extends Dialog{
		
		public List<Device> diviceNameList;
		public Context mContext;
		public ListView diviceListView;
		private DiviceListAdapter mAdapter;
		private Context context;
		
		public PrintDialog(Context context,List<Device> diviceNameList, int theme) {
			super(context, theme);
			this.diviceNameList = diviceNameList;
			this.context = context;
		}
		

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_choose_divice_layout);
			
			getWindow().setGravity(Gravity.BOTTOM);
			//设置对话框的宽度
			WindowManager m = getWindow().getWindowManager();
	        Display d = m.getDefaultDisplay();
	        WindowManager.LayoutParams p = getWindow().getAttributes();
	        p.width = d.getWidth();
	        p.height = LayoutParams.WRAP_CONTENT;
	        getWindow().setAttributes(p);
			
			diviceListView = (ListView)this.findViewById(R.id.divices_listview);
			ImageView clearImageView = (ImageView) this.findViewById(R.id.clearbtn);
			Button printbtn = (Button) this.findViewById(R.id.printBtn);
			
			clearImageView.setOnClickListener(new ClearDialogClick());
			printbtn.setOnClickListener(new SurePrintClick());
			
			mAdapter = new DiviceListAdapter(context,diviceNameList, "");
			diviceListView.setAdapter(mAdapter);
			mAdapter.setAddress(hasSelectIp);
			
			diviceListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					selectIp = dvList.get(position).deviceAddress;
					sendIp = dvList.get(position).deviceAddress;
					mAdapter.setAddress(sendIp);
				}
			});
			
			
		}
		
		public void notifydiviceListView(List<Device> list){
			this.diviceNameList = list;
			mAdapter.notifyDataSetChanged();
		}
		
	  }
	
	public class ClearDialogClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if (null != printdialog && printdialog.isShowing()) 
				printdialog.dismiss();
		}
		
	}
	
	

	
	/**
	 * 打印文本线程
	 *
	 */
	private class PrintDatasThread extends Thread{
		@Override
		public void run() {
			SendData();
		}
	}
	
	
	/**
	 * 监听找到的设备
	 * 
	 * 发送UDP包
	 * @param 网关ip
	 */
	private void searchDevice(String strIp) {
		try {
			
				//开启UDP接收数据监听，获取服务器IP
				upDateDvListThread = new UpDateDvListThread();
				upDateDvListThread.start();
				checkHasDiviceThread = new CheckHasDiviceThread();
				checkHasDiviceThread.start();
				
				// UDP发送数据建立UDP服务器通信
				socket = new DatagramSocket();
				InetAddress serverAddress = InetAddress.getByName(strIp);// 设置对方IP
				String str = "AT+FIND=?\r\n";// 设置要发送的报文
				byte data[] = str.getBytes();// 把字符串str字符串转换为字节数组
				packet = new DatagramPacket(data, data.length, serverAddress, 10002);// 设置发送数据，地址，端口
				new SendUdpPacketThread().start();
			
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(DIVICE_EXCEPTION);
		}
	}
	
	private boolean isContinu = true;
	//单独的线程检测是查询到设备
	private class CheckHasDiviceThread extends Thread{
		
		@Override
		public void run() {
			int time = 0;
			int time2 = 0;//用于显示设备的列表
			
			while (isContinu) {
				SystemClock.sleep(500);
				
				//10秒左右后没有接收到设备信息则跳出循环
				if (time++ > 20) {
					if (dvList.size() == 0) {
						handler.sendEmptyMessage(SEARCH_DEVICES_FAIALED);
					}
					time = 0;
					break;
				}
				
				//超过5秒则更新对话框列表
				if (time2++ > 5) {
					isContinu = false;
					handler.sendEmptyMessage(UPDATE_LIST);
				}
			}
		}
	}
	
	/**
	 * 
	 * 利用死循环 监听找到的设备
	 * 
	 */
	class UpDateDvListThread extends Thread {

		@Override
		public void run() {
			
			try {
				getpacket = new DatagramPacket(data2, data2.length);// 创建一个接收PACKET
			} catch (Exception e) {
			}
			
			while (true) {
				
				if (socket == null) {
					try {
						sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				  //中断线程  
                if (isInterrupted()) {  
                    return ;  
                } 
				
				try {
					
					socket.receive(getpacket);//获取UDP服务器发送数据信息，放入PACKET中
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 判断服务器是否发送数据
				if (getpacket.getAddress() != null) {
					// 获得服务器IP
					String ipStr = getpacket.getAddress().toString()
							.substring(1);
					String macAdd = new String(data2, 0, getpacket.getLength())
							.trim();

					Device d = new Device();
					d.deviceName = macAdd;//mac地址
					d.deviceAddress = ipStr;//ip地址
					if (wgIP.equals(d.deviceAddress)) {
						d.wkState = WorkState.AP;
					} else {
						d.wkState = WorkState.STA;
					}
					
					if (!checkData(dvList, d)) {
						
						if (TextUtils.isEmpty(sendIp)) {
							dvList.add(d);
						}
					}
				}
			}
		}
	}
	
	
	private boolean checkData(List<Device> list, Device d) {
		for (Device device : list) {
			if (device.deviceAddress.equals(d.deviceAddress)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 发送数据包至路由器
	 *
	 */
	public class SendUdpPacketThread extends Thread {
		
		public void run() {
			int timeSpan = 0;
			
			while (sendDatagram) {
				try {
					//10秒后跳出循环停止发送数据报
					if (timeSpan++ > 20) {
						timeSpan = 0;
						break;
					}
					socket.send(packet);
					Thread.sleep(500);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				catch (InterruptedException e) {
					e.printStackTrace();
					dimissDialog();
				}
			}
		}
		
	}
	
	/**
	 * 获取网关IP地址
	 * 
	 */
	private String getIp() {
		WifiManager wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		DhcpInfo di = wm.getDhcpInfo();
		long getewayIpL = di.gateway;

		String ip = intToIp(getewayIpL);// 网关地址
		return ip;
	}

	/**
	 * 打印
	 * 
	 */
    private void SendData()
    {
    	hasSelectIp = sendIp;
    	
    	try 
		{	
    		connect(sendIp,sendPort);
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("1D 21 11"));//换行
    		String companyName = onlineOrderDetail.companyname;
    		outputStream.write(("     " +  companyName).getBytes("GBk"));
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write("================================".getBytes("GBk"));//滑横线
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(("门店名称："+ onlineOrderDetail.storeName).getBytes("GBk"));
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(("门店授权号："+ onlineOrderDetail.mendianSQH).getBytes("GBk"));
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(("单号："+onlineOrderDetail.orderName).getBytes("GBk"));//滑横线
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write("--------------------------------".getBytes());//滑横线
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		//outputStream.write(hexStringToBytes("1B 61 01"));//居中
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write("名称    单价    数量    金额".getBytes("GBk"));
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write("--------------------------------".getBytes("GBk"));//滑横线
    		
    		int j = 1;
    		for (int i = 0; i < onlineOrderDetail.jsonArray.size(); i++) {
    			JsonArray item = onlineOrderDetail.jsonArray.get(i);
    			
    			outputStream.write(hexStringToBytes("1B 40"));//初始化
        		outputStream.write(hexStringToBytes("0A"));//换行
        		outputStream.write((item.name + item.productPici).getBytes("GBk"));//查名，批次
        		
        		outputStream.write(hexStringToBytes("1B 40"));//初始化
        		//outputStream.write(hexStringToBytes("1B 61 01"));//居中
        		outputStream.write(hexStringToBytes("0A"));//换行
        		j = i+1;
        		double itemAllMoney = Double.parseDouble(item.piecePrice) * Double.parseDouble(item.quantity);
        		outputStream.write(("00"+(j)+"     "+item.piecePrice+"     "+item.quantity+"     "
        		+ itemAllMoney).getBytes("GBk"));
			}
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write("--------------------------------".getBytes());//滑横线
    		
    		//"商品金额:"
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
    		StringBuilder builder1 = new StringBuilder("商品金额：");
    		
    		int slength = 35 - (builder1.toString().getBytes()).length 
    				- (NumerUtil.setSaveTwoDecimals(onlineOrderDetail.detailAmount)).getBytes().length;
    		String ssString = com.meetrend.haopingdian.util.StringUtil.repeat(" ", slength);
    		outputStream.write((builder1.toString()+ssString).getBytes("GBk"));
    		
    		//"商品金额数值"
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("1B 61 02"));//居右对齐
    		StringBuilder builder11 = new StringBuilder(NumerUtil.setSaveTwoDecimals(onlineOrderDetail.detailAmount));
    		outputStream.write(builder11.toString().getBytes("GBk"));
    		
    		//"优惠金额："
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
    		StringBuilder builder2 = new StringBuilder();
    		builder2.append("优惠金额：");
    		int ylength = 35 - (builder2.toString().getBytes()).length 
    				- (NumerUtil.setSaveTwoDecimals(onlineOrderDetail.discountAmount)).getBytes().length;
    		String ysString = com.meetrend.haopingdian.util.StringUtil.repeat(" ", ylength);
    		outputStream.write((builder2 + ysString).getBytes("GBk"));
    		
    		//"优惠金额数值"
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("1B 61 02"));//居右对齐
    		StringBuilder builder22 = new StringBuilder(NumerUtil.setSaveTwoDecimals(onlineOrderDetail.discountAmount));
    		outputStream.write(builder22.toString().getBytes("GBk"));
    		
    		//"积分金额："
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
    		StringBuilder builder23 = new StringBuilder();
    		builder23.append("积分金额：");
    		
    		int jlength = 35 - (builder23.toString().getBytes()).length 
    				- (NumerUtil.setSaveTwoDecimals(onlineOrderDetail.integralAmount)).getBytes().length;
    		String jsString = com.meetrend.haopingdian.util.StringUtil.repeat(" ", jlength);
    		outputStream.write((builder23 + jsString).getBytes("GBk"));
    		//"积分金额数值"
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("1B 61 02"));//居右对齐
    		StringBuilder builder233 = new StringBuilder(NumerUtil.setSaveTwoDecimals(onlineOrderDetail.integralAmount));
    		outputStream.write(builder233.toString().getBytes("GBk"));
    		
    		
    		//"合计："
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
    		StringBuilder builder3 = new StringBuilder();
    		builder3.append("合计：");
    		
    		int hlength = 33 - (builder3.toString().getBytes()).length
    				- (NumerUtil.setSaveTwoDecimals(onlineOrderDetail.incomeAmount)).getBytes().length;
    		String hsString = com.meetrend.haopingdian.util.StringUtil.repeat(" ", hlength);
    		outputStream.write((builder3 + hsString).getBytes("GBk"));
    		
    		//"合计金额数值"
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("1B 61 02"));//居右对齐
    		StringBuilder builder33 = new StringBuilder(NumerUtil.setSaveTwoDecimals(onlineOrderDetail.incomeAmount));
    		outputStream.write(builder33.toString().getBytes("GBk"));
    		
    		
    		//"应收："
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
    		StringBuilder builder4 = new StringBuilder();
    		builder4.append("应收：");
    		
    		int yylength = 33 - (builder4.toString().getBytes()).length 
    				- (NumerUtil.setSaveTwoDecimals(onlineOrderDetail.receivableAmount)).getBytes().length;
    		String yyString = com.meetrend.haopingdian.util.StringUtil.repeat(" ", yylength);
    		outputStream.write((builder4 + yyString).getBytes("GBk"));
    		
    		//"应收金额数值"
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("1B 61 02"));//居右对齐
    		StringBuilder builder44 = new StringBuilder(NumerUtil.setSaveTwoDecimals(onlineOrderDetail.receivableAmount));
    		outputStream.write(builder44.toString().getBytes("GBk"));
    		
    		//"实收："
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("1B 61 00"));//左对齐
    		StringBuilder builder5 = new StringBuilder();
    		builder5.append("实收：");
    		
    		int shlength = 35 - (builder5.toString().getBytes()).length 
    				- (NumerUtil.setSaveTwoDecimals(onlineOrderDetail.receivableAmount)).getBytes().length - "现金".getBytes().length;
    		String shString = com.meetrend.haopingdian.util.StringUtil.repeat(" ", shlength/2);
    		outputStream.write((builder5 + shString).getBytes("GBk"));
    		outputStream.write(("现金" + shString).getBytes("GBk"));
    		
    		//"实收金额数值"
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("1B 61 02"));//居右对齐
    		StringBuilder builder55 = new StringBuilder(NumerUtil.setSaveTwoDecimals(onlineOrderDetail.receivableAmount));
    		outputStream.write(builder55.toString().getBytes("GBk"));
    		
    		
    		//"找零："
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		StringBuilder builder6 = new StringBuilder();
    		builder6.append("找零：");
    		int zhlength = 33 - (builder6.toString().getBytes()).length 
    				- (NumerUtil.setSaveTwoDecimals(onlineOrderDetail.changeAmount)).getBytes().length;
    		String zString = com.meetrend.haopingdian.util.StringUtil.repeat(" ", zhlength);
    		outputStream.write((builder6 + zString).getBytes("GBk"));
    		
    		//"找零金额数值"
    		StringBuilder builder66 = new StringBuilder(NumerUtil.setSaveTwoDecimals(onlineOrderDetail.changeAmount));
    		outputStream.write(builder66.toString().getBytes("GBk"));
    		
    		
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(("收银员：" + onlineOrderDetail.checkoutname).getBytes("GBk"));
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		String ddate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    		outputStream.write(("时间："+ddate).getBytes("GBk"));
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(("客服电话："+ onlineOrderDetail.mobile).getBytes("GBk"));
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write(hexStringToBytes("0A"));//换行
    		outputStream.write("================================".getBytes("GBk"));
    		
    		outputStream.write(hexStringToBytes("1B 40"));//初始化
    		outputStream.write(hexStringToBytes("1B 4D 11"));//换行
    		
    		try {
				  Thread.sleep(2000);//此处需要2s,缓冲池已满
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		Bitmap printBitmap = BitmapFactory.decodeFile(SPUtil.getDefault(getActivity()).getErWeiMaPath());
    		
    		if (null != printBitmap) {
    			
    			Log.i("-----------图片不为空----------------", "bitmap is not null");
    			//图片打印
        		//Bitmap bitmap = getImageFromAssetsFile("erweima.png");
        		int h = printBitmap.getHeight();
        		Bitmap resultBitmap = resizeImage(printBitmap, 384, h);
        		byte[] lsendbuf = StartBmpToPrintCode(resultBitmap);
        		outputStream.write(lsendbuf);
        		
			}else {
				
				outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("0A"));//换行
	         	outputStream.write(hexStringToBytes("1B 61 01"));//居中
	         	outputStream.write(hexStringToBytes("1D 21 01"));//纵向放大
	    		String printText = "      谢谢惠顾欢迎再次光临!";
	    		outputStream.write(printText.getBytes("GBk"));
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write("                   ".getBytes("GBk"));
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("1B 4D 11"));//换行
	    		String techName = "         技术支持：好评店 ";
	    		outputStream.write(techName.getBytes("GBk"));
	    		
	    		outputStream.write(hexStringToBytes("1B 40"));//初始化
	    		outputStream.write(hexStringToBytes("0A"));//换行
	    		outputStream.write(hexStringToBytes("1B 4D 11"));//换行
	    		String httpName = "   http://www.haopingdian.cn";
	    		outputStream.write(httpName.getBytes("GBk"));
			}
    		
    		outputStream.flush();//关闭流
    		
    		handler.sendEmptyMessage(PRINT_FINISH);
    		
		} catch (NumberFormatException e) {
			handler.sendEmptyMessage(DIVICE_EXCEPTION);
		} 
		catch (IOException e) {
			e.printStackTrace();
			
		}finally{
			disconnect();
		}
		
    }
    
    
    private void disconnect(){
	
		if(this.inputStream!=null){
			try{
				this.inputStream.close();
			}catch(Exception e){
				
			}
		}
		if(this.outputStream!=null){
			try{
				this.outputStream.close();
			}catch(Exception e){
				
			}
		}
		this.inputStream=null;
		this.outputStream=null;
		if(client!=null){
			try{
				client.close();
			}catch(Exception e){
				
			}
		}
		client=null;
		isConnected = false;
	}
    
    
	private void connect(String ipAddr,String mport) throws UnknownHostException, IOException {
		
		//if(isConnected){
		//	return;
		//}
		
		try {
			
			InetAddress serverAddr = InetAddress.getByName(ipAddr);// TCPServer.SERVERIP
			int port=Integer.valueOf(mport);
			SocketAddress my_sockaddr = new InetSocketAddress(serverAddr, port);
			client = new Socket();
			client.connect(my_sockaddr,5000);
			outputStream = client.getOutputStream();
			inputStream = client.getInputStream();
			isConnected = true;
			//new TCPServerThread().start();
			
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(NO_DIVICE_CAN_CONNACT);
		}
	}
	
		
 	/**
	 * 将字符串形式表示的十六进制数转换为byte数组
	 */
	public static byte[] hexStringToBytes(String hexString)
	{
		hexString = hexString.toLowerCase();
		String[] hexStrings = hexString.split(" ");
		byte[] bytes = new byte[hexStrings.length];
		for (int i = 0; i < hexStrings.length; i++)
		{
			char[] hexChars = hexStrings[i].toCharArray();
			bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
		}
		return bytes;
	}
	
	private static byte charToByte(char c)
	{
		return (byte) "0123456789abcdef".indexOf(c);
	}
	
	private String intToIp(long i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
	
	  private Bitmap getImageFromAssetsFile(String fileName)  
	  {  
	      Bitmap image = null;  
	      AssetManager am = getResources().getAssets();  
	      try  
	      {  
	          InputStream is = am.open(fileName);  
	          image = BitmapFactory.decodeStream(is);  
	          is.close();  
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }  
	  
	      return image;  
	  
	  }
	
	 public InputStream getImageStream(String path) throws Exception{ 
			
	        URL url = new URL(path);  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setConnectTimeout(5 * 1000);  
	        conn.setRequestMethod("GET");  
	        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
	            return conn.getInputStream();  
	        }  
	        return null;  
	  }
	 
	 private String localPath;
	 public void savePictrueFile(Bitmap bm, String fileName) throws IOException {  
		 
		 	localPath = Environment.getExternalStorageDirectory()+"/" + fileName;
		 	File file = new File(localPath);
		 	if (!file.exists()) {
				file.createNewFile();
			}
		 	
	        File myCaptureFile = new File(localPath);  
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
	        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);  
	        bos.flush();  
	        bos.close(); 
	 }
	 
	  //缩放图片
	  public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;

		float scaleWidth = ((float) newWidth) / width;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	 }
	  
	//图片转字节数组
			private byte[] StartBmpToPrintCode(Bitmap bitmap) {
				byte temp = 0;
				int j = 7;
				int start = 0;
				if (bitmap != null) {
					int mWidth = bitmap.getWidth();
					int mHeight = bitmap.getHeight();

					int[] mIntArray = new int[mWidth * mHeight];
					byte[] data = new byte[mWidth * mHeight];
					bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
					encodeYUV420SP(data, mIntArray, mWidth, mHeight);
					byte[] result = new byte[mWidth * mHeight / 8];
					for (int i = 0; i < mWidth * mHeight; i++) {
						temp = (byte) ((byte) (data[i] << j) + temp);
						j--;
						if (j < 0) {
							j = 7;
						}
						if (i % 8 == 7) {
							result[start++] = temp;
							temp = 0;
						}
					}
					if (j != 7) {
						result[start++] = temp;
					}

					int aHeight = 24 - mHeight % 24;
					byte[] add = new byte[aHeight * 48];
					byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * 48];
					System.arraycopy(result, 0, nresult, 0, result.length);
					System.arraycopy(add, 0, nresult, result.length, add.length);

					byte[] byteContent = new byte[(mWidth / 8 + 4)
							* (mHeight + aHeight)];// 打印数组
					byte[] bytehead = new byte[4];// 每行打印头
					bytehead[0] = (byte) 0x1f;
					bytehead[1] = (byte) 0x10;
					bytehead[2] = (byte) (mWidth / 8);
					bytehead[3] = (byte) 0x00;
					for (int index = 0; index < mHeight + aHeight; index++) {
						System.arraycopy(bytehead, 0, byteContent, index * 52, 4);
						System.arraycopy(nresult, index * 48, byteContent,
								index * 52 + 4, 48);

					}
					return byteContent;
				}
				return null;

			}
			

			//转换图片格式
			public void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width,
					int height) {
				////final int frameSize = width * height;
				int r, g, b, y;//, u, v;
				int index = 0;
				////int f = 0;
				for (int j = 0; j < height; j++) {
					for (int i = 0; i < width; i++) {
						r = (rgba[index] & 0xff000000) >> 24;
						g = (rgba[index] & 0xff0000) >> 16;
						b = (rgba[index] & 0xff00) >> 8;
						
						// rgb to yuv
						y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
						/*u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
						v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;*/
						// clip y
						// yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 :
						// y));
						byte temp = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
						yuv420sp[index++] = temp > 0 ? (byte) 1 : (byte) 0;

						// {
						// if (f == 0) {
						// yuv420sp[index++] = 0;
						// f = 1;
						// } else {
						// yuv420sp[index++] = 1;
						// f = 0;
						// }

						// }

					}

				}
				////f = 0;
			}
	 
	//图片下载
	public class DownLoadPictrueThread extends Thread{
		
		public String netPath;
		
		public DownLoadPictrueThread(String netPath){
			this.netPath = netPath;
		}
		
		@Override
		public void run() {
			
			try {
				 String path = Environment.getExternalStorageDirectory()+"/" + "com.meetrend.haopingdian.erweima";
				
				 byte[] bytes = com.meetrend.haopingdian.util.BitmapUtil.readImage(netPath);
				 
				 File file = new File(path);  
		         if(!file.exists()) {
		        	 file.mkdirs(); 
		         } 
		         String savePath = file.getPath()+"/"+"erweima.png";
				 FileOutputStream erweimaOutputStream = new FileOutputStream(savePath);
				 erweimaOutputStream.write(bytes);
				 
				 //保存图片的路径
				 SPUtil.getDefault(getActivity()).saveErweiMaPath(savePath);
				 Log.i("-----------图片下载完成----------------", "is over");
				 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 检测本地是否有已下载的图片
	 * 
	 */
	private boolean hasLocalBitmap(){
		mErweiMaBitmap = BitmapFactory.decodeFile(localPath);
		return mErweiMaBitmap == null ? false : true;
	}
	  
	public void onClickHome(View view){
		getActivity().finish();
	}
	

}
