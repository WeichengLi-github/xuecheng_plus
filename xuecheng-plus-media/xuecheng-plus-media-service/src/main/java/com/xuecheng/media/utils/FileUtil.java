package com.xuecheng.media.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.media.exception.MediaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * @program: xuecheng_plus
 * @description: 文件操作工具类
 * @author: VincentLi
 * @create: 2024-08-04 12:51
 **/
@Slf4j
public class FileUtil {
    public static String getFileMimeType(@NotEmpty(message = "被转换文件名称不能为空！") String fileName) {
        return Optional.ofNullable(ContentInfoUtil.findExtensionMatch(fileName)).map(ContentInfo::getMimeType).orElse("application/octet-stream");
    }
    public static String getObjectPath(@NotEmpty(message = "文件名称不能为空！") String fileMd5Id) {
        try {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).concat("/").concat(fileMd5Id);
        } catch (Exception e) {
            log.error("获取文件路径失败！", e);
            throw new MediaException("获取文件路径失败！");
        }
    }
    public static String getMd5(@NotEmpty(message = "文件名称不能为空！") byte[] fileBytes) {
        try {
            return DigestUtils.md5DigestAsHex(fileBytes);
        } catch (Exception e) {
            log.error("获取文件MD5失败！", e);
            throw new MediaException("获取文件MD5失败！");
        }
    }
}
