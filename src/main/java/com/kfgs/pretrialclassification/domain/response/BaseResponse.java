package com.kfgs.pretrialclassification.domain.response;

import com.kfgs.pretrialclassification.common.exception.CommonResponseEnum;
import com.kfgs.pretrialclassification.common.exception.IResponseEnum;
import lombok.Data;

/**
 * Date: 2020-06-29-10-34
 * Module:
 * Description:
 *  所有返回结果的基类
 * @author:
 */
@Data
public class BaseResponse {
    /**
     * 返回码
     */
    protected int code;
    /**
     * 返回消息
     */
    protected String message;

    public BaseResponse() {
        // 默认创建成功的回应
        this(CommonResponseEnum.SUCCESS);
    }

    public BaseResponse(IResponseEnum responseEnum) {
        this(responseEnum.getCode(), responseEnum.getMessage());
    }

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
