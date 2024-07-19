package com.xuecheng.content.service.impl;


import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CourseCategoryServiceJavaImpl extends CourseCategoryServiceImpl {
    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
       return queryTreeNodesV1(id);
    }

    /**
     * 查询树形结构 （不论sql返回结果是否是层级结构，都可以）
     * 手动加入根节点的子节点，并为其他所有节点加入其父节点
     *
     * 直接遍历也可以，找到根节点，忽略即可，最后直接拿出根节点
     * @param id
     * @return
     */
    private List<CourseCategoryTreeDto> queryTreeNodesV1(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        Map<String, CourseCategoryTreeDto> parentIdMap = courseCategoryTreeDtos.stream().collect(Collectors.toMap(key -> key.getId(), Function.identity()));
        List<CourseCategoryTreeDto> list = new ArrayList<>();
        courseCategoryTreeDtos.forEach(courseCategoryTreeDto -> {
            if (courseCategoryTreeDto.getId().equals(id)) {
                return;
            }
            if (courseCategoryTreeDto.getParentid().equals(id)) {
                list.add(courseCategoryTreeDto);
            }
            CourseCategoryTreeDto parentCourseCategoryTreeDto = parentIdMap.get(courseCategoryTreeDto.getParentid());
            if (parentCourseCategoryTreeDto.getChildrenTreeNodes() == null) {
                parentCourseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<>());
            }
            parentCourseCategoryTreeDto.getChildrenTreeNodes().add(courseCategoryTreeDto);
        });
        return list;
    }

    /**
     * 查询树形结构 （sql必须为层级结构，才能保证）
     * @param id
     * @return
     */
    private List<CourseCategoryTreeDto> queryTreeNodesV2(String id) {
        // 获取所有的子节点
        List<CourseCategoryTreeDto> categoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        // 定义一个List，作为最终返回的数据
        List<CourseCategoryTreeDto> result = new ArrayList<>();
        // 为了方便找子节点的父节点，这里定义一个HashMap，key是节点的id，value是节点本身
        HashMap<String, CourseCategoryTreeDto> nodeMap = new HashMap<>();
        // 将数据封装到List中，只包括根节点的下属节点（1-1、1-2 ···），这里遍历所有节点
        categoryTreeDtos.stream().forEach(item -> {
            // 这里寻找父节点的直接下属节点（1-1、1-2 ···）
            if (item.getParentid().equals(id)) {
                nodeMap.put(item.getId(), item);
                result.add(item);
            }
            // 获取每个子节点的父节点
            String parentid = item.getParentid();
            CourseCategoryTreeDto parentNode = nodeMap.get(parentid);
            // 判断HashMap中是否存在该父节点（按理说必定存在，以防万一）
            if (parentNode != null) {
                // 为父节点设置子节点（将1-1-1设为1-1的子节点）
                List childrenTreeNodes = parentNode.getChildrenTreeNodes();
                // 如果子节点暂时为null，则初始化一下父节点的子节点（给个空集合就行）
                if (childrenTreeNodes == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                // 将子节点设置给父节点
                parentNode.getChildrenTreeNodes().add(item);
            }
        });
        // 返回根节点的直接下属节点（1-1、1-2 ···）
        return result;
    }
    //TODO 直接遍历也可以，找到根节点，忽略即可，最后直接拿出根节点
}
