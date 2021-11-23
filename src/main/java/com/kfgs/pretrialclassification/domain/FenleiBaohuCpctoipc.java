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
@TableName("FENLEI_BAOHU_CPCTOIPC")
public class FenleiBaohuCpctoipc implements Serializable {

    private static final long serialVersionUID = 1L;

        @TableField("CPC")
    private String cpc;

        @TableField("IPC")
    private String ipc;

        @TableField("RANK")
    private Double rank;

        @TableField("VERSION")
    private Double version;

}
