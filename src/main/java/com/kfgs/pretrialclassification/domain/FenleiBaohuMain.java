package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 *
 * @author mango
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("FENLEI_BAOHU_MAIN")
public class FenleiBaohuMain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 案件编号
     */
        @TableField("ID")
    private String id;

    /**
     * 发明名称
     */
        @TableField("MINGCHENG")
    private String mingcheng;

    /**
     * FM,XX
     */
        @TableField("TYPE")
    private String type;

    /**
     * 申请人
     */
        @TableField("SQR")
    private String sqr;

    /**
     * 申请号
     */
        @TableField("SQH")
    private String sqh;

    /**
     * PDF文件路径/文件名
     */
        @TableField("PDF_PATH")
    private String pdfPath;

    /**
     * 状态0：未分配 1：未做完 2：已完成 7:需要裁决
     */
        @TableField("STATE")
    private String state;

    /**
     * 粗分结果
     */
        @TableField("SIMPLECLASSCODE")
    private String simpleclasscode;

    /**
     * 出案日期（最后）
     */
        @TableField("CHUANTIME")
    private Long chuantime;

    /**
     * 进案日期
     */
        @TableField("JINANTIME")
    private Long jinantime;

    /**
     * 转案留言
     */
        @TableField("MESSAGE")
    private String message;

    /**
     *  分配给谁worker
     */
    @TableField(exist = false)

    private String worker;
    /* *
     *  分配给人所在的领域
     */
    @TableField(exist = false)

    private String areaname;



}
