package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: xuecheng_plus
 * @description: 分页响应结果基类
 * @author: VincentLi
 * @create: 2024-07-14 12:01
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T> implements Serializable {
    // 数据列表
    private List<T> items;
    // 总记录数
    private long counts;
    // 当前页码
    private long page;
    // 每页记录数
    private long pageSize;
}
