package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Date: 2020-07-15-10-34
 * Module:
 * Description:
 *
 * @author:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("FENLEI_BAOHU_IPC")
public class FenleiBaohuIPC implements Serializable {
    @TableField("IPC")
    private String IPC;
}
