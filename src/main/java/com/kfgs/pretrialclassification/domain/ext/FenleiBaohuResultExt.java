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
     * 分类员姓名
     */
    private String workerName; // 分类员姓名
    /* *
     * 分类领域
     */
    private String areaName; // 分类领域
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
     * 发明件数
     */
    private String fmCount; //发明件数

    /* *
     * 新型件数
     */
    private String xxCount; //新型件数

    /* *
     * 总件数
     */
    private String totalCount; //总件数
    /* *
     * 发明主分给分类号数
     */
    private String fmzfNumCount; //发明主分给分类号数
    /* *
     * 发明副分给分类号数
     */
    private String fmffNumCount; //发明副分给分类号数
    /* *
     * 发明未给分类号数
     */
    private String fmNoNumCount; //发明未给分类号数
    /* *
     * 新型主分给分类号数
     */
    private String xxzfNumCount; //新型主分给分类号数
    /* *
     * 新型副分给分类号数
     */
    private String xxffNumCount; //新型副分给分类号数
    /* *
     * 新型未给分类号数
     */
    private String xxNoNumCount; //新型未给分类号数
    /* *
     * 主裁决案件数
     */
    private String cjNum; //主裁决案件数
    /* *
     * 副裁决案件数
     */
    private String cjyNum; //副裁决案件数

}
