package com.kfgs.pretrialclassification.caseHandle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;


public interface CaseHandleService {

    //案件清单导入
    void saveInfo(FenleiBaohuMain fenleiBaohuMain);

    //根据 fenlei_baohu_main 表 查询 state = 0的数据
    IPage findByState(String pageNo, String limit);

    //删除数据
    QueryResponseResult deleteDataByID(String id);

    //更新粗分号
    QueryResponseResult updateSimpleclasscode(String id, String simpleclasscode);

    //获取未完成案件清单
    IPage selectByCondition(String pageNo, String limit);

}
