package com.kfgs.pretrialclassification.caseStatistic.controller;


import com.kfgs.pretrialclassification.caseDisposition.service.CaseAllocationService;
import com.kfgs.pretrialclassification.caseStatistic.service.CaseStatisticService;
import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.common.exception.PretrialClassificationException;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mango
 */
@Api("分类保护表的测试")
@RestController
@RequestMapping("/caseStatistic")
public class  CaseStatisticController extends BaseController {

    @Autowired
    CaseStatisticService caseStatisticService;

    @ApiOperation("查询用户列表")
    @GetMapping("/findUserInfo")
    public Map findUserInfo(){
        Map resultMap = new HashMap();
        List areaName = caseStatisticService.findAreaName();
        //List  areaName = fenleiBaohuUserinfoService.findAreaName();
        resultMap.put("treelist",areaName);
        resultMap.put("code",20000);
        return resultMap;
    }

    @ApiOperation("根据传入的实体的pdf字段更新实体的worker")
    @PostMapping("/updateWorker")
    public Map updateWorker(@RequestBody  FenleiBaohuMain fenleiBaohuMain, HttpServletRequest request){
        Map resultMap = new HashMap();
        boolean b = caseStatisticService.updateWorker(fenleiBaohuMain,request);
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


    @ApiOperation("进案量统计")
    @GetMapping("/countCaseIn")
    public Map countCaseIn(String page,String limit,String beginTime,String endTime){
        Map resultMap = new HashMap();
        if(beginTime == null){
            beginTime = "";
        }
        if (endTime == null){
            endTime = "";
        }
        if(beginTime == "" && endTime != ""){
            beginTime = "19000000000000";
            endTime = endTime.replace("-","")+"235959";
        }else if(beginTime != "" && endTime == ""){
            beginTime = beginTime.replace("-","")+"000000";
            endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
            endTime = endTime.replace("-","")+"235959";
        }else if(beginTime != "" && endTime != ""){
            beginTime = beginTime.replace("-","")+"000000";
            endTime = endTime.replace("-","")+"235959";
        }else{
            beginTime="";
            endTime="";
        }
        Map<String, Object> dataTable = getDataTable(caseStatisticService.countCaseIn(page,limit,beginTime,endTime));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("出案量统计")
    @GetMapping("/countCaseOut")
    public Map countCaseOut(String page,String limit,String beginTime,String endTime, String type){
        Map resultMap = new HashMap();
        if(type == null){
            type = "";
        }
        if(beginTime == null){
            beginTime = "";
        }
        if (endTime == null){
            endTime = "";
        }
        if(beginTime == "" && endTime != ""){
            beginTime = "19000000000000";
            endTime = endTime.replace("-","")+"235959";
        }else if(beginTime != "" && endTime == ""){
            beginTime = beginTime.replace("-","")+"000000";
            endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
            endTime = endTime.replace("-","")+"235959";
        }else if(beginTime != "" && endTime != ""){
            beginTime = beginTime.replace("-","")+"000000";
            endTime = endTime.replace("-","")+"235959";
        }else{
            beginTime="";
            endTime="";
        }
        Map<String, Object> dataTable = getDataTable(caseStatisticService.countCaseOut(page,limit,beginTime,endTime,type));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("根据id查找这个案子是谁在做")
    @GetMapping("/findWorkerById")
    public String findWorkerById(String id){
        return caseStatisticService.findWorkerById(id);
        //return fenleiBaohuResultService.findWorkerById(id);
    }

    @ApiOperation("分类保护表，查询所有  get请求测试")
    @GetMapping("/findAll")
    public Map findAll(String page,String limit ) throws PretrialClassificationException {
        Map resultMap = new HashMap();
        Map<String, Object> dataTable = getDataTable(caseStatisticService.findAll(page,limit));
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
        boolean flag = caseStatisticService.sendEmail(ids);
        return resultMap;
    }


    @ApiOperation("put请求测试")
    @PutMapping("/update")
    public Map update(FenleiBaohuMain fenleiBaohuMain){
        Map resultMap = new HashMap();
        boolean update = caseStatisticService.update(fenleiBaohuMain);
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
        boolean put = caseStatisticService.postFenleiBaohuMain(fenleiBaohuMain);
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
        boolean delete = caseStatisticService.deleteFenleiBaohuMain(fenleiBaohuMain);
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
