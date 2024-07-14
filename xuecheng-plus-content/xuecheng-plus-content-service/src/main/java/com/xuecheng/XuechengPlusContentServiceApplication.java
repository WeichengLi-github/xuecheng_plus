package com.xuecheng;

import com.xuecheng.content.mapper.CourseBaseMapper;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xuecheng.content.mapper")
public class XuechengPlusContentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuechengPlusContentServiceApplication.class, args);
    }

}
