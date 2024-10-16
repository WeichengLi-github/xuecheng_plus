package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.util.SpringBeanUtil;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.convert.ContentConvert;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanMediaService;
import com.xuecheng.content.service.TeachplanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {
//    public static final TeachplanService TEACHPLAN_SERVICE = SpringBeanUtil.getBean(TeachplanService.class);
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Resource
    private TeachplanMediaMapper teachplanMediaMapper;
    @Resource
    private TeachplanMediaService teachplanMediaService;
    @Resource(name = "teachplanServiceImpl")
    private TeachplanService TEACHPLAN_SERVICE ;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }
    @Transactional
    @Override
    public void saveTeachplan(Teachplan teachplan) {
        if (teachplan.getId() == null) {
            // 设置排序号
            long orderNum = teachplanMapper.selectMaxOrder(teachplan.getCourseId(), teachplan.getParentid());
            teachplan.setOrderby(Math.toIntExact((Objects.isNull(orderNum) ? 0 : orderNum) + 1));
            // 如果新增失败，返回0，抛异常
            if (teachplanMapper.insert(teachplan) <= 0) XueChengPlusException.cast("新增失败");
        } else {
            // 如果修改失败，返回0，抛异常
            if (teachplanMapper.updateById(teachplan) <= 0) XueChengPlusException.cast("修改失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteTeachplan(@NotEmpty(message = "课程计划id不能为空！") Long teachplanId) {
        if (teachplanMapper.selectCountTeachplanByParentId(teachplanId)  > 0) {
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        }
        teachplanMapper.deleteById(teachplanId);
        teachplanMediaMapper.deleteMediaByTeachplanId(teachplanId);
    }

    @Override
    public void orderByTeachplan(String moveType, Long teachplanId) {
        //todo 处理空指针
        //直接使用接口调用可能存在课程不存在的情况，前端使用的话不存在，后期补齐后端判断
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        // 1:上移 0：下移
        Teachplan moveTeachplan = teachplanMapper.selectOrderUpOrDown(moveType.equals("moveup") ? 1 : 0, teachplan);
        TEACHPLAN_SERVICE.exchangeOrderby(teachplan,moveTeachplan);
        //注释原因：采用代理的方式，因为存在多个sql，防止事务失效
//        exchangeOrderby(teachplan,moveTeachplan);
    }

    /**
     * 交换两个Teachplan的orderby
     * @param teachplan
     * @param tmp
     */
    public void exchangeOrderby(Teachplan teachplan, Teachplan tmp) {
        if (tmp == null)
            XueChengPlusException.cast("已经到头啦，不能再移啦");
        else {
            // 交换 orderby 值
            Integer tempOrderby = teachplan.getOrderby();
            teachplan.setOrderby(tmp.getOrderby());
            tmp.setOrderby(tempOrderby);
            teachplanMapper.updateById(tmp);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Transactional
    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Optional.ofNullable(teachplanMapper.selectById(bindTeachplanMediaDto.getTeachplanId()))
                .filter(teachplan -> teachplan.getGrade() == 2)
                .orElseThrow(() -> new XueChengPlusException("教学计划不存在或绑定媒资信息的不是小节"));
        teachplanMediaService.saveOrUpdate(ContentConvert.INSTANCE.fromBindTeachplanMediaDto(bindTeachplanMediaDto),new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId()));
    }
    @Override
    public void unassociationMedia(Long teachPlanId, Long mediaId) {
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachPlanId)
                .eq(TeachplanMedia::getMediaId, mediaId));
    }
}
