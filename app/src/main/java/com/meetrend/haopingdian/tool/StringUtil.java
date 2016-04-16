package com.meetrend.haopingdian.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class StringUtil {
	private static final String TAG = StringUtil.class.getSimpleName();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");
	private static HanyuPinyinOutputFormat formater;

	/**
	 * 将服务器返回的从1970 年 1 月 1 日的毫秒数转为指定显示形式
	 * 
	 * @param millionSecond
	 * @return
	 */
	public static String formatDateStr(String millionSecond) {
		if (formater == null) {
			formater = new HanyuPinyinOutputFormat();
			formater.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 不要音标
			formater.setVCharType(HanyuPinyinVCharType.WITH_V);
			formater.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		}
		try {
			String tmp = DATE_FORMAT.format(new Date(Long.parseLong(millionSecond)));
			return tmp;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			LogUtil.w(TAG, "formatDateStr error : " + millionSecond);
			return "";
		}
	}

	/**
	 * 输入 pinyinFormat("吴桂发") 输出 wu吴 gui桂 fa发
	 * 
	 * @param hanziStr
	 * @return
	 * @see http://blog.csdn.net/jimzhai/article/details/7530299
	 * @see http://blog.csdn.net/hfhwfw/article/details/6030816
	 */
	
	
	CharacterParser characterParser = null;
	public  String pinyinFormat(String hanziStr) {
		if (null == characterParser) {
			characterParser = new CharacterParser();
		}
		String spellingStr = characterParser.getSelling(hanziStr);
		
		return spellingStr; 
		
//		if (formater == null) {
//			formater = new HanyuPinyinOutputFormat();
//			formater.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 不要音标
//			formater.setVCharType(HanyuPinyinVCharType.WITH_V);
//			formater.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//		}
//		
//		try {
//			StringBuilder sb = new StringBuilder();
//			char[] charArray = hanziStr.toCharArray();
//			
//			for (char ch : charArray) {
//				if (java.lang.Character.toString(ch).matches("[\\u4E00-\\u9FA5]+")) {
//					String[] strArray = PinyinHelper.toHanyuPinyinStringArray(ch, formater);
//					
//					if (null == strArray) {
//						return "";
//					}
//					
//					sb.append(strArray[0]);
//					sb.append(ch);
//					sb.append(" ");
//				} else {
//					sb.append(ch);
//				}
//			}
//			return sb.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//			LogUtil.w(TAG, e.getMessage());
//			return "";
//		} 
	}
	
	public static boolean isEmpty(String str){
		if(str==null || "".equals(str)){
			return true;
		}
		return false;
	}
}
