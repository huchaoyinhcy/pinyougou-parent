package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.Cart;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {



    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 购物车列表哦
     */

    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        String cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartList == null || cartList.equals("")) {
            cartList = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartList, Cart.class);
        if (username.equals("anonymousUser")){
            //未登录状态
            //读取本地购物车
            //cartList_cookie= JSON.parseArray(cartList, Cart.class);
            return cartList_cookie;
        }else {
            //如果登录了
            //从redis中获取
            List<Cart>  cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size()>0){//如果本地存在购物车
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //清楚本地cookie的购物车
                util.CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的数据存入redis中
                cartService.saveCartListToRedis(username,cartList_redis);
            }
            return cartList_redis;
        }
    }


    /**
     * 添加到购物车
     */

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins ="http://localhost:9105" )
    public Result addGoodsToCartList(Long itemId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        try {
            //获取购物车列表
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")){
                //如果是未登录，保存到 cookie
                util.CookieUtil.setCookie(request, response, "cartList",
                        JSON.toJSONString(cartList), 3600 * 24, "utf-8");
                System.out.println("向 cookie 存入数据");
            }else {
                //如果是已登录，保存到 redis
                cartService.saveCartListToRedis(username,cartList);
                System.out.println("从redis取数据");
            }

            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }
}
