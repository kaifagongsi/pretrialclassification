package com.kfgs.pretrialclassification.caseQuery.controller;

import com.kfgs.pretrialclassification.caseQuery.service.CaseConditionQueryService;
import com.kfgs.pretrialclassification.common.exception.PretrialClassificationException;
import com.kfgs.pretrialclassification.common.controller.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mango
 */
@Api("分类main表的检索测试")
@RestController
@RequestMapping("/caseQuery/caseConditionQuery")
public class caseConditionQueryController extends BaseController{

    @Autowired
    CaseConditionQueryService caseConditionQueryService;

    @ApiOperation("main表，查询所有案件")
    @GetMapping("/findAllCase")
    public Map findAllCase(String page,String limit,String id,String name,String sqr,String sqh,String state,String beginTime,String endTime) throws PretrialClassificationException {
        //获取查询条件
        Map resultMap = new HashMap();
        if(state == null || state == "all" || state.equals("all") || state==""){
            state = "";
        }
        if(endTime == null || endTime == ""){
            endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        }
        if(beginTime != null){
            beginTime = beginTime.replace("-","")+"000000";
            endTime = endTime.replace("-","")+"235959";
        }else{
            beginTime="";
            endTime="";
        }
        Map<String, Object> dataTable = getDataTable(caseConditionQueryService.findAll(page,limit,id,name,sqr,sqh,state,beginTime,endTime));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，按案件状态查询案件")
    @GetMapping("/findCaseByState")
    public Map findCaseByState(String page,String limit,String state) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findCaseByState(page,limit,state));
        return resultMap;
    }

    @ApiOperation("main表，根据预审编号查询案件分类信息")
    @GetMapping("/findClassInfoByID")
    public Map findClassInfoByID(String id) throws PretrialClassificationException{

        Map resultMap = new HashMap();
        List dataTable = caseConditionQueryService.findClassInfoByID(id);
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，根据申请号查询案件")
    @GetMapping("/findBySQH")
    public Map findBySQH(String page,String limit,String sqh) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findBySQH(page,limit,sqh));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，根据案件名称查询案件")
    @GetMapping("/findByName")
    public Map findByName(String page,String limit,String name) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findByName(page,limit,name));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，根据申请人查询案件")
    @GetMapping("/findBySQR")
    public Map findBySQR(String page,String limit,String sqr) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findBySQR(page,limit,sqr));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }
}