package com.xuecheng.content.model.convert;


import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContentConvert {
    ContentConvert INSTANCE = Mappers.getMapper(ContentConvert.class);

    @Mappings({
            @Mapping(target = "auditStatus" ,constant = "202002"),
            @Mapping(target = "status" ,constant = "203001"),
            @Mapping(target = "companyId" ,source = "companyId"),
    })
    CourseBase fromAddCourseDto(AddCourseDto addCourseDto, Long companyId);
    CourseBase fromEditCourseDto(EditCourseDto editCourseDto);
    CourseMarket fromEditCourseDtoToCourseMarket(EditCourseDto editCourseDto);
    CourseMarket fromAddCourseDto(AddCourseDto addCourseDto);

}
