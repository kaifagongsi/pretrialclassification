package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuCpctoipc;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mango
 */
@Repository
public interface FenleiBaohuCpctoipcMapper extends BaseMapper<FenleiBaohuCpctoipc> {

    public String getIpcByCpcList(List list);
}
