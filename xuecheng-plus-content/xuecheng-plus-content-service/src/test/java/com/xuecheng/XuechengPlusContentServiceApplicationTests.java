package com.xuecheng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest(classes = XuechengPlusContentServiceApplication.class)
class XuechengPlusContentServiceApplicationTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    void contextLoads() {
        List<CourseBase> courseBase = courseBaseMapper.selectList(new LambdaQueryWrapper<>());
        log.info("查询到数据：{}", courseBase);
        Assertions.assertNotNull(courseBase);
    }
}
