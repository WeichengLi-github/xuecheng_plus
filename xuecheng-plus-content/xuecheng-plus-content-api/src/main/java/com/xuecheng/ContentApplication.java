package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: xuecheng_plus
 * @description: 学成内容模块API启动类
 * @author: VincentLi
 * @create: 2024-07-14 13:31
 **/
@SpringBootApplication(scanBasePackages = "com.xuecheng")
@EnableSwagger2Doc
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
