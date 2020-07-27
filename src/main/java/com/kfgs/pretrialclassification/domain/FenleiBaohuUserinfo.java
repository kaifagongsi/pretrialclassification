package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("FENLEI_BAOHU_USERINFO")
public class FenleiBaohuUserinfo  implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;//序号

    /**
     * 登陆ID
     */
        @TableField("LOGINNAME")
    private String loginname;

    /**
     * 登陆密码
     */
        @TableField("PASSWORD")
    private String password;

    /**
     * 姓名
     */
        @TableField("NAME")
    private String name;

    /**
     * 部门
     */
        @TableField("ORGNAME")
    private String orgname;

    /**
     * 领域
     */
        @TableField("AREANAME")
    private String areaname;

    /**
     * A角，B角
     */
        @TableField("CLASSLEVEL")
    private String classlevel;

    /**
     * id-姓名
     */
        @TableField("WORKERNAME")
    private String workername;

    /**
     * 个人邮箱
     */
        @TableField("EMAIL")
    private String email;

    /**
     * 二级部门
     */
        @TableField("DEP1")
    private String dep1;

    /**
     * 三级部门
     */
        @TableField("DEP2")
    private String dep2;

    /**
     * 角色 admin , user
     */
    @TableField("TYPE")
    private String type;

    @TableField("IPCS")
    private String ipcs;

    @TableField("FIELDS")
    private String fields;

    /**
     * 上一次做案子的时间
     */
    @TableField("LAST_TIME")
    private String lastTime;

    /**
     * 上一次做案子的ipc
     */
    @TableField("LAST_IPC")
    private String lastIpc;

    /**
     * 1在 0不在
     */
    @TableField("IS_ONLINE")
    private String isOnline;
    /**
     * 每个人对应的组长
     */
    @TableField("ADJUDICATOR")
    private String Adjudicator;
}
