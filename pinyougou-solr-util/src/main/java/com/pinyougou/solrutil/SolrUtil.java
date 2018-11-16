package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component()
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入商品数据
     */
    public void importItemData() {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//已审核商品
        List<TbItem> itemList = itemMapper.selectByExample(example);
        for (TbItem item : itemList) {
            System.out.println(item.getId() + " " + item.getTitle() + " " + item.getPrice());
            Map specMap = JSON.parseObject(item.getSpec(),Map.class);//将 spec 字段中的 json 字符串转换为 map
            item.setSpecMap(specMap);//给带注解的字段赋值
        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("结束");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
