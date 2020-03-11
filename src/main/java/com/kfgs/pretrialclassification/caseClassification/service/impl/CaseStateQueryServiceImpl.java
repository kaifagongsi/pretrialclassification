package com.kfgs.pretrialclassification.caseClassification.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseClassification.service.CaseStateQueryService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaseStateQueryServiceImpl implements CaseStateQueryService {

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Override
    @Transactional
    public IPage findAll(String pageNo, String limit) {
        Map resultMap = new HashMap();
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, null);
        return iPage;
    }
}
