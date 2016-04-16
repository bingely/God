package com.meetrend.haopingdian.memberlistdb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.meetrend.haopingdian.bean.StoreInfo;
import com.umeng.socialize.utils.Log;

/**
 * 门店信息 数据库操作
 * @author 肖建斌
 *
 */
public class StoreInfoOpetate {
	
private static StoreInfoOpetate dbOperator = null;
	
	public StoreInfoOpetate(){
		
	}
	
	public static StoreInfoOpetate getInstance(){
		if (dbOperator == null) {
			dbOperator = new StoreInfoOpetate();
		}
		return dbOperator;
	}
	
	//
	public  void saveStoreInfos(Context context,StoreInfo item){
	
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		try {
				sqLiteDatabase.beginTransaction();
				
				ContentValues contentValues = new ContentValues();
				contentValues.put("store_name", item.storeName);
				contentValues.put("store_id", item.storeId);
				contentValues.put("login_account", item.loginCount);
				contentValues.put("login_account_pwd", item.loginPwd);
				contentValues.put("base_url", item.baseUrl);
				contentValues.put("shanghu_num", item.shanghuNum);
				
				sqLiteDatabase.insert(MemberDb.STORE_INFO_TABLE, null, contentValues);//存储
				
				sqLiteDatabase.setTransactionSuccessful();
				System.out.println("门店数据存储成功 !!!");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("门店数据存储失败 !!!");
		}finally{
			
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
			
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param storeId 门店id
	 * @param shanghuNum 商户号
	 * @return
	 */
	public boolean findOneStoreInfo(Context context,String storeId,String shanghuNum){
		
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		Cursor cursor = null;
		String storeName = null;
		String mshanghuNum = null;
		
		sqLiteDatabase.beginTransaction();//开启事务
		try {
			
			cursor = sqLiteDatabase.rawQuery("select * from "+ MemberDb.STORE_INFO_TABLE
					+" where store_id =" + "'"+ storeId +"'" +" and " + "shanghu_num =" + "'"+ shanghuNum +"'", null);
			
			if (!cursor.moveToFirst()) {
				cursor.close();
				return false;
			}
			
			storeName = cursor.getString(cursor.getColumnIndex("store_name"));
			mshanghuNum = cursor.getString(cursor.getColumnIndex("shanghu_num"));
			sqLiteDatabase.setTransactionSuccessful();
			
		} catch (Exception e) {
			
		}finally{
			
			sqLiteDatabase.endTransaction();//结束事务
			if (cursor != null) {
				cursor.close();
			}
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
		}
		
		if (null != storeName && null != mshanghuNum) {
			return true;
		}
		return false;
	}

	/**
	 * 更新门店信息
	 */
	public void upDateStoreLoginPwd(Context context,String loginPwd,StoreInfo historyStoreInfo){

			SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
			sqLiteDatabase.beginTransaction();//开启事务

			try {
				  String updateSql = "update "+ MemberDb.STORE_INFO_TABLE +" set login_account_pwd =" + "'"+ loginPwd +"'"
						+" where store_id =" + "'"+ historyStoreInfo.storeId +"'" +" and " + "shanghu_num =" + "'"+ historyStoreInfo.shanghuNum +"'";
				 sqLiteDatabase.execSQL(updateSql);
				 sqLiteDatabase.setTransactionSuccessful();
				 Log.i("upDateStoreLoginPwd--"+"密码修改成功","j");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				sqLiteDatabase.endTransaction();//结束事务
				if (sqLiteDatabase != null) {
					sqLiteDatabase.close();
				}
			}
	}
	
	/**
	 * 查找商户号为shangWuNum的门店信息
	 * @param context
	 * @param shangWuNum 商户号
	 * @return
	 */
	public List<StoreInfo> getStoreInfoList(Context context,String shangWuNum){
		
		List<StoreInfo> storeInfosList = new ArrayList<StoreInfo>();
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		Cursor cursor = null;
		sqLiteDatabase.beginTransaction();//开启事务
		try {
			cursor = sqLiteDatabase.rawQuery("select * from "+ MemberDb.STORE_INFO_TABLE + " where shanghu_num="+"'"+ shangWuNum +"'", null);
			if (!cursor.moveToFirst()) {
				cursor.close();
				return storeInfosList;
			}
			do {
				
				StoreInfo info = new StoreInfo();
				info.storeName =  cursor.getString(cursor.getColumnIndex("store_name"));
				info.storeId =  cursor.getString(cursor.getColumnIndex("store_id"));
				info.loginCount =  cursor.getString(cursor.getColumnIndex("login_account"));
				info.loginPwd =  cursor.getString(cursor.getColumnIndex("login_account_pwd"));
				info.baseUrl = cursor.getString(cursor.getColumnIndex("base_url"));
				info.shanghuNum = cursor.getString(cursor.getColumnIndex("shanghu_num"));
				storeInfosList.add(info);
				
			} while (cursor.moveToNext());
			
			sqLiteDatabase.setTransactionSuccessful();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			sqLiteDatabase.endTransaction();//结束事务
			if (cursor != null) {
				cursor.close();
			}
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
		}
		
		return storeInfosList;
	}
	
	//删除表中所有数据
	public void clearStoreInfoDatas(Context context){
		try {
			SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
			String sql = "delete from "+ MemberDb.STORE_INFO_TABLE;
			sqLiteDatabase.execSQL(sql);
			System.out.println("db delete success !!!");
		} catch (Exception e) {
			System.out.println("db delete fail !!!");
		}
	}

}
