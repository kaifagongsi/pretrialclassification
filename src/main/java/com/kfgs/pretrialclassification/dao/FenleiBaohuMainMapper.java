package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mango
 */
public interface FenleiBaohuMainMapper extends BaseMapper<FenleiBaohuMain> {

    List<FenleiBaohuMain> findAll();

    IPage<FenleiBaohuMain> findMainByState(Page<FenleiBaohuMain> page, @Param("state") String state);
}
