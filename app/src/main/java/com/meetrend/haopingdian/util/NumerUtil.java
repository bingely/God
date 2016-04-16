package com.meetrend.haopingdian.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class NumerUtil {

	/**
	 * 保留两位小数
	 * 
	 * @param dvalue
	 *            需要格式化的数据
	 * @return
	 */
	public static String getNum(String dvalue) {

		BigDecimal bigDecimal = new BigDecimal(dvalue);
		bigDecimal.setScale(1, BigDecimal.ROUND_DOWN);
		BigDecimal newBigDecimal = bigDecimal;
		DecimalFormat decimalFormat = new DecimalFormat("0.0");// 保留两位小数，并用都好隔开
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		String result = decimalFormat.format(newBigDecimal);
		return result;
	}

	public static String saveOneDecimal(String dvalue) {

		BigDecimal bigDecimal = new BigDecimal(dvalue);
		bigDecimal.setScale(1, BigDecimal.ROUND_DOWN);
		BigDecimal newBigDecimal = bigDecimal;

		DecimalFormat decimalFormat = new DecimalFormat("0.0");// 保留两位小数，并用都好隔开
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		String result = decimalFormat.format(newBigDecimal);

		return result;
	}

	public static String saveThreeDecimal(String dvalue) {

		BigDecimal bigDecimal = new BigDecimal(dvalue);
		bigDecimal.setScale(3, BigDecimal.ROUND_DOWN);
		BigDecimal newBigDecimal = bigDecimal;

		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		String result = decimalFormat.format(newBigDecimal);

		return result;
	}

	/**
	 * 
	 * @param dvalue
	 *            需要格式化的数据
	 * @param scale
	 *            保留几位小数
	 * @return
	 */
	public static String saveScaleDecimal(String dvalue, int scale) {

		BigDecimal bigDecimal = new BigDecimal(dvalue);
		bigDecimal.setScale(scale, BigDecimal.ROUND_DOWN);
		BigDecimal newBigDecimal = bigDecimal;

		DecimalFormat decimalFormat = new DecimalFormat("0.0");// 保留两位小数，并用都好隔开
		// DecimalFormat decimalFormat = new DecimalFormat("0.00");//保留两位小数
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		String result = decimalFormat.format(newBigDecimal);
		// System.out.print(result);
		return result;
	}

	public static String getNum2(String dvalue) {
		BigDecimal bigDecimal = new BigDecimal(dvalue);
		bigDecimal.setScale(1, BigDecimal.ROUND_DOWN);
		BigDecimal newBigDecimal = bigDecimal;

		DecimalFormat decimalFormat = new DecimalFormat("#,###.0");// 保留两位小数，并用都好隔开
		// DecimalFormat decimalFormat = new DecimalFormat("0.00");//保留两位小数
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		String result = decimalFormat.format(newBigDecimal);
		return result;
	}

	public static String formatTwoDecimalsWithDivide(String numstr) {
		if (TextUtils.isEmpty(numstr)
				|| numstr.equals(" ")
				|| numstr.equals("0")
				|| numstr.equals("0.00")
				|| !NumerUtil.isFloat(numstr) || NumerUtil.isInteger(numstr)){
			return "0.00";
		}else{
			BigDecimal bigDecimal = new BigDecimal(numstr);
			bigDecimal.setScale(1, BigDecimal.ROUND_DOWN);
			BigDecimal newBigDecimal = bigDecimal;
			DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
			String result = decimalFormat.format(newBigDecimal);
			return result;
		}
	}

	public static String formatNumStrhDivide(String numstr) {
		if (TextUtils.isEmpty(numstr)
				|| numstr.equals(" ")
				|| numstr.equals("0")
				|| numstr.equals("0.00")
				|| !NumerUtil.isFloat(numstr) || !NumerUtil.isInteger(numstr)){
			return "0";
		}else{
			BigDecimal bigDecimal = new BigDecimal(numstr);
			bigDecimal.setScale(1, BigDecimal.ROUND_DOWN);
			BigDecimal newBigDecimal = bigDecimal;
			DecimalFormat decimalFormat = new DecimalFormat("#,###");
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
			String result = decimalFormat.format(newBigDecimal);
			return result;
		}
	}

	//单价或者总金额都直接截取两位小数，并且做去分处理
	public static String setSaveTwoDecimals(String dvalue) {
		BigDecimal bigDecimal = new BigDecimal(dvalue);
		bigDecimal.setScale(2, BigDecimal.ROUND_DOWN);
		BigDecimal newBigDecimal = bigDecimal;
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 保留两位小数，并用都好隔开
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		String result = decimalFormat.format(newBigDecimal);
		
		//去分处理
		if (!result.endsWith("0")) {
			StringBuilder stringBuilder = new StringBuilder(result);
			result = stringBuilder.deleteCharAt(stringBuilder.length()-1).append('0').toString();
		}
		
		return result;
	}

	public static String setSaveOneDecimals(String dvalue) {
		BigDecimal bigDecimal = new BigDecimal(dvalue);
		bigDecimal.setScale(1, BigDecimal.ROUND_DOWN);
		BigDecimal newBigDecimal = bigDecimal;
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		String result = decimalFormat.format(newBigDecimal);
		return result;
	}

	public static int count(String s, char ch) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ch) {
				count++;
			}
		}

		return count;
	}

	// 浮点型判断
	public static boolean isFloat(String str) {
		if (str == null || "".equals(str))
			return false;
		java.util.regex.Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
		return pattern.matcher(str).matches();
	}

	// 整型判断
	public static boolean isInteger(String str) {
		if (str == null)
			return false;
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}

	/**
	 * 四舍五入算法
	 * @param value
	 * @return
	 */
	public static double bigDecimalRoundHalfUp(double value) {

		BigDecimal decimal = new BigDecimal(value);
		// 保留2位小数
		double result = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				
		
		return result;
	}
	
	public static double bigDecimalSaveOneRoundHalfUp(float value) {

		BigDecimal decimal = new BigDecimal(value);
		// 保留2位小数
		double result = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
				
		
		return result;
	}
	
}