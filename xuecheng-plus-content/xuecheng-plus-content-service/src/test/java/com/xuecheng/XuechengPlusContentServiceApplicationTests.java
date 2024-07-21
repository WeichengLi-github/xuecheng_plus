package com.xuecheng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@SpringBootTest(classes = XuechengPlusContentServiceApplication.class)
class XuechengPlusContentServiceApplicationTests {

    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseCategoryService courseCategoryServiceDatabase;

    @Test
    void testCourseBaseMapper () {
        List<CourseBase> courseBase = courseBaseMapper.selectList(new LambdaQueryWrapper<>());
        log.info("查询到数据：{}", courseBase.toString());
        Assertions.assertNotNull(courseBase);

        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(new Page<>(1, 5), new LambdaQueryWrapper<CourseBase>().like("Nacos".equals("Nacos"), CourseBase::getDescription, "Nacos"));
        log.info("查询到数据：{}", courseBasePage.getRecords().toString());
        Assertions.assertNotNull(courseBasePage);
    }
    @Test
    public void testQueryTreeNodes() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryServiceDatabase.queryTreeNodes("1");
        log.info("查询结果：{}", courseCategoryTreeDtos);
    }
}
