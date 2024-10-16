package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Objects;

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
     *
     * @param pageParams
     * @param queryCourseParamDto
     * @return
     */
    default IPage<CourseBase> selectPageByCondition(PageParams pageParams, QueryCourseParamDto queryCourseParamDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(queryCourseParamDto)) {
            queryWrapper
                    .like(StringUtils.isNotBlank(queryCourseParamDto.getCourseName()), CourseBase::getName, queryCourseParamDto.getCourseName())
                    .eq(StringUtils.isNotBlank(queryCourseParamDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamDto.getAuditStatus())
                    .eq(StringUtils.isNotBlank(queryCourseParamDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamDto.getPublishStatus());
        }
        return selectPage(new Page<>(pageParams.getPageNo(), pageParams.getPageSize()), queryWrapper);
    }
    default CourseBase selectByCompanyIdAndCourseId(@Param("companyId") Long companyId, @Param("courseId") Long courseId) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseBase::getCompanyId, companyId)
                .eq(CourseBase::getId, courseId);
        return selectOne(queryWrapper);
    }
    default Boolean deleteCourseBase(long companyId,long courseId) {
        return delete(new LambdaQueryWrapper<CourseBase>()
                .eq(CourseBase::getId,courseId)
                .eq(CourseBase::getCompanyId,companyId)) > 0;
    }

    CourseBaseInfoDto selectCourseBaseInfoDtoById(@Param("courseId") Long courseId);

}
