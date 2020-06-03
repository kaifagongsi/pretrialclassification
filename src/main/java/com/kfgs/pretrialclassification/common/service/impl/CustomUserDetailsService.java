package com.kfgs.pretrialclassification.common.service.impl;

import com.kfgs.pretrialclassification.common.exception.PretrialClassificationException;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Date: 2020-01-21-17-27
 * Module:
 * Description:
 *
 * @author:
 */
@Service

public class CustomUserDetailsService  implements UserDetailsService {

    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<FenleiBaohuUserinfoExt> userinfoByLoginName = fenleiBaohuUserinfoMapper.getUserinfoByLoginName(s);
        if(Objects.isNull(userinfoByLoginName)){
            try {
                throw  new PretrialClassificationException("用户不存在");
            } catch (PretrialClassificationException e) {
                e.printStackTrace();
            }
        }
        FenleiBaohuUserinfoExt ext = userinfoByLoginName.get(0);
        System.out.println(ext.getLoginname() + "----" + ext.getName() + "==>通过验证" );
        return ext;
    }
}
