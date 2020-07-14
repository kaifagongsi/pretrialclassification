package com.kfgs.pretrialclassification.domain.request;


import com.baomidou.mybatisplus.annotation.TableField;

/**
 * Date: 2020-07-14-16-12
 * Module:
 * Description:
 * 保存分类号是传参
 * @author:
 */
public class classficationParam {


    /**
     * 保护中心编号
     */
    private String id;

    /**
     * CCI
     */
    private String cci;

    /**
     * CCA
     */
    private String cca;

    /**
     * CSETS
     */
    private String csets;

    /**
     * IPCI 由IPCA，IPCMI，IPCOI组合
     */
    private String ipci;

    /**
     * IPCA 附加信息
     */
    private String ipca;

    /**
     * IPCMI 主分类号IPCMI主要发明信息
     */
    private String ipcmi;

    /**
     * IPCOI 副分类号IPCOI其他发明信息
     */
    private String ipcoi;
}
