package com.kfgs.pretrialclassification.common.repeatsubmit;

import com.google.common.cache.Cache;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import io.swagger.models.auth.In;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
/**
 * @author lsy
 */
public class NoRepeatSubmitAop {


    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Resource
    private Cache<String,Integer> cache;

    //log
    FenleiBaohuLog log = new FenleiBaohuLog();

    @Around("execution(* com.kfgs..*Controller.*(..)) && @annotation(nrs)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint,NoRepeatSubmit nrs){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String sessioId = Objects.requireNonNull(RequestContextHolder.getRequestAttributes().getSessionId());
            HttpServletRequest request;
            if (attributes != null){
                request = attributes.getRequest();
                String key = sessioId + "-" + request.getServletPath();
                if (cache.getIfPresent(key) == null){  //缓存中若存在视为重复提交,第一次则写入缓存
                    Object o = proceedingJoinPoint.proceed();
                    cache.put(key,0);
                    return o;
                    //return new QueryResponseResult(CommonCode.SUCCESS,null);
                }else {
                    log.setId(key);
                    log.setMessage("重复提交,操作失败");
                    return new QueryResponseResult(CommonCode.REPEAT_SUBMIT,null);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.setMessage("验证重复提交时出现未知异常，请稍后再试！");
            return new QueryResponseResult(CommonCode.REPEAT_SUBMIT,null);
        }
        return null;
    }




}
