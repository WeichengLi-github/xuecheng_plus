package com.xuecheng.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: xuecheng_plus
 * @description: 课程查询入参
 * @author: VincentLi
 * @create: 2024-07-14 11:54
 **/
@Data
@AllArgsConstructor
public class QueryCourseParamDto {
    // 审核状态
    private String auditStatus;
    // 课程名称
    private String courseName;
    // 发布状态
    private String publishStatus;
}