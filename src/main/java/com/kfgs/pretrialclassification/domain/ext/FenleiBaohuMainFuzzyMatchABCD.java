package com.kfgs.pretrialclassification.domain.ext;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FenleiBaohuMainFuzzyMatchABCD {
    /** 申请人相同，名称相同 */
    private String A;
    /** 申请人相同，名称模糊相同 （去掉一种的前八个字符） */
    private String B;
    /** 申请人不同，名称相同  */
    private String C;
    // 202208011 徐勇确认将 申请人不同、名称模糊 去掉
//    private String D;
}
