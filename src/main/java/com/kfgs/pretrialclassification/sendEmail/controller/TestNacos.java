package com.kfgs.pretrialclassification.sendEmail.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/text/nacos")
public class TestNacos {
    @NacosValue(value = "${pretrialclassification.email.toSanBu:30}",autoRefreshed = true)
    private String toSanBu;

    @GetMapping("/sanbu")
    public String getToSanBu(){
        return toSanBu;
    }
}
