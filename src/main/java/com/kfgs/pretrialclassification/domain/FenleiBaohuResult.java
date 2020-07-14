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
@TableName("FENLEI_BAOHU_RESULT")
public class FenleiBaohuResult implements Serializable {

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
    @TableField("FENPEIREN")
    private String fenpeiren;

    /**
     * 分给我的具体时间
     */
    @TableField("FENPEITIME")
    private String fenpeitime;

    /**
     * 0-未作业(主);
     * 1-未完成(主、副);
     * 2-已完成(主、副)
     */
    @TableField("STATE")
    private Long state;

    /**
     * 出案时间
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
     * IPCI 由IPCA，IPCMI，IPCOI组合
     */
    @TableField("IPCI")
    private String ipci;

    /**
     * IPCA 附加信息
     */
    @TableField("IPCA")
    private String ipca;

    /**
     * IPCMI 主分类号IPCMI主要发明信息
     */
    @TableField("IPCMI")
    private String IPCMI;

    /**
     * IPCOI 副分类号IPCOI其他发明信息
     */
    @TableField("IPCOI")
    private String IPCOI;

    @TableField("MESSAGE")
    private String MESSAGE;
}
