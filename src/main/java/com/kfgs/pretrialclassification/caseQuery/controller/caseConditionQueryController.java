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
import java.util.HashMap;
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
    public Map findAllCase(String page,String limit,String id,String name,String sqr,String sqh) throws PretrialClassificationException {
        Map resultMap = new HashMap();
        Map<String, Object> dataTable = getDataTable(caseConditionQueryService.findAll(page,limit,id,name,sqr,sqh));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，根据预审编号查询案件")
    @GetMapping("/findById")
    public Map findById(String page,String limit,String id) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findById(page,limit,id));
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
