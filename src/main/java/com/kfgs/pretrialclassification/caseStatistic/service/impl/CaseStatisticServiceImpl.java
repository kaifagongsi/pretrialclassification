package com.kfgs.pretrialclassification.caseStatistic.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseStatistic.service.CaseStatisticService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseStatisticServiceImpl implements CaseStatisticService {

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;



    //进案量统计
    @Override
    public IPage countCaseIn(String pageNo, String limit,String begintime,String endtime) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.selectCaseIn(page,begintime,endtime);
    }

    //出案量统计
    @Override
    public IPage countCaseOut(String pageNo, String limit, String begintime, String endtime, String type, String dept1, String dept2, String userName) {
        Page<FenleiBaohuResult> page = new Page<FenleiBaohuResult>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuResultMapper.selectCaseOut(page,begintime,endtime,type, dept1, dept2,userName);
    }


    @Override
    public IPage countCaseOutWithOrg(String pageNo, String limit, String beginTime, String endTime) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.countCaseOutWithOrg(page,beginTime,endTime);
    }
}
