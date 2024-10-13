package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * 根据分片参数查询任务
     * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count 处理条数
     * @return
     * 处理失败的任务没有处理
     * todo 处理失败任务的处理
     */
    @Select("SELECT * FROM xc_media.media_process WHERE id % #{shardTotal} = #{shardIndex} AND status = '1' LIMIT #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);
}
