package com.hanfu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 汉服租赁系统启动类
 */
@SpringBootApplication
@MapperScan("com.hanfu.mapper")
public class HanfuApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanfuApplication.class, args);
        System.out.println("========================================");
        System.out.println("    汉服租赁系统启动成功！");
        System.out.println("    访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
