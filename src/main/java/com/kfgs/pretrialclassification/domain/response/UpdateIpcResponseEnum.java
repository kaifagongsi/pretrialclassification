package com.kfgs.pretrialclassification.domain.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Date: 2020-07-15-09-23
 * Module:
 * Description:
 *
 * @author:
 */

@ToString
@AllArgsConstructor
@NoArgsConstructor
public enum UpdateIpcResponseEnum implements ResultCode{

    CANNOT_PASS_AMEND_MAIN(false,25001,"已导出的案件无法进行分类号更正"),
    CANNOT_PASS_AMEND_UPDATEIPC(false,25002,"分类号更正状态修改失败");


    private UpdateIpcResponseEnum(Boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

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
    }}
