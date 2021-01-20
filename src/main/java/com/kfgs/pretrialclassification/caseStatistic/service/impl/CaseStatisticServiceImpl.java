package com.kfgs.pretrialclassification.caseStatistic.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseDisposition.service.MailService;
import com.kfgs.pretrialclassification.caseStatistic.service.CaseStatisticService;
import com.kfgs.pretrialclassification.common.utils.DateUtil;
import com.kfgs.pretrialclassification.common.utils.IPUtil;
import com.kfgs.pretrialclassification.common.utils.UserUtil;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public IPage countCaseOut(String pageNo, String limit,String begintime,String endtime,String type, String dept,String userName) {
        Page<FenleiBaohuResult> page = new Page<FenleiBaohuResult>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuResultMapper.selectCaseOut(page,begintime,endtime,type,dept,userName);
    }


    @Override
    public IPage countCaseOutWithOrg(String pageNo, String limit, String beginTime, String endTime) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.countCaseOutWithOrg(page,beginTime,endTime);
    }
}
