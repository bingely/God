/**
 * 
 */
package com.meetrend.haopingdian.bean;

public class Point {
	
	public double xValue;
	public double yValue;
	
	public double dataValue;

	public int index;

	
	public Point(float xValue, int index) {
		super();
		this.xValue = xValue;
		this.index = index;
	}


	public Point(float xValue, float yValue) {
		super();
		this.xValue = xValue;
		this.yValue = yValue;
	}


	/**
	 * @param xValue
	 * @param yValue
	 * @param dataValue
	 */
	public Point(double xValue, double yValue, double dataValue) {
		super();
		this.xValue = xValue;
		this.yValue = yValue;
		this.dataValue = dataValue;
	}


	public Point(double xValue, double yValue, int index) {
		super();
		this.xValue = xValue;
		this.yValue = yValue;
		this.index = index;
	}
	
	

}
