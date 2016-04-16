package com.meetrend.haopingdian.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import android.text.TextUtils;

import com.meetrend.haopingdian.fragment.MemberListFragment;
import com.meetrend.haopingdian.tool.StringUtil;

public class ExecutorEntity {
	private static final String TAG = ExecutorEntity.class.getSimpleName();
	
	public String id;
	public String userName;
	public String avatarId;
//	public String storeId;
//	public String storeName;
//	public String address;
	
	
	private final static String ALPHATS ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static List<Executor> convert(List<ExecutorEntity> list) {
		StringUtil stringUtil = new StringUtil();
		TreeSet<String> set = new TreeSet<String> ();
		
		List<Executor> executorList = new ArrayList<Executor>();
		//存储'#'组下的数据
		List<Executor> tempList = new ArrayList<Executor>();
		
		try {
			for (ExecutorEntity item : list) {
				if(!TextUtils.isEmpty(item.userName)){
					
					String pinyinName = stringUtil.pinyinFormat(item.userName);
					String alphat = pinyinName.substring(0, 1).toUpperCase();
					
					if ("".equals(pinyinName) && null != pinyinName) {
						 pinyinName = "##";
					}else if (pinyinName.length() >= 1){
						
						if (!ALPHATS.contains(alphat)) {
							pinyinName = "##";
						}
					}
					
					String tAlphat = pinyinName.substring(0, 1).toUpperCase();
					//存储属于'#'组的真实数据
					if (tAlphat.equals("#")) {
						Executor executor = new Executor(item, pinyinName);
						tempList.add(executor);
					}else{
						executorList.add(new Executor(item, pinyinName));
					}
						
					if (ALPHABET.contains(tAlphat)) {   
						set.add(tAlphat);
					}
				}
			}
			
			//填充虚拟的数据
			for (String str : set) {
				
				Executor executor = new Executor(null, str);
				if (str.equals("#")) 
					tempList.add(0, executor);
				else 
					executorList.add(executor);
				
			}
			//排序
			Collections.sort(executorList, PinyinComparator);
			
			//按26个字母拍完序后，添加'#'组的数据
			executorList.addAll(tempList);
			
			return executorList;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}

	public ExecutorEntity(String id, String userName, String avatarId) {
	super();
	this.id = id;
	this.userName = userName;
	this.avatarId = avatarId;
}

	public static final Comparator<Executor> PinyinComparator = new Comparator<Executor>() {
		@Override
		public int compare(Executor arg0, Executor arg1) {
			return arg0.pinyinName.compareToIgnoreCase(arg1.pinyinName);
		}		
	};
	
 }

