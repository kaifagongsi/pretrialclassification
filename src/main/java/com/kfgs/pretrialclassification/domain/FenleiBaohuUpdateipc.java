package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 保护中心更正分类号申请记录表
 *
 * @author mango
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("FENLEI_BAOHU_UPDATEIPC")
public class FenleiBaohuUpdateIpc implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保护中心编号
     */
        @TableField("ID")
    private String id;

    /**
     * 分类员
     */
        @TableField("WORKER")
    private String worker;

    /**
     * 主分，副分
     */
        @TableField("CLASSTYPE")
    private String classtype;

    /**
     * 案件来源，谁分给我的
     */
        @TableField("AGENT")
    private String agent;

    /**
     * 分给我的具体时间
     */
        @TableField("FENPEITIME")
    private String fenpeitime;

    /**
     * 提出更正分类号申请的具体时间
     */
        @TableField("UPLOADTIME")
    private String uploadtime;

    /**
     * 0-未审批;1-审批通过;2-审批未通过
     */
        @TableField("STATE")
    private String state;

    /**
     * 完成更正分类号申请的时间
     */
        @TableField("CHUANTIME")
    private String chuantime;

    /**
     * CCI
     */
        @TableField("CCI")
    private String cci;

    /**
     * CCA
     */
        @TableField("CCA")
    private String cca;

    /**
     * CSETS
     */
        @TableField("CSETS")
    private String csets;

    /**
     * IPCI
     */
        @TableField("IPCI")
    private String ipci;

    /**
     * IPCA附加信息
     */
        @TableField("IPCA")
    private String ipca;

    /**
     * 主分类号IPCMI主要发明信息
     */
        @TableField("IPCMI")
    private String ipcmi;

    /**
     * 副分类号IPCOI其他发明信息
     */
        @TableField("IPCOI")
    private String ipcoi;


}
