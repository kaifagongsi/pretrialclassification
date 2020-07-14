package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Date: 2020-07-08-15-11
 * Module:
 * Description:
 *
 * @author:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("FENLEI_BAOHU_ADJUDICATION")
public class FenleiBaohuAdjudication implements Serializable {

    /**ipcmi
     * 保护中心编号
     */
    @TableField("ID")
    private String id;

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
    private String ipcmi;

    /**
     * IPCOI 副分类号IPCOI其他发明信息
     */
    @TableField("IPCOI")
    private String ipcoi;
    /**
     *  转案记录
     */
    @TableField("MESSAGE")
    private String message;

    /**
     *  处理人员（某个室主任），登录者,  裁决组长
     */
    @TableField("PROCESSINGPERSON")
    private String processingPerson;

    /**
     *  由于未满足那个条件出发裁决，详细问题（1.两个主分，2.没有主分.或者其他）
     */
    @TableField("PROCESSINGREASONS")
    private String processingreasons;

    /**
     * 进入裁决时间
     */
    @TableField("RUKUTIME")
    private String rukuTime;

    /**
     *  裁决完成时间
     */
    @TableField("FINISHTIME")
    private String finishTime;

    /**
     * 裁决员
     */
    @TableField("ADJUDICATOR")
    private String adjudicator;

    /**
     * 0-未作业(主);
     * 1-未完成(主、副);
     * 2-已完成(主、副);7:需要裁决;8裁决完成
     */
    @TableField("STATE")
    private String state;

}
