package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WeixinPayService {

    /**
     * 生成二维码
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 查询支付状态
     */
    Map queryPayStatus(String out_trade_no);


    /**
     * 关闭支付
     */

    Map closePay(String out_trade_no);
}
