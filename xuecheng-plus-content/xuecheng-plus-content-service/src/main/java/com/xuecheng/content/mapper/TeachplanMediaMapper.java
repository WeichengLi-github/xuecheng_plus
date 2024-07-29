package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMediaMapper extends BaseMapper<TeachplanMedia> {
    default int deleteMediaByTeachplanId(long teachplanId) {
        return delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId, teachplanId));
    }

}
