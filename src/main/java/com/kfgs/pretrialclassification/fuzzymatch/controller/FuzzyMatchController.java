package com.kfgs.pretrialclassification.fuzzymatch.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.kfgs.pretrialclassification.common.utils.MultipartFileToFile;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FuzzyMatchReadExcel;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.FuzzyMatchEnum;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.fuzzymatch.service.FuzzyMatchService;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fuzzymactch")
public class FuzzyMatchController {

    @Autowired
    FuzzyMatchService fuzzyMatchService;

    @Autowired
    UserService userService;

    @NacosValue(value = "${pretrialclassification.fileSave.excel:C:/20210111_bhzx/bhzx/excel/}",autoRefreshed = true)
    private String fileSve;


    @GetMapping("/matchAll")
    public QueryResponseResult matchAll(){
        FenleiBaohuUserinfoExt user = userService.findUserWorkerName();
        if(user.getDep1().equals("系统建设与运维部")){
            if(fuzzyMatchService.getState() == 0){
                fuzzyMatchService.matchAll();
                try {
                    //等待异步线程执行
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(FuzzyMatchEnum.FUZZY_MATCH_ERROR,null);
            }
        }else{
            return new QueryResponseResult(FuzzyMatchEnum.NO_PERMISSION_OPERATION,null);
        }


    }

    @GetMapping("/getMatchState")
    public String getMatchState(){
        return fuzzyMatchService.getProgressState();
    }

    @PostMapping("/uploadFile")
    public QueryResponseResult uploadFile(MultipartFile file){
        try {
            List<FuzzyMatchReadExcel> list = new ArrayList<>();
            MultipartFileToFile.multipartFileToFile(fileSve,file);
            EasyExcel.read(fileSve + file.getOriginalFilename(), FuzzyMatchReadExcel.class, new ReadListener<FuzzyMatchReadExcel>() {
                @Override
                public void invoke(FuzzyMatchReadExcel excel, AnalysisContext analysisContext) {
                    list.add(excel);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                }
            }).sheet().doRead();
            fuzzyMatchService.matchExcel(list,file.getOriginalFilename());
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }catch (Exception e){
            e.printStackTrace();
            return new QueryResponseResult(CommonCode.FAIL,null);
        }

    }



}
