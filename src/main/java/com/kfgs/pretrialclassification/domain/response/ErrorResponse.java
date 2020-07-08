package com.kfgs.pretrialclassification.domain.response;


/**
 * <p>错误返回结果</p>
 */
public class ErrorResponse extends ResponseResult {
    public ErrorResponse() {
    }

    public ErrorResponse(int code, String message) {
        super(code, message);
    }


}
