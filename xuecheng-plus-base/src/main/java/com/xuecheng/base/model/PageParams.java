package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code @program:} xuecheng_plus
 * {@code @description:} 入参基类
 * @author: VincentLi
 * @create: 2024-07-14 11:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {
    //TODO 使用静态最终变量替换魔法值
    /**
     * 当前页码
     */
    private Long pageNo = 1L;
    /**
     * 每页记录数
     */
    private Long pageSize = 30L;
}
