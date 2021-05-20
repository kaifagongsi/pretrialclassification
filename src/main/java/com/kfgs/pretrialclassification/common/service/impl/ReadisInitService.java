package com.kfgs.pretrialclassification.common.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Order(1)
@Component
public class ReadisInitService  implements CommandLineRunner {

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String serverName;

    @Override
    public void run(String... args) throws Exception {
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(serverName);
        HashMap map = new HashMap(10);
        map.put("一部",new String[]{"一室","二室","三室"});
        map.put("二部",new String[]{"一室","二室","三室"});
        map.put("三部",new String[]{"一室","二室","三室","四室","五室"});
        map.put("四部",new String[]{"一室","二室","三室","四室"});
        map.put("专利业务研究与发展部",new String[]{"计划管理室","质量保障室","规划与发展研究室"});
        map.put("系统建设与运维部",new String[]{"项目计划与研究室","数据及网络管理室","项目建设及运营室","运行维护室"});
        System.out.println("向redis中写入部门数据...");
        stringObjectObjectBoundHashOperations.put("dep2s",map);
    }
}
