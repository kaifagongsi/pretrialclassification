package com.kfgs.pretrialclassification.userinfo.controller;

import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.common.utils.JsonResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api("用户信息")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {


    @Autowired
    UserService userService;

    @ApiOperation(value = "获取用户详细信息", notes = "获取用户详细信息")
    @GetMapping("/getUserInfo")
    public JsonResult<Map> findUserInfo() {
        return JsonResult.success(userService.findUserInfo());
    }

}
