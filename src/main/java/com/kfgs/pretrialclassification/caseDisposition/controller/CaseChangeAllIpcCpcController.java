package com.kfgs.pretrialclassification.caseDisposition.controller;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.kfgs.pretrialclassification.caseDisposition.service.CaseChangeAllIpcCpcService;
import com.kfgs.pretrialclassification.common.utils.SecurityUtil;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.response.CaseChangeIpcEnum;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api("分类保护表的测试")
@RestController
@RequestMapping("/caseDisposition/caseChange")
public class CaseChangeAllIpcCpcController {

    @Autowired
    CaseChangeAllIpcCpcService caseChangeAllIpcCpcService;

    @NacosValue(value = "${editIpc:000000}",autoRefreshed = true)
    private String editIpc;

    @PostMapping("/changeOne")
    public QueryResponseResult changeOne(@RequestBody FenleiBaohuResult result){
        return   caseChangeAllIpcCpcService.changOneRow(result);
    }

    @DeleteMapping("/deleteOne")
    public QueryResponseResult deleteOne(@RequestBody FenleiBaohuResult result){
        return   caseChangeAllIpcCpcService.deleteOneRow(result);
    }

    @PostMapping("/caseFinishAll")
    public QueryResponseResult caseFinishAll(@RequestBody List<FenleiBaohuResult> list){
//        return   caseChangeAllIpcCpcService.deleteOneRow(result);
        List<String> ipcmi = new ArrayList<>();
        list.forEach(item ->{
            if(item.getIPCMI() != null){
                ipcmi.add(item.getIPCMI());
            }
        });
        if(ipcmi.size() == 1){
            String workername = SecurityUtil.getLoginUser().getWorkername().split("-")[0];
            List<String> edisIpcs = Arrays.asList(editIpc.split(","));

            if(edisIpcs.contains(workername)){
                //正常合并
                return  caseChangeAllIpcCpcService.caseFinishAll(list);
            }else{
                //当前账号没有权限
                return new QueryResponseResult(CaseChangeIpcEnum.NOT_RULE,null);
            }
        }else{
            //多个主分号，有问题，不合并
            return new QueryResponseResult(CaseChangeIpcEnum.MORE_OR_NULL_IPCMI,null);
        }
    }
}
