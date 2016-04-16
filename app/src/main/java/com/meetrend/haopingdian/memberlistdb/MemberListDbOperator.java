package com.meetrend.haopingdian.memberlistdb;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.meetrend.haopingdian.bean.Member;

public class MemberListDbOperator {
	
	
	private static MemberListDbOperator dbOperator = null;
	
	public MemberListDbOperator(){
		
	}
	
	public static MemberListDbOperator getInstance(){
		
		if (dbOperator == null) {
			dbOperator = new MemberListDbOperator();
		}
		return dbOperator;
	}
	
	//存储执行人
	public  void saveMembers(Context context,List<Member> list){
	
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		try {
			sqLiteDatabase.beginTransaction();
			int size = list.size();
			
			for (int i = 0; i < size; i++) {
				Member item = list.get(i);
				
				ContentValues contentValues = new ContentValues();
				contentValues.put("customerName", item.customerName);
				contentValues.put("managerId", item.managerId);
				contentValues.put("type", item.type);
				contentValues.put("memberId", item.memberId);
				contentValues.put("pictureId", item.pictureId);
				contentValues.put("userId", item.userId);
				contentValues.put("createTime", item.createTime);
				contentValues.put("userName", item.userName);
				contentValues.put("status", item.status);
				contentValues.put("remark", item.remark);
				contentValues.put("mobile", item.mobile);
				contentValues.put("position", item.position);//整形
				contentValues.put("isGroup", item.isGroup);
				contentValues.put("pinyinName", item.pinyinName);
				
				contentValues.put("checkstatus", item.checkstatus == true ? "0" :"-1" );//boolea类型（sql不能存储该类型）
				contentValues.put("isDefault", item.isDefault == true ? "0" :"-1");
				contentValues.put("canTalk", item.canTalk == true ? "0" :"-1");
				
				sqLiteDatabase.insert(MemberDb.TABLE_NAME, null, contentValues);//存储
				
			}
			
			
			sqLiteDatabase.setTransactionSuccessful();
			System.out.println("通讯录数据存储成功 !!!");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("通讯录数据存储失败 !!!");
		}finally{
			
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
			
		}
	}
	
	
	//存储执行人
	public  void saveOneMember(Context context,Member member){
	
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		
		try {
				sqLiteDatabase.beginTransaction();
			
				Member item = member;
				ContentValues contentValues = new ContentValues();
				contentValues.put("customerName", item.customerName);
				contentValues.put("managerId", item.managerId);
				contentValues.put("type", item.type);
				contentValues.put("memberId", item.memberId);
				contentValues.put("pictureId", item.pictureId);
				contentValues.put("userId", item.userId);
				contentValues.put("createTime", item.createTime);
				contentValues.put("userName", item.userName);
				contentValues.put("status", item.status);
				contentValues.put("remark", item.remark);
				contentValues.put("mobile", item.mobile);
				contentValues.put("position", item.position);//整形
				contentValues.put("isGroup", item.isGroup);
				contentValues.put("pinyinName", item.pinyinName);
				
				contentValues.put("checkstatus", item.checkstatus == true ? "0" :"-1" );
				contentValues.put("isDefault", item.isDefault == true ? "0" :"-1");
				contentValues.put("canTalk", item.canTalk == true ? "0" :"-1");
				
				sqLiteDatabase.insert(MemberDb.TABLE_NAME, null, contentValues);//存储
			
				sqLiteDatabase.setTransactionSuccessful();
				System.out.println("通讯录数据存储成功 !!!");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("通讯录数据存储失败 !!!");
		}finally{
			
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
			
		}
	}
	
	//获取所有执行人
	public List<Member> getMemberList(Context context){
		
		List<Member> executorList = new ArrayList<Member>();
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		Cursor cursor = null;
		sqLiteDatabase.beginTransaction();//开启事务
		try {
			cursor = sqLiteDatabase.rawQuery("select * from "+ MemberDb.TABLE_NAME, null);
			if (!cursor.moveToFirst()) {
				cursor.close();
				return executorList;
			}
			do {
				Member member = new Member();
				member.customerName =  cursor.getString(cursor.getColumnIndex("customerName"));
				member.managerId =  cursor.getString(cursor.getColumnIndex("managerId"));
				member.type =  cursor.getString(cursor.getColumnIndex("type"));
				member.memberId =  cursor.getString(cursor.getColumnIndex("memberId"));
				member.pictureId =  cursor.getString(cursor.getColumnIndex("pictureId"));
				member.userId =  cursor.getString(cursor.getColumnIndex("userId"));
				member.createTime =  cursor.getString(cursor.getColumnIndex("createTime"));
				member.userName =  cursor.getString(cursor.getColumnIndex("userName"));
				member.status =  cursor.getString(cursor.getColumnIndex("status"));
				member.remark =  cursor.getString(cursor.getColumnIndex("remark"));
				member.mobile =  cursor.getString(cursor.getColumnIndex("mobile"));
				member.position =  cursor.getInt(cursor.getColumnIndex("position"));//整形
				member.isGroup =  cursor.getInt(cursor.getColumnIndex("isGroup"));//整形
				
				member.pinyinName =  cursor.getString(cursor.getColumnIndex("pinyinName"));
				member.checkstatus =  cursor.getString(cursor.getColumnIndex("checkstatus")).equals("0") ? true : false;
				member.isDefault =  cursor.getString(cursor.getColumnIndex("isDefault")).equals("0") ? true : false;
				member.canTalk =  cursor.getString(cursor.getColumnIndex("canTalk")).equals("0") ? true : false;
				
				executorList.add(member);
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
	
	//模糊查找数据
	public ArrayList<Member> searchMembers(Context context,String key){
		
		ArrayList<Member> searchList = new ArrayList<Member>();
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		Cursor cursor = null;
		sqLiteDatabase.beginTransaction();//开启事务
		try {
			cursor = sqLiteDatabase.rawQuery("select * from "+ MemberDb.TABLE_NAME +" where customerName like '"+ "%"+key+"%'", null);//
			if (!cursor.moveToFirst()) {
				cursor.close();
				return searchList;
			}
			do {
				
				Member member = new Member();
				member.customerName =  cursor.getString(cursor.getColumnIndex("customerName"));
				member.managerId =  cursor.getString(cursor.getColumnIndex("managerId"));
				member.type =  cursor.getString(cursor.getColumnIndex("type"));
				member.memberId =  cursor.getString(cursor.getColumnIndex("memberId"));
				member.pictureId =  cursor.getString(cursor.getColumnIndex("pictureId"));
				member.userId =  cursor.getString(cursor.getColumnIndex("userId"));
				member.createTime =  cursor.getString(cursor.getColumnIndex("createTime"));
				member.userName =  cursor.getString(cursor.getColumnIndex("userName"));
				member.status =  cursor.getString(cursor.getColumnIndex("status"));
				member.remark =  cursor.getString(cursor.getColumnIndex("remark"));
				member.mobile =  cursor.getString(cursor.getColumnIndex("mobile"));
				member.position =  cursor.getInt(cursor.getColumnIndex("position"));//整形
				member.isGroup =  cursor.getInt(cursor.getColumnIndex("isGroup"));//整形
				
				member.pinyinName =  cursor.getString(cursor.getColumnIndex("pinyinName"));
				member.checkstatus =  cursor.getString(cursor.getColumnIndex("checkstatus")).equals("0") ? true : false;
				member.isDefault =  cursor.getString(cursor.getColumnIndex("isDefault")).equals("0") ? true : false;
				member.canTalk =  cursor.getString(cursor.getColumnIndex("canTalk")).equals("0") ? true : false;
				
				searchList.add(member);
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
		return searchList;
		
	}
	
	/**
	 * 根据会员的userId查找会员信息
	 * @param context
	 * @param userId 会员id
	 * @return
	 */
	public boolean findOneMember(Context context,String userId){
		
		String customerName = null;
		
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		Cursor cursor = null;
		
		sqLiteDatabase.beginTransaction();//开启事务
		try {
			
			cursor = sqLiteDatabase.rawQuery("select * from "+ MemberDb.TABLE_NAME
					+" where userId =" + "'"+ userId +"'" , null);
			
			if (!cursor.moveToFirst()) {
				cursor.close();
				return false;
			}
			
			customerName = cursor.getString(cursor.getColumnIndex("customerName"));
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
		
		if (null != customerName) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 删除表中的某条记录
	 * @param context
	 */
	public void clearOndMember(Context context,Member member){
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		sqLiteDatabase.beginTransaction();//开启事务
		
		try {
			 sqLiteDatabase.delete(MemberDb.TABLE_NAME, "userId = ?", new String[]{member.userId});
			 sqLiteDatabase.setTransactionSuccessful();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			sqLiteDatabase.endTransaction();//结束事务
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
		}
		
	}
	
	//更新状态
//	public void UpDateMemberCheckStatus(Context context,Member member){
//		
//		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
//		sqLiteDatabase.beginTransaction();//开启事务
//		
//		try {
//			 sqLiteDatabase.execSQL("update " + MemberDb.TABLE_NAME + "set checkstatus= 0 where userId=" + member.userId);
//			 sqLiteDatabase.setTransactionSuccessful();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			
//			sqLiteDatabase.endTransaction();//结束事务
//			if (sqLiteDatabase != null) {
//				sqLiteDatabase.close();
//			}
//		}
//		
//	}
	
	
	
	//删除表中所有数据
	public void clearMemberDatas(Context context){
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		sqLiteDatabase.beginTransaction();//开启事务
		
		try {
			String sql = "delete from "+ MemberDb.TABLE_NAME;
			sqLiteDatabase.execSQL(sql);
			
			sqLiteDatabase.setTransactionSuccessful();
			System.out.println("db delete success !!!");
		} catch (Exception e) {
			System.out.println("db delete fail !!!");
		}finally{
			
			sqLiteDatabase.endTransaction();//结束事务
			if (sqLiteDatabase != null) {
				sqLiteDatabase.close();
			}
		}
		
	}
	
	
	//数据库记录的总条数
	public long getSQLiteMemberListSize(Context context){
		
		long count = 0;
		Cursor cursor = null;
		
		SQLiteDatabase sqLiteDatabase = MemberDb.getInstance(context).getWritableDatabase();
		sqLiteDatabase.beginTransaction();//开启事务
		try {
			
//				cursor = sqLiteDatabase.rawQuery("select count(*) from "+ MemberDb.TABLE_NAME,null);
//				//游标移到第一条记录准备获取数据
//				cursor.moveToFirst();
//				count = cursor.getLong(0);
//				sqLiteDatabase.setTransactionSuccessful();
				String sql = "select count(*) from "+ MemberDb.TABLE_NAME;
				SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
				count = statement.simpleQueryForLong();
			
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
		
		return count;
	}
	
}