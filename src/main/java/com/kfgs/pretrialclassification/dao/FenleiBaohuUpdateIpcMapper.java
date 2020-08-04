package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUpdateipc;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUpdateipcExt;
import org.apache.ibatis.annotations.Param;

/**
 * @author mango
 */
public interface FenleiBaohuUpdateipcMapper extends BaseMapper<FenleiBaohuUpdateipc> {

    IPage<FenleiBaohuUpdateipcExt> selectFenleiBaohuUpdateIpcPage(Page<FenleiBaohuUpdateipc> page, @Param("state") String state);


    int updateByIdAndWorker(@Param("id") String id, @Param("worker") String worker, @Param("state") String state);
}
