package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 
 *
 * @author mango
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("FENLEI_BAOHU_ADJU_INFOR_BACKUP")
public class FenleiBaohuAdjuInforBackup implements Serializable {

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
     * 粗分进案，主分转案，副分转案
     */
        @TableField("CLASSTYPE")
    private String classtype;

    /**
     * 粗分进案，谁转出来的
     */
        @TableField("FENPEIREN")
    private String fenpeiren;

    /**
     * 分给我的具体时间
     */
        @TableField("FENPEITIME")
    private String fenpeitime;

    /**
     * 0-未作业(主);
1-未完成(主、副);
2-已完成(主、副)7:裁决中;8:裁决完成;9:更正
     */
        @TableField("STATE")
    private String state;

    /**
     * 出案时间
     */
        @TableField("CHUANTIME")
    private String chuantime;

    /**
     * CCI主分+副分
     */
        @TableField("CCI")
    private String cci;

    /**
     * CCA附件信息
     */
        @TableField("CCA")
    private String cca;

    /**
     * CSETS组合码
     */
        @TableField("CSETS")
    private String csets;

    /**
     * IPCI由IPCA，IPCMI，IPCOI组合
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
    private String IPCMI;

    /**
     * 副分类号IPCOI其他发明信息
     */
        @TableField("IPCOI")
    private String IPCOI;

    /**
     * 转案记录
     */
        @TableField("MESSAGE")
    private String MESSAGE;


}
