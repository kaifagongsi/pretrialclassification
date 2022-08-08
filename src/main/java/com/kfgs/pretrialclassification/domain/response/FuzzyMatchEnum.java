package com.kfgs.pretrialclassification.domain.response;

import com.kfgs.pretrialclassification.common.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuzzyMatchEnum implements ResultCode {
    NO_PERMISSION_OPERATION(false,27002,"暂无权限进行次操作"),
    FUZZY_MATCH_ERROR(false,27001,"任务下发失败，当前任务已下发");


    //操作是否成功
    boolean success;

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
