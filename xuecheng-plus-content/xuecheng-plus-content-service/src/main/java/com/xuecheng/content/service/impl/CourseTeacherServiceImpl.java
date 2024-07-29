package com.xuecheng.content.service.impl;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.service.CourseTeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Resource
    private CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        return courseTeacherMapper.selectListByCourseId(courseId);
    }

    @Override
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        if (courseTeacher.getCourseId() != null) {
            boolean update = courseTeacherMapper.updateById(courseTeacher) <= 0;
            if (update) {
                XueChengPlusException.cast("修改失败");
            }
        } else {
            boolean insert = courseTeacherMapper.insert(courseTeacher) <= 0;
            if (insert) {
                XueChengPlusException.cast("新增失败");
            }
        }
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        if (courseTeacherMapper.deleteCourseTeacher(courseId,teacherId)) {
            XueChengPlusException.cast("删除失败！");
        }
    }
}
