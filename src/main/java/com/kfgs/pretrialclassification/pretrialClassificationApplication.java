package com.kfgs.pretrialclassification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication
@EnableRedisHttpSession
public class pretrialClassificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(pretrialClassificationApplication.class, args);
    }
}
