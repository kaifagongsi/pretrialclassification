package com.kfgs.pretrialclassification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication
@EnableRedisHttpSession
public class pretrialClassificationApplication {
    public static void main(String[] args) {
        System.out.println("------------------------");
        System.out.println("---11111111-----");
        SpringApplication.run(pretrialClassificationApplication.class, args);
    }
}
