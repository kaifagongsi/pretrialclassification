package com.kfgs.pretrialclassification.caseStatistic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CaseStatisticService {

    //进案量统计
    IPage countCaseIn(String pageNo, String limit,String beginTime,String endTime);

    //出案量统计
    IPage countCaseOut(String pageNo, String limit, String beginTime, String endTime, String type, String dept1, String dept2, String userName);

    // 出案量统计，按照各个保护中心
    IPage countCaseOutWithOrg(String pageNo, String limit,String beginTime,String endTime);
}
