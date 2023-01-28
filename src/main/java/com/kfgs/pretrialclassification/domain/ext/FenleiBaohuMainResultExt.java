package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FenleiBaohuMainResultExt extends FenleiBaohuMain {


    /*
     IPCA
     */
    private String ipca;
    /*
    IPCOI
     */
    private String ipcoi;
    /*
    /*
    IPCMI
     */
    private String ipcmi;
    /*
        主分类员
         */
    private String mainworker;

    /*
    副分类员
     */
    private String assworker;


    /*
        主分类
         */
    private String maintype;

    /*
    副分类
     */
    private String asstype;

    /*
        出案类型
         */
    private String chuantype;


    /**
     * 是否有相似案件
     */
    private boolean isSimilarCases;

    private String cityCode;
}
