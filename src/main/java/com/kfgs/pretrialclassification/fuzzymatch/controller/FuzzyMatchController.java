package com.kfgs.pretrialclassification.fuzzymatch.controller;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.kfgs.pretrialclassification.fuzzymatch.service.FuzzyMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fuzztmactch")
public class FuzzyMatchController {

    @Autowired
    FuzzyMatchService fuzzyMatchService;

    @GetMapping("/matchAll")
    public void matchAll(){
        fuzzyMatchService.matchAll();
        System.out.println("1231456");
    }
}
