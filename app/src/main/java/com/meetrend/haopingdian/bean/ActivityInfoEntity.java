package com.meetrend.haopingdian.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ActivityInfoEntity implements Serializable{

	/**
	 * 活动主题
	 */
	public String name;
	/**
	 * 活动状态
	 */
	public String status;
	/**
	 * 详情图片
	 */
	public String pictures;
	/**
	 * 参与人数
	 */
	public String joinNumber;
	/**
	 * 阅读次数
	 */
	public String readNumber;
	/**
	 * 分享次数
	 */
	public String shareNumber;
	/**
	 * 活动图片
	 */
	public ArrayList<EventPictrue> activityImages;
	/**
	 * 活动参与人
	 */
	public ArrayList<EventPeople> activeRecords;
	/**
	 * 活动说明
	 */
	public String activityExplain;
	/**
	 * 活动详情
	 */
	public String detailExplain;
	/**
	 * 活动开始时间
	 */
	public String activityStartDate;
	/**
	 * 活动结束时间
	 */
	public String activityEndDate;
	/**
	 * 活动截止时间
	 */
	public String deadline;
	/**
	 * 活动地址
	 */
	public String address;
	/**
	 * 活动主办方
	 */
	public String activitySponsor;
	/**
	 * 活动参与方式
	 */
	public String activityParticipation;
	/**
	 * 活动限制人数
	 */
	public String limitTheNumber;
	/**
	 * 活动详情图片
	 */
	public String activityPicture;
	/**
	 * 评论数量
	 */
	public String numCommet;
	/**
	 * 点赞数量
	 */
	public String thumbsUp;
	/**
	 * 签到人数
	 */
	public String signNum;
	/**
	 * 图片
	 */
	public String pictureId;
	
}
