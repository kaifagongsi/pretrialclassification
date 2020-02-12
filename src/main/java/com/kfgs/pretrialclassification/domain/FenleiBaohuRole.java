package com.kfgs.pretrialclassification.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

/**
 * Date: 2020-01-21-22-58
 * Module:
 * Description:
 *
 * @author:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("FENLEI_BAOHU_ROLE")
public class FenleiBaohuRole implements GrantedAuthority {

    @TableField("ID")
    private String id;

    @TableField("ROLENAME")
    private String rolename;

    @Override
    public String getAuthority() {
        return rolename;
    }
}
