package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-07-14
 */
public interface TeachplanService extends IService<Teachplan> {
    List<TeachplanDto> findTeachplanTree(Long courseId);
    void saveTeachplan(Teachplan teachplan);
}
