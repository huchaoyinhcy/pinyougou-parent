package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileOutputStream;
import java.io.FileWriter;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {


        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map dataModel = new HashMap<>();

            //加载商品数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);

            //加载商品扩展数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);

            //商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey((goods.getCategory1Id())).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey((goods.getCategory2Id())).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey((goods.getCategory3Id())).getName();
            dataModel.put("itemCat1", itemCat1);
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);

            //sku列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//状态为1有效
            criteria.andGoodsIdEqualTo(goodsId);//指定 SPU ID
            example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList", itemList);

            //Writer out = new FileWriter(pagedir + goodsId + ".html");
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pagedir + goodsId + ".html"), "utf-8");
            template.process(dataModel, out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
