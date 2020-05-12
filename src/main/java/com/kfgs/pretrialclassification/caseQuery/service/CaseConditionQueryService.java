package com.kfgs.pretrialclassification.caseQuery.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface CaseConditionQueryService {

    //查询所有案件
    IPage findAll(String pageNo, String limit,String id,String name,String sqr,String sqh);

    //根据搜索条件查询案件
    IPage findByCondition(String pageNo, String limit, Map conList);

    //根据预审编号查询案件
    IPage findById(String pageNo,String limit,String id);

    //根据申请号查询案件
    IPage findBySQH(String pageNo,String limit,String sqh);

    //根据案件名称查询案件
    IPage findByName(String pageNo,String limit,String name);

    //根据申请人查询案件
    IPage findBySQR(String pageNo,String limit,String sqr);
}
