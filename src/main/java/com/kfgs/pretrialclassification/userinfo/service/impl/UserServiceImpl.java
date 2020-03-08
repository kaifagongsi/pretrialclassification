package com.kfgs.pretrialclassification.userinfo.service.impl;

import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    FenleiBaohuUserinfoMapper userinfoMapper;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    private BoundHashOperations<String, String, Object> tokenStorage() {
        return redisTemplate.boundHashOps(jwtTokenUtils.getTokenHeader());
    }

    @Override
    public FenleiBaohuUserinfoExt validateUsername(String username) {
        BoundHashOperations<String, String, Object> stringStringObjectBoundHashOperations = tokenStorage();
        Object o = stringStringObjectBoundHashOperations.get(username);
        System.out.println(o.toString());
        return (FenleiBaohuUserinfoExt) tokenStorage().get(username);
    }
    @Override
    public Map findUserInfo() {
        Map result = new HashMap<>();
        // 从SecurityContextHolder中获取到，当前登录的用户信息。
        FenleiBaohuUserinfoExt userDetails = (FenleiBaohuUserinfoExt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 根据用户Id，获取用户详细信息。    getUserinfoByLoginNameWithRole
        FenleiBaohuUserinfoExt fenleiBaohuUserinfoExt = userinfoMapper.getUserinfoByLoginName(userDetails.getLoginname()).get(0);
        //设置权限
        Map data = new HashMap<>();
        data.put("roles",fenleiBaohuUserinfoExt.getAuthorities());
        data.put("introduction",(fenleiBaohuUserinfoExt.getType() == "admin") ? "管理员" : "用户");
        data.put("name",fenleiBaohuUserinfoExt.getName());
        data.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        result.put("code",200);
        result.put("data",data);
        return data;
    }
}
