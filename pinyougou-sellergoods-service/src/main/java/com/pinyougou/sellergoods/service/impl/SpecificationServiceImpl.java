package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;


import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.Specification;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //插入规格
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.insert(tbSpecification);

        //插入规格选项
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setId(tbSpecification.getId());//获取规格id并设置
            specificationOptionMapper.insert(specificationOption);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {

        //保存修改的规格
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        //删除原有的规格参数
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());//指定规格的id
        specificationOptionMapper.deleteByExample(example);

        //循环插入
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }


    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //查询规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //查询规格选项
        TbSpecificationOptionExample emexample = new TbSpecificationOptionExample();

        TbSpecificationOptionExample.Criteria criteria = emexample.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(emexample);


        Specification specification = new Specification();
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptionList(tbSpecificationOptions);
        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除规格
            specificationMapper.deleteByPrimaryKey(id);
            //删除规格选项
            TbSpecificationOptionExample example=new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
