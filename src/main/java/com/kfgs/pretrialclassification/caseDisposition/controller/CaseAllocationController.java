package com.kfgs.pretrialclassification.caseDisposition.controller;


import com.kfgs.pretrialclassification.caseDisposition.service.CaseAllocationService;
import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.common.exception.PretrialClassificationException;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mango
 */
@Api("分类保护表的测试")
@RestController
@RequestMapping("/caseDisposition/caseAllocation")
public class CaseAllocationController extends BaseController {

    @Autowired
    CaseAllocationService caseAllocationService;

    @ApiOperation("查询用户列表")
    @GetMapping("/findUserInfo")
    public Map findUserInfo(){
        Map resultMap = new HashMap();
        List areaName = caseAllocationService.findAreaName();
        //List  areaName = fenleiBaohuUserinfoService.findAreaName();
        resultMap.put("treelist",areaName);
        resultMap.put("code",20000);
        return resultMap;
    }

    @ApiOperation("根据传入的实体的pdf字段更新实体的worker")
    @PostMapping("/updateWorker")
    public Map updateWorker(@RequestBody  FenleiBaohuMain fenleiBaohuMain, HttpServletRequest request){
        Map resultMap = new HashMap();
        boolean b = caseAllocationService.updateWorker(fenleiBaohuMain,request);
        //boolean b = fenleiBaohuResultService.updateWorker(fenleiBaohuMain,request);
        if(b){
            resultMap.put("code",20000);
            resultMap.put("message","更新成功");
        }else{
            resultMap.put("code",20000);
            resultMap.put("message","更新失败");
        }
        return  resultMap;
    }

    @ApiOperation("查询main表状态为1的")
    @GetMapping("/findMainByState")
    public Map findMainByState(String page,String limit){
        Map resultMap = new HashMap();
        Map<String, Object> dataTable = getDataTable(caseAllocationService.findMainByState(page,limit));
        //Map<String, Object> dataTable = getDataTable(fenleiBaohuMainService.findMainByState(page,limit));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("根据id查找这个案子是谁在做")
    @GetMapping("/findWorkerById")
    public String findWorkerById(String id){
        return caseAllocationService.findWorkerById(id);
        //return fenleiBaohuResultService.findWorkerById(id);
    }

    @ApiOperation("分类保护表，查询所有  get请求测试")
    @GetMapping("/findAll")
    public Map findAll(String page,String limit ) throws PretrialClassificationException {
        Map resultMap = new HashMap();
        Map<String, Object> dataTable = getDataTable(caseAllocationService.findAll(page,limit));
        //Map<String, Object> dataTable = getDataTable(fenleiBaohuMainService.findAll(page,limit));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }
    @ApiOperation("根据传入的ID进行发送邮件")
    @PostMapping("/sendEmail")
    public Map sendEmail(@RequestBody String[] ids){
        System.out.println(ids);
        Map resultMap = new HashMap();
        boolean flag = caseAllocationService.sendEmail(ids);
        return resultMap;
    }


    @ApiOperation("put请求测试")
    @PutMapping("/update")
    public Map update(FenleiBaohuMain fenleiBaohuMain){
        Map resultMap = new HashMap();
        boolean update = caseAllocationService.update(fenleiBaohuMain);
        //boolean update = fenleiBaohuMainService.update(fenleiBaohuMain);
        if(update){
            resultMap.put("code",20000);
        }else{
            resultMap.put("code",50000);
        }
        resultMap.put("data",null);
        return resultMap;
    }
    @ApiOperation("post请求测试")
    @PostMapping("/postFenleiBaohuMain")
    public Map putFenleiBaohuMain(  FenleiBaohuMain fenleiBaohuMain){
        Map resultMap = new HashMap();
        boolean put = caseAllocationService.postFenleiBaohuMain(fenleiBaohuMain);
        //boolean put = fenleiBaohuMainService.putFenleiBaohuMain(fenleiBaohuMain);
        if(put){
            resultMap.put("code",20000);
        }else{
            resultMap.put("code",50000);
        }
        resultMap.put("data",null);
        return resultMap;
    }

    @DeleteMapping("/deleteFenleiBaohuMain")
    public Map deleteFenleiBaohuMain(  FenleiBaohuMain fenleiBaohuMain){
        Map resultMap = new HashMap();
        boolean delete = caseAllocationService.deleteFenleiBaohuMain(fenleiBaohuMain);
        //boolean delete = fenleiBaohuMainService.deleteFenleiBaohuMain(fenleiBaohuMain);
        if(delete){
            resultMap.put("code",20000);
        }else{
            resultMap.put("code",50000);
        }
        resultMap.put("data",null);
        return resultMap;
    }



}
