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
@TableName("FENLEI_BAOHU_LOG")
public class FenleiBaohuLog implements Serializable {

    private static final long serialVersionUID = 1L;

        @TableField("ID")
    private String id;

        @TableField("MESSAGE")
    private String message;

        @TableField("TIME")
    private String time;

        @TableField("RESULT")
    private String result;


}
