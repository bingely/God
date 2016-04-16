package com.meetrend.haopingdian.env;

public class Server {
	
	/**
	 * 获取商务号path
	 */
	public static final String BUSINESS_URL = "http://www.haopingdian.cn/saas/api/Saas.host";
	//public static final String DAYI = "http://wx.baiyetea.com/crm/";//百业茶园

	/**
	 * 大益
	 */
	//	public static final String DAYI = "http://wx.yestae.cn/crm/";
	
	/**
	 * 李立
	 */
	//	public static final String DAYI = "http://10.1.15.85:8085/lili/";
	
	/**
	 * 测试
	 */
	//public static final String DAYI = "http://10.1.20.200:9090/dayi/";
	
	
	//public static final String DAYI = "http://10.1.15.10:8080/msp/";//温志翔
	public static final String DAYI = "http://10.1.15.74:8082/hyt/";
	
	public static  String BASE_URL = DAYI;
	 
	/**
	 * 登录
	 */
	public static final String LOGIN_URL ="api/Ecp.OnlineUser.apiLogin";
	/** 
	 * 导入会员
	 */												  
	public static final String IMPORT_MEMBER_URL ="api/Crm.Customer.importMember";
	/** 
	 * 导入会员2
	 */												  
	public static final String IMPORT_MEMBER_URL_NO2 ="api/Crm.Customer.addMember";
	
	/** 
	 * 会员列表
	 */
	public static final String MEMBER_LIST_URL ="api/Crm.Customer.memberList"; 
	/** 
	 * 删除会员 
	 */
	public static final String DELETE_MEMBER_URL ="api/Crm.Customer.deleteMember";
	/** 
	 * 邀请会员
	 */
	public static final String INVITE_MEMBER_URL ="api/Crm.Customer.inviteMember";
	/** 
	 * 会员详情
	 */
	public static final String MEMBER_INFO_URL ="api/Crm.Customer.memberInformation";
	/**
	 * 修改会员资料
	 */
	public static final String UPDATE_MEMBER_URL ="api/Crm.Customer.updateMember";
	/**
	 *订单列表
	 */
	public static final String ORDER_LIST_URL ="api/Crm.Order.orderList";
	/**
	 * 确认订单列表
	 */
	public static final String CONFIRMEDORDER_LIST_URL ="api/Crm.Order.confirmedOrderList";

	/**
	 * 获取线上订单详情;
	 */
	public static final String ONLINE_ORDER_DETAIL_URL ="api/Crm.OrderDetail.getOrderDetailById";

	/**
	 * 执行人列表;
	 */
	public static final String EXECUTOR_LIST_URL ="api/Ecp.Employee.loadTagExecutorList";
	/**
	 * 变更执行人
	 */
	public static final String CHANGE_EXECUTOR ="api/Crm.Order.sendDeliverTips";
	/**
	 * 添加执行人;
	 */
	public static final String ADD_EMPLOYEE_URL ="api/Ecp.Employee.addEmployee";

	/**
	 * 订单确认;
	 */
	public static final String FIREM_ORDER_URL ="api/Ecp.Employee.firmOrder";

	/**
	 * 库存历史订单记录;
	 */
	public static final String ORDER_HISTORY_URL ="api/Crm.Order.doQueryHistoryOrder";

	/**
	 * 聊天列表;
	 */
	public static final String TALK_LIST_URL ="api/Wx.Talk.talkList";

	/**
	 * 聊天详情;
	 */
	public static final String TALK_DETAIL_URL ="api/Wx.Talk.talkDetail";
	/**
	 * 库存产品类型、年份;
	 */
	public static final String INVENTORY_CATALOG_URL ="api/Crm.Inventory.inventoryProductCatalog";

	/**
	 * 库存根据年份得到产品列表;
	 */
	public static final String GET_PRODUCT_BYID_URL ="api/Crm.Inventory.getProductByYearId";

	/**
	 * 库存详情;
	 */
	public static final String GET_INVENTORYBYID_URL ="api/Crm.Inventory.getInventoryById";

	/**
	 * 修改库存详情;
	 */
	public static final String UPDATA_INVENTORY_URL ="api/Crm.Inventory.updateInventoryModel";

	/**
	 * 全局搜索;
	 */
	public static final String GLOABL_SEARCH_URL ="api/Wx.Talk.globalSearch";

	/**
	 * 上传;
	 */
	public static final String UPLOAD_URL ="api/Ecp.Picture.upload.jup";

	/**
	 * 发送消息;
	 */
	public static final String SEND_MSG_URL ="api/Wx.Talk.sendMessage";

	/**
	 * 发送多媒体消息
	 */
	public static final String SEND_MULTIMSG_URL ="api/Wx.Talk.sendMessage.jup";
	/**
	 * 找回密码;
	 */
	public static final String FORGET_PASSWORD_URL ="api/Ecp.User.forgetPassword";

	/**
	 * 发送短信
	 */
	public static final String SEND_APP = "api/Ecp.User.sendAPP";
	/**
	 * 重置密码;
	 */
	public static final String RESET_PASSWORD_URL ="api/Ecp.User.resetPassword";

	/**
	 * 获取门店列表;
	 */
	public static final String GET_STORELIST_URL ="api/Crm.Store.getStoreListbyId";

	/**
	 * 取消订单;
	 */
	public static final String CANCEL_ORDER_URL ="api/Crm.Order.cancelOrder";

	/**
	 * 确认转账;
	 */
	public static final String CONFIRM_TRANSFER_URL ="api/Crm.Order.confirmTransfer";

	/**
	 * 查看物流;
	 */
	public static final String CHECKLOGISTICS ="api/Crm.Order.getExpress";
	/**
	 * 收入;
	 */
	public static final String INCOME_ACCOUNT_URL ="api/Crm.Order.inComeAccount";

//	/**
//	 * 门店订单详情;
//	 */
//	public static final String STORE_ORDER_DETAIL_URL ="api/Crm.OrderDetail.storeOrderDetail";

	/**
	 * 订单结账;
	 */
	public static final String CHECKOUT_URL ="api/Crm.Order.checkOut";

	/**
	 * 确认结账;
	 */
	public static final String CONFIRM_CHECKOUT_URL ="api/Crm.Order.confirmCheckOut";

	/**
	 * 包间列表;
	 */
	public static final String ROOM_LIST_URL ="api/Crm.Room.allRroom";
	
	/**
	 * 我的信息
	 * */
	public static final String GET_MY_INFOS="api/Ecp.User.myInformation";
	
	/**
	 * 获取门店二维码
	 */
	public static final String GET_MY_ER = "api/Crm.Store.getStoreQRCode";
	
	/**
	 * 修改我的信息
	 * */
	public static final String UPDATE_MY_INFOS="api/Ecp.User.updateInformation";
	
	/**
	 * 我的二维码
	 * */
	public static final String GET_MY_CODE="api/Ecp.User.myQRCode";
	
	/**
	 * 修改稿密码
	 * */
	public static final String UPDATE_PWSSWORD="api/Ecp.OnlineUser.modifyPassword";
	
	/**
	 * 获取系统更新消息
	 * */
	public static final String SYSTEM_UPDATE="api/Ecp.SystemUpdate.lastVersion";
	
	/**
	 * 版本更新2
	 */
	public static final String SYSTEM_UPDATE2 = "http://www.haopingdian.cn/saas/api/Ecp.SystemUpdate.lastVersion";
	
	/**
	 * 问题反馈
	 * */
	public static final String FEED_BACK="api/Crm.FeedBack.addFeedBack";
	
	
	/**
	 * 功能介绍
	 * */
	public static final String FUNCTION_DETAIL="api/Crm.FeedBack.introduction";
	
	/**
	 * 系统通知
	 * */
	public static final String SYSTEM_NOTIFY="api/Crm.SystemUpdate.systemMessage";
	
	/**
	 * 获取群组列表
	 * */
	public static final String GET_GROUP_lIST="api/Ecp.Group.myGroup";
	
	/**
	 * 创建群组
	 * */
	public static final String CREATE_GROUP="api/Ecp.Group.createGroup";
	
	/**
	 * 获取群组信息
	 * */
	public static final String GET_GROUP_INFO="api/Ecp.Group.getGroupById";
	
	/**
	 * 发送多人消息
	 * */
	public static final String SEND_MULTI_MSG="api/Wx.Talk.sendMultiMessage";
	
	/**
	 * 茶品列表
	 * 
	 * */
	
//	public static final String PRODUCT_LIST = "api/Crm.Inventory.productList";
	public static final String PRODUCT_LIST = "api/Crm.Inventory.newProductList";

	/**
	 * 商品管理
	 */
	public static final String PRODUCT_MANAGER_LIST = "/api/Crm.Product.getTakeOutListData";

	/**
	 * 门店下单
	 * 
	 * */
	public static final String FAST_TAKE_ORDER = "api/Crm.Order.fastTakeOrder";
	
	/**
	 * 获取群历史记录
	 * 
	 * */
	public static final String HISTORY_MESSAGE = "api/Ecp.GroupMessage.historyMsg";
	/**
	 * 添加快递单号
	 * 
	 * */
	public static final String ADD_ORDER_EXPRESSCODE = "api/Crm.Order.addOrderExpressCode";
	
	
	/**
	 * 新版成员列表
	 * 
	 * */
	public static final String APP_MEMBER_USER_LIST = "api/Ecp.User.appMemberUserList";
	/**
	 * 收入
	 * 
	 * */
	public static final String NEW_INCOME_ACCOUNT = "api/Crm.Order.newIncomeAccount";
	
	/**
	 * 获得标签列表
	 * */
	public static final String MY_TAG_LIST = "api/Crm.Tag.myTagList";
	/**
	 * 
	 * 更新标签
	 * */
	public static final String UPDATE_TAG = "api/Crm.Tag.updateTag";
	
	/**
	 * 获得标签下的成员
	 * 
	 * */
	public static final String GETTAGDETAIL = "api/Crm.Tag.getTagDetail";
	
	/**
	 * 添加标签
	 * */
	public static final String ADD_TAG =  "api/Crm.Tag.createTag";
	
	/**
	 * 添加活动
	 */
	public static final String ADD_ACTIVITY =  "api/Crm.MemberActivity.createMemberActivity";
	/**
	 * 更新活动
	 */
	public static final String UPDATE_ACTIVITY =  "api/Crm.MemberActivity.updateMemberActivity";
	/**
	 * 活动列表
	 */
	public static final String ACTIVITYS =  "api/Crm.MemberActivity.memberActivityList";
	
	
	public static final String NEW_SEND_MULTI_MSG= "api/Ecp.GroupMessage.sendMessage";
	/**
	 * 群组消息列表
	 * */
	public static final String GROUP_MSG_LIST =  "api/Ecp.GroupMessage.groupMessageList";
	
	/**
	 * 
	 * 活动详情
	 * */
	public static final String EVENT_DETAIL =  "api/Crm.MemberActivity.memberActivityDetail";
	
	/**
	 * 
	 * 发送活动
	 * */
	public static final String SEND_EVENT =  "api/Crm.MemberActivity.sendActivity";
	/**
	 * 
	 * 推荐排序
	 * */
	public static final String JOIN_RECOMMENT =  "api/Crm.MemberActivity.listActivityRecommendUser";
	
	/**
	 * 
	 * 推荐排序
	 * */
	public static final String JOIN_ARRIVE =  "api/Crm.MemberActivity.listActivityUserAndSign";
	
	/**
	 * 获取分享人列表
	 * */
	public static final String SHARE_PEOPLE_LIST =  "api/Crm.MemberActivity.listActivityShareUser";
	
	/**
	 * 获取评论列表
	 * */
	public static final String GET_COMMENT_LIST =  "api/Crm.MemberActivity.getCommentList";
	
	
	/**
	 * 
	 * 点赞
	 * */
	public static final String DIANZAN =  "api/Crm.MemberActivity.thumbsUp";
	
	/**
	 * 
	 *评论
	 * */
	public static final String DISCUSS =  "api/Crm.MemberActivity.updateComment";
	
	/**
	 * 回复评论
	 * */
	public static final String REPLAY_DISCUSS =  "api/Crm.MemberActivity.replyComment";
	
	/**
	 * 获取活动二维码
	 */
	public static final String GET_QRC = "api/Crm.MemberActivity.getActivityQrcCode";
	
	
	/**
	 * 删除消息列表
	 */
	public static final String DELMSG = "api/Wx.Talk.delTalk";
	
	
	/**
	 * 删除群消息
	 */
	public static final String DEL_GMSG = "api/Ecp.GroupMessage.delMessage";
	
	
	/**
	 * 消息列表
	 * 
	 * */
	public static final String MESSAGE_LIST = "api/Wx.Talk.userAndGroupTalkList";
	
	/**
	 * 退出登录
	 * 
	 * */
	public static final String LOGIN_OUT = "api/Ecp.OnlineUser.logout";
	
	/**
	 * 
	 * 删除群组
	 * */
	public static final String DELETE_TAG = "api/Crm.Tag.deleteTag";
	
	/**
	 * 验证授权码是否正确
	 */
	public static final String JADGE_SHOUQUANMA = "api/Crm.AuthorizeCode.checkValidAuthCode";
	
	
	/**
	 * 获取授权码
	 */
	public static final String GET_SHOUQUANMA = "api/Crm.AuthorizeCode.returnAuthCode";
	
	/**
	 * 修改授权密码
	 */
	public static final String MODIFY_SHOUQUANMA_PWD = "api/Crm.AuthorizeCode.changeAuthPwd";
	
	/**
	 * 订单详情，通过电话号码查找用户的姓名
	 */
	public static final String FROM_PHONE_FIND_NAME = "api/Crm.Customer.queryName";
	
	/**
	 * 订单详情，扫描客户二维码后请求
	 */
	public static final String SCAN_CUS_RQUEST = "api/Crm.Customer.scan";
	
	/**
	 * 微信刷卡支付
	 */
	public static final String WEIXIN_PAY = "api/Crm.Order.WFTHalfPenny";
	
	/**
	 * 产品扫描
	 */
	public static final String SCAN_PRODUCT = "api/Crm.Order.getProducts";
	
	/**
	 * 微信支付结果轮询
	 */
	public static final String GET_PAY_RESULT = "api/Crm.Order.getOrderPayResult";
	
	/**
	 * 订单管理列表
	 */
	public static final String ORDER_MANAGER_LIST = "/api/Crm.Order.orderList";
	
	/**
	 * 活动主题管理
	 */
	public static final String EVENTTYPE_LIST = "/api/Ecp.DictionaryItem.getDictionaryItemsByParentId";
	
	/**
	 * 点赞列表接口
	 */
	public static final String PRISE_LIST = "api/Crm.MemberActivity.thumbsUpList";
	
	/**
	 * 分享人数
	 */
	public static final String SHARENUMREQUEST = "/api/Crm.MemberActivity.statisticsShareNum";

	
	/**
	 * 提交马上分配的人数
	 */
	public static final String COMMIT_DISTRIBUTE_REQUEST = "/api/Crm.Customer.assign";
	
	
	/**
	 * 现金结账
	 */
	public static final String CURRENT_MONEY_PAY ="/api/Crm.Order.completeOrder";
	
	
	/**
	 * 优惠金额
	 */
	public static final String  PREFERENTIAL_REQUEST = "api/Crm.Order.calculateDiscountAmount";
	
	
	/**
	 *送货确认
	 */
	public static final String  SEND_PRODUCT_REQUEST = "/api/Crm.Order.finishDelivery";
	
	
	/**
	 * 线上订单修改
	 */
	public static final String  ONLINE_ORDER_MODIFY = "/api/Crm.Order.modifyOnlineOrder";
	
	/**
	 * 门店盘点列表
	 */
	public static final String  STORE_ORDER_LIST = "/api/Wx.StoreCheck.storeCheckList";
	
	/**
	 * 门店盘点详情
	 */
	public static final String  STORE_LIST_DETAIL = "/api/Wx.StoreCheck.storeCheckDetail";
	
	
	/**
	 * 门店盘点新增
	 */
	public static final String  STORECHECK_ADD = "/api/Wx.StoreCheck.addStoreCheck";
	
	/**
	 * 门店盘点编辑
	 */
	public static final String  STORECHECK_EDIT = "/api/Wx.StoreCheck.editStoreCheck";
	
	
	/**
	 * 再次发送付款码
	 */
	public static final String  AGIN_GET_PAYCODE = "/api/Crm.Order.sendPayCodeAgain";
	/**
	 * 余额支付
	 */
	public static final String  YUEPAY = "/api/Crm.Order.confirmPay";
	
	
	/**
	 * 数据中心
	 */
	public static final String  DATACENTER = "/api/Crm.Statistics.dataCentre";
	
	/**
	 * 今日收入
	 */
	public static final String  TODAY_INCOMME = "/api/Crm.Statistics.todayPayeddetails";
	
	/**
	 * 今日访客
	 */
	public static final String  TODAY_CUSTOMERS = "/api/Crm.Statistics.visitNum";
	
	/**
	 * 今日下单或者新增客户数
	 */
	public static final String  TODAY_ORDER_LIST_OR_ADD_CUSTOMERS = "/api/Crm.Statistics.orderCount";
	
	
	/**
	 * 市场参考价
	 */
	public static final String  MARKET_PRICE_URL = "/api/Crm.MarketPrice.marketPriceList";
	
	
	/**
	 * 市场参考价详情
	 */
	public static final String  MARKET_DETAIL = "/api/Crm.MarketPrice.marketPriceDetails";
	
	/**
	 * 计算积分总金额
	 */
	public static final String  CALCULATE_POINT_AMOUNT = "/api/Crm.Order.calculatePointAmount";
	
	/**
	 * 获取优惠券列表
	 */
	public static final  String  GET_DISCOUNT_LIST = "/api/Crm.Order.getVoucherList";
	
	/**
	 * 检查积分是否可兑现
	 */
	public static final  String  CHECK_POINTS_IS_ENABLE = "/api/Crm.Order.checkCalculatePointAmount";
	
	/**
	 * 签到列表
	 */
	public static final  String  SIGN_LIST = "/api/Crm.Checkin.todayList";
	
	/**
	 * 查询周围地点
	 */
	public static final  String  NEAR_SIGN_LIST = "/api/Crm.Checkin.search";
	
	
	/**
	 * 签到接口
	 */
	public static final  String  SIGN_REQUEST = "/api/Crm.Checkin.checkin";
	
	/**
	 * 功能介绍
	 */
	public static final  String  FUNCATION_DES = "/Crm.Checkin.Introduce.jdp";
	
	/**
	 * 市场参考价关键字
	 */
	public static final  String  KEYCHARLIST = "/api/Crm.MarketPrice.hotSeek";

	/**
	 * 门店收款码
	 */
	public static final  String  STORE_EWM_INFO = "/api/Wx.PayCode.skm";

	/**
	 * 修改门店收款码
	 */
	public static final String MODIFY_STORE_SKM ="/api/Wx.PayCode.updatePayCode";

	/**
	 * 门店收款码列表
	 */
	public static final String PAY_CODE_LIST ="/api/Wx.PayCode.payCodeList";

	/**
	 * 列表收款码详情
	 */
	public static final String LIST_PAYCODE_INFO ="/api/Wx.PayCode.selfPay";


	/**
	 * 新建收款码详情
	 */
	public static final String CTEATE_PAYCODE_INFO ="/api/Wx.PayCode.createPayCode";


	/**
	 * 产品上架和下架
	 */
	public static final String PRODUCT_UP_DOWN ="/api/Crm.Inventory.upOrDownWX";


	/**
	 * 产品分享
	 */
	public static final String PRODUCT_SHAARE = "/api/Crm.Inventory.shareInventory";

	/**
	 *更新商品库
	 */
	public static final String UPDATE_PRODUCTS = "/api/Crm.Product.appUpdateProduct";

	/**
	 *获取商品详情
	 */
	public static final String GET_PRODUCT_DETAIL = "/api/Crm.Product.getAppProductDetail";

	/**
	 *商品分类
	 */
	public static final String PRODUCT_TYPES = "/api/Crm.Product.getProductCatalogList";

	/**
	 *商品添加
	 */
	public static final String PRODUCT_ADD = "/api/Crm.Product.appCreateProduct";
	/**
	 *删除商品
	 */
	public static final String PRODUCT_DEL = "/api/Crm.Product.deleteProduct";

	/**
	 *单位选择
	 */
	public static final String UNIT_CHOOSE = "/api/Crm.Product.loadUnitList";

	/**
	 * 更新库存
	 */
	public static final String UPDATE_PRODUCT_FROM_REPONSETY = "/api/Crm.Product.appUpdateProduct";

	/**
	 * 运费模板
	 */
	public static final String YUNFEIMOBAN = "/api/Crm.Inventory.loadFreighTemplate";

	/**
	 * 判断是否需要库存
	 */
	public static final String JUDGE_CAN_MODIFY_KUCUN = "/api/Crm.Product.showInventory";

	/**
	 * 判断产品是否可以删除
	 */
	public static final String JUDGE_PRODUCT_IS_CAN_DEL = "/api/Crm.Product.isCanDelete";
}