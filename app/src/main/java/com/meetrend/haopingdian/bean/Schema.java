package com.meetrend.haopingdian.bean;

/**
 * 订单管理的 查询类型
 * 
 *
 */
public class Schema {
	/**
	 * 	查询方案名称
	 */
	public String text;
	/**
	 * 	查询方案Id
	 */
	public String value;
	
	/**
	 * 是否默认显示
	 * 
	 * */
	public boolean isDefault;

	public Schema(String text, String value,boolean isDefault) {
		this.text = text;
		this.value = value;
		this.isDefault = isDefault;
	}
	
	
	public Schema(){
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Schema [text=" + text + ", value=" + value + "]";
	}

}
