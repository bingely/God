package com.meetrend.haopingdian.tool;

public class Code {
	public static final int TIME = 0x01;
	public static final int COUNTDOWN = 0x111;
	public static final int COUNTTIME = 0x124;
	
	public static final int FULL = 0x333;
	public static final int FAILED = 0x110;
	public static final int EMPTY = 0x120;
	public static final int TIMEOUT = 0x911;
	public static final int SUCCESS = 0x1314;
	public static final int INIT = 0x122;
	
	public static final int UPDATE = 0x123;
	
	
	
	//联系人显示模式标识
	public final static String MODE="showMode";
	//联系人的显示模式
	public final static int NORMAL_MODE = 0x211;
	public final static int SHOW_MULTI_CHECK_MODE = 0X222;
	public final static int SHOW_SINGLE_NO_CHECK_MODE = 0X233;
	public final static int ShOW_OK_MULTI_CHECK_MODE = 0X244;
	public final static int ADD_MEMBER_TO_GROUP= 0X255;//添加成员至群组
	public final static int GROUP_SENDING = 0X555;//群发
	
	
	//群组 是否是店小二 和非店小二
	public final static String GROUP_MODE = "group_mode";
	public final static int ASSITANT = 0X911;//点小二
	public final static int NOT_ASSITANT = 0X922;//非店小二
	
	public final static String FROM_TYPE ="to";
	//从添加群组界面跳转的标识
	public final static int ADD_MEMBER = 0X999;
	
	//编辑分组
	public final static String EDIT_TYPE = "edit_type";
	public final static int CREATE_GROUP = 0X555;//创建分组
	public final static int UPDATE_GROUP = 0X666;//更新分组
	
	
	//群组
	public final static int  GROUP_LIST_EMPTY = 0X777;//群组为空
	public final static int GROUP_LIST_SUCCESS = 0X888;//群组列表请求成功
	
	//成员无图像时的默认图片id
	public final static String DEFAULT_PID = "4f7d9891-a2cc-4340-bb54-20aafc1336e1";
	
	
	
	public final static int SCAN_CUSTOMER_SUCCESS = 0X218;//扫描客户二维码成功
	public final static int EDIT_ENABLE_SUCCESS = 0X219;//实收金额框可以编辑
	
	
}