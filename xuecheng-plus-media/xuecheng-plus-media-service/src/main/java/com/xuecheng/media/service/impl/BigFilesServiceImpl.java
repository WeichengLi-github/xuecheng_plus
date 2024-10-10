package com.xuecheng.media.service.impl;

import com.xuecheng.base.exception.RestErrorResponse;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.exception.MediaException;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.BigFilesService;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.MinioUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class BigFilesServiceImpl implements BigFilesService {
    @Value("${minio.bucket.videofiles}")
    private String video_files;
    @Resource
    private MediaFileService mediaFileService;
    @Resource
    private MediaFilesMapper mediaFilesMapper;
    @Resource
    private MinioClient minioClient;

    //TODO 针对stream的iterate，讲义中写的内容进行复测，讲义实现的方案实现
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            return RestResponse.success(false);
        }
        try {
            if (minioClient.getObject(GetObjectArgs.builder()
                    .bucket(mediaFiles.getBucket())
                    .object(mediaFiles.getFilePath())
                    .build()) == null) {
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        // 获取分块目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        try {
            // 不存在返回false
            if (minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(video_files)
                    .object(chunkFilePath)
                    .build()) == null) {
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            // 出异常也返回false
            return RestResponse.success(false);
        }
        // 否则返回true
        return RestResponse.success();
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        // 分块文件路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;
        if (!MinioUtil.uploadVideoFile(bytes, video_files, chunkFilePath)) {
            return RestResponse.validfail("上传文件失败！");
        }
        return RestResponse.success(true);
    }

    //TODO 是否需要事务管理，或者是异步等
    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        // 下载分块文件
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);
        // 获取源文件名
        String fileName = uploadFileParamsDto.getFilename();
        // 获取源文件扩展名
        String extension = fileName.substring(fileName.lastIndexOf("."));
        // 创建出临时文件，准备合并
        File mergeFile = null;
        try {
            mergeFile = File.createTempFile(fileName, extension);
        } catch (IOException e) {
            XueChengPlusException.cast("创建合并临时文件出错");
        }
        try {
            // 缓冲区
            byte[] buffer = new byte[1024];
            // 写入流，向临时文件写入
            try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw")) {
                // 遍历分块文件数组
                for (File chunkFile : chunkFiles) {
                    // 读取流，读分块文件
                    try (RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r")) {
                        int len;
                        while ((len = raf_read.read(buffer)) != -1) {
                            raf_write.write(buffer, 0, len);
                        }
                    }
                }
            } catch (Exception e) {
                XueChengPlusException.cast("合并文件过程中出错");
            }
            uploadFileParamsDto.setFileSize(mergeFile.length());
            // 对文件进行校验，通过MD5值比较
            try (FileInputStream mergeInputStream = new FileInputStream(mergeFile)) {
                String mergeMd5 = DigestUtils.md5Hex(mergeInputStream);
                if (!fileMd5.equals(mergeMd5)) {
                    XueChengPlusException.cast("合并文件校验失败");
                }
                log.debug("合并文件校验通过：{}", mergeFile.getAbsolutePath());
            } catch (Exception e) {
                XueChengPlusException.cast("合并文件校验异常");
            }
            String mergeFilePath = getChunkFileFolderPath(fileMd5);
            // 将本地合并好的文件，上传到minio中，这里重载了一个方法
            MinioUtil.uploadVideoFile(mergeFile.getAbsolutePath(), video_files, mergeFilePath + extension);
            log.debug("合并文件上传至MinIO完成{}", mergeFile.getAbsolutePath());
            // 将文件信息写入数据库
            MediaFiles mediaFiles = mediaFileService.addFilesInfoToDb(companyId, uploadFileParamsDto, mergeFilePath, fileMd5, video_files);
            if (mediaFiles == null) {
                XueChengPlusException.cast("媒资文件入库出错");
            }
            log.debug("媒资文件入库完成");

            return RestResponse.success();
        } finally {
            for (File chunkFile : chunkFiles) {
                try {
                    chunkFile.delete();
                } catch (Exception e) {
                    log.debug("临时分块文件删除错误：{}", e.getMessage());
                }
            }
            try {
                mergeFile.delete();
            } catch (Exception e) {
                log.debug("临时合并文件删除错误：{}", e.getMessage());
            }
        }
    }

    /**
     * 下载分块文件
     *
     * @param fileMd5    文件的MD5
     * @param chunkTotal 总块数
     * @return 分块文件数组
     */
    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        // 作为结果返回
        File[] files = new File[chunkTotal];
        // 获取分块文件目录
        String chunkFileFolder = getChunkFileFolderPath(fileMd5);
        for (int i = 0; i < chunkTotal; i++) {
            // 获取分块文件路径
            String chunkFilePath = chunkFileFolder + i;
            File chunkFile = null;
            try {
                // 创建临时的分块文件
                chunkFile = File.createTempFile("chunk" + i, null);
            } catch (Exception e) {
                MediaException.cast("创建临时分块文件出错：" + e.getMessage());
            }
            // 下载分块文件
            chunkFile = downloadFileFromMinio(chunkFile, video_files, chunkFilePath);
            // 组成结果
            files[i] = chunkFile;
        }
        return files;
    }

    /**
     * 从Minio中下载文件
     *
     * @param file       目标文件
     * @param bucket     桶
     * @param objectName 桶内文件路径
     * @return
     */
    private File downloadFileFromMinio(File file, String bucket, String objectName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             InputStream inputStream = minioClient.getObject(GetObjectArgs
                     .builder()
                     .bucket(bucket)
                     .object(objectName)
                     .build())) {
            IOUtils.copy(inputStream, fileOutputStream);
            return file;
        } catch (Exception e) {
            XueChengPlusException.cast("查询文件分块出错");
        }
        return null;
    }

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }
}
