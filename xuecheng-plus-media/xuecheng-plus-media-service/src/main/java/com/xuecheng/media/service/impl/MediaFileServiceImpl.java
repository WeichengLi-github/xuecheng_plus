package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.exception.MediaException;
import com.xuecheng.media.mapper.MediaFilesMapper;
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
    private MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

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
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFile,String md5Id) {
        MediaFiles fileInfo = super.getById(md5Id);
        if (fileInfo != null) {
            return MediaConvert.INSTANCE.poToDto(fileInfo);
        }
        MediaFiles mediaFiles = currentProxy.addFilesInfoToDb(companyId, uploadFileParamsDto, localFile, md5Id);
        //上传文件
        String filename = uploadFileParamsDto.getFilename();
        boolean uploadFile = MinioUtil.uploadFile(bucket_files,
                FileUtil.getObjectPath(filename),
                localFile,
                FileUtil.getFileMimeType(filename));
        if (!uploadFile) {
            throw new MediaException("上传文件失败！");
        }
        return MediaConvert.INSTANCE.poToDto(mediaFiles);
    }

    @Transactional
    public MediaFiles addFilesInfoToDb(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFile, String md5Id) {

        MediaFiles mediaFiles = MediaConvert.INSTANCE.dtoToPo(uploadFileParamsDto);
        mediaFiles.setId(md5Id);
        mediaFiles.setCompanyId(companyId);
        mediaFiles.setBucket(bucket_files);
        String objectPath = FileUtil.getObjectPath(uploadFileParamsDto.getFilename());
        mediaFiles.setFilePath(objectPath);
        mediaFiles.setFileId(md5Id);
        mediaFiles.setUrl("/" + bucket_files + "/" + objectPath);
        mediaFiles.setAuditStatus("002003");
        boolean save = super.save(mediaFiles);
        if (!save) {
            throw new MediaException("保存文件信息失败！");
        }
        return mediaFiles;
    }
}

