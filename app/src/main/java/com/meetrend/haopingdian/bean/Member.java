package com.meetrend.haopingdian.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.meetrend.haopingdian.fragment.MemberListFragment;
import com.meetrend.haopingdian.tool.StringUtil;

public class Member implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final static String ALPHATS ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	
	public String customerName;
	public String managerId;
	public String type;//值为"member" ,"employee"
	public String memberId;
	public String pictureId;
	public String userId;
	public String createTime;
	public String userName;
	public String status ="0";//0：未邀请1：已邀请2：已绑定
	public String remark;
	public String mobile;
	public boolean checkstatus;//是否选中
	public int position;//当前位置
	
	public boolean isDefault;//true 是否是默认的店小二
	public boolean canTalk;//是否可以聊天
	public int isGroup;//是否是分组,用于通讯录分组，-1是分组0是非分组
	public String pinyinName;//用于分组排列的字段
	
	
	public Member(){
		
	}
	
	
	public Member(String sortLetters, String memberId,
			String pictureId, String userId, String status, String remark,
			String mobile, String customerName) {
		super();
		//this.sortLetters = sortLetters;
		this.memberId = memberId;
		this.pictureId = pictureId;
		this.userId = userId;
		this.status = status;
		this.remark = remark;
		this.mobile = mobile;
		this.customerName = customerName;
	}
	
	public Member(String sortLetters, String memberId,
			String pictureId, String userId, String status, String remark,
			String mobile, String customerName,String type) {
		super();
		//this.sortLetters = sortLetters;
		this.memberId = memberId;
		this.pictureId = pictureId;
		this.userId = userId;
		this.status = status;
		this.remark = remark;
		this.mobile = mobile;
		this.customerName = customerName;
		this.type = type;
	}
	
	public Member(String pictureId, String userId,String customerName,String type) {
		super();
		this.pictureId = pictureId;
		this.userId = userId;
		this.customerName = customerName;
		this.type = type;
	}
	
	public Member(String memberId, String pictureId, String userId,String createTime, String userName) {
		super();
		this.memberId = memberId;
		this.pictureId = pictureId;
		this.userId = userId;
		this.createTime = createTime;
		this.userName = userName;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * 
	 * 按字母排序
	 * 
	 * */
	public static ArrayList<Member> convert(List<Member> list) {
		
		StringUtil stringUtil = new StringUtil();
		
		TreeSet<String> set = new TreeSet<String>();
		ArrayList<Member> memberList = new ArrayList<Member>();
		
		//存储'#'对应的member
		List<Member> tempList = new ArrayList<Member>();
		
		for (Member item : list) {
			
			String pinyinName = "";
			if (item != null) {
				
				pinyinName = stringUtil.pinyinFormat(item.customerName);
				
				//Log.i("## pinyinName", pinyinName);
				
				if ("".equals(pinyinName) && null != pinyinName) {
					
					 pinyinName = "##";
					 
				}else if (pinyinName.length() >= 1){
					
					String firstName = pinyinName.substring(0, 1);
					if (!ALPHATS.contains(firstName)) {
						pinyinName = "##";
					}
				}
				
				String alphat =  pinyinName.substring(0, 1).toUpperCase();//取一个字符
				
				//存储属于'#'组的真实数据
				if (alphat.equals("#")) {
					 
					item.pinyinName = pinyinName;
					item.status = item.status;
					tempList.add(item);
				}else{
					
					item.pinyinName = pinyinName +" ";//此处一定要加空格（勿动）
					item.status = item.status;
					memberList.add(item);
				}
					
				if (MemberListFragment.ALPHABET.contains(alphat)) {   
					set.add(alphat);
				}
			}
		}	
		
		//填充虚拟的数据,显示分组的组名
		Member member = null;
		for (String str : set) {
			
			member = new Member();
			member.pinyinName = str;
			member.isGroup = -1;
			
			if (str.equals("#")) 
				//添加属于'#'组的的虚拟数据
				tempList.add(0, member);
			else 
				memberList.add(member);
		}
		
		//排序
		Collections.sort(memberList, Member.PinyinComparator);
		
		//for (int i = 0; i < memberList.size(); i++) {
			//Log.i("--------pingyin------------", memberList.get(i).pinyinName);
		//	Log.i("---------------------", memberList.get(i).isGroup+"");
		//}
		
		//按26个字母拍完序后，添加'#'组的数据
		memberList.addAll(tempList);
		
		return memberList;
	}
	
	/**
	 * 删除后排序
	 * @return
	 */
//	public static ArrayList<Member> AfterDelSort(List<Member> list) {
//		
//		TreeSet<String> set = new TreeSet<String>();
//		ArrayList<Member> memberList = new ArrayList<Member>();
//		
//		//存储'#'对应的member
//		List<Member> tempList = new ArrayList<Member>();
//		
//		for (Member item : list) {
//			
//			String alphat =  item.pinyinName.substring(0, 1).toUpperCase();//取一个字符
//				
//			if (MemberListFragment.ALPHABET.contains(alphat)) {   
//				set.add(alphat);
//			}
//		}	
//		
//		//填充虚拟的数据,显示分组的组名
//		Member member = null;
//		for (String str : set) {
//			member = new Member();
//			member.pinyinName = str;
//			member.isGroup = -1;
//			
//			if (str.equals("#")) 
//				//添加属于'#'组的的虚拟数据
//				tempList.add(0, member);
//			else 
//				memberList.add(member);
//		}
//		
//		//排序
//		Collections.sort(memberList, Member.PinyinComparator);
//		
//		//按26个字母拍完序后，添加'#'组的数据
//		memberList.addAll(tempList);
//		
//		return memberList;
//	}

	public static boolean hasSpecialCharacter(String str) {
		String regEx = "€[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
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

	public static final Comparator<Member> PinyinComparator = new Comparator<Member>() {
		@Override
		public int compare(Member arg0, Member arg1) {
			return arg0.pinyinName.compareToIgnoreCase(arg1.pinyinName);
		}
	};
	
	
	
	public boolean isCheckstatus() {
		return checkstatus;
	}

	public void setCheckstatus(boolean checkstatus) {
		this.checkstatus = checkstatus;
	}

	public enum STATUS {
		NOINVITE, INVITE, BIND
	};

//	private static final Map<Integer, STATUS> intToTypeMap = new HashMap<Integer, STATUS>();
//	static {
//		for (STATUS type : STATUS.values()) {
//			intToTypeMap.put(type.ordinal(), type);
//		}
//	}
//	
//	public static STATUS fromInt(int i) {
//		STATUS type = intToTypeMap.get(Integer.valueOf(i));
//		if (type == null) {
//			return null;
//		} else {
//			return type;
//		}
//	}



}