package com.kfgs.pretrialclassification.common.utils;

import com.kfgs.pretrialclassification.common.vo.LoginUser;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Security工具类
 */
@Component
@Slf4j
public class SecurityUtil {

    @Autowired
    private SessionRegistry sessionRegistry;

    /**
     * 从ThreadLocal获取其自己的SecurityContext，从而获取在Security上下文中缓存的登录用户
     */
    public static FenleiBaohuUserinfoExt getLoginUser() {
        FenleiBaohuUserinfoExt user = null;
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails) {
            user = (FenleiBaohuUserinfoExt) auth.getPrincipal();
        }
        return user;
    }

    /**
     * 指定loginName从sessionRegistry中删除user
     */
    public void sessionRegistryRemoveUserByLoginName(String loginName){
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object allPrincipal : allPrincipals) {
            LoginUser user = (LoginUser) allPrincipal;
            if(user.getLoginname().equals(loginName)){
                List<SessionInformation> allSessions = sessionRegistry.getAllSessions(user, true);
                if (allSessions != null) {
                    for (SessionInformation sessionInformation : allSessions) {
                        sessionInformation.expireNow();
                        sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                    }
                }
                break;
            }
        }
    }

    /**
     * 向sessionRegistry注册user
     */
    public void sessionRegistryAddUser(String sessionId, Object user){
        sessionRegistry.registerNewSession(sessionId,user);
    }

    public List<Object> sessionRegistryGetAllPrincipals(){
        return sessionRegistry.getAllPrincipals();
    }
}