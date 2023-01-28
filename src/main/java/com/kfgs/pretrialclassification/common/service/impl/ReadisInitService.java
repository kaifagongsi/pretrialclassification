package com.kfgs.pretrialclassification.common.service.impl;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Order(1)
@Component
@Slf4j
public class ReadisInitService  implements CommandLineRunner {

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @NacosValue("${spring.application.name}")
    private String serverName;

    @Autowired
    private FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;

    @Override
    public void run(String... args) throws Exception {
        //0.初始化一级部门
        intiDep1();
        //1.处室化二级部门
        initDep2();
        //2.初始化裁决组长
        initArbiter();

    }

    private void intiDep1() {
        //1.处室话部门
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(serverName);
        stringObjectObjectBoundHashOperations.put("dep1s",fenleiBaohuUserinfoMapper.selectDistinctDep1());
        log.info("向redis中写入一级部门数据...");
    }

    public void initArbiter() {
        List<String> distinctAdjudicator = fenleiBaohuUserinfoMapper.selectDistinctAdjudicator();
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(serverName);
        stringObjectObjectBoundHashOperations.put("distinctAdjudicator",distinctAdjudicator);
        log.info("向redis中写入已设置的裁决组长");
    }

    public void initDep2(){
        //1.处室话部门
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(serverName);
        List<String> dep1s = (List<String>)stringObjectObjectBoundHashOperations.get("dep1s");
        HashMap map = new HashMap(10);
        for(String dep1: dep1s){
            map.put(dep1,fenleiBaohuUserinfoMapper.selectDistinctDep2ByDep1(dep1));
        }
        log.info("向redis中写入二级部门数据...");
        stringObjectObjectBoundHashOperations.put("dep2s",map);
    }
}
