package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: xuecheng_plus
 * @description: 课程基础信息接口
 * @author: VincentLi
 * @create: 2024-07-14 12:11
 **/
@RestController
@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")
public class CourseBaseInfoController {
    @Resource
    private CourseBaseService courseBaseService;
    @PostMapping("/course/list")
    @ApiOperation("课程查询接口")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamDto queryCourseParams) {
        return courseBaseService.queryCourseBaseList(pageParams, queryCourseParams);
    }
}
