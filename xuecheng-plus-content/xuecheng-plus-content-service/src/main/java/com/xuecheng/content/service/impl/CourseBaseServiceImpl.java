package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.convert.ContentConvert;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.service.CourseMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

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
    private static final String IS_CHARGE = "201001";
    @Resource
    private CourseBaseMapper courseBaseMapper;
    @Resource
    private CourseMarketMapper courseMarketMapper;
    @Resource
    private CourseTeacherMapper courseTeacherMapper;
    @Resource
    private TeachplanMapper teachplanMapper;
    @Resource
    private CourseCategoryMapper courseCategoryMapper;
    @Resource
    private CourseMarketService courseMarketService;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamDto queryCourseParamDto) {
        return PageResult.toPageResult(courseBaseMapper.selectPageByCondition(pageParams, queryCourseParamDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        CourseBase courseBase = ContentConvert.INSTANCE.fromAddCourseDto(addCourseDto, companyId);
        int baseInsert = courseBaseMapper.insert(courseBase);
        Long courseId = courseBase.getId();
        // 封装课程营销信息
        CourseMarket courseMarket = ContentConvert.INSTANCE.fromAddCourseDto(addCourseDto);
        courseMarket.setId(courseId);
        boolean marketInsert = saveCourseMarket(courseMarket);
        if (baseInsert <= 0 || marketInsert) {
            XueChengPlusException.cast("新增课程基本信息失败");
        }
        // 3. 返回添加的课程信息
        return getCourseInfo(courseId);
    }

    private CourseBaseInfoDto getCourseBaseInfoV1(Long courseId) {
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

    /**
     * 根据课程id获取课程基本信息，包括营销和分类名称
     *
     * @param courseId 课程id
     * @return 课程基本信息
     */
    private CourseBaseInfoDto getCourseBaseInfoV2(Long courseId) {
        return courseBaseMapper.selectCourseBaseInfoDtoById(courseId);
    }

    public CourseBaseInfoDto getCourseInfo(Long courseId) {
        return getCourseBaseInfoV2(courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectByCompanyIdAndCourseId(companyId, courseId);
        if (Objects.isNull(courseBase)) {
            XueChengPlusException.cast("只允许修改本机构的课程");
        }
        // 拷贝对象
        courseBase = ContentConvert.INSTANCE.fromEditCourseDto(editCourseDto);
        // 更新
        courseBaseMapper.updateById(courseBase);
        // 对象拷贝
        CourseMarket courseMarket = ContentConvert.INSTANCE.fromEditCourseDtoToCourseMarket(editCourseDto);
        courseMarket.setId(courseId);
        if (!saveCourseMarket(courseMarket)) {
            XueChengPlusException.cast("课程保存或修改失败！");
        }
        return getCourseInfo(courseId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delectCourse(Long companyId, Long courseId) {
        //TODO 课程计划绑定的媒资信息是否需要删除
        Boolean deleteCourseBase = courseBaseMapper.deleteCourseBase(companyId, courseId);
        if (!deleteCourseBase) {
            XueChengPlusException.cast("只允许删除本机构的课程！");
        }
        courseMarketMapper.deleteById(courseId);
        teachplanMapper.deleteCourseTeachPlan(courseId);
        courseTeacherMapper.deleteCourseTeacher(courseId);
    }

    private boolean saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            XueChengPlusException.cast("请设置收费规则");
        }
        // 如果课程收费，则判断价格是否正常
        if (charge.equals(IS_CHARGE)) {
            Float price = courseMarket.getPrice();
            if (price == null || price <= 0) {
                XueChengPlusException.cast("课程设置了收费，价格不能为空，且必须大于0");
            }
        }
        // 有则更新，无则插入
        return courseMarketService.saveOrUpdate(courseMarket);
    }
}
