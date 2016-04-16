package com.meetrend.haopingdian.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.meetrend.haopingdian.fragment.MemberListFragment;
import com.meetrend.haopingdian.tool.StringUtil;

public class People {
	public String pinyin;
	public String name;
	public String phoneNumber;
	public int status = 0;//1已添加 0 没有添加
	public boolean isCheck;//是否选中

	public People(String name, String phoneNumber,String pinyin) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.pinyin = pinyin;
	}
	@Override
	public String toString() {
		return "name : " + name + ", " + "phoneNumber : " + phoneNumber + ",pinyin : " + pinyin;
	}
	
	
//	public String getSortLetters() {
//		return sortLetters;
//	}
//	public void setSortLetters(String sortLetters) {
//		this.sortLetters = sortLetters;
//	}


//	public static List<People> convert(List<People> list) {
//		TreeSet<String> set = new TreeSet<String>();
//		List<People> pList = new ArrayList<People>();
//		int i =0;
//		for (People item : list) {
//			
//			String pinyinName = StringUtil.pinyinFormat(item.name);
//			item.pinyin = pinyinName;
//			pList.add(item);
//			set.add(pinyinName.substring(0, 1).toUpperCase()); //过滤 得到首字母集合
//		}		
//		
//		for (String str : set) {
//			pList.add(new People(str, "",str));
//		}
//		Collections.sort(pList, People.PinyinComparator);
//		
//		return pList;
//	}
	
	public String getPinyin() {
		return pinyin;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * 对联系人排序
	 * 
	 * @param list
	 * 
	 * @return
	 */
	private final static String ALPHATS ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	public static List<People> convert(List<People> list) {
		StringUtil stringUtil = new StringUtil();
			List<People> resultList = new ArrayList<People>();
			//存储'#'字符的对象
			List<People> tempList = new ArrayList<People>();
			
			TreeSet<String> set = new TreeSet<String>();
			
			for (People people : list) {
				String pinyinName = null;
				if (people != null) {
					pinyinName = stringUtil.pinyinFormat(people.name);
					
					if ("".equals(pinyinName) && null != pinyinName) {
						 pinyinName = "##";
					}else if (pinyinName.length() >= 1){
						
						String firstName = pinyinName.substring(0, 1).toUpperCase();
						if (!ALPHATS.contains(firstName)) {
							pinyinName = "##";
						}
					}
					
					people.setPinyin(pinyinName);
					String alphat =  (pinyinName.substring(0, 1)).toUpperCase();//取一个字符并转成字符串
					
					//存储属于'#'组的真实数据
					if (alphat.equals("#")) {
						tempList.add(people);
					}else{
						resultList.add(people);
					}

					if (ALPHATS.contains(alphat)) {   
						set.add(String.valueOf(alphat));
					}else {
						if (set.contains("#")) {
							continue;
						}
						set.add("#");
					}
				}
			 }	
			
			//填充虚拟的数据
			for (String str : set) {
				
				People people = new People("", "", str);
				if (str.equals("#")) 
					//添加属于'#'组的的虚拟数据
					tempList.add(0, people);
				else 
					resultList.add(people);
			}
			
			Collections.sort(resultList, People.PinyinComparator);
			
			resultList.addAll(tempList);
			
		return resultList;
	}

	public static boolean hasSpecialCharacter(String str) {
		 String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
	public static final Comparator<People> PinyinComparator = new Comparator<People>() {
		@Override
		public int compare(People arg0, People arg1) {
			return arg0.pinyin.compareToIgnoreCase(arg1.pinyin);
		}		
	};
}