package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import lombok.Data;
import lombok.ToString;

/* *
 * @author : mango
 * @date : 2020/3/11 14:00
 */
@Data
@ToString
public class FenleiBaohuResultExt  extends FenleiBaohuResult {

    /* *
     * 分类代码
     */
    private String fldm; //分类代码
    /* *
     * 一级部门
     */
    private String dep1;
    /* *
     * 二级部门
     */
    private String dep2;
    /* *
     * 组织部门 FL  JG
     */
    private String orgname;
    /* *
     * 发明名称
     */
    private String mingcheng;
    /* *
     * 邮箱
     */
    private String email;
    /* *
     * fldmworker
     */
    private String fldmworker;
    /* *
     * 粗分结果
     */
    private String simpleclasscode;
    /* *
     * 进案时间
     */
    private String jinantime;
    /* *
     * 分配时间
     */
    private String fenpeitime;
}
