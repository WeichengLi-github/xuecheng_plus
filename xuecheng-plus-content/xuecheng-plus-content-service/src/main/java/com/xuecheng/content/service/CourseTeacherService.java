package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-07-14
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    List<CourseTeacher> getCourseTeacherList(Long courseId);

    CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher);

    void deleteCourseTeacher(Long courseId, Long teacherId);
}
