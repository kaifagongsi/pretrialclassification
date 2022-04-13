package com.kfgs.pretrialclassification.domain.response;

public enum UserinfoCode implements ResultCode {
    USERINFO_FAIL_HAVINGARBITER(false,26001,"删除失败，此人员为裁决组长，并且任有分类员将其设置为上一级裁决组长"),
    USERINFO_FAIL_CANNOTUPDATA_ONINE(false,26001,"修改失败，该用户下，仍有案件未处理"),
    USERINFO_SUCCESS(true,20000,"操作成功");


    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;


    private UserinfoCode(boolean success, int code, String message) {
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
