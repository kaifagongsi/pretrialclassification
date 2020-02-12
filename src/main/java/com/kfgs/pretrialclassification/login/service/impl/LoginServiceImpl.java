package com.kfgs.pretrialclassification.login.service.impl;

import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.login.service.LoginService;
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
        System.out.println(token);
        System.out.println("redis存储：userDetails: {}" +  userDetails);
        BoundHashOperations<String, String, Object> stringStringObjectBoundHashOperations = tokenStorage();
        String key = userDetails.getLoginname();
        stringStringObjectBoundHashOperations.put(key, userDetails);
        return token;
    }
}
