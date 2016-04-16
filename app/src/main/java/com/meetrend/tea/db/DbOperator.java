package com.meetrend.tea.db;

import java.util.ArrayList;
import java.util.List;

//import u.aly.cu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.meetrend.haopingdian.bean.ExecutorEntity;

public class DbOperator {
	
	
	private static DbOperator dbOperator = null;
	
	public DbOperator(){
		
	}
	
	public static DbOperator getInstance(){
		
		if (dbOperator == null) {
			dbOperator = new DbOperator();
		}
		return dbOperator;
	}
	
	//存储执行人
	public  void saveExecutors(Context context,List<ExecutorEntity> list){
	
		SQLiteDatabase sqLiteDatabase = ExcutorDb.getInstance(context).getWritableDatabase();
		
		try {
			sqLiteDatabase.beginTransaction();
			for (int i = 0; i < list.size(); i++) {
				ExecutorEntity item = list.get(i);
				String id = item.getId();
				String name = item.getUserName();
				String pid = item.getAvatarId();
				
				ContentValues contentValues = new ContentValues();
				contentValues.put("id", id);//登录用户名
				contentValues.put("userName", name);
				contentValues.put("avatarId", pid);
				sqLiteDatabase.insert("excutor", null, contentValues);//存储
			}
			sqLiteDatabase.setTransactionSuccessful();
			System.out.println("db insert success !!!");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("db insert fail !!!");
		}finally{
			
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
			
		}
	}
	
	//获取所有执行人
	public List<ExecutorEntity> getExecutorList(Context context){
		
		List<ExecutorEntity> executorList = new ArrayList<ExecutorEntity>();
		SQLiteDatabase sqLiteDatabase = ExcutorDb.getInstance(context).getWritableDatabase();
		Cursor cursor = null;
		sqLiteDatabase.beginTransaction();//开启事务
		try {
			cursor = sqLiteDatabase.rawQuery("select * from excutor", null);
			if (!cursor.moveToFirst()) {
				cursor.close();
				return executorList;
			}
			do {
				String id = cursor.getString(cursor.getColumnIndex("id"));
				String name = cursor.getString(cursor.getColumnIndex("userName"));
				String avatarId = cursor.getString(cursor.getColumnIndex("avatarId"));
				ExecutorEntity entity = new ExecutorEntity(id, name, avatarId);
				executorList.add(entity);
			} while (cursor.moveToNext());
			
			sqLiteDatabase.setTransactionSuccessful();
			System.out.println("db get data success !!!");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("db get data fail !!!");			
		}finally{
			sqLiteDatabase.endTransaction();//结束事务
			if (cursor != null) {
				cursor.close();
			}
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
		}
		return executorList;
	}
	
	
	//删除表中所有数据
	public void clearTable(Context context){
		try {
			SQLiteDatabase sqLiteDatabase = ExcutorDb.getInstance(context).getWritableDatabase();
			String sql = "delete from excutor";
			sqLiteDatabase.execSQL(sql);
			System.out.println("db delete success !!!");
		} catch (Exception e) {
			System.out.println("db delete fail !!!");
		}
	}
	
}
