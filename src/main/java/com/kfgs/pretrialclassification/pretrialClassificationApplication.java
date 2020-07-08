package com.kfgs.pretrialclassification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication
@EnableRedisHttpSession
@ComponentScan( value = {
        "com.kfgs.pretrialclassification",
        "com.kfgs.pretrialclassification.common.exception",
        })
public class pretrialClassificationApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(pretrialClassificationApplication.class, args);

        for (String name : applicationContext.getBeanDefinitionNames()) {
            System.out.println(name);
        }

    }
}
