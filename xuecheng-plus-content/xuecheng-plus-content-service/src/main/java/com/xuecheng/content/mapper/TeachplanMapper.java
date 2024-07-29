package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    //todo 解决空指针
    default long selectMaxOrder(Long courseId, Long parentId) {
        return selectOne(new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getParentid, parentId)
                .orderByDesc(Teachplan::getOrderby)).getOrderby();
    }
    default long selectCountTeachplanByParentId(long teachplanId) {
        return selectCount(new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getParentid, teachplanId));
    }

    //todo 解决空指针
    default Teachplan selectOrderUpOrDown(int direction, Teachplan teachplan) {
        return selectOne(new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .lt(direction == 1,Teachplan::getOrderby, teachplan.getOrderby())
                .orderByDesc(direction == 1,Teachplan::getOrderby)
                .gt(direction == 0,Teachplan::getOrderby, teachplan.getOrderby())
                .orderByAsc(direction == 0,Teachplan::getOrderby)
        );
    }
    default boolean deleteCourseTeachPlan(long courseId) {
        return delete(new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getCourseId, courseId)) > 0;
    }
    List<TeachplanDto> selectTreeNodes(Long courseId);
}
