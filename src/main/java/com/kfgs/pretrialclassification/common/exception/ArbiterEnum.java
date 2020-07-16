package com.kfgs.pretrialclassification.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * 功能描述:
 * 〈〉
 *  裁决异常处理
 * @param null 1
 * @return :
 * @author : lxl
 * @date : 2020/7/13 14:26
 */
@Getter
@AllArgsConstructor
public enum ArbiterEnum implements BusinessExceptionAssert {

    GET_USERNAME_FAILE(21001,"获取当前登录姓名错误"),
    GET_Arbiter_CASE_FAILE(21002,"根据裁决ID获取裁决案件失败");

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;
}
