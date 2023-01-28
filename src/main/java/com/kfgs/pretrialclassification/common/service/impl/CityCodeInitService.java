package com.kfgs.pretrialclassification.common.service.impl;

import com.kfgs.pretrialclassification.dao.FenleiBaohuCitycodeMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuCitycode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Order(2)
@Component
@Slf4j
public class CityCodeInitService  implements CommandLineRunner {

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String serverName;

    @Autowired
    FenleiBaohuCitycodeMapper fenleiBaohuCitycodeMapper;

    @Override
    public void run(String... args) throws Exception {
        //1.处室话部门
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(serverName);
        List<FenleiBaohuCitycode> fenleiBaohuCitycodes = fenleiBaohuCitycodeMapper.selectList(null);
        HashMap<String,String> map = new HashMap(fenleiBaohuCitycodes.size()  * 2 );
        fenleiBaohuCitycodes.forEach(item ->{
            map.put(item.getCity(),item.getCode());
        });
        stringObjectObjectBoundHashOperations.put("cityCode",map);
        log.info("向redis中写入城市对应编码数据...");
    }
}
