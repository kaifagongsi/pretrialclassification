package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;

import java.util.List;

/**
 * @author mango
 */
public interface FenleiBaohuUserinfoMapper extends BaseMapper<FenleiBaohuUserinfo> {
    List<FenleiBaohuUserinfo> findAreaName();

    List<FenleiBaohuUserinfoExt> getUserinfoByLoginName(String loginname);

    List<FenleiBaohuUserinfoExt> getUserinfoByLoginNameWithRole(String loginname);
}
