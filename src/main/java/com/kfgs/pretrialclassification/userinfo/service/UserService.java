package com.kfgs.pretrialclassification.userinfo.service;

import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map findUserInfo();

    FenleiBaohuUserinfoExt findUserWorkerName();

    //登录时要验证的用户名密码
    FenleiBaohuUserinfoExt validateUsername(String username);

    QueryResponseResult findUserList(String pageNo,String pageSize,Map map);

    QueryResponseResult addUserInfo(FenleiBaohuUserinfo fenleiBaohuUserinfo);

    QueryResponseResult checkLoginName(String loginname);

    QueryResponseResult getUserInfoByLoginName(String loginname);

    QueryResponseResult deleteUserByLoginname(String loginname);

    QueryResponseResult departmentRotation(String ymlName,String department);

    QueryResponseResult updateUserinfo(FenleiBaohuUserinfo fenleiBaohuUserinfo);

    QueryResponseResult chenckUserEmail(String email);

    QueryResponseResult getInitDep1s();

    QueryResponseResult getInitDep2s(String dep1);

    List<String> getUserFullNameByList(List<String> loginList);

    QueryResponseResult updatePasswordByLoinname(String oldPassword, String newPassword, String loginname);
}
