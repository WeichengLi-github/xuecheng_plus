package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {

    default List<CourseTeacher> selectListByCourseId(long courseId) {
        return selectList(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId));
    }
    default boolean deleteCourseTeacher(long courseId,long teacherId) {
        return delete(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getId, teacherId)
                .eq(CourseTeacher::getCourseId, courseId)) < 0 ? Boolean.FALSE : Boolean.TRUE;
    }
    default boolean deleteCourseTeacher(long courseId) {
        return delete(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId)) > 0;
    }
}
