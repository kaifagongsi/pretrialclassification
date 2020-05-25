package com.kfgs.pretrialclassification.caseStatistic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CaseStatisticService {
    // 查询人员 类别 （专家、导师、AB角）
    List findAreaName();

    //进案量统计
    IPage countCaseIn(String pageNo, String limit,String beginTime,String endTime);

    //出案量统计
    IPage countCaseOut(String pageNo, String limit,String beginTime, String endTime);

    // 据id查找这个案子是谁在做
    String findWorkerById(String id);

    //分类保护表，查询所有
    IPage findAll(String page, String limit);

    //进案量查询
    IPage selectByExample(String pageNo, String state);

    // 根据传入实体中的pdf 修改worker
    boolean updateWorker(FenleiBaohuMain fenleiBaohuMain, HttpServletRequest request);

    // put 请求测试
    boolean update(FenleiBaohuMain fenleiBaohuMain);
    // post 请求 测试
    boolean postFenleiBaohuMain(FenleiBaohuMain fenleiBaohuMain);
    //delete 请求测试
    boolean deleteFenleiBaohuMain(FenleiBaohuMain fenleiBaohuMain);
    //发送邮件
    boolean sendEmail(String[] ids);
}
