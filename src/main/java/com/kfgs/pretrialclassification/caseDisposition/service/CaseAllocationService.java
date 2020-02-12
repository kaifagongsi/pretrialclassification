package com.kfgs.pretrialclassification.caseDisposition.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CaseAllocationService {
    // 查询人员 类别 （专家、导师、AB角）
    List findAreaName();

    //根据 fenlei_baohu_main 表 查询 state = 1的数据
    IPage findMainByState(String pageNo, String limit);

    // 据id查找这个案子是谁在做
    String findWorkerById(String id);

    //分类保护表，查询所有
    IPage findAll(String page, String limit);


    // 根据传入实体中的pdf 修改worker
    boolean updateWorker(FenleiBaohuMain fenleiBaohuMain, HttpServletRequest request);

    // put 请求测试
    boolean update(FenleiBaohuMain fenleiBaohuMain);
    // post 请求 测试
    boolean postFenleiBaohuMain(FenleiBaohuMain fenleiBaohuMain);
    //delete 请求测试
    boolean deleteFenleiBaohuMain(FenleiBaohuMain fenleiBaohuMain);
}
