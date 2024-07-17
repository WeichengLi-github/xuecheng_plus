package com.xuecheng.base.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    /**
     * 提供分页结果直接向最终结果的构造函数
     *
     * @param page
     * @return
     */
    public static <T> PageResult<T> toPageResult(IPage<T> page) {
        return PageResult.<T>builder()
                .items(page.getRecords())
                .counts(page.getTotal())
                .page(page.getCurrent())
                .pageSize(page.getSize()).build();
    }
    //TODO 补充其他分页结果参数的构造函数
}
