package com.meetrend.haopingdian.bean;

public class Executor {
	
	public ExecutorEntity entity;
	public String pinyinName;
	public boolean isSelected;
	
	public Executor(ExecutorEntity entity, String pinyinName) {
		this.entity = entity;
		this.pinyinName = pinyinName;
	}

	public boolean isSelected() {
		return isSelected;
	}


	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}