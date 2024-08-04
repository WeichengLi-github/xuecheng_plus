package com.xuecheng.media;

import io.minio.*;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream ;
import java.io.FilterInputStream;
import java.nio.file.Paths;

/**
 * @program: xuecheng_plus
 * @description: Minio测试类
 * @author: VincentLi
 * @create: 2024-08-04 10:51
 **/
public class MinioTest {
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.3:9001")
            .credentials("admin", "admin123")
            .build();

    @Test
    @SneakyThrows
    public void upload() {
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("video")
                        .object("zhaoyi.jpg")//上传到minio之后的路径和文件名
                        .filename("E:\\Photo\\Camera\\IMG_20210402_185423.jpg")//本地文件路径和文件
                        .build());
    }

    @Test
    @SneakyThrows
    public void download() {
        minioClient.downloadObject(DownloadObjectArgs.builder()
                .bucket("video")
                .object("zhaoyi.jpg")
                .filename("E:\\Photo\\Camera\\test.jpg")
                .build());
    }
    @Test
    @SneakyThrows
    public void delete() {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket("video")
                        .object("zhaoyi.jpg")
                        .build());
    }
    @Test
    @SneakyThrows
    public void getObject() {
        FilterInputStream object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("video")
                        .object("zhaoyi.jpg")
                        .build());
        FileOutputStream outputStream = new FileOutputStream(Paths.get("E:\\Photo\\Camera\\ceshi.jpg").toFile());

        IOUtils.copy(object, System.out);
    }
}
