package com.xuecheng.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService extends IService<MediaFiles>{

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

 /**
  * @description 上传文件的通用接口
  * @param companyId           机构id
  * @param uploadFileParamsDto 文件信息
  * @param bytes               文件字节数组
  * @param folder              桶下边的子目录
  * @param objectName          对象名称
  * @return com.xuecheng.media.model.dto.UploadFileResultDto
  */
 UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] fileBytes);
 MediaFiles addFilesInfoToDb(Long companyId, UploadFileParamsDto uploadFileParamsDto, String objectName,String md5Id,String bucket);

 /**
  * 预览接口
  * @param mediaId 媒资信息id
  * @return 媒资文件url
  */
 MediaFiles getFileById(String mediaId);

 /**
  * 添加视频待处理任务表
  * @param mediaFile 文件信息
  * @param contentType 文件类型
  */
 void addWaitTask(MediaFiles mediaFile, String contentType);
}
