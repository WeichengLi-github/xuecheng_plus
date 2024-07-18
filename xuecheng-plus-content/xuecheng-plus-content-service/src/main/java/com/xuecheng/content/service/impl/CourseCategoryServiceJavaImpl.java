package com.xuecheng.content.service.impl;


import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import javax.annotation.Resource;
import java.util.List;

public class CourseCategoryServiceJavaImpl extends CourseCategoryServiceImpl{
    @Resource
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        return null;
    }
}
