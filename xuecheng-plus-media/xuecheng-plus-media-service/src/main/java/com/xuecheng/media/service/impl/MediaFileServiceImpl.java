package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.exception.MediaException;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.convert.MediaConvert;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.FileUtil;
import com.xuecheng.media.utils.MinioUtil;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
public class MediaFileServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFileService {
    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    private MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        queryWrapper.eq(!StringUtils.isEmpty(queryMediaParamsDto.getFileType()), MediaFiles::getFileType, queryMediaParamsDto.getFileType());


        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] fileBytes) {
        String md5Id = FileUtil.getMd5(fileBytes);
        MediaFiles fileInfo = currentProxy.getById(md5Id);
        if (fileInfo != null) {
            return MediaConvert.INSTANCE.poToDto(fileInfo);
        }
        MediaFiles mediaFiles = currentProxy.addFilesInfoToDb(companyId, uploadFileParamsDto,FileUtil.getObjectPath(md5Id), md5Id,bucket_files);
        //上传文件
        String filename = uploadFileParamsDto.getFilename();
        boolean uploadFile = MinioUtil.uploadFile(bucket_files,
                FileUtil.getObjectPath(md5Id),
                new ByteArrayInputStream(fileBytes),
                FileUtil.getFileMimeType(filename));
        if (!uploadFile) {
            throw new MediaException("上传文件失败！");
        }
        return MediaConvert.INSTANCE.poToDto(mediaFiles);
    }

    @Transactional
    public MediaFiles addFilesInfoToDb(Long companyId, UploadFileParamsDto uploadFileParamsDto, String objectName,String md5Id,String bucket) {

        MediaFiles mediaFiles = MediaConvert.INSTANCE.dtoToPo(uploadFileParamsDto);
        mediaFiles.setId(md5Id);
        mediaFiles.setCompanyId(companyId);
        mediaFiles.setBucket(bucket);
        mediaFiles.setFilePath(objectName);
        mediaFiles.setFileId(md5Id);
        String contentType = ContentInfoUtil.findExtensionMatch(uploadFileParamsDto.getFilename()).getMimeType();
        if (contentType.contains("mp4") || contentType.contains("image")) {
            mediaFiles.setUrl("/" + bucket_files + "/" + objectName + uploadFileParamsDto.getFilename());
        }
        mediaFiles.setAuditStatus("002003");
        boolean save = false;
        try {
            save = super.save(mediaFiles);
        } catch (Exception e) {
            log.error("文件信息入库失败，原因：{}",e);
            MediaException.cast("文件保存失败");
        }
        currentProxy.addWaitTask(mediaFiles,contentType);
        if (!save) {
            throw new MediaException("保存文件信息失败！");
        }
        return mediaFiles;
    }

    public void addWaitTask(MediaFiles mediaFile, String contentType) {

        //todo 适配其他视频类型
        if (contentType.equals("video/x-msvideo") && mediaProcessMapper.insert(MediaConvert.INSTANCE.dtoToDto(mediaFile)) <= 0) {
            MediaException.cast("保存avi视频到待处理表失败");
        }

    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);
        if (mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())) {
            MediaException.cast("视频还没有转码处理");
        }
        return mediaFiles;
    }

}

