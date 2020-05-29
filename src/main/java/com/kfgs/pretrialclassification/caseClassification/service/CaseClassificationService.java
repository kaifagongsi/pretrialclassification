package com.kfgs.pretrialclassification.caseClassification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CaseClassificationService {

    //按状态查询登录用户的案件
    IPage findCaseByState(String page, String limit, String state, String classtype, String user);
}
