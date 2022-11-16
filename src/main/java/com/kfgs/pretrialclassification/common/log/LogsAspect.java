package com.kfgs.pretrialclassification.common.log;

import com.alibaba.fastjson.JSON;
import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//定义日志切面
@Aspect
@Component
public class LogsAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogsAspect.class);



    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Pointcut("@annotation(com.kfgs.pretrialclassification.common.log.Log)")
    public void logPointCut(){

    }

    /**
     * 设置操作异常切入点记录异常日志，扫描所有controller包下操作
     * @return
     * @throws Throwable
     */
    @Pointcut("execution(* com.kfgs..*Controller..*.*(..))")
    public void exceptionLogPointCut(){

    }
    /**
     * 正常返回通知，拦截用户操作日志，若连接点抛出异常，则不会执行
     */
    @AfterReturning(value = "logPointCut()",returning = "keys")
    public void saveLog(JoinPoint joinPoint,Object keys){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        FenleiBaohuLog fenleiBaohuLog = new FenleiBaohuLog();
        try {
            //从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取织入点所在的方法
            Method method = signature.getMethod();
            //获取操作
            Log log = method.getAnnotation(Log.class);
            if (log != null){
                //获取请求的类名
                String className = joinPoint.getTarget().getClass().getName();
                //获取请求的方法名
                String methodName = method.getName();
                methodName = className + "." + methodName;
                fenleiBaohuLog.setMethod(methodName);
                /**请求参数,将参数所在的数组转换成json
                 * args:参数值
                 * params:参数名（和参数值一一对应）
                 */
                String paramStr = "";
                Object[] args = joinPoint.getArgs();
                //获取到方法的所有参数名的字符串数组
                String[] params = signature.getParameterNames();
                for(int i=0;i<args.length;i++){
                    paramStr += params[i] + ":" + args[i] + ",";
                }
                fenleiBaohuLog.setParams(paramStr);
                /*Map<String,String> parMap = converMap(request.getParameterMap());
                String params = JSON.toJSONString(parMap);
                fenleiBaohuLog.setParams(params);*/
                //请求用户ID
                String tokenHeader = this.jwtTokenUtils.getTokenHeader();
                String completeToken = request.getHeader(tokenHeader);
                final String tokenValue = jwtTokenUtils.interceptCompleteToken(completeToken);
                // 根据 token值，获取 用户的 username
                String userid = jwtTokenUtils.getUsernameFromToken(tokenValue) == null?"":jwtTokenUtils.getUsernameFromToken(tokenValue);
                fenleiBaohuLog.setId(userid);
                //请求时间
                fenleiBaohuLog.setTime(new Date().toString());
                //操作结果
                fenleiBaohuLog.setMessage("未报异常");
                //返回结果
                fenleiBaohuLog.setResult(JSON.toJSONString(keys));

                //打印结果
                printLog(fenleiBaohuLog);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     * @param joinPoint,e
     */
    @AfterThrowing(pointcut = "exceptionLogPointCut()",throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint,Throwable e){
        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        FenleiBaohuLog fenleiBaohuLog = new FenleiBaohuLog();
        try {
            //从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取织入点所在的方法
            Method method = signature.getMethod();
            //请求用户ID
            String tokenHeader = this.jwtTokenUtils.getTokenHeader();
            String completeToken = request.getHeader(tokenHeader);
            final String tokenValue = jwtTokenUtils.interceptCompleteToken(completeToken);
            // 根据 token值，获取 用户的 username
            String userid = jwtTokenUtils.getUsernameFromToken(tokenValue) == null?"":jwtTokenUtils.getUsernameFromToken(tokenValue);
            fenleiBaohuLog.setId(userid);
            //获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            //获取请求方法
            String methodName = method.getName();
            methodName = className + "." + methodName;
            //请求的参数
            Map<String,String> parMap = converMap(request.getParameterMap());
            String params = JSON.toJSONString(parMap);
            fenleiBaohuLog.setMethod(methodName);
            fenleiBaohuLog.setParams(params);
            fenleiBaohuLog.setMessage("异常:" + e.getClass().getName());
            fenleiBaohuLog.setTime(new Date().toString());
            fenleiBaohuLog.setResult("");
            //打印结果
            printLog(fenleiBaohuLog);
        }catch (Exception ew){
            ew.printStackTrace();
        }
    }

    public void printLog(FenleiBaohuLog fenleiBaohuLog){
        String ss = "\n" + "-----------------------------操作日志START--------------------------------"+"\n"
                + "用户ID:" + fenleiBaohuLog.getId() + "\n"
                + "完成时间:" + fenleiBaohuLog.getTime() + "\n"
                + "请求方法:" + fenleiBaohuLog.getMethod() + "\n"
                + "请求参数:" + fenleiBaohuLog.getParams() + "\n"
                + "请求结果:" + fenleiBaohuLog.getMessage() + "\n"
                + "返回值:" + fenleiBaohuLog.getResult() + "\n"
                + "-----------------------------操作日志END--------------------------------";
        /*logger.info("用户ID:" + fenleiBaohuLog.getId() + "\n");
        logger.info("请求时间:" + fenleiBaohuLog.getTime() + "\n");
        logger.info("请求方法:" + fenleiBaohuLog.getMethod() + "\n");
        logger.info("请求参数:" + fenleiBaohuLog.getParams() + "\n");
        logger.info("请求结果:" + fenleiBaohuLog.getMessage() + "\n");
        logger.info("返回值:" + fenleiBaohuLog.getResult() + "\n");*/
        logger.info(ss);
    }
    /***
     * 转换request请求参数
     */
    public Map<String,String> converMap(Map<String,String[]> paramMap){
        Map<String,String> rtnMap = new HashMap<String, String>();
        for (String key : paramMap.keySet()){
            rtnMap.put(key,paramMap.get(key)[0]);
        }
        return rtnMap;
    }

    /*@Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        *//*long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长
        long time = System.currentTimeMillis() - beginTime;
        //异步保存日志
        saveLog(point,time);*//*
        Object result = null;
        try {
            //执行方法
            result = point.proceed();
            //保存请求日志
            saveRequestLog(point);
        }catch (Exception e){
            //保存异常日志
            saveExceptionLog(point,e.getMessage());
        }
        return result;
    }

    private void saveRequestLog(ProceedingJoinPoint point){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        logger.info("请求路径:" + request.getRequestURI());
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        logger.info("请求方法:" + method.getName());
        //获取方法上LogFilter注解
        Log log = method.getAnnotation(Log.class);
        String value = log.value();

    }

    private void saveExceptionLog(ProceedingJoinPoint point,String exeMsg){
        logger.info("捕获异常:" + exeMsg);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        logger.info("请求路径:" + request.getRequestURI());
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        logger.info("请求方法:" + method.getName());
    }
    //处理日志
    void saveLog(ProceedingJoinPoint joinPoint,long time) throws  InterruptedException{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = signature.getName();
        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        logger.info("---------------------接口日志--------------------"+"\n"+"类名称:"+className
                +"\n"+"方法名:"+methodName+"\n"+"执行时间:"+time+"毫秒");
    }*/


}
