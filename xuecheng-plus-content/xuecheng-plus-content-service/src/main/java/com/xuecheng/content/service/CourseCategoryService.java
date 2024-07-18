package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-07-14
 */
public interface CourseCategoryService extends IService<CourseCategory> {
    /**
     * 课程分类查询
     * @param id 根节点id
     * @return 根节点下面的所有子节点
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
