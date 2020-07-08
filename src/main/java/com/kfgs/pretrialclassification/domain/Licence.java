package com.kfgs.pretrialclassification.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

/**
 * <pre>
 *
 * </pre>
 *
 * @author sprainkle
 * @date 2019/5/3
 */
@Data
@TableName("licence")
public class Licence {

    @Id
    @TableField("LICENCE_ID")
    @NotNull
    private Long licenceId;

    @TableField("ORGANIZATION_ID")
    private Long organizationId;

    @TableField("LICENCE_TYPE")
    @NotNull
    private String licenceType;

    @TableField("PRODUCT_NAME")
    @NotNull
    private String productName;

    @TableField("LICENCE_MAX")
    private Integer licenceMax;

    @TableField("LICENCE_ALLOCATED")
    private Integer licenceAllocated;


}
