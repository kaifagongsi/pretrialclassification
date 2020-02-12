package com.kfgs.pretrialclassification.userinfo.service;

import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map findUserInfo();
    //登录时要验证的用户名密码
    FenleiBaohuUserinfoExt validateUsername(String username);

}
