package com.kfgs.pretrialclassification.userinfo.controller;

import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.common.utils.JsonResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api("用户信息")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {


    @Autowired
    UserService userService;

    @ApiOperation(value = "获取登录用户详细信息", notes = "获取登录用户详细信息")
    @GetMapping("/getUserInfo")
    public JsonResult<Map> findUserInfo() {
        return JsonResult.success(userService.findUserInfo());
    }

    @ApiOperation(value = "获取人员列表")
    @PostMapping("/getUserList/{pageNo}/{pageSize}") //如何有小数点：请添加  :.+
    public QueryResponseResult findUserList(@PathVariable("pageNo") String pageNo, @PathVariable("pageSize")String pageSize,@RequestBody Map map){
        return  userService.findUserList(pageNo,pageSize,map);
    }

    @ApiOperation(value = "新增用户")
    @PostMapping("/addUserInfo")
    public QueryResponseResult addUserInfo(@RequestBody FenleiBaohuUserinfo fenleiBaohuUserinfo){
        return  userService.addUserInfo(fenleiBaohuUserinfo);
    }

    @ApiOperation(value = "检查loginname是否重复")
    @PostMapping("/checkLoginName")
    public QueryResponseResult checkLoginName(@RequestBody  String loginname){
        return  userService.checkLoginName(loginname);
    }

    @ApiOperation(value = "跟据loginname，获取用户", notes = "跟据loginname，获取用户")
    @GetMapping("/getUserInfoByLoginName/{loginname}")
    public QueryResponseResult getUserInfoByLoginName(@PathVariable("loginname") String loginname){
        return  userService.getUserInfoByLoginName(loginname);
    }

    @ApiOperation(value = "跟据loginname，删除用户", notes = "跟据loginname，删除用户" )
    @DeleteMapping("/deleteUserByLoginname/{loginname}")
    public QueryResponseResult deleteUserByLoginname(@PathVariable("loginname") String loginname){
        return  userService.deleteUserByLoginname(loginname);
    }

    @ApiOperation(value = "部门轮换")
    @GetMapping("/departmentRotation/{ymlName}/{department}")
    public QueryResponseResult departmentRotation(@PathVariable("ymlName") String ymlName,@PathVariable("department") String department){
        return  userService.departmentRotation(ymlName,department);
    }

}
