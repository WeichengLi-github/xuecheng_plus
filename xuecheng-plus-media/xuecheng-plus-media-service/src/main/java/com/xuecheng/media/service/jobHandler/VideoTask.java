package com.xuecheng.media.service.jobHandler;

import com.xuecheng.base.util.Mp4VideoUtil;
import com.xuecheng.media.exception.MediaException;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.BigFilesService;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.MinioUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @program: xuecheng_plus
 * @description: 视频转换任务处理类
 * @author: VincentLi
 * @create: 2024-10-13 19:11
 **/
@Component
@Slf4j
public class VideoTask {
    @Value("${videoprocess.ffmpegpath}")
    String ffmpegPath;

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    private MediaFileService mediaFileService;
    @Autowired
    private BigFilesService bigFilesService;

    @Autowired
    private MediaFileProcessService mediaFileProcessService;
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws InterruptedException {
        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();
        int processorNumber = Runtime.getRuntime().availableProcessors();
        // 查询待处理任务，一次处理的任务数与cpu核心数相同
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardTotal, shardIndex, processorNumber);
        CountDownLatch countDownLatch = new CountDownLatch(mediaProcessList.size());
        // 未查询到待处理任务，结束方法
        if (mediaProcessList.isEmpty()) {
            log.debug("查询到的待处理任务数为0");
            countDownLatch.countDown();
            return;
        }
        // 要处理的任务数
        int size = mediaProcessList.size();
        // 查询到任务，创建size个线程去处理
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
        mediaProcessList.forEach(mediaProcess -> threadPool.execute(() -> {
            //todo 是否需要开启任务环节，真的会存在断网导致重新分配执行器导致重复执行的情况
            //todo 每步失败是否需要保存失败状态
            // 开头直接down是否可以

            String status;
            // 桶
            String bucket = mediaProcess.getBucket();
            // 文件路径
            String filePath = mediaProcess.getFilePath();
            // 原始文件的md5
            String fileId = mediaProcess.getFileId();
            File originalFile = null;
            File mp4File = null;
            try {
                // 将原始视频下载到本地，创建临时文件
                originalFile = File.createTempFile("original", null);
                // 处理完成后的文件
                mp4File = File.createTempFile("mp4", ".mp4");
            } catch (IOException e) {
                log.error("处理视频前创建临时文件失败");
                countDownLatch.countDown();
                MediaException.cast("处理视频前创建临时文件失败");
            }
            try {
                bigFilesService.downloadFileFromMinio(originalFile, bucket, filePath);
            } catch (Exception e) {
                log.error("下载原始文件过程中出错：{}，文件信息：{}", e.getMessage(), mediaProcess);
                countDownLatch.countDown();
                MediaException.cast("下载原始文件过程出错");
            }
            // 调用工具类将avi转为mp4
            String result = null;
            try {
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, originalFile.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
                // 获取转换结果，转换成功返回success 转换失败返回错误信息
                result = videoUtil.generateMp4();
            } catch (Exception e) {
                log.error("处理视频失败，视频地址：{}，错误信息：{}", originalFile.getAbsolutePath(), e.getMessage());
                countDownLatch.countDown();
                MediaException.cast("处理视频失败");
            }
            //转换成功，上传到MinIO
                    // 设置默认状态为失败
                    status = "3";
            String url = null;
            if ("success".equals(result)) {
                // 根据文件md5，生成objectName
                String objectName = bigFilesService.getChunkFileFolderPath(fileId).substring(0,37).concat(mediaProcess.getFilename()+".mp4");
                try {
                    MinioUtil.uploadVideoFile(mp4File.getAbsolutePath(), bucket, objectName);
                } catch (Exception e) {
                    log.error("上传文件失败：{}", e.getMessage());
                    countDownLatch.countDown();
                    MediaException.cast("上传文件失败");
                }
                // 处理成功，将状态设为成功
                status = "2";
                // 拼接url，准备更新数据
                url = "/" + bucket + "/" + objectName;
            }
            // 记录任务处理结果url
            mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), status, fileId, url, result);
            countDownLatch.countDown();
        }));

        // 等待，为了防止无线等待，这里设置一个超时时间为30分钟（很充裕了），若到时间还未处理完，则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);

    }
}
