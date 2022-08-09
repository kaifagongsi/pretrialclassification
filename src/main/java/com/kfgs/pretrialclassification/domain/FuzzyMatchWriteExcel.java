package com.kfgs.pretrialclassification.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FuzzyMatchWriteExcel {
    /** 申请号 */
    @ExcelProperty(index = 0)
    private String sqh;

    /** 发明名称 */
    @ExcelProperty(index = 1)
    private String fmmc;

    /** 申请人 */
    @ExcelProperty(index = 2)
    private String sqr;

    /**  预审编号 */
    @ExcelProperty(index = 3)
    private String ysbh;

    /**  保护中心 */
    @ExcelProperty(index = 4)
    private String bhzx;

    /**  申请主题 */
    @ExcelProperty(index = 5)
    private String bhzxfmmc;

    /**  申请主题 */
    @ExcelProperty(index = 6)
    private String sqzt;

    /**  ipc */
    @ExcelProperty(index = 7)
    private String ipc;

    /**  cci */
    @ExcelProperty(index = 8)
    private String cci;
    /**  cca */
    @ExcelProperty(index = 9)
    private String cca;
    /**  csets */
    @ExcelProperty(index = 10)
    private String csets;
    /**  分类员 */
   /* @ExcelProperty(index = 11)
    private String fly;*/
    /**  匹配类型 */
    @ExcelProperty(index = 12)
    private String pplx;
    /**  出案日期 */
    @ExcelProperty(index = 11)
    private String carq;
}
