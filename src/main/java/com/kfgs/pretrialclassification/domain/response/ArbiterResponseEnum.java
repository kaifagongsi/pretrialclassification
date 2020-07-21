package com.kfgs.pretrialclassification.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
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
public enum ArbiterResponseEnum implements ResultCode{

    CANNOT_RESOLVE_ABBREVIATION(false,23000,"系统无法解析您的简写方式，请进一步完善填写内筒"),
    CANNOT_RESOLVE_CLASSVERSION(false,23001,"填入分类号的版本不对"),
    CSETS_2000_CANNOT_BE_HERE(false,23002,"Csets的各组第一个2000系列的号必须在CCA中出现"),
    CSETS_2000_MUST_IN_CCA_OR_CCI(false,23003,"Csets的各组第一个非2000系列的号必须在CCA或CCI中出现"),
    IPCMI_IPCOI_IPCA_REPEAT(false,23004,"主分、副分、附加信息或者CCI、CCA中有重复分类号"),
    CSETS_TOO_MORE_ZU(false,23005,"csets超过99组"),
    CSETS_TOO_MORE_GE(false,23006,"csets超过99个,或者小于2个");


    private ArbiterResponseEnum(Boolean success, int code, String message){
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
