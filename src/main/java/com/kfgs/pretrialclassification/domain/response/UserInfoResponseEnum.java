package com.kfgs.pretrialclassification.domain.response;

public enum UserInfoResponseEnum implements ResultCode {
    SUCCESS(true,20201,"用户查询成功"),
    FAIL(false,10201,"用户查询失败"),
    EMAIL_FAIL(false,10202,"用户邮箱校验失败"),
    USER_PARAMS_ERROE(false,10203,"人员参数异常，请刷新页面重新尝试");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;


    private UserInfoResponseEnum(Boolean success, int code, String message){
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
