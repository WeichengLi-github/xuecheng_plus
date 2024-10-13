package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: xuecheng_plus
 * @description: 待处理任务实现类
 * @author: VincentLi
 * @create: 2024-10-13 18:14
 **/
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    private MediaFilesMapper mediaFilesMapper;
    @Autowired
    private MediaProcessMapper mediaProcessMapper;
    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    @Transactional
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        // 查询这个任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            log.debug("更新任务状态时，此任务：{}，为空", taskId);
            return;
        }
        LambdaQueryWrapper<MediaProcess> queryWrapper = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        // 如果任务失败
        if ("3".equals(status)) {
            log.debug("任务失败：{}", taskId);
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcess_u.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.update(mediaProcess_u, queryWrapper);
            return;
        }
        // 任务成功，将其从待处理任务表中删除，同时新增历史处理表记录
        if ("2".equals(status)) {
            // 更新文件表
            MediaFiles mediaFiles = new MediaFiles();
            mediaFiles.setUrl(url);
            mediaFilesMapper.update(mediaFiles, new LambdaQueryWrapper<MediaFiles>().eq(MediaFiles::getId, mediaProcess.getFileId()));

            mediaProcess.setStatus("2");
            mediaProcess.setUrl(url);
            mediaProcess.setFinishDate(LocalDateTime.now());
            MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
            // 两张表的属性完全一致，直接拷贝
            BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
            // 向历史处理表新增数据
            mediaProcessHistoryMapper.insert(mediaProcessHistory);
            // 同时删除待处理任务表中的数据
            mediaProcessMapper.deleteById(taskId);
        }
    }
}
