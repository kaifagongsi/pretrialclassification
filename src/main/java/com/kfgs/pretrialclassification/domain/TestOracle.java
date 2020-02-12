package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;


/**
 * Date: 2020-01-08-22-31
 * Module:
 * Description:
 *
 * @author:
 */
@Data
@ToString
@TableName("testorcale")
public class TestOracle {
    private Integer id;
    private String name;
}
