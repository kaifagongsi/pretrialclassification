package com.kfgs.pretrialclassification.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UpdateIpcEnum implements BusinessExceptionAssert {
    CANNOT_UPDATE_NOT_GET_CASE(22001,"无法更新，该案件状态异常，请刷新页面重试");

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;
}
