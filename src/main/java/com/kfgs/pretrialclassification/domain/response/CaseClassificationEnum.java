package com.kfgs.pretrialclassification.domain.response;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public enum CaseClassificationEnum implements ResultCode {

    INVALID_RESAVE(false,30001,"非法操作,该案件已出案"),
    INVALID_CASE_RULED(false,30002,"非法操作,该案件在裁决中"),
    INVALID_CASE_UPDATE(false,30003,"非法操作,该案件已提出更正"),
    INVALID_CASE_FINISH(false,30004,"非法操作，该案件已完成出案"),
    INVALID_CASE_STATE(false,30005,"非法操作,请刷新后重试或联系管理员"),
    INCALID_CASE_RETRANS(false,30006,"存在人员重复转案，操作取消"),
    NO_TRANS_WORKER(false,30007,"无效转案");


    private CaseClassificationEnum(Boolean success, int code, String message){
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
        return this.success;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}
