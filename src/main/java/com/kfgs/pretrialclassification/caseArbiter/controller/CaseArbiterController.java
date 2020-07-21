package com.kfgs.pretrialclassification.caseArbiter.controller;

import com.kfgs.pretrialclassification.caseArbiter.service.CaseArbiterService;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

/**
 * Date: 2020-07-08-15-25
 * Module:
 * Description:
 *
 * @author:
 */
@Api("裁决模块")
@RestController
@RequestMapping("/caseArbiter")
public class CaseArbiterController {

    @Autowired
    CaseArbiterService caseArbiterService;

    @ApiOperation("获取裁决初始化列表")
    @GetMapping("/getArbiterInitList/{pageNum}/{pageSize}")
    public QueryResponseResult getArbiterInitList(@PathVariable("pageNum") int pageNum,@PathVariable("pageSize") int pageSize){
        return  caseArbiterService.getArbiterInitList(pageNum,pageSize);
    }

    @ApiOperation("根据传入的实体的id更新分类号")
    @PostMapping("/saveAribiterClassfication")
    public QueryResponseResult saveAribiterClassfication(@RequestBody FenleiBaohuAdjudicationExt fenleiBaohuAdjudicationExt){
        return caseArbiterService.saveAribiterClassfication(fenleiBaohuAdjudicationExt);
    }

    @ApiOperation("传入分类号进行校验")
    @PostMapping("/checkIPC/{code}")
    public QueryResponseResult checkIPC_CCI_CCA(@RequestBody FenleiBaohuAdjudicationExt ext, @PathVariable("code") String code ){
        return caseArbiterService.checkIPC_CCI_CCA(ext,code);
    }

    @ApiOperation("传入分类号进行校验")
    @PostMapping("/checkCsets")
    public QueryResponseResult checkIPC_CCI_CCA( @RequestBody FenleiBaohuAdjudicationExt ext ){
        return caseArbiterService.checkClassCodeCsets(ext);
    }
}
