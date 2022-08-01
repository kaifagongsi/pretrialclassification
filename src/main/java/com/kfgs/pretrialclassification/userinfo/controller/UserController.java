package com.kfgs.pretrialclassification.userinfo.controller;

import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.common.utils.JsonResult;
import com.kfgs.pretrialclassification.common.utils.SecurityUtil;
import com.kfgs.pretrialclassification.common.vo.LoginUser;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api("用户信息")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {


    @Autowired
    UserService userService;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String serverName;


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
    @ApiOperation(value = "更新个人信息")
    @PostMapping("/updateUserInfo")
    public QueryResponseResult updateUserinfo(@RequestBody FenleiBaohuUserinfo fenleiBaohuUserinfo){
        return userService.updateUserinfo(fenleiBaohuUserinfo);
    }

    @ApiOperation(value = "更新个人密码")
    @PostMapping("/changePssword/{oldPassword}/{newPassword}/{loginname}")
    public QueryResponseResult changePssword(@PathVariable("oldPassword") String oldPassword,@PathVariable("newPassword") String newPassword,@PathVariable("loginname") String loginname){
        return userService.updatePasswordByLoinname(oldPassword,newPassword,loginname);
    }

    @ApiOperation(value = "检查邮箱是否唯一")
    @PostMapping("/chenckUserEmail/{email}")
    public QueryResponseResult chenckUserEmail(@PathVariable String email){
        return userService.chenckUserEmail(email);
    }

    @ApiOperation(value = "获取数据库中的部门信息")
    @GetMapping("/getInitDep1s")
    public QueryResponseResult getInitDep1s(){
        List list = (List)redisTemplate.boundHashOps(serverName).get("dep1s");
        QueryResult queryResult = new QueryResult();
        queryResult.setList(list);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    @ApiOperation(value = "获取数据库中的部门信息下的处室信息")
    @GetMapping("/getInitDep2sByDep1/{dep1}")
    public QueryResponseResult getInitDep2s(@PathVariable String dep1){
        return  userService.getInitDep2s(dep1);
    }

   /* 没有人会点退出，改功能没有什么意义
    @ApiOperation(value = "获取当前登录人")
    @GetMapping("/getUserOnLine")
    public QueryResponseResult getUserOnLine(){
        List<Object> allPrincipals = securityUtil.sessionRegistryGetAllPrincipals();
        List<String> loginList = new ArrayList();
        for(Object o : allPrincipals){
            LoginUser user = (LoginUser) o;
            loginList.add(user.getLoginname());
        }
        List<String> fullName = userService.getUserFullNameByList(loginList);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(fullName);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }*/
}
