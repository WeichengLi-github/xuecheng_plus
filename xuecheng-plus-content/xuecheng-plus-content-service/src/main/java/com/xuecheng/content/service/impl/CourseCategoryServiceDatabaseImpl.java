package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import javax.annotation.Resource;
import java.util.List;

public class CourseCategoryServiceDatabaseImpl extends CourseCategoryServiceImpl{
    @Resource
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        return courseCategoryMapper.selectTreeNodesV2(id);
    }
}
