package com.xuecheng.media.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;

import java.io.File;

public interface BigFilesService {
    /**
     * 检查文件是否存在
     *
     * @param fileMd5 文件的md5
     * @return
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 检查分块是否存在
     * @param fileMd5       文件的MD5
     * @param chunkIndex    分块序号
     * @return
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
    /**
     * 上传分块
     * @param fileMd5   文件MD5
     * @param chunk     分块序号
     * @param bytes     文件字节
     * @return
     */
    RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes);
    RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);
    File downloadFileFromMinio(File file, String bucket, String objectName);
    String getChunkFileFolderPath(String fileMd5);
}
