package com.kfgs.pretrialclassification.common.repeatsubmit;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.regexp.RE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @内存缓存，防止重复提交
 * @author lsy
 */
@Configuration
public class UrlCache {
    @Bean
    public Cache<String,Integer> getCache(){
        return CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build();
    }
}
