package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-07-14
 */
public interface CourseBaseService extends IService<CourseBase> {
    /**
     * 课程分页查询
     * @param pageParams
     * @param queryCourseParamDto
     * @return
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamDto queryCourseParamDto);
}
