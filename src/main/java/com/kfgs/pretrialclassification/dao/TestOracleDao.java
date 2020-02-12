package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfgs.pretrialclassification.domain.TestOracle;

import java.util.List;

public interface TestOracleDao extends BaseMapper<TestOracle> {
    public List<TestOracle> findAll();
}
