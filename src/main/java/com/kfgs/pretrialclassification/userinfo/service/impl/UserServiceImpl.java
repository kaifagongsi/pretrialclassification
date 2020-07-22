package com.kfgs.pretrialclassification.userinfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.common.jwt.JwtTokenUtils;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.*;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    FenleiBaohuUserinfoMapper userinfoMapper;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    private BoundHashOperations<String, String, Object> tokenStorage() {
        return redisTemplate.boundHashOps(jwtTokenUtils.getTokenHeader());
    }

    @Override
    public FenleiBaohuUserinfoExt validateUsername(String username) {
        BoundHashOperations<String, String, Object> stringStringObjectBoundHashOperations = tokenStorage();
        Object o = stringStringObjectBoundHashOperations.get(username);
        System.out.println(o.toString());
        return (FenleiBaohuUserinfoExt) tokenStorage().get(username);
    }
    @Override
    public Map findUserInfo() {
        Map result = new HashMap<>();
        // 从SecurityContextHolder中获取到，当前登录的用户信息。
        FenleiBaohuUserinfoExt userDetails = null;
        try{
            userDetails = (FenleiBaohuUserinfoExt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (Exception e){
            e.printStackTrace();
        }

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
    }


    @Override
    public QueryResponseResult findUserList(String pageNo,String pageSize,Map map) {
        Page<FenleiBaohuUserinfo> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(pageSize));
        String dep1 = null;
        String dep2 = null;
        String isOnline = null;
        if(map.get("dep1") != null && StringUtils.isNotEmpty(map.get("dep1").toString())){
            if(map.get("dep1").toString().equalsIgnoreCase("FL")){
                dep1 = "分类审查部";
            }else if(map.get("dep1").toString().equalsIgnoreCase("JG")){
                dep1 = "数据加工部";
            }
        }
        if(map.get("dep2") != null && StringUtils.isNotEmpty(map.get("dep2").toString())){
            dep2 = map.get("dep2").toString();
        }
        if(map.get("isOnline") != null && !map.get("isOnline").toString().equals("all") && StringUtils.isNotEmpty(map.get("isOnline").toString())){
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
    public QueryResponseResult addUserInfo(FenleiBaohuUserinfo fenleiBaohuUserinfo) {
        System.out.println(fenleiBaohuUserinfo);
        //设置分类/加工
        fenleiBaohuUserinfo.setOrgname(fenleiBaohuUserinfo.getDep1());
        if(fenleiBaohuUserinfo.getDep1().equals("FL")){
            fenleiBaohuUserinfo.setDep1("分类审查部");
        }else if(fenleiBaohuUserinfo.getDep1().equals("JG")){
            fenleiBaohuUserinfo.setDep1("数据加工部");
        }
        fenleiBaohuUserinfo.setEmail(fenleiBaohuUserinfo.getEmail()+"@cnipa.gov.cn");
        fenleiBaohuUserinfo.setWorkername(fenleiBaohuUserinfo.getLoginname()+"-"+fenleiBaohuUserinfo.getName());
        int insert = userinfoMapper.insertEntity(fenleiBaohuUserinfo);
        if(1 == insert){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    @Override
    public QueryResponseResult checkLoginName(String loginname) {
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
    public QueryResponseResult getUserInfoByLoginName(String loginname) {
        FenleiBaohuUserinfo userinfo = userinfoMapper.selectOneByLoginname(loginname);
        Map resultMap = new HashMap();
        QueryResult queryResult = new QueryResult();
        if(userinfo == null){
            return new QueryResponseResult(UserInfoCode.FAIL,null);
        }else{
            resultMap.put("item",userinfo);
            queryResult.setMap(resultMap);
            return new QueryResponseResult(UserInfoCode.SUCCESS,queryResult);
        }
    }

    @Override
    public QueryResponseResult deleteUserByLoginname(String loginname) {
        QueryWrapper<FenleiBaohuUserinfo> wrapper = new QueryWrapper();
        wrapper.eq("loginname",loginname);
        int delete = userinfoMapper.delete(wrapper);
        if(1 == delete){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
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
            System.out.println("这是修改前："+mapDepartmentRotation.get("departmentRotation"));

            mapDepartmentRotation.put("departmentRotation",department);
            yaml.dump(map, new OutputStreamWriter(new FileOutputStream(url.getFile())));
            log.info("修改了部门轮换，旧值为：" + mapDepartmentRotation.get("departmentRotation") + "。新值为：" + department);
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }
}
