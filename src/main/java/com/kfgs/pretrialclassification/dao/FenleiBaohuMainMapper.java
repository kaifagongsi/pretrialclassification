package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author mango
 */
public interface FenleiBaohuMainMapper extends BaseMapper<FenleiBaohuMain> {

    IPage<FenleiBaohuMain> findAll();

    IPage<FenleiBaohuMain> findMainByState(Page<FenleiBaohuMain> page, @Param("state") String state);

    IPage<FenleiBaohuMain> selectCaseIn(Page<FenleiBaohuMain> page, @Param("begintime") String begintime, @Param("endtime") String endtime);

    //IPage<FenleiBaohuMainResultExt> selectByCondition(IPage<FenleiBaohuMainResultExt> page, @Param("ew") Wrapper<FenleiBaohuMainResultExt> queryWrapper);

    IPage<FenleiBaohuMainResultExt> selectByCondition(IPage<FenleiBaohuMainResultExt> page, @Param("id") String id,@Param("name") String name,@Param("sqr") String sqr,@Param("sqh") String sqh,@Param("worker") String worker,@Param("state") String state,@Param("begintime") String begintime,@Param("endtime") String endtime);

    FenleiBaohuMain searchByCondition(@Param("id") String id,@Param("sqh") String sqh,@Param("mingcheng") String mingcheng);

    String getCaseID(@Param("sqh") String sqh,@Param("mingcheng") String mingcheng);

    int insertEntity(FenleiBaohuMain fenleiBaohuMain);

    int findDoubleByID(@Param("id") String id, @Param("oraginization") String oraginization);

    IPage<FenleiBaohuMain> findByState(Page<FenleiBaohuMain> page, @Param("state") String state);

    String getType(@Param("id") String id);

    int updateCaseRule(@Param("id") String id,@Param("state") String state);

    int updateIpciCciCcaCsetsById(@Param("ipci") String ipci, @Param("cci")String cci, @Param("cca")String cca, @Param("csets") String csets, @Param("id") String id);

    int updateByIdAndWithOutNotExport(@Param("id") String id, @Param("ipci")String ipci,@Param("cci") String cci, @Param("cca")String cca, @Param("csets")String csets);
}
