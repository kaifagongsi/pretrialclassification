package com.kfgs.pretrialclassification.caseClassification.controller;

import com.kfgs.pretrialclassification.caseClassification.service.CaseStateQueryService;
import com.kfgs.pretrialclassification.common.exception.PretrialClassificationException;
import com.kfgs.pretrialclassification.common.controller.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mango
 */
@Api("分类main表的测试")
@RestController
@RequestMapping("/caseClassification/caseStateQuery")
public class CaseStateQueryController extends BaseController{

    @Autowired
    CaseStateQueryService caseStateQueryService;

    @ApiOperation("main表，查询所有案件")
    @GetMapping("/findAllCase")
    public Map findAllCase(String page,String limit ) throws PretrialClassificationException {
        Map resultMap = new HashMap();
        Map<String, Object> dataTable = getDataTable(caseStateQueryService.findAll(page,limit));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

}
