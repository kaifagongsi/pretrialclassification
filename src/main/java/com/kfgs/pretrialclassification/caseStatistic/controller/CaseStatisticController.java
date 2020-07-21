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
    public Map countCaseOut(String page,String limit,String beginTime,String endTime, String type, String dept){
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
        if(dept == null){
            dept = "";
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
        Map<String, Object> dataTable = getDataTable(caseStatisticService.countCaseOut(page,limit,beginTime,endTime,type,dept,userName));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

}
