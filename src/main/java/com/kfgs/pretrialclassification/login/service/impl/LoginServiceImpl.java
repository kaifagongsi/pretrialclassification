package com.kfgs.pretrialclassification.login.service.impl;

import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.common.utils.SecurityUtil;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.login.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    private BoundHashOperations<String, String, Object> tokenStorage() {
        return redisTemplate.boundHashOps(jwtTokenUtils.getTokenHeader());
    }
    @Override
    public String login(String username, String password) {
        // 把表单提交的 username  password 封装到 UsernamePasswordAuthenticationToken中
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        FenleiBaohuUserinfoExt userDetails = (FenleiBaohuUserinfoExt) userDetailsService.loadUserByUsername(username);
        String token = jwtTokenUtils.generateToken(userDetails);
        log.info("redis存储：userDetails: {}" +  userDetails);
        BoundHashOperations<String, String, Object> stringStringObjectBoundHashOperations = tokenStorage();
        String key = userDetails.getLoginname();
        stringStringObjectBoundHashOperations.put(key, userDetails);
        return token;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String tokenHeader = this.jwtTokenUtils.getTokenHeader();
        String completeToken = request.getHeader(tokenHeader);
        String username = "";
        if (completeToken != null && completeToken.startsWith(this.jwtTokenUtils.getTokenHead())) {
            //1.redis 中去掉认证信息
            final String tokenValue = jwtTokenUtils.interceptCompleteToken(completeToken);
            // 根据 token值，获取 用户的 username
            username = jwtTokenUtils.getUsernameFromToken(tokenValue);
            BoundHashOperations<String, String, Object> stringObjectObjectBoundHashOperations = tokenStorage();
            Long delete = stringObjectObjectBoundHashOperations.delete(username);
            log.info(username + "进入退出操作，删除redis"  + "数量：" + delete);
        }
        //2.设置 token 为null
        response.setHeader(tokenHeader, null);
        //3. 设置SecurityContextHolder 为null
        SecurityContextHolder.getContext().setAuthentication(null);
        //4. 删除sessionRegistry
        securityUtil.sessionRegistryRemoveUserByLoginName(username);
    }
}
