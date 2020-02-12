package com.kfgs.pretrialclassification.test.service.impl;

import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public void textRedis(){
        redisTemplate.boundHashOps("testInfo").put("name","张三");
        redisTemplate.boundHashOps("testInfo").put("age",13);
        redisTemplate.boundHashOps("testInfo").put("sex","男");
        Set set = redisTemplate.boundHashOps("testInfo").keys();
        System.out.println(set);
        List testInfo = redisTemplate.boundHashOps("testInfo").values();
        System.out.println(testInfo);
    }


}
