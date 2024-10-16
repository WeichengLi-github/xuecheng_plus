package com.xuecheng.media.service.jobHandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * 测试执行器
 */
@Slf4j
@Component
public class SimpleJob {
    @XxlJob("testJob")
    public void testJob() {
        log.debug("开始执行.......");
    }
}