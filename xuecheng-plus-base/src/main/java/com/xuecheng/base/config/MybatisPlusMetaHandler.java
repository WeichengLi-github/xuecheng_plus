package com.xuecheng.base.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @program: xuecheng_plus
 * @description: MyBatisPlus元数据填充
 * @author: VincentLi
 * @create: 2024-07-28 11:53
 **/
@Slf4j
@Component
public class MybatisPlusMetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject,"createDate",LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject,"changeDate",LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setFieldValByName("changeDate",LocalDateTime.now(),metaObject);
    }
}
