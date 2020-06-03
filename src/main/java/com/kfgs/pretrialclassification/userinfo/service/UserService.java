package com.kfgs.pretrialclassification.userinfo.service;

import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map findUserInfo();
    //登录时要验证的用户名密码
    FenleiBaohuUserinfoExt validateUsername(String username);

    QueryResponseResult findUserList(String pageNo,String pageSize,Map map);

    QueryResponseResult addUserInfo(FenleiBaohuUserinfo fenleiBaohuUserinfo);

    QueryResponseResult checkLoginName(String loginname);

    QueryResponseResult getUserInfoByLoginName(String loginname);

    QueryResponseResult deleteUserByLoginname(String loginname);

    QueryResponseResult departmentRotation(String ymlName,String department);
}
