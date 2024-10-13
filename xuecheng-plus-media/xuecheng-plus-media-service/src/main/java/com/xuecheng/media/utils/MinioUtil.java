package com.xuecheng.media.utils;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.util.SpringBeanUtil;
import com.xuecheng.media.exception.MediaException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @program: xuecheng_plus
 * @description: Minio工具类
 * @author: VincentLi
 * @create: 2024-08-04 13:09
 **/
@Slf4j
public class MinioUtil {
    private static final MinioClient minioClient = SpringBeanUtil.getBean(MinioClient.class);

    public static boolean uploadFile(String bucketName, String objectName, ByteArrayInputStream inputStream, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .contentType(contentType)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            log.debug("上传文件失败,路径名： {} ，文件类型： {}", objectName, contentType);
        } catch (Exception e) {
            log.error("上传文件失败,路径名： {} ，文件类型： {}", objectName, contentType, e);
            return false;
        }
        return true;
    }

    /**
     * 将本地文件上传到minio
     *
     * @param bytes   文件
     * @param bucket     桶
     * @param objectName 对象名称
     */
    public static boolean uploadVideoFile(byte[] bytes, String bucket, String objectName) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(stream, stream.available(), -1)
                    .contentType(FileUtil.getFileMimeType(objectName))
                    .build());
        } catch (Exception e) {
            log.error("上传文件到文件系统出错！",e);
            return false;
        }
        return true;
    }

    /**
     * 将本地文件上传到minio
     * @param filePath      本地文件路径
     * @param bucket        桶
     * @param objectName    对象名称
     */
    public static void uploadVideoFile(String filePath, String bucket, String objectName) {
        try {
            minioClient.uploadObject(UploadObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .contentType(FileUtil.getFileMimeType(objectName))
                    .build());
        } catch (Exception e) {
            XueChengPlusException.cast("上传到文件系统出错");
        }
    }
    public static void deleteChunkFiles(String bucket, String objName, int chunkTotal) {
        Stream.iterate(0, i -> ++i)
                //++不应该是先+后用嘛，为啥第一个还是0
                .limit(chunkTotal)
                .forEach(i -> {
                    try {
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(bucket)
                                        .object(objName.concat(Integer.toString(i)))
                                        .build());
                    } catch (Exception e) {
                        log.error("Failed to delete chunk: {}", objName.concat(Integer.toString(i)), e);
                    }
                });
    }

}
