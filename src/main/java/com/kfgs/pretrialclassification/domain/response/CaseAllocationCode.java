package com.kfgs.pretrialclassification.domain.response;

public enum CaseAllocationCode implements ResultCode {
    SUCCESS(true,20101,"邮件发送成功"),
    FAIL(false,10101,"邮件发送失败");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;


    private CaseAllocationCode(Boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
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
