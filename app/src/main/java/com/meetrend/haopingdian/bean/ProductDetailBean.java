package com.meetrend.haopingdian.bean;

import java.util.List;

/**
 * Created by a on 2016/3/11.
 * 商品管理 产品详情
 */
public class ProductDetailBean {

    public String productId;
    public String productName;
    public String avatarId;
    public String catalogName;

    public String catalogId;
    public String minPrice;
    public String maxPrice;
    public String inventoryId;

    public String costPrice;
    public String offerUnitPrice;
    public String unitId;
    public String unitName;

    public String model1Value;
    public String model2Value;
    public String unitInventory;
    public String description;
    public String format;

    public String service;
    public List<ProductDetailPicBean> array;//详情图片

    public String freightAmount;//运费
    public String freightSet;//0:固定运费;1:运费模版
    public String templateId;//运费模板id
    public String templateName;//运费模板名字

    public String tempName;
}
