package com.xuecheng.media.utils;

import com.xuecheng.base.util.SpringBeanUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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

}
