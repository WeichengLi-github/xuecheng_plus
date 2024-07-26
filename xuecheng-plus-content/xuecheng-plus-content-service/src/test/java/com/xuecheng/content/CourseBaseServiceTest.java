package com.xuecheng.content;

import com.xuecheng.XuechengPlusContentServiceApplication;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @program: xuecheng_plus
 * @description: 课程基本信息测试类
 * @author: VincentLi
 * @create: 2024-07-14 22:34
 **/
@SpringBootTest(classes = XuechengPlusContentServiceApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class CourseBaseServiceTest {
    @Resource
    private CourseBaseService courseBaseService;

    @Test
    public void testBaseService() {
        PageResult<CourseBase> courseBasePageResult = courseBaseService.queryCourseBaseList(new PageParams(1L, 10L), new QueryCourseParamDto(null, null, null));
        log.info("查询结果：{}", courseBasePageResult.getItems().toString());
    }
}
