package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程基本信息 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseBaseMapper extends BaseMapper<CourseBase> {
    /**
     * 条件查询课程基本信息
     * @param pageParams
     * @param queryCourseParamDto
     * @return
     */
    default IPage<CourseBase> selectPageByCondition(PageParams pageParams, QueryCourseParamDto queryCourseParamDto) {
        return selectPage(new Page<CourseBase>(pageParams.getPageNo(), pageParams.getPageSize()),new LambdaQueryWrapper<CourseBase>()
                .like(StringUtils.isNotBlank(queryCourseParamDto.getCourseName()),CourseBase::getName,queryCourseParamDto.getCourseName())
                .eq(StringUtils.isNotBlank(queryCourseParamDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamDto.getAuditStatus())
                .eq(StringUtils.isNotBlank(queryCourseParamDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamDto.getPublishStatus()));
    }

}
