package com.kfgs.pretrialclassification.domain.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 裁决模块参数
 */
@Data
@ToString
public class ArbiterParam implements Serializable {
    private String dep1;
    private String dep2;
    private String person;
}
