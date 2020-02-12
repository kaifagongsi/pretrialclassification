package com.kfgs.pretrialclassification.domain.ext;

import com.kfgs.pretrialclassification.domain.FenleiBaohuRole;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Date: 2020-01-19-20-21
 * Module:
 * Description:
 *
 * @author:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FenleiBaohuUserinfoExt extends FenleiBaohuUserinfo implements UserDetails, Serializable{

    /**
     * 父类id
     */
    private int pId;

    /**
     *子类有List
     */
    private List children;
    
    /**
     * 是否记住我
     */
    private int rememberMe;

    @NotNull(message = "验证码不能为空")
    @Length(min = 4, max = 4, message = "验证码长度是四位")
    private String codeText;

    @NotNull(message = "验证码 KEY 不能为空")
    private String codeKey;

    /***
     * 权限列表
     */
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return getLoginname();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

}
