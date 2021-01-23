package com.kfgs.pretrialclassification.caseQuery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseQuery.service.CaseConditionQueryService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUpdateipcMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUpdateIpc;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CaseConditionQueryServiceImpl implements CaseConditionQueryService {

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuUpdateipcMapper fenleiBaohuUpdateipcMapper;

    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;

    @Override
    @Transactional
    //查询所有案件
    public IPage findAll(String pageNo, String limit,String id,String name,String sqr,String sqh,String worker,String state,String begintime,String endtime) {
        Map resultMap = new HashMap();
        Page<FenleiBaohuMainResultExt> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        //IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, null);
        IPage<FenleiBaohuMainResultExt> iPage = fenleiBaohuMainMapper.selectByCondition(page,id,name,sqr,sqh,worker,state,begintime,endtime);
        return iPage;
    }

    @Override
    @Transactional
    //根据查询条件查询案件
    public IPage findByCondition(String pageNo,String limit,Map conList){
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        if(conList != null) {
            if (conList.get("id").toString() != "") {
                String id = conList.get("id").toString();
                queryWrapper.like("id", id);
            }
            if ( conList.get("name").toString() != "") {
                String name = conList.get("name").toString();
                queryWrapper.like("name", name);
            }
        }
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;

    }
    @Override
    @Transactional
    //根据案件状态查询案件
    public IPage findCaseByState(String pageNo,String limit,String state){
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        /*state:
        0:进案未分配
        1：进案已分配未出案
        2：已出案
         */
        queryWrapper.eq("state",state);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page,queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据预审编号查询案件
    public List<FenleiBaohuResult> findClassInfoByID(String id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        //模糊查询
        queryWrapper.eq("id",id);
        Map resultMap = new HashMap();
        //List<FenleiBaohuResult> list = fenleiBaohuResultMapper.selectList(queryWrapper);
        List<FenleiBaohuResult> list = fenleiBaohuResultMapper.selectListByID(id);
        return list;
    }

    @Override
    public List findUpdateInfoByID(String id, String worker) {
        QueryWrapper queryWrapper = new QueryWrapper();
        //精确查询
        queryWrapper.eq("id",id);
        String name = fenleiBaohuUserinfoMapper.selectUpdateWorkerName(worker);
        queryWrapper.eq("worker",name);
        queryWrapper.eq("state",0);
        List<FenleiBaohuUpdateIpc> list = fenleiBaohuUpdateipcMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    @Transactional
    //根据申请号查询案件
    public IPage findBySQH(String pageNo, String limit,String sqh) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("sqh",sqh);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据案件名称查询案件
    public IPage findByName(String pageNo, String limit,String name) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("mingcheng",name);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据申请人查询案件
    public IPage findBySQR(String pageNo, String limit,String sqr) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("sqr",sqr);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }
}
