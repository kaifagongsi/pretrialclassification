package com.kfgs.pretrialclassification.caseArbiter.controller;

import com.kfgs.pretrialclassification.caseArbiter.service.CaseArbiterService;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import com.kfgs.pretrialclassification.domain.request.ArbiterParam;
import com.kfgs.pretrialclassification.domain.response.CaseFinishResponseEnum;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;

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

    @ApiOperation("获取裁决组长初始化列表")
    @GetMapping("/getArbiterInitList/{pageNum}/{pageSize}")
    public QueryResponseResult getArbiterInitList(@PathVariable("pageNum") int pageNum,@PathVariable("pageSize") int pageSize){
        return  caseArbiterService.getArbiterInitList(pageNum,pageSize);
    }
    @ApiOperation("获取裁决员初始化列表")
    @GetMapping("/getArbiterPersonInitList/{pageNum}/{pageSize}")
    public QueryResponseResult getArbiterPersonInitList(@PathVariable("pageNum") int pageNum,@PathVariable("pageSize") int pageSize){
        return  caseArbiterService.getArbiterPersonInitList(pageNum,pageSize);
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

    @ApiOperation("根据预审编号查询案件分类信息,并返回裁决员给出的分类号")
    @GetMapping("/findClassInfoByID/{sid}")
    public QueryResponseResult findClassInfoByID(@PathVariable("sid") String id ){
        return caseArbiterService.findClassInfoByID(id);
    }

    @ApiOperation("添加裁决人员时的动态列表")
    @PostMapping("/findAribiterPersonList")
    public QueryResponseResult findAribiterPersonList(@RequestBody ArbiterParam arbiterParam){
        return caseArbiterService.findAribiterPersonList(arbiterParam);
    }

    @ApiOperation("更新案件的裁决员")
    @PostMapping("/updateAribiterPerson/{id}")
    public QueryResponseResult updateAribiterPerson(@RequestBody ArrayList<ArbiterParam> list,@PathVariable("id")String id){
        return caseArbiterService.updateAribiterPerson(list,id);
    }

    @ApiOperation("查询某个裁决案件的裁决员信息")
    @GetMapping("/findAdjudicatorWorker/{id}")
    public QueryResponseResult findAdjudicatorWorker(@PathVariable("id") String id){
        return caseArbiterService.findAdjudicatorWorker(id);
    }

    @ApiOperation("裁决组长出案操作")
    @GetMapping("/arbiterChuAn/{id}")
    public QueryResponseResult arbiterChuAn(@PathVariable("id") String id){
        return caseArbiterService.arbiterChuAn(id);
    }

    @ApiOperation("案件触发裁决，向裁决表中添加数据")
    @PostMapping("/insertIntoAdjudication/{id}")
    public QueryResponseResult insertIntoAdjudication(@PathVariable("id") String id, CaseFinishResponseEnum caseFinishResponseEnum){
        return caseArbiterService.insertIntoAdjudication(id,caseFinishResponseEnum);
    }
}
