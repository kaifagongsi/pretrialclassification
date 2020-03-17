package com.kfgs.pretrialclassification.caseClassification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CaseStateQueryService {

    //查询所有案件
    IPage findAll(String pageNo, String limit);

}
