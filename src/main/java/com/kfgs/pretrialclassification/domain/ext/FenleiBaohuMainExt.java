package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FenleiBaohuMainExt extends FenleiBaohuMain {


    /* *
     * 发明件数
     */
    private String fmCount; //发明件数

    /* *
     * 新型件数
     */
    private String xxCount; //新型件数

    /* *
     * 总件数
     */
    private String totalCount; //总件数

    /* *
     * 未分配件数
     */
    private String wfpCount; //未分配件数

    /* *
     * 未完成件数
     */
    private String wccCount; //未完成件数

    /* *
     * 已完成件数
     */
    private String ywcCount; //已完成件数

    /* *
     * 需要裁决件数
     */
    private String cjCount; //裁决件数



}
