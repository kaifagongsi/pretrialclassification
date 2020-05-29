package com.kfgs.pretrialclassification.caseClassification.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaseClassificationServiceImpl implements CaseClassificationService {

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Override
    @Transactional
    //按状态查询分类员下案件
    public IPage findCaseByState(String pageNo, String limit, String state, String classtype, String user) {
        Map resultMap = new HashMap();
        Page<FenleiBaohuMainResultExt> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMainResultExt> iPage = fenleiBaohuResultMapper.selectCaseByState(page,state,classtype,user);
        return iPage;
    }
}
