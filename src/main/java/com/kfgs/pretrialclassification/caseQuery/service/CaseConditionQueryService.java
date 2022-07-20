package com.kfgs.pretrialclassification.caseQuery.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface CaseConditionQueryService {

    //查询所有案件
    IPage findAll(String pageNo, String limit,String id,String name,String oraginization,String sqr,String sqh,String worker,String state,String begintime,String endtime,String enterBeginTime,String enterEndTime);

    //根据搜索条件查询案件
    IPage findByCondition(String pageNo, String limit, Map conList);

    //根据案件状态查询案件
    IPage findCaseByState(String pageNo,String limit,String state);

    //根据预审编号查询案件
    List findClassInfoByID(String id);

    //根据预审编号和分类员查询提交更正案件
    List findUpdateInfoByID(String id,String worker);

    //根据申请号查询案件
    IPage findBySQH(String pageNo,String limit,String sqh);

    //根据案件名称查询案件
    IPage findByName(String pageNo,String limit,String name);

    //根据申请人查询案件
    IPage findBySQR(String pageNo,String limit,String sqr);

    //Excel导出
    QueryResponseResult exportExcel(List<String> list, HttpServletResponse response);

    //批量导出Excel为压缩包
    QueryResponseResult exportExcelToZip(List<String> list,HttpServletResponse response);
}
