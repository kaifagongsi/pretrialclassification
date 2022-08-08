package com.kfgs.pretrialclassification.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FuzzyMatchReadExcel {
    @ExcelProperty(index = 0)
    private String sqh;

    @ExcelProperty(index = 1)
    private String fmmc;

    @ExcelProperty(index = 2)
    private String sqr;
}
