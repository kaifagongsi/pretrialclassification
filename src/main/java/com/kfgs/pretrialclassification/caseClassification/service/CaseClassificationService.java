package com.kfgs.pretrialclassification.caseClassification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;

import java.util.List;
import java.util.Map;

public interface CaseClassificationService {

    //按状态查询登录用户的案件
    IPage findCaseByState(String page, String limit, String state, String classtype, String user,String begintime,String endtime);

    //获取案件详情
    Map getCaseInfo(String id, String worker);

    //个人单一出案
    int updateResult(String id,String worker,String state);

    //案件结束出案
    int updateMain(String id);

    //分类员条件查询案件
    FenleiBaohuMain searchByCondition(String id, String sqh, String mingcheng);

    //获取案件主副分详情
    List<FenleiBaohuResult> getSingleResult(String id, String sqh, String mingcheng);

    //查询转案人员列表
    List<String> getTransWorkerList(String id);

    //转案
    boolean caseTrans(List<FenleiBaohuResult> list);
}
