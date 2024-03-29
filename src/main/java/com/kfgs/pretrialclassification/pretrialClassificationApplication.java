package com.kfgs.pretrialclassification;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableScheduling //开启定时任务
@SpringBootApplication
@EnableRedisHttpSession
@NacosPropertySource(dataId = "nw-bhzx", autoRefreshed = true)
public class pretrialClassificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(pretrialClassificationApplication.class, args);
    }
}
