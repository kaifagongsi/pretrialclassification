package com.kfgs.pretrialclassification.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Date: 2020-01-09-11-33
 * Module:
 * Description:
 *
 * @author:
 */
@Configuration
@MapperScan(value = "com.kfgs.pretrialclassification.dao")
public class MyBatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
