package com.meetrend.haopingdian.bean;

import java.util.Comparator;

/**
 * 关联年份和ID
 * @author tigereye
 *
 */
public class YearPair {
	public String yearId;
	public String yearName;
	public YearPair(String id, String name) {
		yearId = id;
		yearName = name;
	}
	
	@Override
	public String toString() {
		return "YearPair [yearId=" + yearId + ", yearName=" + yearName + "]";
	};
	
    public static final YearPairComparator COMPARATOR = new YearPairComparator();
	private static final class YearPairComparator implements Comparator<YearPair> {
		@Override
		public int compare(YearPair lhs, YearPair rhs) {
			return lhs.yearName.compareTo(rhs.yearName);
		}
		
	}
}
