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
@TableName("FENLEI_BAOHU_CITYCODE")
public class FenleiBaohuCitycode implements Serializable {

    private static final long serialVersionUID = 1L;

        @TableField("CITY")
    private String city;

        @TableField("CODE")
    private String code;


}
