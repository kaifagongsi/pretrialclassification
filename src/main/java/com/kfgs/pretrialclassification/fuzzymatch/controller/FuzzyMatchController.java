package com.kfgs.pretrialclassification.fuzzymatch.controller;

import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.FuzzyMatchEnum;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.fuzzymatch.service.FuzzyMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fuzzymactch")
public class FuzzyMatchController {

    @Autowired
    FuzzyMatchService fuzzyMatchService;

    @GetMapping("/matchAll")
    public QueryResponseResult matchAll(){
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
    }

    @GetMapping("/getMatchState")
    public String getMatchState(){
        return fuzzyMatchService.getProgressState();
    }




}
