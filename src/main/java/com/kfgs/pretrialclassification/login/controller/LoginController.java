package com.kfgs.pretrialclassification.login.controller;

import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.common.utils.JsonResult;
import com.kfgs.pretrialclassification.common.utils.VerifyCodeUtils;
import com.kfgs.pretrialclassification.common.vo.LoginUser;
import com.kfgs.pretrialclassification.common.vo.TokenValue;
import com.kfgs.pretrialclassification.login.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api("用户认证")
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    @Lazy
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation(value = "用户登录认证", notes = "用户名，密码登录格式 {\"username\":\"admin\",\"password\":\"admin\"}")
    @PostMapping("/login")
    public JsonResult<TokenValue> login(@RequestBody @Validated LoginUser user, BindingResult br) {
        if (br.hasErrors()) {
            String message = br.getFieldError().getDefaultMessage();
            return JsonResult.fail(message);
        }
        // 根据 CodeKey 从 redis 中获取到 codeText
        String codeText = redisTemplate.opsForValue().get(user.getCodeKey());
        // 比较 redis 中的 codeText 和 用户输入的 codeText
        // 将 redis.codeText 和 user.codeText 都转换成小写，然后比较
        if (org.springframework.util.StringUtils.isEmpty(codeText) || !codeText.toLowerCase().equals(user.getCodeText().toLowerCase())) {
            return JsonResult.fail("验证码错误！");
        }
        System.out.println("LoginUser : " + user);
        try {
            String jwtToken = loginService.login(user.getLoginname(), user.getPassword());
            //String jwtToken = fenleiBaohuUserinfoService.login(user.getLoginname(), user.getPassword());
            TokenValue tokenValue = TokenValue.builder()
                    .header(jwtTokenUtils.getTokenHeader())
                    .value(jwtToken)
                    .prefix(jwtTokenUtils.getTokenHead())
                    .expiration(jwtTokenUtils.getExpiration())
                    .build();
            // 登录成功后！删除 redis 中的验证码
            redisTemplate.delete(user.getCodeKey());
            return JsonResult.success("登录成功", tokenValue);
        } catch (AuthenticationException ex) {
            return JsonResult.fail("用户名或密码错误");
        }
    }

    @GetMapping("testCorseGet")
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
    }

    @ApiOperation(value = "获取图形验证码", notes = "获取图形验证码, codeKey 前端传入一个随机生成的字符串")
    @GetMapping("/verify/code/{codeKey}")
    public void imageVerifyCode(@PathVariable String codeKey, HttpServletResponse response) throws IOException {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 获取 图片 验证码
        VerifyCodeUtils.ImageVerifyCode image = VerifyCodeUtils.getImage();
        // 获取 图片 验证码中的文本
        String codeText = image.getCodeText();
        // 将验证码的 codeKey 和 codeText , 保存在 redis 中，有效时间为 60 分钟
        redisTemplate.opsForValue().set(codeKey, codeText, 60, TimeUnit.MINUTES);
        ImageIO.write(image.getImage(), "JPEG", response.getOutputStream());
    }

    @ApiOperation(value = "退出操作")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request,HttpServletResponse response){
        loginService.logout(request,response);
    }


}
