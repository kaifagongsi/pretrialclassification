package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfgs.pretrialclassification.domain.TestOracle;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestOracleDao extends BaseMapper<TestOracle> {
    public List<TestOracle> findAll();
}
