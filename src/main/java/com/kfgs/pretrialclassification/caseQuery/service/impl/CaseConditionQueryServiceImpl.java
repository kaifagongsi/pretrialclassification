package com.kfgs.pretrialclassification.caseQuery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseQuery.service.CaseConditionQueryService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaseConditionQueryServiceImpl implements CaseConditionQueryService {

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Override
    @Transactional
    //查询所有案件
    public IPage findAll(String pageNo, String limit,String id,String name,String sqr,String sqh) {
        Map resultMap = new HashMap();
        //判断是否有输入条件
        int flag = 0;
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        QueryWrapper queryWrapper = new QueryWrapper();
        if(id != "" && id != null && id.length()!=0){
            flag = 1;
            queryWrapper.like("id",id);
        }
        if(name != "" && name != null && name.length()!=0){
            flag = 1;
            queryWrapper.like("name",name);
        }
        if(sqr != "" && name != null && name.length()!=0){
            flag = 1;
            queryWrapper.like("sqr",sqr);
        }
        if(sqh != "" && sqh != null && sqh.length()!= 0){
            flag = 1;
            queryWrapper.like("sqh",sqh);
        }
        if(flag == 1){
            IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
            flag = 0;
            return iPage;
        } else{
            IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, null);
            return iPage;
        }
    }

    @Override
    @Transactional
    //根据查询条件查询案件
    public IPage findByCondition(String pageNo,String limit,Map conList){
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        if(conList != null) {
            if (conList.get("id").toString() != "") {
                String id = conList.get("id").toString();
                queryWrapper.like("id", id);
            }
            if ( conList.get("name").toString() != "") {
                String name = conList.get("name").toString();
                queryWrapper.like("name", name);
            }
        }
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;

    }
    @Override
    @Transactional
    //根据预审编号查询案件
    public IPage findById(String pageNo, String limit,String id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        //模糊查询
        queryWrapper.like("id",id);
        Map resultMap = new HashMap();
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据申请号查询案件
    public IPage findBySQH(String pageNo, String limit,String sqh) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("sqh",sqh);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据案件名称查询案件
    public IPage findByName(String pageNo, String limit,String name) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("mingcheng",name);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据申请人查询案件
    public IPage findBySQR(String pageNo, String limit,String sqr) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("sqr",sqr);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }
}
