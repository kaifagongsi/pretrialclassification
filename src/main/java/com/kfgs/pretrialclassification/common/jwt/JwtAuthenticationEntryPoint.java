package com.kfgs.pretrialclassification.common.jwt;

import com.kfgs.pretrialclassification.common.utils.JsonResult;
import com.kfgs.pretrialclassification.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * Date: 2020-02-03-16-51
 * Module:
 * Description:
 *  认证失败，处理类
 * @author:
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        /**身份认证未通过*/
        response.getWriter().write(
                JsonUtils.objectToJson(
                        JsonResult.result(
                                JsonResult.HttpStatus.UNAUTHORIZED.getStatus(),
                                "未认证，请在前端系统进行认证"
                        )
                )
        );
    }
}
