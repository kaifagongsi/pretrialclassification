package com.kfgs.pretrialclassification.domain.response;;

import com.kfgs.pretrialclassification.common.exception.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public enum CaseChangeIpcEnum implements ResultCode {
    NOT_RULE(true,23003,"当前账号没有权限"),
    CASE_MAIN_INFO_ERROR(true,23002,"当前案件在MAIN表中，异常，请联系李晓亮"),
    MORE_OR_NULL_IPCMI(true,23001,"主分类号异常");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public int code() {
        return 0;
    }

    @Override
    public String message() {
        return null;
    }
}
