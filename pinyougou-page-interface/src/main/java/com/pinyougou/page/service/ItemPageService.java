package com.pinyougou.page.service;


/**
 * 商品详情页接口
 */
public interface ItemPageService {

    /**
     * 生成商品详细页
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);


    /**
     * 删除商品详情页
     */
    boolean delectItemHtml(Long[]goodsIds);
}

