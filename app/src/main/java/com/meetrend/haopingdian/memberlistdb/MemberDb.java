package com.meetrend.haopingdian.memberlistdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 肖建斌
 *
 */
public class MemberDb extends SQLiteOpenHelper{

	public final static String DBNAME = "members_db";
	//通讯录列表搜索
	public final static String TABLE_NAME = "memberstable";
	//门店切换列表
	public final static String STORE_INFO_TABLE = "storeinfo_table";
	
	public final static String sql =  "create table " + TABLE_NAME +
			"(_id integer primary key autoincrement, "
			
			+ "customerName"+","
			+ "managerId"+"," 
			+ "memberId"+"," 
			+ "pictureId"+"," 
			+ "userId"+"," 
			+ "createTime"+"," 
			+ "userName"+"," 
			+ "status"+"," 
			+ "remark"+"," 
			+ "mobile"+"," 
			+ "checkstatus"+"," 
			+ "position"+"," 
			+ "isDefault"+"," 
			+ "canTalk"+"," 
			+ "isGroup"+"," 
			+ "pinyinName"+","
			+ "type"
			
			+");";
	
	
	//存储门店信息表
	public final static String storeinfo_sql =  "create table " + STORE_INFO_TABLE +
			"(_id integer primary key autoincrement, "
			
			+ "store_name"+","
			+ "store_id"+"," 
			+ "login_account"+"," 
			+ "login_account_pwd"+","
			+ "base_url"+","
			+ "shanghu_num"
			+");";
	
	
	public MemberDb(Context context) {
		super(context, DBNAME, null, 1);
	}
	
	
	private static MemberDb excutorDb = null;
	
	public static MemberDb getInstance(Context context){
		
		if (excutorDb == null) {
			excutorDb = new MemberDb(context);
		}
		return excutorDb;
		
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(sql);
		db.execSQL(storeinfo_sql);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		//用oldVersion来标识标识是从旧版本升级过来的
		switch (oldVersion) {
		case 1:
			
			break;
		case 2:
			
			break;

		default:
			break;
		}
		
	}

	//降级
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onDowngrade(db, oldVersion, newVersion);
	}
}