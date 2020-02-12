package com.kfgs.pretrialclassification.test.controller;

import com.kfgs.pretrialclassification.dao.TestOracleDao;
import com.kfgs.pretrialclassification.domain.User;
import com.kfgs.pretrialclassification.test.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * <H3>pretrialclassification</H3>
 * <p></p>
 *
 * @author : 你的名字
 * @date : 2020-01-07 17:10
 */
@Api("测试dao")
@RestController
public class TestController {

    @Autowired
    TestOracleDao testorcaleDao;

    @Autowired
    TestService testService;

    @ApiOperation("测试 session ")
    @GetMapping("/hello")
    public String printSession(HttpSession session, String name) {
        String storedName = (String) session.getAttribute("name");
        if (storedName == null) {
            session.setAttribute("name", name);
            storedName = name;
        }
        return "hello " + storedName;
    }

    @ApiOperation("测试 testCorsePost ")
    @PostMapping("testCorsePost")
    public Map testCors( User user){
        System.out.println(user.getUsername() + "---" + user.getPassword());
        Map map = new HashMap();
        map.put("token","admin-token");
        Map resultMap = new HashMap();
        resultMap.put("data",map);
        resultMap.put("code",20000);
        return resultMap;
    }

    @ApiOperation("测试redis")
    @GetMapping("/testRedis")
    public void testRedis(){
        testService.textRedis();
    }


    /*@GetMapping("testCorseGet")
    public Map testCorseGet(@RequestParam("username") String username, @RequestParam("password")String password, HttpServletRequest req){
        System.out.println(username + "---" + password);
        Map resultMap = new HashMap();
        if(StringUtils.isNotBlank(username) || StringUtils.isNotBlank(password)){
            Map map = new HashMap();
            //把用户逇信息，存入session的作用域
            HttpSession session = req.getSession();
            session.setAttribute("USER_IN_SESSION" , username + password);
            map.put("token","admin-token");
            resultMap.put("data",map);
            resultMap.put("code",20000);
        }else{
            resultMap.put("code",50000);
        }
        return resultMap;
    }*/
}
