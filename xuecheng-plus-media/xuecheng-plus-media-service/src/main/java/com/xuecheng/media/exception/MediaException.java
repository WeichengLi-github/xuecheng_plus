package com.xuecheng.media.exception;

import com.xuecheng.base.exception.XueChengPlusException;

/**
 * @program: xuecheng_plus
 * @description: 媒资服务异常类
 * @author: VincentLi
 * @create: 2024-08-04 14:18
 **/
public class MediaException extends XueChengPlusException {
    private String errMessage;
    public MediaException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }
    public String getErrMessage() {
        return errMessage;
    }
    public MediaException() {
        super();
    }
    public static void cast(String errMessage) {
        throw new MediaException(errMessage);
    }
}
