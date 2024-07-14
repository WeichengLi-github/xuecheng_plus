package com.xuecheng.content.controller;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @program: xuecheng_plus
 * @description: dsa
 * @author: VincentLi
 * @create: 2024-07-14 20:33
 **/
@RestController
@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")
public class test {
    @Autowired
    private CourseBaseService courseBaseService;
    @GetMapping ( "/course/list")
    @ApiOperation("课程查询接口")
    public void list() {
        System.out.printf("结果：", courseBaseService.list());
    }
}
