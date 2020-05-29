package com.kfgs.pretrialclassification.caseClassification.controller;

import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api("分类员查询的测试")
@RestController
@RequestMapping("/caseClassification")
public class caseClassificationController extends BaseController {

    @Autowired
    CaseClassificationService caseClassificationService;

    @ApiOperation("result表，按状态查询案件")
    @GetMapping("/findCaseByState")
    public Map findCaseByState(String page,String limit,String state,String user){
        Map resultMap = new HashMap();
        //user = "772510-姜彪";
        if(state == null){
            state="0";
        }
        String classtype = "主";
        //新分待审
        if(state == "0" || state.equals("0")){
            classtype="主";
        }
        //已分待出
        if(state == "1" || state.equals("1")){
            classtype="主";
        }
        //转案待审,作为副分，状态不为2
        if(state == "2" || state.equals("2")){
            state = "";
            classtype = "副";
        }
        //已出案,状态为2
        if (state == "3" || state.equals("3")){
            state = "2";
            classtype="";
        }
        Map<String, Object> dataTable = getDataTable(caseClassificationService.findCaseByState(page,limit,state,classtype,user));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

}
