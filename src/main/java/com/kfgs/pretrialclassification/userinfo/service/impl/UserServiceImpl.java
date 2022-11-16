package com.kfgs.pretrialclassification.userinfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.common.log.Log;
import com.kfgs.pretrialclassification.common.service.impl.ReadisInitService;
import com.kfgs.pretrialclassification.common.utils.TrimeUtil;
import com.kfgs.pretrialclassification.common.utils.UUIDUtil;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.*;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    FenleiBaohuUserinfoMapper userinfoMapper;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    private FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${pretrialclassification.mailboxsuffix}")
    private String emailSuffix;

    @Value("${pretrialclassification.dep1s}")
    private String dep1s;

    @Value("${spring.application.name}")
    private String serverName;

    @Autowired
    ReadisInitService readisInitService;

    private String adjudicatorPattern = "\\d{6}";

    private BoundHashOperations<String, String, Object> tokenStorage() {
        return redisTemplate.boundHashOps(jwtTokenUtils.getTokenHeader());
    }
    private BoundHashOperations<String, String, Object> tokenStorage(String key) {
        return redisTemplate.boundHashOps(key);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryResponseResult updatePasswordByLoinname(String oldPassword, String newPassword, String loginname) {
        if( 1 == userinfoMapper.updatePasswordByLoginname(oldPassword,newPassword,loginname)){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(UserInfoResponseEnum.CHANG_PASSWORD_ERROE,null);
        }
    }

    @Override
    @Log
    public FenleiBaohuUserinfoExt validateUsername(String username) {
        BoundHashOperations<String, String, Object> stringStringObjectBoundHashOperations = tokenStorage();
        Object o = stringStringObjectBoundHashOperations.get(username);
        if(  null == o){
            return null;
        }else{
             return (FenleiBaohuUserinfoExt) tokenStorage().get(username);
        }
    }
    @Override
    @Log
    public Map findUserInfo() {
        Map result = new HashMap<>();
        // 从SecurityContextHolder中获取到，当前登录的用户信息。
        FenleiBaohuUserinfoExt userDetails = null;
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //未登录
            if("anonymousUser".equalsIgnoreCase(principal.toString())){ //anonymousUser
                log.error("当前用户未登录");
                return null;
            }
            userDetails = (FenleiBaohuUserinfoExt) principal;
            // 根据用户Id，获取用户详细信息。    //   getUserinfoByLoginName
            //getUserinfoByLoginNameWithRole  改表以后携带权限查询
            //getUserinfoByLoginName         改表之前不带权限查询
            FenleiBaohuUserinfoExt fenleiBaohuUserinfoExt = userinfoMapper.getUserinfoByLoginNameWithRole(userDetails.getLoginname()).get(0);
            //设置权限
            Map data = new HashMap<>();
            // 此处是权限
            data.put("roles",fenleiBaohuUserinfoExt.getAuthorities());
            //此处是用户名称，无关权限
            data.put("introduction",(fenleiBaohuUserinfoExt.getType() == "admin") ? "用户" : "管理员");
            data.put("name",fenleiBaohuUserinfoExt.getName());
            result.put("code",200);
            result.put("data",data);
            return data;
        }catch (Exception e){
            log.error("当前用户未登录");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public FenleiBaohuUserinfoExt findUserWorkerName(){
        // 从SecurityContextHolder中获取到，当前登录的用户信息。
        FenleiBaohuUserinfoExt userDetails = null;
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //未登录
            if("anonymousUser".equalsIgnoreCase(principal.toString())){ //anonymousUser
                log.error("当前用户未登录");
                return null;
            }
            userDetails = (FenleiBaohuUserinfoExt) principal;
            // 根据用户Id，获取用户详细信息。    //   getUserinfoByLoginName
            //getUserinfoByLoginNameWithRole  改表以后携带权限查询
            //getUserinfoByLoginName         改表之前不带权限查询
            return userinfoMapper.getUserinfoByLoginNameWithRole(userDetails.getLoginname()).get(0);
        }catch (Exception e){
            log.error("当前用户未登录");
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public QueryResponseResult findUserList(String pageNo,String pageSize,Map map) {
        Page<FenleiBaohuUserinfo> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(pageSize));
        String dep1 = null;
        String dep2 = null;
        String isOnline = null;
        if(map.get("dep1") != null && StringUtils.isNotEmpty(map.get("dep1").toString())){
           /* if(map.get("dep1").toString().equalsIgnoreCase("FL")){
                dep1 = "分类审查部";
            }else if(map.get("dep1").toString().equalsIgnoreCase("JG")){
                dep1 = "数据加工部";
            }*/
            dep1 = map.get("dep1").toString();
        }
        if(map.get("dep2") != null && StringUtils.isNotEmpty(map.get("dep2").toString())){
            dep2 = map.get("dep2").toString();
        }
        if(map.get("isOnline") != null && !"all".equals(map.get("isOnline").toString()) && StringUtils.isNotEmpty(map.get("isOnline").toString())){
            isOnline = map.get("isOnline").toString();
        }
        IPage<FenleiBaohuUserinfo> pageInfo = userinfoMapper.getUserList(page,dep1,dep2,isOnline);
        Map reultMap = new HashMap();
        reultMap.put("items", pageInfo.getRecords());
        reultMap.put("total", pageInfo.getTotal());
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(reultMap);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    @Override
    @Log
    public QueryResponseResult addUserInfo(FenleiBaohuUserinfo fenleiBaohuUserinfo) {
        try {
            TrimeUtil.objectToTrime(fenleiBaohuUserinfo);
            String data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
            fenleiBaohuUserinfo.setLastTime(data);
            fenleiBaohuUserinfo.setEmail(fenleiBaohuUserinfo.getEmail()+"@"+emailSuffix);
            fenleiBaohuUserinfo.setWorkername(fenleiBaohuUserinfo.getLoginname()+"-"+fenleiBaohuUserinfo.getName());
            int insert = userinfoMapper.insertSelective(fenleiBaohuUserinfo);
            //后期用Aop 解决
            FenleiBaohuLog log = new FenleiBaohuLog();
            FenleiBaohuUserinfo info =(FenleiBaohuUserinfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.setId(UUIDUtil.getUUID());
            log.setMessage("新增用户：" + fenleiBaohuUserinfo.toString() + "，操作人:" +  info.getLoginname() + "" + info.getName() + info.getWorkername());
            log.setTime(data);
            log.setResult("新增用户");
            fenleiBaohuLogMapper.insert(log);
            if(1 == insert){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    @Override
    public QueryResponseResult checkLoginName(String loginname) {
        loginname = loginname.replaceAll("[\\t\\n\\r]","").trim().replaceAll(" +","");
        FenleiBaohuUserinfo userinfo = userinfoMapper.selectOneByLoginname(loginname);
        Map resultMap = new HashMap();
        if(userinfo == null){
            resultMap.put("flag",true);
        }else{
            resultMap.put("flag",false);
        }
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(resultMap);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    @Override
    @Log
    public QueryResponseResult getUserInfoByLoginName(String loginname) {
        FenleiBaohuUserinfo userinfo = userinfoMapper.selectOneByLoginname(loginname);
        userinfo.setEmail(userinfo.getEmail().replace("@"+emailSuffix,""));
        Map resultMap = new HashMap();
        QueryResult queryResult = new QueryResult();
        if(userinfo == null){
            return new QueryResponseResult(UserInfoResponseEnum.FAIL,null);
        }else{
            resultMap.put("item",userinfo);
            queryResult.setMap(resultMap);
            return new QueryResponseResult(UserInfoResponseEnum.SUCCESS,queryResult);
        }
    }

    @Override
    @Log
    public QueryResponseResult deleteUserByLoginname(String loginname) {
        //1.从redis中获取是否有
        BoundHashOperations<String, Object, Object> pretrialClassification = redisTemplate.boundHashOps(serverName);
        List distinctAdjudicator = (List)pretrialClassification.get("distinctAdjudicator");
        if( distinctAdjudicator.size() == 0 && distinctAdjudicator.isEmpty()){
            distinctAdjudicator = userinfoMapper.selectDistinctAdjudicator();
            BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(serverName);
            stringObjectObjectBoundHashOperations.put("distinctAdjudicator",distinctAdjudicator);
            log.info("向redis中写入已设置的裁决组长");
        }
        if(distinctAdjudicator.contains(loginname)){
            return new QueryResponseResult(UserinfoCode.USERINFO_FAIL_HAVINGARBITER,null);
        }else{
            QueryWrapper<FenleiBaohuUserinfo> wrapper = new QueryWrapper();
            wrapper.eq("loginname",loginname);
            int delete = userinfoMapper.delete(wrapper);
            //后期用Aop 解决
            FenleiBaohuLog log = new FenleiBaohuLog();
            FenleiBaohuUserinfo info =(FenleiBaohuUserinfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.setId(UUIDUtil.getUUID());
            log.setMessage("删除用户：" + loginname + "，操作人:" +  info.getLoginname() + "" + info.getName() + info.getWorkername());
            log.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
            log.setResult("删除用户");
            fenleiBaohuLogMapper.insert(log);
            if(1 == delete){
                readisInitService.initArbiter();
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }
    }

    @Override
    public QueryResponseResult departmentRotation(String ymlName,String department) {
        try {
            URL url = UserServiceImpl.class.getClassLoader().getResource(ymlName);
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            dumperOptions.setPrettyFlow(false);
            Yaml yaml = new Yaml(dumperOptions);
            Map map  = (Map)yaml.load(new FileInputStream(url.getFile()));
            Map mapDepartmentRotation  = (Map) map.get("pretrialclassification");
            log.info("这是修改前："+mapDepartmentRotation.get("departmentRotation"));
            mapDepartmentRotation.put("departmentRotation",department);
            yaml.dump(map, new OutputStreamWriter(new FileOutputStream(url.getFile())));
            log.info("修改了部门轮换，旧值为：" + mapDepartmentRotation.get("departmentRotation") + "。新值为：" + department);
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    @Override
    @Log
    public QueryResponseResult updateUserinfo(FenleiBaohuUserinfo fenleiBaohuUserinfo) {
        try{
            //1.如果修改了用户的在岗和不在岗
            FenleiBaohuUserinfo dbInfo = userinfoMapper.selectOneByLoginname(fenleiBaohuUserinfo.getLoginname());
            if(dbInfo.getIsOnline() != fenleiBaohuUserinfo.getIsOnline()){
                List<FenleiBaohuResult> results = fenleiBaohuResultMapper.selectUnFinishListByLoginName(String.valueOf(fenleiBaohuUserinfo.getWorkername()));
                if(results != null && results.size() != 0){
                    return new QueryResponseResult(UserinfoCode.USERINFO_FAIL_CANNOTUPDATA_ONINE,null);
                }
            }
            String adjudicator = fenleiBaohuUserinfo.getAdjudicator();
            if( StringUtils.isNotEmpty(adjudicator) && adjudicator.length() ==6 && Pattern.compile(adjudicatorPattern).matcher(adjudicator).find()){
                String time = fenleiBaohuUserinfo.getLastTime();
                TrimeUtil.objectToTrime(fenleiBaohuUserinfo);
                QueryWrapper<FenleiBaohuUserinfo> wrapper = new QueryWrapper();
                wrapper.eq("loginname",fenleiBaohuUserinfo.getLoginname());
                fenleiBaohuUserinfo.setEmail(fenleiBaohuUserinfo.getEmail()+"@"+emailSuffix);
                fenleiBaohuUserinfo.setWorkername(fenleiBaohuUserinfo.getLoginname()+"-"+fenleiBaohuUserinfo.getName());
                fenleiBaohuUserinfo.setLastTime(time);
                int update = userinfoMapper.update(fenleiBaohuUserinfo,wrapper);
                //后期用Aop 解决
                FenleiBaohuLog log = new FenleiBaohuLog();
                FenleiBaohuUserinfo info =(FenleiBaohuUserinfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                log.setId(UUIDUtil.getUUID());
                log.setMessage("更新用户：" + fenleiBaohuUserinfo.toString() + "，操作人:" +  info.getLoginname() + "" + info.getName() + info.getWorkername());
                log.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
                log.setResult("更新用户");
                fenleiBaohuLogMapper.insert(log);
                if(1 == update){
                    readisInitService.initArbiter();
                    return new QueryResponseResult(CommonCode.SUCCESS,null);
                }else{
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }
            }else{
                return new QueryResponseResult(UserinfoCode.USERINFO_FAIL_ADJUDICATOR,null);
            }
        }catch (Exception e){
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    @Override
    public QueryResponseResult chenckUserEmail(String email) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("email",email+"@"+emailSuffix);
        Integer integer = userinfoMapper.selectCount(queryWrapper);
        if(integer < 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(UserInfoResponseEnum.EMAIL_FAIL,null);
        }

    }

    /**
     * 放弃使用 因为直接存在redis 中 了
     * @return
     */
    @Override
    public QueryResponseResult getInitDep1s() {
        //List<String> list = userinfoMapper.selectDistinctDep1();
        List<String> list = Arrays.asList(dep1s.split(","));
        QueryResult queryResult = new QueryResult();
        queryResult.setList(list);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    @Override
    public QueryResponseResult getInitDep2s(String dep1) {
        // 直接从redis 获取数据
        BoundHashOperations<String, String, Object> stringStringObjectBoundHashOperations = tokenStorage(serverName);
        HashMap dep2s = (HashMap)stringStringObjectBoundHashOperations.get("dep2s");
        List<String> list = (List<String>) dep2s.get(dep1);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(list);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    /**
     * 返回用户的名称+代码+领域
     * @param loginList 需要获取的用户登录名称的list
     * @return
     */
    @Override
    public List<String> getUserFullNameByList(List<String> loginList) {
        return userinfoMapper.selectFullNameByLoginList(loginList);
    }
}
