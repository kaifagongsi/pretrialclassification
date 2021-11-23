package com.kfgs.pretrialclassification.caseClassification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;

import java.util.List;
import java.util.Map;

public interface CaseClassificationService {

    //按状态查询登录用户的案件
    IPage findCaseByState(String page, String limit, String state, String classtype, String user,String begintime,String endtime);

    //获取案件详情
    Map getCaseInfo(String id, String worker);

    //个人单一出案
    int updateResult(String id,String worker,String state);

    //main表，案件结束出案
    int updateMain(FenleiBaohuMain fenleiBaohuMain);

    //分类员条件查询案件
    FenleiBaohuMain searchByCondition(String id, String sqh, String mingcheng);

    //获取案件主副分详情
    List<FenleiBaohuResult> getSingleResult(String id, String sqr, String mingcheng);

    //查询转案人员列表
    List<String> getTransWorkerList(String id);

    //转案
    QueryResponseResult caseTrans(List<FenleiBaohuResult> list);

    //获取案件出案状态
    List<String> getCaseUnFinish(String id);

    //判断案件是否需要进裁决,返回理由或分类号
    QueryResponseResult caseRule(String id);
    //QueryResponseResult caseRule(FenleiBaohuResult fenleiBaohuResult);

    //保存分类号信息
    QueryResponseResult saveClassificationInfo(FenleiBaohuResult fenleiBaohuResult);

    //更改案件状态为裁决
    //int  updateCaseRule(String id,String ruleState);

    //个人出案
    QueryResponseResult caseFinish(String id,String user);

    //提交更正
    QueryResponseResult caseCorrect(FenleiBaohuResult fenleiBaohuResult);


    //最后一个人出案
    public QueryResponseResult lastFinish( String id,String user,QueryResponseResult queryResponseResult );

    // 判断是否最后一个人出案
    QueryResponseResult judgeIfLastFinish(String id);

    QueryResponseResult cpcToIpc(String cci, String cca);
}
