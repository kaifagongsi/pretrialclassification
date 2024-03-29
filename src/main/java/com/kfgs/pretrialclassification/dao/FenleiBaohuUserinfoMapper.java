package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.request.ArbiterParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mango
 */
@Repository
public interface FenleiBaohuUserinfoMapper extends BaseMapper<FenleiBaohuUserinfo> {
    List<FenleiBaohuUserinfo> findAreaName();

    List<FenleiBaohuUserinfoExt> getUserinfoByLoginName(String loginname);

    List<FenleiBaohuUserinfoExt> getUserinfoByLoginNameWithRole(String loginname);

    IPage<FenleiBaohuUserinfo> getUserList(IPage<FenleiBaohuUserinfo> page, @Param("dep1") String dep1,  @Param("dep2")String dep2,@Param("isOnline") String isOnline);

    int insertSelective(FenleiBaohuUserinfo fenleiBaohuUserinfo);

    int insertEntity(FenleiBaohuUserinfo fenleiBaohuUserinfo);

    FenleiBaohuUserinfo selectOneByLoginname(String loginname);

    List<String> selectListByDep1AndDep2(@Param("dep1") String dep1,@Param("dep2") String dep2);


    List<ArbiterParam> selectListByWorkerName(List list);

    String selectAdjudicatorByWorkerName(String workerName);

    String selectUpdateWorkerName(@Param("worker") String worker);

    List<String> selectEmailByList(List<String> to);

    List<String> selectDistinctDep1();

    List<String> selectFullNameByLoginList(List<String> loginList);

    List<String> selectDistinctAdjudicator();

    List<String> selectDistinctDep2ByDep1(String dep1);

    int updatePasswordByLoginname(@Param("oldPsssword") String oldPsssword,@Param("newPassword")String newPassword,@Param("loginName")String loginName);

}
