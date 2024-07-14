package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

/**
 * @program: xuecheng_plus
 * @description: 课程基础信息接口
 * @author: VincentLi
 * @create: 2024-07-14 12:11
 **/
@RestController
@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")
public class CourseBaseInfoController {
    @PostMapping("/course/list")
    @ApiOperation("课程查询接口")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamDto queryCourseParams) {
//        CourseBase courseBase = new CourseBase();
//        courseBase.setId(15L);
//        courseBase.setDescription("测试课程");
//        PageResult<CourseBase> result = new PageResult<>();
//        result.setItems(Arrays.asList(courseBase));
//        result.setPage(1);
//        result.setPageSize(10);
//        result.setCounts(1);
        return PageResult.<CourseBase>builder()
                .items(Collections.singletonList(CourseBase.builder()
                        .id(15L)
                        .description("测试课程")
                        .build()))
                .page(1)
                .pageSize(10)
                .counts(1)
                .build();
    }
}
