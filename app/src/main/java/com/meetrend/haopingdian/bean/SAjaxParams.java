package com.meetrend.haopingdian.bean;


import java.io.Serializable;
import net.tsz.afinal.http.AjaxParams;

public class SAjaxParams implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AjaxParams ajaxParams;

	public AjaxParams getAjaxParams() {
		return ajaxParams;
	}

	public void setAjaxParams(AjaxParams ajaxParams) {
		this.ajaxParams = ajaxParams;
	}

}
