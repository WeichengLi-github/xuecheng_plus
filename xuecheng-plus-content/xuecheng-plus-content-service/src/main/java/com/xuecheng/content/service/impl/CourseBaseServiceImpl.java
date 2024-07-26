package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {
    @Resource
    private CourseBaseMapper courseBaseMapper;
    @Resource
    private CourseMarketMapper courseMarketMapper;
    @Resource
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamDto queryCourseParamDto) {
        return PageResult.toPageResult(courseBaseMapper.selectPageByCondition(pageParams, queryCourseParamDto));
    }
    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        //TODO 使用mapstruct统一模型转化、精简代码长度、初始值
        // 2. 封装请求参数
        // 封装课程基本信息
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);
        // 2.1 设置默认审核状态（去数据字典表中查询状态码）
        courseBase.setAuditStatus("202002");
        // 2.2 设置默认发布状态
        courseBase.setStatus("203001");
        // 2.3 设置机构id
        courseBase.setCompanyId(companyId);
        // 2.4 设置添加时间
        courseBase.setCreateDate(LocalDateTime.now());
        // 2.5 插入课程基本信息表
        int baseInsert = courseBaseMapper.insert(courseBase);
        Long courseId = courseBase.getId();
        // 封装课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        courseMarket.setId(courseId);
        // 2.6 判断收费规则，若课程收费，则价格必须大于0
        String charge = courseMarket.getCharge();
        if ("201001".equals(charge)) {
            Float price = addCourseDto.getPrice();
            if (price == null || price.floatValue() <= 0) {
                throw new RuntimeException("课程设置了收费，价格不能为空，且必须大于0");
            }
        }
        // 2.7 插入课程营销信息表
        int marketInsert = courseMarketMapper.insert(courseMarket);
        if (baseInsert <= 0 || marketInsert <= 0) {
            throw new RuntimeException("新增课程基本信息失败");
        }
        // 3. 返回添加的课程信息
        return getCourseBaseInfo(courseId);
    }

    private CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        // 1. 根据课程id查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null)
            return null;
        // 1.1 拷贝属性
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        // 2. 根据课程id查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 2.1 拷贝属性
        if (courseMarket != null)
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        // 3. 查询课程分类名称，并设置属性
        // 3.1 根据小分类id查询课程分类对象
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        // 3.2 设置课程的小分类名称
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        // 3.3 根据大分类id查询课程分类对象
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        // 3.4 设置课程大分类名称
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }
}
