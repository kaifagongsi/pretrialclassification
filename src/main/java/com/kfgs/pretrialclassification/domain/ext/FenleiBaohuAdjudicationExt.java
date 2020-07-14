package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Date: 2020-07-08-15-11
 * Module:
 * Description:
 *
 * @author:
 */
@Data
@ToString
public class FenleiBaohuAdjudicationExt extends FenleiBaohuAdjudication   {

    //案件名称
    private String mingcheng;
    //案件入保护中心事件
    private String jinantime;
    //sqh
    private String sqh;

}
