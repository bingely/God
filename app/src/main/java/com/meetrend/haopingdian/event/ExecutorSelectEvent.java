package com.meetrend.haopingdian.event;

public class ExecutorSelectEvent {

	private String executorId;
	
	public ExecutorSelectEvent(String executorId) {
		this.executorId = executorId;
	}
	
	public String getExecutorId() {
		return executorId;
	}

}
