package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author mango
 */
public interface FenleiBaohuUserinfoMapper extends BaseMapper<FenleiBaohuUserinfo> {
    List<FenleiBaohuUserinfo> findAreaName();

    List<FenleiBaohuUserinfoExt> getUserinfoByLoginName(String loginname);

    List<FenleiBaohuUserinfoExt> getUserinfoByLoginNameWithRole(String loginname);

    IPage<FenleiBaohuUserinfo> getUserList(IPage<FenleiBaohuUserinfo> page, @Param("dep1") String dep1,  @Param("dep2")String dep2,@Param("isOnline") String isOnline);

    int insertEntity(FenleiBaohuUserinfo fenleiBaohuUserinfo);

    FenleiBaohuUserinfo selectOneByLoginname(String loginname);
}
