package com.xuecheng.content;

import com.xuecheng.XuechengPlusContentServiceApplication;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.content.service.impl.CourseCategoryServiceDatabaseImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @program: xuecheng_plus
 * @description: 课程分类查询测试类
 * @author: VincentLi
 * @create: 2024-07-21 13:45
 **/
@SpringBootTest(classes = XuechengPlusContentServiceApplication.class)
@Slf4j
public class CourseCategoryServiceTest {
    @Autowired
    private CourseCategoryService courseCategoryServiceDatabase;
    @Test
    public void testQueryTreeNodes() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryServiceDatabase.queryTreeNodes("1");
        log.info("查询结果：{}", courseCategoryTreeDtos);
    }
}
