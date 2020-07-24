package com.kfgs.pretrialclassification.domain.response;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public enum CaseFinishResponseEnum implements ResultCode {

    NO_IPCMI(false,24000,"无主分号，需要裁决"),
    ONE_MORE_IPCMI(false,24001,"多个主分号，需要裁决"),
    MAX_NUM_CSETS(false,24002,"超过两人给出组合码且组合码总数多于99组，需要裁决"),
    FM_NO_CCI(false,24003,"发明案件的的CPC分类号不能为空，需要裁决");





    private CaseFinishResponseEnum(Boolean success, int code, String message){
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
