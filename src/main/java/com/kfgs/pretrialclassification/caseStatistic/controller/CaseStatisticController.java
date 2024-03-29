package com.kfgs.pretrialclassification.caseStatistic.controller;


import com.kfgs.pretrialclassification.caseStatistic.service.CaseStatisticService;
import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

    @Autowired
    UserService userService;

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
    public Map countCaseOut(String page,String limit,String beginTime,String endTime, String type, String dept1,String dept2){
        //获取当前登录用户信息
        Map userMap = userService.findUserInfo();
        String userName = "";
        if(userMap != null && userMap.size() > 0){
            if(userMap.get("introduction") != null && StringUtils.equals("管理员",userMap.get("introduction").toString())){
            }else{
                userName = userMap.get("name").toString();
            }
        }
        Map resultMap = new HashMap();
        if(type == null){
            type = "";
        }
        if(dept1 == null){
            dept1 = "";
        }
        if(dept2 == null){
            dept2 = "";
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
        Map<String, Object> dataTable = getDataTable(caseStatisticService.countCaseOut(page,limit,beginTime,endTime,type,dept1,dept2,userName));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("出案量统计按照地方的保护中心")
    @GetMapping("/caseOutWithOrg")
    public Map caseOutWithOrg(String page,String limit,String beginTime,String endTime){
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

    @ApiOperation("工作量统计")
    @GetMapping("/accountWork")
    public Map accountWork(String page,String limit,String beginTime,String endTime, String type, String dept1,String dept2){
        //获取当前登录用户信息
        Map userMap = userService.findUserInfo();
        String userName = "";
        if(userMap != null && userMap.size() > 0){
            if(userMap.get("introduction") != null && StringUtils.equals("管理员",userMap.get("introduction").toString())){
            }else{
                userName = userMap.get("name").toString();
            }
        }
        Map resultMap = new HashMap();
        if(type == null){
            type = "";
        }
        if(dept1 == null){
            dept1 = "";
        }
        if(dept2 == null){
            dept2 = "";
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
        Map<String, Object> dataTable = getDataTable(caseStatisticService.accountWork(page,limit,beginTime,endTime,type,dept1,dept2,userName));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }
}
