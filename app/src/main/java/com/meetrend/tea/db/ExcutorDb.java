package com.meetrend.tea.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExcutorDb extends SQLiteOpenHelper{

	public final static String DBNAME = "dayi_db";
	public final static String TABLE_NAME = "excutor";
	
	//3个字段 
	public final static String sql =  "create table " + TABLE_NAME +
			"(_id integer primary key autoincrement, "
			+ "id"+","
			+ "userName"+"," 
			+ "avatarId"+");";
	
	public ExcutorDb(Context context) {
		super(context, DBNAME, null, 1);
	}
	
	
	private static ExcutorDb excutorDb = null;
	
	public static ExcutorDb getInstance(Context context){
		
		if (excutorDb == null) {
			excutorDb = new ExcutorDb(context);
		}
		return excutorDb;
		
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(sql);//执行sql语句
		
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
