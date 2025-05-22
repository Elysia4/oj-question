package com.ely.elyoj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ElyOJ在线评测系统启动类
 */
@SpringBootApplication
@MapperScan("com.ely.elyoj.mapper")
@EnableTransactionManagement
public class ElyOjApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ElyOjApplication.class);
        application.setAllowCircularReferences(true);
        application.run(args);
    }

} 