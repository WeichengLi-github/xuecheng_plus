package com.xuecheng.media.model.convert;


import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MediaConvert {
     MediaConvert INSTANCE = Mappers.getMapper(MediaConvert.class);
     MediaFiles dtoToPo(UploadFileParamsDto dto);
     UploadFileResultDto poToDto(MediaFiles po);
     MediaProcess dtoToDto(MediaFiles dto);
}
